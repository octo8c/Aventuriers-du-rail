package src.main.java.view;
import java.util.*;
import java.util.concurrent.TimeUnit;
import src.main.java.controller.Joueur;
import src.main.java.internet.Client;
import src.main.java.model.FonctionJeu;
import src.main.java.model.Jeu;
import src.main.java.model.Plateau;

public class ModeLoader{
    private static boolean ONLINE;
    public static final int FONCTION_TEMPS = 1;
    public static final int FONCTION_TOUR = 2 ;
    private static int nmbTourMax=Integer.MAX_VALUE;
    private static int nmbMinutesMax=Integer.MAX_VALUE;
    private static FonctionJeu fonctionJeu=null;
    private static Client client=null;

    /**
     * Debut de l'ecran de l'utilisateur en X
     */
    private static int startX;

    /**
     * Debut de l'ecran de l'utilisateur en Y
     */
    private static int startY;
    /**
     *Largeur d'ecran de l'utilisateur actuelle
     */
    private static double currentHeight;
    /**
     *Largeur de l'ecran qui a servit a faire les points
     */
    private static double baseHeight;
    /**
     * Taille de l'ecran de base qui a servit a placer les villes
     */
    private static double baseLength;
    /**
     * Longueur de l'ecran de l'utilisateur actuel
     */
    private static double currentLength;
    /**
     * @return Renvoie la longueur de base de l'ecran
     */
    public static double getBaseLength() {
        return baseLength;
    }
    /**
     * @return Renvoie la largeur de base de l'ecran qui a place les points
     */
    public static double getBaseHeight() {
        return baseHeight;
    }
    public static boolean isOnline(){
        return ONLINE;
    }
    public static int getStartX(){
        return startX;
    }
    public static int getStartY(){
        return startY;
    }
    public static void setStartX(int x){
        startX = x;
    }
    public static void setStartY(int y){
        startY = y;
    }
    /**
     * @return Renvoie la longueur de l'ecran actuelle de l'utilisateur
     */
    public static double getCurrentLength() {
        return currentLength;
    }

    public static double getCurrentHeight() {
        return currentHeight;
    }
    public static void setNmbTourMax(int nmbTour){
        nmbTourMax=nmbTour;
    }
    public static void setTimeMax(int nmbMinutes){
        nmbMinutesMax=nmbMinutes;
    }
    public static void loadBaseScreenSize(int length,int height){
        baseLength=length;
        baseHeight=height;
    }
    public static void loadCurrentScreenSize(int length,int height){
        currentHeight=height;
        currentLength=length;
    }
    /**
     * Remplace la fonction de jeu default par cette fonction pour le mode online
     * @param fonction Fonction jeu par default
     */
    public static void setFonctionJeu(FonctionJeu fonction){
        fonctionJeu=fonction;
    }
    public static void setClientJoueur(Client c){
        client=c;
    }
    public static Client getClient(){
        return client;
    }
    /**
     * Renvoie la fonction jeu qu'on veut en fonction de ce que l'utilisateur a choisi
     * Resultats en fonction  de ind :
     * 0 -> jeu au temps avec nombre de wagon
     * 1 -> jeu au tour avec nombre de wagon
     * autre -> jeu au nombre de wagon uniquement
     * @param ind La fonction jeu qu'on a decide de choisir 
     * @return La foncyion fin de jeu qu'on souhaite
     */
    public static FonctionJeu getFonctionFin(int ind){
        switch (ind) {
            case 1:{//La partie s'arrete au bout du nombre de minutes selectionnes
                FonctionJeu fonctionJeuTemps=new FonctionJeu(ind){
                    @Override
                    public Joueur fin(Jeu jeu, Plateau plateau) {
                        if(TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis()-jeu.getStartTime())>=nmbMinutesMax){
                            return plateau.getCurrentJoueur();
                        }else{
                            Iterator<Joueur>itJoueur=plateau.iteratorJoueur();
                            while(itJoueur.hasNext()){Joueur joueur=itJoueur.next();
                                if(!joueur.hasEnoughWagon()){
                                    return joueur;
                                }
                            }
                            return null;
                            }
                        };
                    };
                    
                    return fonctionJeuTemps; 
                }
            case 2:{
                    FonctionJeu fonctionJeuTour=new FonctionJeu(ind) {
                        @Override
                        public Joueur fin(Jeu jeu, Plateau plateau) {
                            if(jeu.getNmbTour()>nmbTourMax){
                                return plateau.getCurrentJoueur();
                            }else{
                                Iterator<Joueur>itJoueur=plateau.iteratorJoueur();
                                while(itJoueur.hasNext()){Joueur joueur=itJoueur.next();
                                    if(!joueur.hasEnoughWagon()){
                                        return joueur;
                                    }
                                }
                                return null;
                            }
                        };
                    };   
                    return fonctionJeuTour;
                }
            
            default: {//Tour infinie le jeu s'arrete simplement quand un joueur n'a plus  assez de wagon
                if(fonctionJeu==null){
                FonctionJeu fonctionJeuWagon = new FonctionJeu(10) {
                    @Override
                    public Joueur fin(Jeu jeu, Plateau plateau) {
                        Iterator<Joueur>itJoueur=plateau.iteratorJoueur();
                        while(itJoueur.hasNext()){
                            Joueur joueur=itJoueur.next();
                            if(!joueur.hasEnoughWagon()){return joueur;}}
                        return null;
                    };
                };
                    return fonctionJeuWagon;
                }else{
                    return fonctionJeu;
                }
                    
            }
        }
    }
    /**
     * set le mode online en fonction de la valeur de bool
     * @param bool
     */
    public static void setOnline(boolean bool) {
        ONLINE =  bool;
    }
    public static <T> void setTurnOrder(ArrayList<T>listToOrder,int[]tabValue){
        // Créez une liste temporaire pour stocker les indices des éléments de listJoueur
        ArrayList<Integer> indices = new ArrayList<>();
        for (int i = 0; i < listToOrder.size(); i++) {
            indices.add(i);
        }
        
        // Triez les indices en fonction des valeurs de diceValues
        Collections.sort(indices, new Comparator<Integer>() {
            @Override
            public int compare(Integer i1, Integer i2) {
                return Integer.compare(tabValue[i2], tabValue[i1]); // Triez dans l'ordre décroissant des valeurs de diceValues
            }
        });
        
        // Créez une liste temporaire pour réorganiser listJoueur
        ArrayList<T> temp = new ArrayList<>(listToOrder);
        for (int i = 0; i < listToOrder.size(); i++) {
            listToOrder.set(i, temp.get(indices.get(i)));
        }
    }
}