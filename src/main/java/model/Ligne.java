package src.main.java.model;

import java.awt.Point;
import java.io.Serializable;

import src.main.java.controller.Joueur;
import src.main.java.view.ViewLoader;

public class Ligne implements Cloneable ,Serializable {
    private final Ville ville1;
    private final Ville ville2;
    private final int nmbCases;
    private final Couleur couleur;
    private Joueur propriétaire;
    private final boolean estTunnel;
    private final double angle;
    private int nmbLocomotives;
    /**
     * @param ville1 Premiere ville acessible par le chemin
     * @param ville2 Deuxieme ville acessible par le chemin
     * @param nmbCases Nombre de cases/Longueur du chemin
     * @param c Couleur d chemin ,Tout les wagon devront etre de la meme couleur
     */
    public Ligne(Ville ville1,Ville ville2,int nmbCases,Couleur c,boolean estTunnel,int nmbLocomotives){
        this.ville1=ville1;
        this.ville2=ville2;
        this.nmbCases=nmbCases;
        this.couleur=c;
        this.estTunnel=estTunnel;
        this.nmbLocomotives=nmbLocomotives;
        this.angle=angle();
    }
    /**
     * @return Renvoie l'angle par rapport a l'axis x en radiant
     */
    private double angle(){
        if (ville1.getX()==ville2.getX()){
            return Math.PI/2;//90 degres
        }else{
            return Math.atan2(ville2.getY()-ville1.getY(),ville2.getX()-ville1.getX());
        }
    }
    /**
     * Constructeur pour les lignes avec locomotives
     * @param v1
     * @param v2
     * @param nmbcases
     * @param nmbLocomotives
     */
    public Ligne(Ville v1,Ville v2,int nmbcases,int nmbLocomotives){
        if(v1.y()<v2.y()){
            this.ville1=v1;
            this.ville2=v2;
        }else if(v1.y()==v2.y()){
            if(v1.x()<v2.x()){
                ville1=v1;
                ville2=v2;
            }else{
                ville1=v2;
                ville2=v1;
            }
        }else{
            this.ville1=v2;
            this.ville2=v1;
        }
        this.nmbCases=nmbcases;
        this.couleur=Couleur.JOKER;
        this.estTunnel=false;
        this.nmbLocomotives=nmbLocomotives;
        this.angle=angle();
    }
    public double getAngle(){
        return this.angle;
    }
    /**
     * @return Renvoie le nombre de locomotives necessaire pour acheter la ligne
     */
    public int getNmbLocomotives(){
        return this.nmbLocomotives;
    }
    /**
     * @return Renvoie true si la ligne est un tunnel et false sinon
     */
    public boolean isTunnel(){
        return this.estTunnel;
    }
    /**
     * @return Renvoie le joueur j proprietaire de la ligne
     */
    public Joueur getJoueur(){
        return this.propriétaire;
    }
    /**
     * Definie le joueur j comme proprietaire de la ligne
     * @param j Joueur mis en tant que prorietaire
     */
    public void setJoueur(Joueur j){
        this.propriétaire=j;
    }
    /**
     * @return Renvoie true si la ligne n'a aucun proprietaire et false sinon
     */
    public boolean isEmpty(){
        return this.propriétaire==null;
    }
    /**
     * @return Renvoie le nombre de cases de la ligne
     */
    public int getNmbCases(){
        return nmbCases;
    }
    /**
     * @return Renvoie la couleur de la ligne
     */
    public Couleur getCouleur(){
        return couleur;
    }
    /**
     * @return Renvoie la 1ere ville deservi par la ligne
     */
    public Ville getVille1(){
        return ville1;
    }

    /**
     * @return Renvoie la 2eme ville deservi par la ligne
     */
    public Ville getVille2(){
        return ville2;
    }
    /**
     * Prends une ville1 et si elle est ville1 ou ville2 on renvoie l'autre ville ou null si la ville passe en parametre n'est ni vill1 ou ville2
     * @param v Prends en parametre une ville 
     * @return Renvoie la seconde ville
     */
    public Ville getVille(Ville v){
        if(ville1==v){
            return ville2;
        }
        if(ville2==v){
            return ville1;
        }
        return null;
    }
    public String toString(){
        return "|"+ville1.getNom()+" <-> "+ville2.getNom()+"|"+this.nmbCases+"|"+couleur;
    }
    public boolean isBuyable(Joueur j){
        return this.isEmpty()&&j.achetable(this);
    }
    public boolean isProprietaire(Joueur j){
        return this.propriétaire==j;
    }
    public static int cout(Ligne ligne){
        switch(ligne.nmbCases){
            case 2: return 2;
            case 3: return 4;
            case 4: return 6;
            case 5: return 8;
            case 6: return 9;
            case 7: return 11;
            default : return ligne.nmbCases;
        }
    }
    public boolean isVilleInLigne(Ville v1,Ville v2){
        return (this.ville1==v1&&ville2==v2)||(this.ville1==v2&&this.ville2==v1);
    }
    /**
     * @return Renvoie la distance entre les 2 lignes qui compose la ligne
     */
    public int distance(){
        int x=ville1.x()-ville2.x();
        int y=ville1.y()-ville2.y();
        return (int)Math.sqrt(x*x+y*y);
    }
    /**
     * Renvoie un tableau des points de la ligne intermediaire entre ville 1 et ville2 
     * Algorithme de Bresenham
     * @param quantity Le nombre de points qu'on veut obtenir
     * @return Renvoie un tableau de tout les points divisee par la quantite qu'on en veut
     */
    public Point[] getPoints(int quantity) {
        return ViewLoader.getPoints(quantity, ville1.getPoint(), ville2.getPoint());
    }
}
