package src.main.java.internet;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import javax.swing.JFrame;

import src.main.java.model.FonctionJeu;
import src.main.java.model.Jeu;
import src.main.java.model.Plateau;
import src.main.java.model.SauvegardePlateau;
import src.main.java.view.MainFrame;
import src.main.java.view.ModeLoader;
public class Serveur implements Runnable{
    private ArrayList<ConnectionHandler>listConnectionHandlers;
    private Plateau plateauServeur;
    private FonctionJeu fonctionJeu;
    private boolean isRunning;
    private MainFrame mf;
    private Jeu jeu;
    private final int port;
    private boolean init;
    private final int nmbJoueur;//Le nombre de joueur auquel on doit attendre qu'il se connecte
    private int idCLient;//Quelle client peut jouer
    private boolean isPlaying;
    private boolean parcouring;
    public Serveur(Plateau plateau,int indFonctionFin,int port){
        this.port=port;
        this.listConnectionHandlers=new ArrayList<ConnectionHandler>();
        this.plateauServeur=plateau;
        this.fonctionJeu=ModeLoader.getFonctionFin(indFonctionFin);
        this.idCLient=0;
        this.isRunning = true;
        this.nmbJoueur = plateau.getNmbJoueur();
        this.init=true;
        this.isPlaying = true;
        this.parcouring=false;
    }
    public void run(){
        while(init){
            try{
                Thread.sleep(2000);
            }catch(InterruptedException ie){
                ie.printStackTrace();
            }
        }
        while(isPlaying&&isRunning){
            updateAll();
            // System.out.println("Envoie du plateau a tout les joueurs");
            updatePlateauServeur();
            // System.out.println("La mise a jour du plateau c'est bien passe");
            updatePlaying();
            updateRunning();
            nextCLient();//Pour qu'au prochain tour ca soit au prochain client de jouer
            timeOut();
            // System.out.println("Nombre de joueur "+listConnectionHandlers.size());
        }
        if(!isRunning&&listConnectionHandlers.size()>0){//Pour indiquer a tout les client que la partie s'arrete 
            updateAll();
        }
        int nmbAction=0;
        //Phase de la selection des lignes pour les gares
        while(nmbAction<listConnectionHandlers.size()&&isRunning){
            updateAll();
            updatePlateauServeur();
            nmbAction++;
        }
        disconnect();
    }
    private void timeOut(){
        parcouring=false;
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {

        }
    }
    /**
     * Initialise le pseudo de tout les joueur dans le plateau avec le pseudo de leur client respectif
     */
    private void initPseudo() {
        for(int i=0;i<listConnectionHandlers.size();i++){
            plateauServeur.setPseudo(i, listConnectionHandlers.get(i).getPseudo());
        }       
    }
    /**
     * Envoie tout les information de base a chaque client
     */
    private void initComm(){
        parcouring = true;
        for(ConnectionHandler connectionHandler:listConnectionHandlers){
            connectionHandler.initComm(plateauServeur,fonctionJeu);
        }
        parcouring = false;
    }
    public void setJouer(){
        plateauServeur.getIndJoueur();
    }
    public void updatePlaying(){
        if(jeu.IsFinished()){
            this.isPlaying = false;
        }
    }
    public void updateRunning(){
        if(listConnectionHandlers.size()==0){
            this.isRunning=false;
        }
    }
    /**
     * Mets a jour le serveur en recuperant toutes les info du joueur
     */
    public void updatePlateauServeur(){
        try{
            SauvegardePlateau upPlateau = listConnectionHandlers.get(idCLient).getUpdatedPlateau();//Nouveau plateau envoye par le joueur apres avoir finis de jouer
            // System.out.println(upPlateau);
            if(upPlateau !=null){
                // System.out.println("On mets a jour le plateau ");
                //On mets a jour a le plateau partout pour le serveur
                this.plateauServeur=upPlateau.getPlateau();
                // System.out.println("Le serveur a bien modifié");
                this.jeu.setPlateau(plateauServeur);
                // System.out.println("Le jeu a bien été modifié");
                this.mf.setPlateau(plateauServeur);
                // System.out.println("La mainFrame a bien modifié");
                //On suppose qu'on garde le temps du serveur
                plateauServeur.nextJoueur();//On mets a jour sur le plateau le tour a chaque fois 
                this.mf.repaint();
            }else{
                //Pour l'instant on considere qu'en cas d'erreur le coup du joueur n'a pas compte
                System.err.println("coup du joueur "+idCLient+"pas pris en compte car erreur lors de la communication");
            }
        }catch(IOException ioe){
            //Erreur de deconnection assurée
            System.out.println("Erreur deconnexion ");
            listConnectionHandlers.remove(idCLient);//On retire le joueur qui deviat jouer
        }
    }
    private void updateAll(){
        SauvegardePlateau save = new SauvegardePlateau(plateauServeur,System.currentTimeMillis(),jeu.getTimeElapsed(),jeu.getNmbTour(), fonctionJeu);
        parcouring = true;
        for(int i=0;i<listConnectionHandlers.size();i++){
            // System.out.println("On update le client "+i+""+(idCLient == i));
            if(listConnectionHandlers.get(i).isRunning()){
                listConnectionHandlers.get(i).update(save,idCLient==i,plateauServeur.getJoueur(idCLient));
            }
        }
        parcouring = false;
        // System.out.println("Fin de mise a jour");
    }
    private void nextCLient(){
        idCLient = (idCLient+1)%listConnectionHandlers.size();
    }
    public void disconnectConnectionHandler(ConnectionHandler ch){//Retire le connectionHandler de la table des connection
        while(!parcouring){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                parcouring=false;
            }
        }
        this.listConnectionHandlers.remove(ch);
        // System.out.println("Le client a bien été deconnecté");
    }
    /**
     * Deconnecte out les joueurs de la partie
     */
    public void disconnect(){
        parcouring = true;
        for(ConnectionHandler ch:listConnectionHandlers){
            if(ch != null){
                ch.disconnect();
            }
        }
        parcouring = false;
    }
    public void setJeu(Jeu jeu2) {
        this.jeu = jeu2;
    }
    public Plateau getPlateau() {
        return this.plateauServeur;
    }
    public void setMainFrame(MainFrame mainFrame) {
       this.mf= mainFrame;
    }
    public void init(JFrame frame) throws IOException {
        // System.out.println("Le serveur c'est bien lancé");
        ServerSocket serverSocket = new ServerSocket(port);
        while(listConnectionHandlers.size()<nmbJoueur){//On ajoute tout les joueurs au serveur 
                Socket socket = serverSocket.accept();
                ConnectionHandler connectionHandler = new ConnectionHandler(this,socket);
                Thread thread = new Thread(connectionHandler);
                thread.start();
                listConnectionHandlers.add(connectionHandler);
                // System.out.println("Oui la connection c'est bien passe");
                frame.repaint();
        }
        initPseudo();
        initComm();
        serverSocket.close();
        // System.out.println("Toutes les communication de base on été faite on att que le jeu se lance(Serveur)");
    }
    public void setTurnOrder(int[]tabValue){
        ModeLoader.setTurnOrder(listConnectionHandlers,tabValue);
    }
    /**
     * @return Renvoie le nombre de joueur connecté actuel
     */
    public int getNmbConnected(){
        return this.listConnectionHandlers.size();
    }
    public String getPort() {
        return String.valueOf(port);
    }
    public int getNmbJoueur(){
        return this.nmbJoueur;
    }
    /**
     * desactive l'initialisation
     */
    public void setInit(){
        this.init=false;
    }
    public void stopRunning() {
        this.isRunning=false;
    }
    /**
     * @return Renvoie true si le serveur est entrain de parcourir la liste des connection handler
     */
    public boolean getParcouring(){
        return this.parcouring;
    }
}