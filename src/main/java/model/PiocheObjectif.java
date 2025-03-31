package src.main.java.model;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

import javax.naming.NameNotFoundException;

public class PiocheObjectif implements Serializable {
    private Queue<CarteObjectif> piocheObjectifs;

    public PiocheObjectif() {
        this.piocheObjectifs = new ArrayDeque<>();
    }

    public void addCarteObjetif(CarteObjectif co) {
        this.piocheObjectifs.add(co);
    }

    public CarteObjectif pioche() throws NameNotFoundException {
        if (!this.piocheObjectifs.isEmpty()) {
            return piocheObjectifs.poll();
        } else {
            throw new NameNotFoundException();
        }
    }

    public void shuffle() {
        LinkedList<CarteObjectif> listeCartes = new LinkedList<>(this.piocheObjectifs);
        Collections.shuffle(listeCartes);
        this.piocheObjectifs = new LinkedList<>(listeCartes);
    }

    public CarteObjectif piocher() {
        if (!this.piocheObjectifs.isEmpty()) {
            CarteObjectif carte = this.piocheObjectifs.poll();
            return carte;
        } else {
            System.out.println("La pioche est vide");
            return null;
        }
    }

    public boolean isEmpty() {
        return piocheObjectifs.isEmpty();
    }

    public void ajoutFondPioche(CarteObjectif cw) {
        this.piocheObjectifs.add(cw);
        this.piocheObjectifs.remove();
    }
}
