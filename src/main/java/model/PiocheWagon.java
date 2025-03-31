package src.main.java.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

import javax.naming.NameNotFoundException;

public class PiocheWagon implements Serializable {
    private Queue<CarteWagon> piocheWagon;
    private Queue<CarteWagon> defausse;
    private CarteWagon[] cartesVisibles;

    public PiocheWagon() {
        this.piocheWagon = new LinkedList<CarteWagon>();
        this.defausse = new LinkedList<CarteWagon>();
    }

    public void ajouterCarte(CarteWagon c) {
        piocheWagon.add(c);
    }

    public void shuffle() {
        // Convertir la file en une liste
        LinkedList<CarteWagon> listeCartes = new LinkedList<>(piocheWagon);
        // Mélanger la liste
        Collections.shuffle(listeCartes);
        // Remettre les éléments mélangés dans la file
        piocheWagon = new LinkedList<>(listeCartes);
    }

    public CarteWagon pioche() throws NameNotFoundException {
        CarteWagon carte = piocheWagon.poll(); // Retirer et retourner le premier élément de la file
        if (carte != null) {
            return carte;
        } else {
            ((PiocheWagon) defausse).shuffle();
            for (CarteWagon carteWagon : defausse) {
                piocheWagon.add(carteWagon);
            }
            carte = piocheWagon.poll();
            
            return carte;
        }
    }

    public void piocherWagon() {
        cartesVisibles = new CarteWagon[5]; // Initialisation du tableau des cartes visibles
        for (int i = 0; i < cartesVisibles.length; i++) {
            CarteWagon carte = piocheWagon.poll(); // Retirer et retourner le premier élément de la file
            if (carte != null) {
                cartesVisibles[i] = carte; // Ajout de la carte au tableau des cartes visibles
            } else {
                System.out.println("La pioche wagon est vide");
                break; // Sortir de la boucle si la pioche est vide
            }
        }
    }

    public CarteWagon[] getCartesVisible() {
        return cartesVisibles;
    }
    /**
     * @param index indice de la carte dans le tableau carteVisibles
     * @return Renvoie la carte visibles a la position index et la remplace
     */
    public CarteWagon getCarteWagon(int index){
        CarteWagon carte = cartesVisibles[index];
        remplacerCarteVisible(index);
        return carte;
    }

    public void remplacerCarteVisible(int index) {
        cartesVisibles[index] = piocheWagon.poll();
    }

    public boolean deuxLocomotives() {
        int count=0;
        for (int i = 0; i < cartesVisibles.length - 1; i++) { // -1 carte la carte du dessus de la pioche n'est pas prise en compte
            if (cartesVisibles[i].getCouleur() == Couleur.JOKER) {
                count++;
            }
        }
        return count >= 2 ? true : false;
    }
    
    /**
     * Ajoute la carte au fond de la pioche et pas au dessus
     * @param cw La carte ajouté
     */
    public void ajoutFondCarte(CarteWagon cw){
        this.defausse.add(cw);
        this.piocheWagon.remove();
    }
}
