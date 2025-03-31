package src.main.java.internet;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.*;


public class FrameIpServeur extends JFrame {
    private PanelIpServeur panelIpServeur;
    public FrameIpServeur(Serveur serveur){
        panelIpServeur = new PanelIpServeur(serveur);
        panelIpServeur.serveur = serveur;
        add(panelIpServeur);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        pack();
    }
    private static class PanelIpServeur extends JPanel{
    private JLabel labelIp;
    private JLabel labelNmbJoueur;
    private JLabel labelPort;
    private String attente;
    private Serveur serveur;
    public PanelIpServeur(Serveur serveur){
        this.serveur = serveur;
        this.attente="";
        init();
    }
    public void init(){
        JPanel panel = new JPanel(new BorderLayout());
        JPanel panelLabel = new JPanel(new GridLayout(4, 1));
        JPanel panelLabelPort= new JPanel(new FlowLayout(FlowLayout.CENTER));
        JPanel panelLabelIp= new JPanel(new FlowLayout(FlowLayout.CENTER));
        JPanel panelLabelNmbJoueur= new JPanel(new FlowLayout(FlowLayout.CENTER));
        JPanel panelCenter = new JPanel(new GridBagLayout());

        this.labelPort = new JLabel("Le port du serveur est "+serveur.getPort());
        this.labelIp = new JLabel(ipString());
        this.labelNmbJoueur = new JLabel("Il y a actuellement "+serveur.getNmbConnected()+"Joueur connectes");

        panelLabelIp.add(labelIp);
        panelLabelNmbJoueur.add(labelNmbJoueur);
        panelLabelPort.add(labelPort);
        panelLabel.add(Box.createVerticalStrut(125));
        panelLabel.add(panelLabelPort);
        panelLabel.add(panelLabelIp);
        panelLabel.add(panelLabelNmbJoueur);
        panelCenter.add(panelLabel);
        panel.add(panelCenter,BorderLayout.CENTER);
        this.add(panel);
    }
    private String ipString(){
        try{
            URI uri = new URI("http://checkip.amazonaws.com/");
            URL url = uri.toURL();//Renvoie l'adresse ip en ligne (normalement )
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
            String ipString = "";
            while(!bufferedReader.ready()){
                ipString = "";//On prends un peu de ressource histoire de laisser le temps au processseur d'ouvrir le buffreredReader
            }
            ipString = bufferedReader.readLine().trim();
            return ipString;
        }catch(MalformedURLException e){
            e.printStackTrace();
            //L'url ne marche pas
            return "L'URL ne marche pas :(";
        }catch(IOException ioe){
            //La connection Internet ne marche pas
            ioe.printStackTrace();
            return "Visiblement votre connection ne marche pas ou alors ces le reader qui ne marche pas";
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return "L'adresse est mal tape";
        }
    }
    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        if(attente.length()==4){
            attente = ".";
        }else{
            attente=attente+".";
        }
        if(serveur!=null){
            labelNmbJoueur.setText("Il y a actuellement "+serveur.getNmbConnected()+" Joueur connectes "+attente);
            labelPort.setText("Le port du serveur (Au cas ou vous avez oublié : "+serveur.getPort());
        }
    }
    }
}
