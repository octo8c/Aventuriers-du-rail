package src.main.java.model;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.TimeUnit;
import javax.swing.JLabel;
import src.main.java.controller.Joueur;
import src.main.java.view.FrameClassement;
import src.main.java.view.MainFrame;

    /**
     * Constructeur de la classe Jeu.
     *
     * @param p  Le plateau de jeu.
     * @param mf La fenêtre principale du jeu.
     * @param fj La fonction de jeu à utiliser.
     */
public class Jeu implements Runnable ,Serializable{
    private Plateau p;
    private final Thread t;
    private final MainFrame mainFrame;
    private int nmbTour;
    private long timePaused;
    private long startTime;
    private long timeElapsed;
    private FonctionJeu fonctionJeu;
    private boolean gameFinished;
    private boolean sleeping;
    private boolean threadStop;

    public Jeu(Plateau p,MainFrame mf,FonctionJeu fj){
        this.p=p;
        this.mainFrame=mf;
        this.nmbTour=0;
        this.t=new Thread(this);
        t.start();
        this.startTime=System.currentTimeMillis();
        this.fonctionJeu=fj;
        sleeping = false;
        timePaused = 0;
        threadStop = false;
    }

    /**
     * Retourne le nombre de tours joués.
     *
     * @return Le nombre de tours joués.
     */
    public int getNmbTour(){
        return this.nmbTour;
    }

    /**
     * Retourne le temps de début du jeu.
     *
     * @return Le temps de début du jeu en millisecondes.
     */
    public long getStartTime(){
        return this.startTime;
    }

    /**
     * Méthode exécutée par le thread.
     */
    @Override
    public void run() {
        Joueur firstJoueur=p.getCurrentJoueur();
        try {
            sleeping=true;
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        sleeping=false;
        while(fonctionJeu.fin(this, p)==null){
            if(firstJoueur==p.getCurrentJoueur()&&p.getTour()){
                nmbTour++;
                mainFrame.getJLabelTour().setText("NOMBRE DE TOUR : "+nmbTour);
                p.setTour();
            }
            if(firstJoueur!=p.getCurrentJoueur()){
                p.setTour();
            }
            try {
                sleeping = true;
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Attention Erreur");
                sleeping = false;
            }
            if(this.threadStop){//On arrete la fonction si jms l'appel est effectué
                return;
            }
            sleeping = false;
            mainFrame.repaint();
            timeElapsed=System.currentTimeMillis()-(startTime-timePaused);
            mainFrame.getJLabelTime().setText("Timer : "+TimeUnit.MILLISECONDS.toMinutes(timeElapsed)%60+":"+TimeUnit.MILLISECONDS.toSeconds(timeElapsed)%60);
        }
        gameFinished=true;//Indique que la partie vas finir
        int nmbNextJoueur=p.getNmbJoueur();
        // On fait un tour de boucle supplementaire pour que les autres joueurs finissent de jouer
        // La valeur de current joueur sera mis a jour a force d'interagir avec le plateau
        while(p.getNmbNextJoueur()-nmbNextJoueur<p.getNmbJoueur()){
            try {
                sleeping = true;
                Thread.sleep(2000);
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Attention Erreur");
                sleeping = false;
            }
            sleeping = false;
            long timeElapsed=System.currentTimeMillis()-startTime;
            mainFrame.getJLabelTime().setText("Timer : "+TimeUnit.MILLISECONDS.toMinutes(timeElapsed)%60+":"+TimeUnit.MILLISECONDS.toSeconds(timeElapsed)%60);
        }
        //Pour desactiver la pioche
        this.mainFrame.getPanelJeu().setPiocheOff();
        //Pour activer le mode selection ligne
        this.mainFrame.getPanelJeu().setSelectionLigneGare();
        Iterator<Ville>itVille=p.iteratorVille();
        while(itVille.hasNext()){
            Ville v=itVille.next();
            if(v.hasGare()){
                this.p.setCurrentJoueur(v.getJoueur());
                JLabel labelJoueur=new JLabel("Ces au tour de "+p.getCurrentJoueur()+"pour selectionner une ligne qu'il aimerais prendre depuis ca gare");
                this.mainFrame.getPanelJeu().add(labelJoueur);
                while(v.getLigneGare()==null);
                this.mainFrame.getPanelJeu().remove(labelJoueur);
            }
        }
        try{
            Thread.sleep(2000);
        }catch(InterruptedException ie){
            ie.printStackTrace();
        }
        new FrameClassement(mainFrame, p);
        System.out.println("Fin du jeu");

    }
    public void setFonctionJeu(FonctionJeu fj){
        if(sleeping){
            this.fonctionJeu = fj;
        }else{
            //On att que le thread soit en pause pour eviter les erreurs
            while(!sleeping){
                System.out.println("On att encore le debut du sleep");
            }
            System.out.println("Ces bon il dors");
            this.fonctionJeu=fj;
        }
    }

    public boolean IsFinished(){
        return this.gameFinished;
    }

     /**
     * Met à jour la pioche du jeu.
     * Met à jour les labels des cartes dans la fenêtre principale.
     */
    public void updatePioche(){
        mainFrame.getPanelPioche().updateCarteLabels();
        mainFrame.getPanelPioche().UpdatePioche(p, p.getCurrentJoueur());
    }

    /**
     * Définit le plateau de jeu.
     * Si le thread est en pause, le plateau est directement affecté.
     * Sinon, attend que le thread soit en pause pour éviter les erreurs avant d'affecter le plateau.
     *
     * @param plateau Le plateau de jeu à définir.
     */
    public void setPlateau(Plateau plateau) {
        if(sleeping){
            this.p= plateau;
        }else{
            while(!sleeping){
                System.out.println("On att encore le debut du sleep");
            }
            System.out.println("Ces bon il dors");
            this.p=plateau;
        }
        
    }
    public long getTimeElapsed(){
        return this.timeElapsed;
    }
    public void setTimePaused(long time){
        this.timePaused=time;
    }
    public void setNmbTour(int tour){
        this.nmbTour=tour;
    }

    /**
     * Fonction permettant d'arreter le thread de jeu
     */
    public void stopThread(){
        threadStop = true;
    }
    public Plateau getPlateau(){
        return this.p;
    }
    public FonctionJeu getFonctionJeu(){
        return this.fonctionJeu;
    }

    public void ResetTimer() {
        this.startTime=System.currentTimeMillis();
    }
}
