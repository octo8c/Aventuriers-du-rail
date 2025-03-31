package src.main.java.chiffrement;

import java.io.*;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;

public final class Elgamal {

    /* (Le reste du code a été du jeu aventuriers du rail a été fait avant l'apprentisage de mécanisme comme les mutex
     * il n'est donc pas otpimises a certain moment)
     *
     * Cette classe est un ajout suplémentaire à un projet effectué pendant le 2ème semestre de 2ème année de licence :
     * une adaptation du jeu de société les aventuriers-du-rail avec un mode internet 
     * Le choix du systeme cryptographique d'Elgamal a été fait pour ca facilité mais également sa sécurité
     * L'adaptation est base sur cette implementation https://en.wikipedia.org/wiki/ElGamal_signature_scheme
     * Les parametre comme le nombre de bits de la cle ou meme le nombre de verification  
     * sur la primalité de la clé sont plus bas que les normes recommendes pour des raisons d'optimisation
     * Pour utiliser des systemes de cryptographique plus sur, il faut mieux se basé sur le package java.security
     * 
     * Les contraintes pour cette classe sont de ne s'appuyer sur aucun algorithme déja fait 
     * le seul package utilisé est BigInteger pour faciliter la manipulation de grands entier
     * (!!!La fonction de haching n'est pas a utilisés et devrait etre refaite complement !!!)
     * 
     * Les anciens commit ne sont pas affichés car le serveur ou le projet était hebergé a fermé.
     * Des optimisation non ajoutés serait par exemple du paralelisme sur le calcul de certaines cle, 
     * ou meme une fonction de hashage plus poussé comme SHA256
     */

    /**
     * Le nombre de bits maximum de l'entier
     */
    private static final int NMB_BITS = 1024;
    /**
     * PublicKey du chiffrement
     */
    private PublicKey publicKey;
    /**
     * La privateKey pour encrypter les communications
     */
    private PrivateKey privateKey;
    /**
     * Liste de toutes les publicKeys en communication
     */
    private ArrayList<PublicKey> listPublicKeys;
    /**
     * Genere un objet content une cle publique et une cle primaire
     * et mettant en place le chiffrement el gamal
     * 
     * @param n le parametre de sécurité
     */
    private SecureRandom secureRandom;
    

    public Elgamal(int n) {
        secureRandom = new SecureRandom();
        BigInteger premier_germain = generate_sophie_germain_number(n, this.secureRandom);
        BigInteger qBigInteger = premier_germain.multiply(BigInteger.TWO).add(BigInteger.ONE);
        BigInteger groupGenerator = generateurModulo(qBigInteger, premier_germain);
        BigInteger a = new BigInteger(qBigInteger.bitLength(), this.secureRandom)
                .mod(premier_germain.subtract(BigInteger.TWO))
                .add(BigInteger.ONE);
        BigInteger A = groupGenerator.modPow(a, qBigInteger);
        publicKey = new PublicKey(qBigInteger, groupGenerator, A);
        privateKey = new PrivateKey(qBigInteger, a);
        listPublicKeys = new ArrayList<PublicKey>();
    }

    public CryptedMessage encrypt(Object object) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream oos;
        try {
            oos = new ObjectOutputStream(byteArrayOutputStream);
            oos.writeObject(object);
            oos.flush();
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        /* Chiffrement */
        BigInteger message = new BigInteger(1, byteArrayOutputStream.toByteArray());
        BigInteger b = new BigInteger(NMB_BITS, secureRandom);
        b = b.mod(this.privateKey.p());
        BigInteger bigB = this.publicKey.generateur().modPow(b, this.privateKey.p());
        BigInteger c = this.publicKey.A().modPow(b, this.privateKey.p()).multiply(message).mod(this.privateKey.p());

        /* Signature */
        BigInteger k, s, r;
        s = BigInteger.ZERO;
        r = BigInteger.ZERO;
        BigInteger minusOne = this.privateKey.p().subtract(BigInteger.ONE);

        while (s.compareTo(BigInteger.ZERO) == 0) {
            do {
                k = new BigInteger(minusOne.bitLength(), secureRandom).add(BigInteger.TWO).mod(minusOne);

            } while (k.gcd(minusOne).compareTo(BigInteger.ONE) != 0);
            r = this.publicKey.generateur().modPow(k, this.privateKey.p());
            BigInteger hash = hashing(message).mod(minusOne);
            BigInteger right = this.privateKey.a().multiply(r).mod(minusOne);
            BigInteger kModInverse = k.modInverse(minusOne);
            s = hash.subtract(right).multiply(kModInverse).mod(minusOne);
        }
        return new CryptedMessage(bigB, c, r, s);
    }

    public boolean verify(CryptedMessage cryptedMessage, BigInteger message) {
        if (cryptedMessage.r().compareTo(BigInteger.ZERO) <= 0
                || cryptedMessage.r().compareTo(this.privateKey.p()) >= 0) {
            return false;
        } else if (cryptedMessage.s().compareTo(BigInteger.ZERO) <= 0
                || cryptedMessage.s().compareTo(this.privateKey.p().subtract(BigInteger.ONE)) >= 0) {
            return false;
        }
        BigInteger big = this.publicKey.generateur().modPow(
                hashing(message).mod(this.privateKey.p().subtract(BigInteger.ONE)),
                this.privateKey.p());
        BigInteger reste = this.publicKey.A().modPow(cryptedMessage.r(), this.privateKey.p())
                .multiply((cryptedMessage.r().modPow(cryptedMessage.s(), this.privateKey.p())))
                .mod(this.privateKey.p());
        return big.compareTo(reste) == 0;
    }

    public Object decrypt(CryptedMessage cryptedMessage, int ind) {
        PublicKey publicKey = listPublicKeys.get(ind);
        BigInteger s = cryptedMessage.bigB().modPow(this.privateKey.a(), publicKey.p());
        BigInteger message = cryptedMessage.c().multiply(s.modInverse(publicKey.p())).mod(publicKey.p());
        Object obj = null;
        byte[] bytes = message.toByteArray();
        if (bytes.length > 0 && bytes[0] == 0) {// Pour eviter le padding
            bytes = Arrays.copyOfRange(bytes, 1, bytes.length);
        }
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        try {
            ObjectInputStream ois = new ObjectInputStream(bais);
            obj = ois.readObject();
            ois.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
            System.exit(1);// Tentative d'envoie de donne mauvaise
        }

        if (!this.verify(cryptedMessage, message)) {
            System.err.println("Modifcation des coups/Corruption des donnes lors du transport");
        }
        return obj;
    }

    public void setKey(PublicKey publicKey) {
        this.listPublicKeys.add(publicKey);
    }

    public static BigInteger hashing(BigInteger big) {
        byte[] tab = big.toByteArray();
        BigInteger hash = BigInteger.ZERO;
        for (byte b : tab) {
            hash = hash.shiftLeft(5).xor(hash.shiftRight(3));
            hash = hash.add(BigInteger.valueOf(b * 0x9e3779b9));
            hash = hash.xor(BigInteger.valueOf(b << 7));
        }
        return hash;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    private static BigInteger generateurModulo(BigInteger q, BigInteger p) {
        BigInteger i = BigInteger.TWO;
        while (i.compareTo(q) <= 0) {
            if (!(i.modPow(BigInteger.TWO, q).compareTo(BigInteger.ONE) == 0)
                    && !(i.modPow(p, q).compareTo(BigInteger.ONE) == 0)) {
                return i;
            }
            i = i.add(BigInteger.ONE);
        }
        return i;// Normalement ca ne peut pas arriver
    }

    /**
     * @param n Le parametre de sécurité
     * @return Renvoie un nombre de sophie germain
     */
    private static BigInteger generate_sophie_germain_number(int nmb_test, SecureRandom rand) {
        BigInteger integer = new BigInteger(NMB_BITS, rand);
        /* On test si le nombre genere est un nombre premier de sophie germain */
        while (test_miller_rabin(integer, nmb_test, rand)
                && test_miller_rabin(integer.multiply(BigInteger.TWO).add(BigInteger.ONE), nmb_test, rand)) {
            integer = new BigInteger(NMB_BITS, rand);
        }

        return integer;
    }

    private static boolean test_miller_rabin(BigInteger big, int nmbTest, SecureRandom random) {
        /* Calcul s et d */
        BigInteger subBigInteger = big.subtract(BigInteger.ONE);
        BigInteger d = subBigInteger;
        int s = 1;
        while (d.mod(BigInteger.TWO).equals(BigInteger.ZERO)) {
            d = d.divide(BigInteger.TWO);
            s++;
        }
        BigInteger x;
        for (int i = 0; i < nmbTest; i++) {
            BigInteger a = new BigInteger(NMB_BITS, random);
            x = a.modPow(d, big);
            if (x.compareTo(BigInteger.ONE) == 0 || x.compareTo(subBigInteger) == 0) {
                return false;
            }
            for (int j = 0; j < s; j++) {
                x = x.modPow(BigInteger.TWO, big);
                if (x.compareTo(subBigInteger) == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            String str = "JE suis un message crypte"+i;
            Elgamal elgamal = new Elgamal(32);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream oos;
            try {
                oos = new ObjectOutputStream(byteArrayOutputStream);
                oos.writeObject(str);
                oos.flush();
                oos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            CryptedMessage cm = elgamal.encrypt(str);
            elgamal.listPublicKeys.add(elgamal.publicKey);
            String messageDecrypte = (String) elgamal.decrypt(cm, 0);
            System.out.println(messageDecrypte);
        }
    }
}
