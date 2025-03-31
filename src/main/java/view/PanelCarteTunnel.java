package src.main.java.view;
import java.awt.*;
import java.util.ArrayList;

import javax.swing.*;
import src.main.java.model.*;
import src.main.java.model.InterPlateau;
public class PanelCarteTunnel extends JPanel {
    private final static int NB_CARTE=3;
    private final InterPlateau inter;
    private final ArrayList<CarteWagon>listCarteWagon;
    private final JLabel labelReussite;
    private boolean hasEnoughCarte=true;
    public PanelCarteTunnel(InterPlateau interPlateau,Ligne ligne){
        setLayout(new BorderLayout());
        this.inter=interPlateau;
        this.listCarteWagon=new ArrayList<CarteWagon>();
        for (int i=0;i<NB_CARTE;i++){
            listCarteWagon.add(interPlateau.piocheWagon());
        }
        for(int i=0;i<NB_CARTE;i++){
            hasEnoughCarte&=inter.getCurrentJoueur().hasCarte(listCarteWagon.get(i).getCouleur(), 1);
        }
        this.labelReussite=new JLabel();
        if(hasEnoughCarte){
            labelReussite.setText("Vous avez assez de carte pour acheter le tunnel !!!");
            inter.achatLigne(interPlateau.getCurrentJoueur(), ligne);
        }else{
            labelReussite.setText("Vous n'avez pas assez de carte pour acheter le tunnel :( (vous passez votre tour) ");
        }
        add(labelReussite,BorderLayout.SOUTH);
    }
    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        for(int i=0;i<NB_CARTE;i++){
            g.drawImage(listCarteWagon.get(i).getImage(),i*200,(i),150,150,null);
        }
    }
}