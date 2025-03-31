package src.main.java.model;

import java.awt.image.BufferedImage;

import src.main.java.view.ViewLoader;

public class CarteObjectif implements Carte{
    private Ville ville1;
    private Ville ville2;
    private boolean success;
    private int point;

    public CarteObjectif(Ville ville1, Ville ville2, Boolean success,int point){
        this.ville1=ville1;
        this.ville2=ville2;
        this.success=success;
        this.point=point;
    }

    public Ville getVille1(){
        return this.ville1;
    }

    public Ville getVille2(){
        return this.ville2;
    }

    public int getPoint(){
        return this.point;
    }

    /**
     * @return Renvoie true si la carte 
     */
    public Boolean getSuccess(){
        return this.success;
    }

    public void setSuccess(Boolean success){
        this.success=success;
    }

    @Override
    public boolean estWagon() {
        return false;
    }
    public BufferedImage getImage(){
        return ViewLoader.getImage(this);
    }
    public String toString(){
        return this.ville1.getNom()+" <-> " + this.ville2.getNom() + " : " + this.point + "pts";
    }
}
