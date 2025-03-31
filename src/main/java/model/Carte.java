package src.main.java.model;

import java.awt.image.BufferedImage;
import java.io.Serializable;

public interface Carte extends Serializable {
    /**
     * Methode pour savoir si la carte est bien une carteWagon
     * @return Return true si la carte est une carte Wagon et faux sinon
     */
    boolean estWagon();
    BufferedImage getImage();
}
