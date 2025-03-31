package src.main.java.chiffrement;

import java.math.BigInteger;

public record PublicKey(BigInteger p,BigInteger generateur,BigInteger A) {
    public String toString(){
        return "[ Cle : "+p.toString()+" ,Generateur : "+generateur+" ,A :"+A.toString()+"]";
    }
}
