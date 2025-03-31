package src.main.java.model;

import src.main.java.controller.*;
public class DoubleLigne extends Ligne {
    public static final int GAUCHE = 1 ;
    public static final int DROITE = 2 ;
    private DoubleLigne ligne1;
    private int cote;
    public DoubleLigne(Ville v1,Ville v2,int nbCases,Couleur c,boolean estTunnel,int nmbLocomotives){
        super(v1, v2, nbCases, c,estTunnel,nmbLocomotives);
        this.cote = GAUCHE;
    }
    public DoubleLigne(Ville v1,Ville v2,int nbCases,Couleur c,DoubleLigne ligne,boolean estTunnel,int nmbLocomotives){
        super(v1, v2, nbCases, c,estTunnel,nmbLocomotives);
        this.ligne1=ligne;
        ligne.ligne1=this;
        this.cote=DROITE;
    }
    public int getCote(){
        return this.cote;
    }
    @Override
    public boolean isBuyable(Joueur j){
        return this.isEmpty()&&(this.ligne1.isEmpty()||this.ligne1.getJoueur()!=j);
    }
    @Override
    public boolean isProprietaire(Joueur j){
        boolean flag=super.isProprietaire(j);
        return ligne1.isProprietaire(j, flag);
    }
    private boolean isProprietaire(Joueur j,boolean flag){
        return flag&&super.isProprietaire(j);
    }
}
