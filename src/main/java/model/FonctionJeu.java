package src.main.java.model;

import java.io.Serializable;

import src.main.java.controller.Joueur;

public abstract class FonctionJeu implements Serializable{
    private final int idFonction;
    public FonctionJeu(int idFonction){
        this.idFonction=idFonction;
    }
    public int getFonctionFin(){
        return idFonction;
    }
    /**
     * @param jeu le thread du jeu
     * @param plateau Le plateau 
     * @return Le joueur qui a fait finir la partie
     */
    public abstract Joueur fin(Jeu jeu,Plateau plateau);
}
