package src.main.java.internet;

import java.io.*;
import java.net.*;

import src.main.java.chiffrement.CryptedMessage;
import src.main.java.chiffrement.Elgamal;
import src.main.java.chiffrement.PublicKey;
import src.main.java.controller.Joueur;
import src.main.java.model.*;
public class ConnectionHandler implements Runnable{
    private static final int NMB_SECURITY = 32;//Parametre de securite du chiifrement
    private Elgamal elgamal;
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Serveur serveur;
    private String pseudo;
    private boolean isRunning;
    private SauvegardePlateau sauvegardePlateau;
    private boolean treated;//Indique si le plateau a déja été recuperer
    public ConnectionHandler(Serveur serveur,Socket socket){
        this.serveur=serveur;
        this.socket=socket;
        this.isRunning = true;
        this.setTreated(true);
        this.elgamal = new Elgamal(NMB_SECURITY);
    }
    public void run(){
        try {
            out=new ObjectOutputStream(socket.getOutputStream());
            in=new ObjectInputStream(socket.getInputStream());
            setPseudo();
            setKey();
            while(isRunning){
                Object obj = in.readObject();
                if (obj instanceof Deconnexion){
                    throw new Deconnexion();
                }else if(obj instanceof SauvegardePlateau){
                    sauvegardePlateau = (SauvegardePlateau)obj;
                    this.setTreated(false);
                    // System.out.println("Le packet a bien été recupére");
                    //On suppose que le joueur ne vas pas hacker la partie et s'ajouter des tour
                    //Donc on prends les packages a l'infinis
                }else{
                    System.out.println("J'ai recu un objet mauvais mais je sais pas je fais quoi");
                }
                
            }
        }catch(ClassNotFoundException e){
        }catch(IOException e) {
        }catch(Deconnexion deco){
        }finally{
            disconnect();
        }
    }

    public void setKey(){
        try{
            out.writeObject(elgamal.getPublicKey());//On envoie la cle public au client
            elgamal.setKey((PublicKey)in.readObject());
            out.flush();
            
        }catch(ClassNotFoundException|IOException ioe){
            ioe.printStackTrace();
        }
    }
    public void disconnect(){
        try{
            // System.out.println("On deconnecte le joueur ");
            in.close();
            out.close();
            socket.close();
            isRunning = false;
            serveur.disconnectConnectionHandler(this);
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    public void setPseudo(){
        try{
            String message = "Bonjour nouveau joueur quelle est votre pseudo ?";
            this.out.writeObject(elgamal.encrypt(message));
            this.pseudo = ((String)elgamal.decrypt((CryptedMessage)in.readObject(),0));
        }catch(ClassNotFoundException|IOException ioe){
            ioe.printStackTrace();
            System.out.println("Probleme de lecture de pseudo ");
        }
    }
    public void initComm(Plateau plateauServeur,FonctionJeu fonctionJeu) {
        try{
            SauvegardePlateau sauvegardePlateau = new SauvegardePlateau(plateauServeur, System.currentTimeMillis(), 0,0,fonctionJeu);
            out.writeObject(elgamal.encrypt(sauvegardePlateau));
            out.flush();
        }catch(IOException ioe){
            ioe.printStackTrace();
        }
    }
    /**
     * Envoie au client le nouveau plateau , si ces a son tour de jouer et son joueur 
     * @param obj sauvegarde du plateau 
     * @param play True si ces au tour du joueur de jouer false sinon
     * @param joueur son joueur actuel
     */
    public void update(SauvegardePlateau obj,Boolean play,Joueur joueur){
        try{
            this.out.writeObject(elgamal.encrypt(obj));//On envoie le nouveau plateau au joueur
            this.out.writeObject(elgamal.encrypt(play));//Si le joueur a le droit de jouer ou pas
            this.out.writeObject(elgamal.encrypt(isRunning));//Signifie que le client doit continuer de recevoir des informations
            this.out.writeObject(elgamal.encrypt(joueur));//Envoie le joueur qui le represente au client
            // System.out.println("Oui j'ai bien envoyé toutes les info");
            this.out.flush();
        }catch(IOException ioe){
            ioe.printStackTrace();
        }
    }
    /**
     * @return Renvoie le nouveau plateau ou le plateau actuel si le connectionHandler ces deconnecte ou que le thread a eu une erreur
     * @throws IOException peut throws une exception si il y a une erreur
     */
    public SauvegardePlateau getUpdatedPlateau() throws IOException {
        if(isRunning && isConnected()){
            if(!getTreated()){
                // System.out.println("Le plateau serveur n'était pas traité");
                this.setTreated(true);//Le plateau a bien été traité
                return sauvegardePlateau;
            }else{
                // System.out.println("Le plateau serveur était traité on att le prochain");
                while(getTreated()&&isRunning){
                    try{
                        Thread.sleep(2000);
                    }catch(InterruptedException ie){
                        ie.printStackTrace();
                        System.out.println("Interuption de l'attente");
                        setTreated(true);
                    }
                }
                setTreated(true);
                //On att que le plateau ait été mis a jour
                return sauvegardePlateau;
            }
        }
        return null;
    }
    public boolean isConnected(){
        return this.socket.isConnected();
    }
    /**
     * @return Renvoie le pseudo seulement quand la communication sur le pseudo a finis
     */
    public String getPseudo() {
        while(this.pseudo == null){
            Thread.onSpinWait();
        }
        return this.pseudo;
    }
    public void setIsRunning(boolean b) {
        this.isRunning = b;
    }
    public void setTreated(boolean bool){
        this.treated = bool;
    }
    public boolean getTreated(){
        return this.treated;
    }
    public boolean isRunning() {
        return this.isRunning;
    }
}