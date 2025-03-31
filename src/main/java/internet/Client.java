package src.main.java.internet;

import src.main.java.chiffrement.*;
import src.main.java.controller.Joueur;
import src.main.java.model.*;
import src.main.java.view.MainFrame;
import src.main.java.view.ModeLoader;

import java.io.*;
import java.net.*;

import javax.swing.*;
public class Client implements Runnable {
    private static final int PARAM = 32;
    private static final int maxTry = 3;
    private Socket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    /**
     *Le joueur qui correspond au client
     */
    private Joueur joueurClient;
    private Plateau plateau;
    private MainFrame mainFrame;
    private Jeu jeu;
    private String pseudo;
    private boolean isRunning;
    private final Elgamal elgamal;
    private boolean play;// Signifie si le joueur peut envoyer des information

    public Client(String ip, int port) throws IOException {
        socket = new Socket(ip, port);
        elgamal = new Elgamal(PARAM);
        oos = new ObjectOutputStream(socket.getOutputStream());
        ois = new ObjectInputStream(socket.getInputStream());
        isRunning = true;
        play = false;
    }

    /**
     * Communication initial qui initialise le plateau la fonction de jeu et le joueur du client 
     */
    private void initComm() {
        try {
            // On suppose qu'on envoie d'abord le plateau puis la fonction jeu
            SauvegardePlateau save = (SauvegardePlateau)elgamal.decrypt((CryptedMessage)ois.readObject(),0);
            plateau = save.getPlateau();
            FonctionJeu fj = save.getFonctionJeu();
            ModeLoader.setFonctionJeu(fj);// Remplace la fonction jeu default
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initilaisation de la cle de decryptage et envoie de la cle de cryptage
     */
    private void initKey(){
        try {
            elgamal.setKey((PublicKey)ois.readObject());
            oos.writeObject(elgamal.getPublicKey());
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Initialisation de tout le client
     */
    public void init(){
        initKey();
        initPseudo();
        initComm();
    }
    public void run(){
        packageLoop();
    }
    public void initGui(){
        mainFrame = new MainFrame(plateau);
        mainFrame.showJeu();
        //On show jeu pour eviter que chaque joueur puisse prendre une fonction de jeu differentes
    }
    public void initPseudo() {
        PrintWriter pw = new PrintWriter(oos);
        String line;
        try {
            InputStreamReader isr = new InputStreamReader(ois);
            BufferedReader bufferedReader = new BufferedReader(isr);
            int nmbTry = 0;
            while (!bufferedReader.ready() && nmbTry++ < maxTry){
                Thread.onSpinWait();//Fait attendre de meilleur maniere pour ensuite le faire redemarer
            }
            line = bufferedReader.readLine();
            if (line.equals("Bonjour nouveau joueur quelle est votre pseudo ?")) {
                pw.println(pseudo);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
            System.err.println("Erreur dans le traitement du pseudo");
        }
    }

    /**
     * Mets a jour les info du client a chaque tour
     */
    public void packageLoop() {
        try {
            // System.out.println("Lancement du packageLoop");
            while (isRunning) {
                /*En attent du nouveau packet */
                SauvegardePlateau plateau = (SauvegardePlateau)elgamal.decrypt((CryptedMessage)ois.readObject(), 0);//On recupere le plateau envoyé
                boolean play = (Boolean)elgamal.decrypt((CryptedMessage)ois.readObject(), 0);
                boolean running = (Boolean)elgamal.decrypt((CryptedMessage)ois.readObject(), 0);
                joueurClient = (Joueur)elgamal.decrypt((CryptedMessage)ois.readObject(), 0);//On recupere le nouveau joueur
                treatPackage(plateau, play, running);
            }
            JOptionPane.showMessageDialog(mainFrame,"Deconnexion du client");
        } catch (IOException ioe) {
            if(isRunning){//Si running est encore vrai ces une erreur sinon ca peut etre une deconnexion
                ioe.printStackTrace();
            }
        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        } catch( Exception e){
            e.printStackTrace();
        }finally{
            disconnect();
        }
    }

    /**
     * Traite le package recuperer par la boucle et mets a jour le plateau partout
     * @param save Sauvegarde du plateau envoyé par le serveur
     * @param play2 Indique si le clients doit jouer
     * @param running Mets a jour le running
     * @throws IOError
     */
    private void treatPackage(SauvegardePlateau save, boolean play2, boolean running) throws IOError {
        this.plateau = save.getPlateau();
        this.isRunning = running;
        this.play = play2;
        // System.out.println("Le client a bien été mis a jour");
        mainFrame.setPlateau(plateau);
        mainFrame.setJouer(play2);
        mainFrame.setOfflineButton(play2);
        // System.out.println("La frame a bien été mis a jour");
        jeu.setPlateau(plateau);
        jeu.setNmbTour(save.getNmbTour());
        //On laisse le client s'occuper de son temps            
    }

    /**
     * @param plateau Envoie le plateau apres que le joueur est finis sont tour
     */
    public void sendPackage(Plateau plateau) {
        try {
            SauvegardePlateau sauvegardePlateau = new SauvegardePlateau(plateau,System.currentTimeMillis(),jeu.getTimeElapsed(), jeu.getNmbTour(),jeu.getFonctionJeu());
            oos.writeObject(elgamal.encrypt(sauvegardePlateau));
            oos.flush();
            // System.out.println("Le package est bien partie(Client)");
        } catch (IOException ioe) {
            System.err.println("Erreur lors de l'envoie du packet");
        }
    }

    public void setIsRunning(boolean b) {
        this.isRunning = b;
    }
    public void stopPlay(){
        mainFrame.setPlateau(plateau);
        mainFrame.setJouer(play);
        mainFrame.setOfflineButton(play);
    }
    
    public void disconnect() {
        try {
            if(isRunning==true||!socket.isClosed()){
                isRunning = false;
                oos.close();
                ois.close();
                socket.close();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }
    public Joueur getJoueur(){
        return this.joueurClient;
    }

    public void setJeu(Jeu jeu2) {
        this.jeu = jeu2;
    }
    public void setMainFrame(MainFrame mf){
        this.mainFrame = mf;
    }

    /**
     * Envoie au serveur quand le joueur quitte la partie
     */
    public void sendQuitting() {
        try{
            oos.writeObject(elgamal.encrypt(new Deconnexion("Quitte la partie")));
            oos.flush();
        }catch(IOException ioe){
            //On force la deconnexion
        }
    }
}
