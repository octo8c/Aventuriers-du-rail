package src.main.java.model;

import java.awt.image.BufferedImage;

import src.main.java.view.ViewLoader;

public class CarteWagon implements Carte{
    private Couleur couleur;

    public CarteWagon(Couleur couleur){
        this.couleur=couleur;
    }

    public Couleur getCouleur(){
        return this.couleur;
    }
    @Override
    public boolean estWagon(){
        return true;
    }
    public BufferedImage getImage(){
        return ViewLoader.getImage(this);
    }

}