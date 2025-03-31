package src.main.java.internet;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.*;
import javax.swing.event.*;

import src.main.java.model.Plateau;
import src.main.java.view.EcranPresentation;
import src.main.java.view.MainFrame;
import src.main.java.view.ModeLoader;

public class PanelConnectionServeur extends JPanel implements DocumentListener {
    private JTextField textPort;
    private JLabel labelPort;
    private JButton buttonStart;
    private String port;
    private JLabel labelNmbJoueur;
    private JTextField textnmbJoueur;
    private String nmbJoueur;
    private JButton retour;
    private MainFrame mainFrame;
    private Image img;
    private int indFonctionFin = 10;//Je recupere le numero de la fonction fin pris par le joueur
    public PanelConnectionServeur(EcranPresentation ep) {

        retour = new JButton("retour");
        labelPort = new JLabel();
        labelPort.setText("Taper le port que vous voulez");
        labelPort.setFont(new Font("Arial",Font.PLAIN, 16));
        textPort = new JTextField();
        textPort.getDocument().addDocumentListener(this);
        textPort.setFont(new Font("Arial",Font.PLAIN, 16));
        textnmbJoueur = new JTextField();
        textnmbJoueur.getDocument().addDocumentListener(this);
        textnmbJoueur.setFont(new Font("Arial",Font.PLAIN, 16));
        labelNmbJoueur = new JLabel();
        labelNmbJoueur.setText("Choissisez le bon nombre de joueur");
        labelNmbJoueur.setFont(new Font("Arial",Font.PLAIN, 16));
        buttonStart = new JButton("Ok");
        buttonStart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae){
                try{
                    int nmbJ= Integer.parseInt(nmbJoueur);
                    ModeLoader.setOnline(true);
                    if(nmbJ>5 || nmbJ<=0){throw new InternalError();}
                    Plateau plateau = Plateau.plateauFichier(new File("src/main/resources/plateau.txt"),nmbJ);
                    Serveur serveur = new Serveur(plateau,indFonctionFin,Integer.parseInt(port));
                    FrameIpServeur frame = new FrameIpServeur(serveur);
                    frame.repaint();
                    CountDownLatch cdl =new CountDownLatch(1);
                    javax.swing.SwingUtilities.invokeLater(()->{
                        Thread thread = new Thread(()->{
                            try {
                                ep.dispose();
                                serveur.init(frame);
                                cdl.countDown();
                                frame.dispose();
                                mainFrame.setVisible(true);
                            } catch (IOException e) {
                                JOptionPane.showMessageDialog(frame,"Erreur au moment de creer le serveur verifiez votre connection retour a l'ecran de presentation");
                                frame.dispose();
                                ModeLoader.setOnline(false);
                                new EcranPresentation();//On relance un ecran de presentation
                            }
                        });
                           thread.start();     
                    
                    });
                    javax.swing.SwingUtilities.invokeLater(() ->{mainFrame = new MainFrame(plateau, serveur);mainFrame.setVisible(false);});
                    javax.swing.SwingUtilities.invokeLater(()->{Thread thread = new Thread(() ->{ try {
                        cdl.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    serveur.run();
                });thread.start();}
                );
                    }catch(InternalError ie){
                        ModeLoader.setOnline(false);
                        JOptionPane.showMessageDialog(PanelConnectionServeur.this, "Erreur le nombre de joueur ne peut etre compris que entre 0 et 5");
                    }catch(Exception e){
                        //e.printStackTrace();
                        ModeLoader.setOnline(false);
                        JOptionPane.showMessageDialog(PanelConnectionServeur.this, "Erreur attention a bien tapez des entier dans les champs de texte");
            }
            }});
        buttonStart.setOpaque(true);
        retour.addActionListener(ae -> ep.showPresentation());
        JPanel panelGrid = new JPanel(new GridLayout(3,2));
        panelGrid.add(labelPort);
        panelGrid.add(textPort);
        panelGrid.add(labelNmbJoueur);
        panelGrid.add(textnmbJoueur);
        panelGrid.add(buttonStart);
        panelGrid.add(retour);
        //panelGrid.setOpaque(false);
        panelGrid.setPreferredSize(new Dimension(1000, 400));
        JPanel panelCentreY= new JPanel(new GridLayout(2,1));
        panelCentreY.setOpaque(false);
        panelCentreY.add(Box.createHorizontalStrut(150));
        panelCentreY.add(panelGrid);
        JPanel panelCentre = new JPanel(new BorderLayout());
        panelCentre.add(panelCentreY,BorderLayout.CENTER);
        panelCentre.setOpaque(false);
        add(panelCentre);
        try{
            img=javax.imageio.ImageIO.read(new File("src/main/resources/images/fondFarWest.jpg"));
        }catch(IOException ioe){

        }
    }

    private void updatePort() {
        this.port = textPort.getText().trim();
        this.nmbJoueur = textnmbJoueur.getText().trim();
    }

    @Override
    public void changedUpdate(DocumentEvent arg0) {
        updatePort();
    }

    @Override
    public void insertUpdate(DocumentEvent arg0) {
        updatePort();
    }

    @Override
    public void removeUpdate(DocumentEvent arg0) {
        updatePort();
    }

    @Override
    public void paintComponent(Graphics g){
        if(img!=null){
            g.drawImage(img, 0,0,getWidth(), getHeight(), null);
        }
    }

}
