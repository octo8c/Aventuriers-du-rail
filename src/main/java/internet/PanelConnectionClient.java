package src.main.java.internet;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.*;
import javax.swing.*;
import javax.swing.event.*;

import src.main.java.view.EcranPresentation;
import src.main.java.view.ModeLoader;

public class PanelConnectionClient extends JPanel implements DocumentListener {
    private JLabel labelInfo;
    private JLabel labelPort;
    private JLabel labelIp;
    private JLabel labelPseudo;
    private JTextField textIp;
    private JTextField textPseudo;
    private JTextField textPort;
    private JButton retour;
    private JButton boutonFin;
    private String pseudo;
    private String ip;
    private String port;
    private Image image;
    public PanelConnectionClient(EcranPresentation ecran) {
        this.labelInfo = new JLabel();
        labelInfo.setText("Tapez toutes les info puis clique sur Ok pour tenter la connection");
        this.labelPort = new JLabel("Port");
        this.labelPort.setText("Tapez le port du serveur que vous voulez rejoindre");
        this.labelPort.setFont(new Font("Arial",Font.PLAIN,16));

        this.labelIp = new JLabel();
        this.labelIp.setFont(new Font("Arial",Font.PLAIN,16));
        this.labelIp.setText("Tapez l'ip du serveur que vous voulez rejoindre/(localhost si vous voulez jouez sur le meme pc)");

        this.labelPseudo = new JLabel();
        this.labelPseudo.setText("Tapez le pseudo que vous voulez avoir ");
        this.labelPseudo.setFont(new Font("Arial",Font.PLAIN,16));

        this.retour = new JButton("Retour");
        this.retour.setFont(new Font("Arial",Font.PLAIN,16));
        //this.retour.setOpaque(false);
        this.retour.setContentAreaFilled(false);
        this.retour.addActionListener(ae -> ecran.showPresentation());

        this.textIp = new JTextField();
        this.textIp.setFont(new Font("Arial",Font.PLAIN,16));
        //this.textIp.setOpaque(false);
        this.textPseudo = new JTextField();
        //this.textPseudo.setOpaque(false);
        this.textPseudo.setFont(new Font("Arial",Font.PLAIN,16));
        this.textPort = new JTextField();
        //this.textPort.setOpaque(false);
        this.textPort.setFont(new Font("Arial",Font.PLAIN,16));

        textIp.getDocument().addDocumentListener(this);
        textPort.getDocument().addDocumentListener(this);
        textPseudo.getDocument().addDocumentListener(this);

        this.boutonFin = new JButton("Jouer");
        this.boutonFin.setContentAreaFilled(false);
        this.boutonFin.setFont(new Font("Arial",Font.PLAIN,16));
        this.boutonFin.setOpaque(false);
        this.boutonFin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                ModeLoader.setOnline(true);
                try{
                    Client client = new Client(ip ,Integer.parseInt(port)); 
                    // System.out.println("Client bien crée");
                    client.setPseudo(pseudo);
                    ModeLoader.setClientJoueur(client);
                    CountDownLatch cdl= new CountDownLatch(1);
                    ecran.dispose();
                    javax.swing.SwingUtilities.invokeLater(()->{client.init();cdl.countDown();System.out.println("Le countdown a été effectué");});
                    javax.swing.SwingUtilities.invokeLater(()->client.initGui());
                    javax.swing.SwingUtilities.invokeLater(()->{Thread thread = new Thread(()->{
                        // System.out.println("Ouuui le vrai thread est bien lancé");
                        try {
                            cdl.await();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        // System.out.println("Le package est lancé");
                        client.run();
                    });thread.start();});
                }catch(IOException ioe){
                    ioe.printStackTrace();
                    String erreurMessage="Il y a une erreur pendant la connection verifiez votre connection l'adresse ip, le port et si il n'y a pas de probleme de firewall";
                    JOptionPane.showMessageDialog(PanelConnectionClient.this,erreurMessage ,"Probleme connexion",JOptionPane.ERROR_MESSAGE);
                    ModeLoader.setOnline(false);
                    ModeLoader.setClientJoueur(null);
                }
            }
        });
        this.pseudo = "";
        this.ip = "";
        this.port = "";
        try {
            this.image = javax.imageio.ImageIO.read(new File("src/main/resources/images/saloon.jpg"));
        } catch (IOException e) {
            image = null;
        }
        JPanel panelGrid=new JPanel(new GridBagLayout());
        panelGrid.setOpaque(false);
        JPanel panelPort=new JPanel(new GridLayout(1,2));
        //panelPort.setOpaque(false);
        panelPort.setPreferredSize(new Dimension(200, 100));
        panelPort.add(labelPort);
        panelPort.add(textPort);

        JPanel panelIp=new JPanel(new GridLayout(1,2));
        //panelIp.setOpaque(false);
        panelIp.add(labelIp);
        panelIp.add(textIp);

        JPanel panelPseudo = new JPanel(new GridLayout(1,2));
        //panelPseudo.setOpaque(false);
        panelPseudo.add(labelPseudo);
        panelPseudo.add(textPseudo);

        JPanel panelBouton = new JPanel(new GridLayout(1,2));
        //panelBouton.setOpaque(false);
        panelBouton.add(retour);
        panelBouton.add(boutonFin);
        
        JPanel panelLayout=new JPanel(new GridLayout(5,1));
        panelLayout.setOpaque(false);
        panelLayout.add(Box.createVerticalStrut(150));
        panelLayout.add(panelPort);
        panelLayout.add(panelIp);
        panelLayout.add(panelPseudo);
        panelLayout.add(panelBouton);
        
        panelGrid.add(panelLayout);
        JPanel panelCentre=new JPanel(new BorderLayout());
        panelCentre.setOpaque(false);
        panelCentre.add(panelGrid,BorderLayout.CENTER);
        add(panelCentre);
    }

    private void updatePseudoIp() {
        this.ip = textIp.getText().trim();
        this.pseudo = textPseudo.getText().trim();
        this.port = textPort.getText().trim();
    }

    @Override
    public void changedUpdate(DocumentEvent arg0) {
        updatePseudoIp();
    }

    @Override
    public void insertUpdate(DocumentEvent arg0) {
        updatePseudoIp();
    }

    @Override
    public void removeUpdate(DocumentEvent arg0) {
        updatePseudoIp();
    }
    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        if(image !=null){
            g.drawImage(image,0, 0,getWidth(),getHeight(),null);
        }
    }
}