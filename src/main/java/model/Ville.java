package src.main.java.model;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import src.main.java.controller.Joueur;
import src.main.java.view.ViewLoader;
public class Ville extends Ellipse2D implements Serializable{
    private final String nom;
    private final ArrayList<Ligne>lignes;
    private final int x;
    private final int y;
    private Joueur gare;
    private Ligne ligneGare;
    public Ville(String n,ArrayList<Ligne>l,int x,int y){
        this.nom=n;
        this.lignes=l;
        this.x=x;
        this.y=y;
        this.gare=null;
        this.ligneGare=null;
    }
    public Point getPoint(){
        return new Point(x, y);
    }
    /**
     * @return Renvoie le nom de la ville
     */
    public String getNom(){
        return this.nom;
    }
    public Ligne getLigneGare(){
        return this.ligneGare;
    }
    public void setLigneGare(Ligne l){
        this.ligneGare=l;
    }
    public boolean hasGare(){
        return this.gare!=null;
    }
    public void setGare(Joueur joueur){
        this.gare=joueur;
    }
    /**
     * @return Renvoie le joueur qui possede une gare
     */
    public Joueur getJoueur(){
        return this.gare;
    }
    /**
     * Revoie un iterateur de toutes les lignes a itére
     * @return Renvoie un iterator de toutes les lignes qui deservent la ville
     */
    public Iterator<Ligne> getLigne() {
        return lignes.iterator();
    }
    public void ajoutLigne(Ligne l){
        if(!lignes.contains(l)){
            lignes.add(l);
        }
    }
    public void supLignes(Ligne l){
        lignes.remove(l);
    }
    public double getX(){
        return this.x-ViewLoader.getRaduisVille()/2;
    }
    public double getY(){
        return this.y-ViewLoader.getRaduisVille()/2;
    }
    public int x(){
        return this.x-ViewLoader.getRaduisVille()/2;
    }
    public int y(){
        return this.y-ViewLoader.getRaduisVille()/2;
    }
    /**
     * Compare les positions des 2 villes et renvoie la ville la plus proche de 0 0 
     * @param v La ville a comparee
     * @return Renvoie soit la ville sur qui on a fait l'appel si egalite ou quelle est bien la plus petite v sinon
     */
    public Ville compareTo(Ville v){
        int distance=x-v.x+y-v.y;
        if(distance==0){
            return this;
        }else{
            if(distance>0){
                return this;
            }else{
                return v;
            }
        }
    }
    public String toString(){
        return this.getNom()+" x : "+this.x+" y :"+this.y;
    }
    /**
     * Renvoie la ligne entre les 2 ville si elle existe et null sinon
     * @param v1 la ville a verifier
     * @return
     */
    public Ligne ligneBetweenVille(Ville v1){
        for(Ligne l:this.lignes){
            if(l.isVilleInLigne(v1, this)){
                return l;
            }
        }
        return null;
    }
    /**
     * Verifie que le points est bien dans le cercle de la ville
     * @param p Cooordonnes du points a regarde
     * @return Renvoie true si le points passe en prametre est bien dans le cercle de la ville
     */
    public boolean contains(Point p){
        int normX=Math.abs(p.x-this.x);
        int normY=Math.abs(p.y-this.y);
        return normX<ViewLoader.getRaduisVille()/2&&normY<ViewLoader.getRaduisVille()/2;
    }
    @Override
    public Rectangle2D getBounds2D() {
       return new Rectangle(x-ViewLoader.getRaduisVille(), y-ViewLoader.getRaduisVille(), ViewLoader.getRaduisVille(), ViewLoader.getRaduisVille());
    }
    @Override
    public double getHeight() {
        return ViewLoader.getRaduisVille();
    }
    @Override
    public double getWidth() {
        return ViewLoader.getRaduisVille();
    }
    @Override
    public boolean isEmpty() {
        return true;
    }
    @Override
    public void setFrame(double arg0, double arg1, double arg2, double arg3) {
        //Normalment on vas jms s'en servir
    }
}
