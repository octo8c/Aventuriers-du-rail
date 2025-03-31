package src.main.java.view;

import java.awt.CardLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.*;

import src.main.java.internet.Serveur;
import src.main.java.model.Plateau;
import src.main.java.model.SauvegardePlateau;

public class MainFrame extends JFrame implements WindowListener{
    private JPanel mainPanel;
    private Plateau plateau;
    private PanelSelection panelSelec;
    private PanelJeu panelJeu;
    private PanelInventaire panelInv;
    private PanelPioche panelPioche;
    private Serveur serveur;
    private final CardLayout cardLayout;

    public MainFrame(Plateau p){
        plateau=p;
        cardLayout = new CardLayout();
        panelSelec = new PanelSelection(this);
        panelJeu = new PanelJeu(p,this);
        panelInv = new PanelInventaire(p, this);
        panelPioche = new PanelPioche(p, this);

        panelJeu.setVisible(true);
        panelInv.setVisible(false);
        panelPioche.setVisible(false);

        mainPanel=new JPanel();
        mainPanel.setLayout(cardLayout);
        mainPanel.add(panelSelec, "Selection");
        mainPanel.add(panelJeu,"Jeu");
        mainPanel.add(panelInv, "Inventaire");
        mainPanel.add(panelPioche, "Pioche");

        add(mainPanel);

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setResizable(false);
        setVisible(true);
        pack();
        mainPanel.revalidate();
        mainPanel.repaint();
        validate();
        ViewLoader.loadImage(plateau.getNmbJoueur());//On load toutes les images
        if(ModeLoader.isOnline()){
            setOnline();
        }
        this.addWindowListener(this);
    }
    public MainFrame(Plateau plateau , Serveur serveur){
        this(plateau);
        this.serveur =serveur;
        serveur.setJeu(panelJeu.getJeu());
        this.setJouer(false);
        this.panelJeu.setInventaire(false);
        this.panelJeu.setPiocheOff();
        this.setOfflineButton(false);
        serveur.setMainFrame(this);
    }

    public MainFrame(SauvegardePlateau sauvegardePlateau){
        this(sauvegardePlateau.getPlateau());
        this.panelJeu.getJeu().setTimePaused(sauvegardePlateau.getTempsPlateau());
        this.panelJeu.getJeu().setNmbTour(sauvegardePlateau.getNmbTour());
        this.panelJeu.getJeu().setFonctionJeu(sauvegardePlateau.getFonctionJeu());
    }
    
    public Plateau getPlateau() {
        return plateau;
    }

    public void showJeu() {
        cardLayout.show(mainPanel, "Jeu");
        if(serveur!=null){
            serveur.setInit();
        }
    }

    public void showInventaire() {
        cardLayout.show(mainPanel, "Inventaire");
    }

    public void showPioche() {
        cardLayout.show(mainPanel, "Pioche");
    }

    public void showSelection() {
        cardLayout.show(mainPanel, "Selection");
    }

    public JLabel getJLabelTour(){
        return panelJeu.getLabelTour();
    }
    
    public JLabel getJLabelTime(){
        return panelJeu.getLabelTime();
    }

    public PanelJeu getPanelJeu(){
        return this.panelJeu;
    }

    public PanelInventaire getPanelInventaire() {
        return this.panelInv;
    }

    public PanelSelection getPanelSelection() {
        return this.panelSelec;
    }

    public PanelPioche getPanelPioche() {
        return this.panelPioche;
    }
    
    public void revalidate(){
        getContentPane().revalidate();
    }

    public void setJouer(boolean bool){
        if(bool){
            this.panelJeu.play();
        }else{
            this.panelJeu.noPlay();
        }
    }
    public void setOfflineButton(boolean bool){
        this.panelJeu.disableOfflineButton(bool);
    }

    public void setPlateau(Plateau plateau2) {
        this.plateau=plateau2;
        this.panelJeu.setPlateau(plateau2);
        this.panelPioche.setPlateau(plateau2);
        this.panelJeu.getJeu().setPlateau(plateau2);
    }
    public void setOnline(){
        this.setJouer(false);
        this.setOfflineButton(false);
        this.panelJeu.setInventaire(false);
        if(ModeLoader.getClient()!=null){
            ModeLoader.getClient().setJeu(this.panelJeu.getJeu());
            ModeLoader.getClient().setMainFrame(this);
        }
        
    }
    @Override
    public void windowActivated(WindowEvent arg0) {
    }
    @Override
    public void windowClosed(WindowEvent arg0) {
    
    }
    @Override
    public void windowClosing(WindowEvent arg0) {
        if(ModeLoader.isOnline()){
            if(ModeLoader.getClient()!=null){
                ModeLoader.getClient().sendQuitting();
                ModeLoader.getClient().disconnect();
            }else {
                //si ces le serveur
                getServeur().stopRunning();
                getServeur().disconnect();
            }
        }
        panelJeu.getJeu().stopThread();
        dispose();
        System.exit(0);
    }
    @Override
    public void windowDeactivated(WindowEvent arg0) {
        
    }
    @Override
    public void windowDeiconified(WindowEvent arg0) {
       
    }
    @Override
    public void windowIconified(WindowEvent arg0) {
        
    }
    @Override
    public void windowOpened(WindowEvent arg0) {
        
    }
    public Serveur getServeur(){
        return this.serveur;
    }
}
