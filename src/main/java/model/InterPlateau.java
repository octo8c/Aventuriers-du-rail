package src.main.java.model;
import java.util.*;
import src.main.java.controller.Joueur;

/*Interface plateau qui sers a ne pas melanger le model et la vue il manque porbablement des trucs dont vous pouvez rajoutez mais evitez de tous mettre */
public interface InterPlateau {
    /**
     * Renvoie le joueur a qui ces le tour de joue
     * @return Renvoie le joueur actule
     */
    public Joueur getCurrentJoueur();

    /**
     * @return Renvoie un iterateur de toutes les villes dans le plateau
     */
    public Iterator<Ville>iteratorVille();
    /**
     * @return Renvoie un iterateur de toutes les lignes du plateau
     */
    public Iterator<Ligne>iteratorLigne();
    public Iterator<Joueur>iteratorJoueur();
    public ArrayList<Ligne>constructable(Joueur j);
    public ArrayList<Map.Entry<Joueur,Integer>>classementsJoueur();
    public ArrayList<Ville>itVilleGare();
    public void achatLigne(Joueur j,Ligne l);
    public void achatTunnel(Joueur joueur,Ligne ligne);
    /**
     * Mets a jour le pseudo du joueur situe a la case I de la liste
     * @param i Position du joueur
     * @param pseudo Pseudo a mettre
     */
    public void setPseudo(int i,String pseudo);
    public void setAvatarColor(int i, String c);
    /**
     * Surcharge la methode achatTunnel pour faire en sorte que la couleur passe en parametre soit la couleur qu'on veut achetes
     * @param joueur Joueur qui veut acheter la ligne
     * @param ligne Ligne qu'on souhaite achete
     * @param couleur Couleur qu'on vas regarder a la place de la couleur de la ligne
     */
    public void achatLigne(Joueur joueur,Ligne ligne,Couleur couleur);
    public void achatLigne(Joueur joueur,Ligne ligne,Couleur couleur,int nmbCases);
    public void placeGare(Joueur j,Ville v);
    /**
     * Pioche une carteWagon
     * @return Renvoie une carte de la piocheWagon
     */
    public CarteWagon piocheWagon();
    /**
     * @return Renvoie une carte Objectif de la pile de carte
     */
    public CarteObjectif piocheObjectif();
    /**
     * Fais passer la main au nouveau joueur
     */
    public void nextJoueur();
}
