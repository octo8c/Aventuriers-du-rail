package src.main.java.controller;
import java.util.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.Serializable;

import src.main.java.model.Carte;
import src.main.java.model.CarteObjectif;
import src.main.java.model.CarteWagon;
import src.main.java.model.Couleur;
import src.main.java.model.Ligne;
import src.main.java.model.Ville;
import src.main.java.view.ViewLoader;
public class Joueur implements Serializable {
    private static int indCouleur;
    private static Color[] tabColors={
        new Color(51, 252, 255),// Bleu
        new Color(255, 255, 51),// Jaune
        new Color(227, 51, 255),// Violet
        new Color(255, 51, 60),// Rouge
        new Color(51, 255, 76),// Vert
    };
    private final Map<Couleur,Integer>mapCarteWagon;
    private final ArrayList<CarteObjectif>listCarteObjectif;
    private int nmbWagon;
    private final int indColor;
    private int nmbGare;
    private String pseudo;
    private Ville villeClick;
    private String couleurAvatar;
    public BufferedImage getGare(){
        return ViewLoader.getGare(this.indColor);
    }
    public void setVilleCLick(Ville v){
        this.villeClick=v;
    }
    
    public Ville getVilleClick(){
        return this.villeClick;
    }
    /**
     * Initialise un joueur Vide avec un nombre de wagon donne en parametre
     */
    public Joueur(int nmbWagon){
        this.listCarteObjectif=new ArrayList<CarteObjectif>();
        this.mapCarteWagon=new HashMap<Couleur,Integer>();
        for(Couleur c:Couleur.values()){
            mapCarteWagon.put(c, 3);
        }
        mapCarteWagon.put(Couleur.JOKER,0);
        this.nmbWagon=nmbWagon;
        this.nmbGare=3;
        this.indColor=indCouleur;
        indCouleur++;
        this.villeClick=null;
        this.couleurAvatar = null;
    }
    public int getIndColor(){
        return this.indColor;
    }
    public void setPseudo(String p){
        this.pseudo=p;
    }

    public void setAvatarColor(String c) {
        this.couleurAvatar = c;
    }

    public String getAvatarColor() {
        return this.couleurAvatar;
    }

    public String getPseudo(){
        return this.pseudo;
    }
    public Color getColor(){
        return tabColors[indColor];
    }
    /**
     * Augmente le nombre de gare pose du joueur
     */
    public void decreaseGare(){
        this.nmbGare-=1;
    }
    /**
     * @return Renvoie true si le joueur a encore suffisant de wagon pour continuer a jouer
     */
    public boolean hasEnoughWagon(){
        return this.nmbWagon>=3;
    }
    /**
     * Renvoie le nombre de gare de que le Joueur a poses
     * @return Renvoie a quel gare est le joueur
     */
    public int getNmbGare(){
        return this.nmbGare;
    }
    /**
     * Renvoie true si la ligne l est achetable par le joueur j
     * @param l La ligne a achete
     * @return
     */
    public boolean achetable(Ligne l){
        return this.getNmbWagon()>=l.getNmbCases()&&this.hasCarte(l.getCouleur(), l.getNmbCases());
    }
    public int getNmbWagon(){
        return this.nmbWagon;
    }
    /**
     * @param nmb Retire nmb wagon au nombre wagon
     */
    public void decreaseWagon(int nmb){
        this.nmbWagon-=nmb;
    }
    /**
     * Verifie si le joueur a suffisament de carte
     * @param c Couleur c pour savoir si 
     * @param nmb Nmb de carte
     * @return True si le joueur a suffisament de carte de cette couleur ou false sinon
     */
    public boolean hasCarte(Couleur c,int nmb){
        return this.nmbCarteOfCouleur(c)>=nmb;
    }
    public void couleurEnoughCarte(int nmb){
        for(Couleur value:Couleur.values()){
            if(value!=Couleur.JOKER){
                if(hasCarte(value, nmb)){
                }
            }
        }
    }

    /**
     * @return Renvoie true si le joueur a une gare
     */
    public boolean hasGare(){
        return this.nmbGare>0;
    }
    /**
     * Renvoie le nombre de carte que le joueur possede de la couleur demande
     * @param c Couleur qu'on verifie 
     * @return le nombre de carte de cette couleur
     */
    public int nmbCarteOfCouleur(Couleur c){
        return this.mapCarteWagon.get(c);
    }
    /**
     * Ajoute une carte qui sera sois ajoute a la map si ces une carteWagon soit a la liste des carteObjectif
     * @param cw Carte a ajoute a la main du joueur
     */
    public void addCarte(Carte c){
        if(c.estWagon()){
            CarteWagon cw=(CarteWagon)c;
            mapCarteWagon.replace(cw.getCouleur(), mapCarteWagon.get(cw.getCouleur())+1);
        }else{
            CarteObjectif carteO=(CarteObjectif)c;
            listCarteObjectif.add(carteO);
        }
    }
    /**
     * @param c Couleur de la carte a enleve
     * @param nmb Nmb de carte de cette Couleur a enleve
     */
    private boolean removeCarteWagon(Couleur c,int nmb){
            if(mapCarteWagon.get(c)>=nmb){
                this.mapCarteWagon.replace(c, mapCarteWagon.get(c)-nmb);
                return true;
            }
            return false;
        }    
    /**
     * Verifie si le joueur a nmb carte de Couleur c et assez de wagon a pose restant
     * si ces le cas les retire de la main du joueur,retire nmb wagon du total de wagon et renvoie true
     * Si le joueur n'a pas assez de carte alors renvoie faux
     * @param c Couleur des cartes a enleve
     * @param nmb Nombre de carte a enleve
     * @return Renvoie true si les cartes ont bien été enleve et false sinon
     */
    public boolean jouerCarte(Couleur c,int nmb){
        if(nmbWagon>=nmb){
            if(hasCarte(c, nmb)){
                removeCarteWagon(c, nmb);
                nmbWagon-=nmb;
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }
    }
    public Iterator<CarteObjectif> iteratorCarteObjectif(){
        return listCarteObjectif.iterator();
    }
    public int getNmbCartesObjectifs() {
        return listCarteObjectif.size();
    }
    /**
     * Calcul toutes les couleur ou le joueur a nmb CarteWagon
     * @param nmb Prends le nombre de carte
     * @return Renvoie toutes les couleur ou le joueur a au moins nmb carteWagon
     */
    public Iterator<Couleur>itCouleurEnough(int nmb){
        ArrayList<Couleur>listCouleur=new ArrayList<Couleur>();
        for(Map.Entry<Couleur,Integer>mapEntry:mapCarteWagon.entrySet()){
            if(mapEntry.getValue()>=nmb){
                listCouleur.add(mapEntry.getKey());
            }
        }
        return listCouleur.iterator();
    }
    public String toString(){
        return this.pseudo+" "+this.getColor();
    }
}
    