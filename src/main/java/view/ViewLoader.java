package src.main.java.view;
import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;

import src.main.java.model.Carte;
import src.main.java.model.Couleur;
import src.main.java.model.Ligne;
import src.main.java.model.CarteWagon;
public class ViewLoader {
    private static  final int LINE_SIZE=15;
    private static  final int RADIUS_VILLE = 25;
    private static BufferedImage [] tabCarteWagon;
    private static BufferedImage [] tabCarteGare;
    private static BufferedImage [][] tabWagon;
    /**
     * @return Renvoie le raduis des ville dessines
     */
    public static int getRaduisVille(){
        return RADIUS_VILLE;
    }
    public static int getLineSize(){
        return LINE_SIZE;
    }
    public static double getDoubleLineSize(){
        return (double)LINE_SIZE;
    }
    /**
     * Charge toutes les images dans la memoire
     * @param nmbJoueur
     */
    public static void loadImage(int nmbJoueur){
        tabCarteWagon=new BufferedImage[Couleur.values().length];
        for(int i=0;i<Couleur.values().length;i++){
            try {
                tabCarteWagon[i]=ImageIO.read(new File("src/main/resources/images/cards/eu_WagonCard_"+Couleur.values()[i].toString().toLowerCase()+".png"));
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Probleme de fichier pour : "+i);
                System.out.println("src/main/resources/images/cards/eu_WagonCard_"+Couleur.values()[i].toString().toLowerCase()+".png");
                System.exit(1);
            }
        }
        String [] tab={"ROSE","JAUNE","ROUGE","VERT","BLEU"};
        tabCarteGare = new BufferedImage[nmbJoueur];
            for (int i = 0; i < nmbJoueur; i++) {
                try{
                    tabCarteGare[i]=javax.imageio.ImageIO.read(new File("src/main/resources/images/images/gare-"+tab[i]+".png"));
                }catch(IOException ioException){
                    System.err.println("Erreur pour la gare de couleur "+tab[i]);
                    ioException.printStackTrace();
                    tabCarteGare[i]=null;
                }
            }
        //AJout de toutes les images de wagon
        tabWagon=new BufferedImage[tab.length][6];
        for (int i = 0; i < tab.length; i++) {
            for(int j = 1;j<=6;j++){
                try{
                    tabWagon[i][j-1]=javax.imageio.ImageIO.read(new File("src/main/resources/images/wagons/"+tab[i]+"/image1x"+j+".png"));
                }catch(IOException e){
                    System.err.println("erreur fichier"+tab[i]);
                    System.err.println("src/main/resources/images/wagons/"+tab[i]+"/image1x"+j+".png");
                    tabWagon[i][j]=null;
                    e.printStackTrace();
                }
            }
        }
        
    }
    /**
     * Renvoie l'indice de l'image
     * @param ligne
     * @return
     */
    public static BufferedImage getWagon(Ligne ligne){
        int indImage = getIndBuffImage(ligne);
        return tabWagon[ligne.getJoueur().getIndColor()][indImage];
    }
    /**
     * Renvoie l'indice correspondant a l'angle de la ligne pour les wagon
     * @param ligne la ligne possedant un angle 
     * @return
     */
    public static int getIndBuffImage(Ligne ligne){
        double pi=Math.PI;
        double angle = ligne.getAngle();
        int indImage=0;
        if(angle>=0){
            if(angle<=(pi/24)||angle>=23*pi/24){
                indImage=0;
            }else if(angle<=(7*pi)/24){
                indImage=4;
            }else if(angle<=(11*pi)/24){
                indImage=5;
            }else if(angle<=(15*pi)/24){
                indImage=3;
            }else if(angle<=(19*pi)/24){
                indImage=2;
            }else if(angle<=(23*pi)/24){
                indImage=1;
            }else{
                indImage=0;
            }
        }else{
            if(angle>=(pi/12)*-1||angle<=-(11*pi)/12){
                indImage=0;
            }else if(angle>=-(pi*3/12)){
                indImage=2;
            }else if(angle>=-(5*pi)/12){
                indImage=2;
            }else if(angle>=-(7*pi)/12){
                indImage=3;
            }else if(angle>=-(9*pi)/12){
                indImage=4;
            }else if(angle>=-(11*pi)/12){
                indImage=5;
            }
        }
        return indImage;
    }
    /**
     * Renvoie l'image de la gare correspondant a l'incide id
     * @param id
     * @return
     */
    public static BufferedImage getGare(int id){
        if(id<0||id>=tabCarteGare.length){
            return null;
        }else{
            return tabCarteGare[id];
        }
    }

    /**
     * 
     * @param carte
     * @return Renvoie l'image corrrespondant a la couleur de la carte
     */
    public static BufferedImage getImage(Carte carte){
        if(carte.estWagon()){
            CarteWagon cw=(CarteWagon)carte;
            switch (cw.getCouleur()) {
                case RED:return tabCarteWagon[0];
                case BLUE:return tabCarteWagon[1];
                case BLACK:return tabCarteWagon[2];
                case YELLOW:return tabCarteWagon[3];
                case GREEN:return tabCarteWagon[4];
                case PURPLE:return tabCarteWagon[5];
                case WHITE:return tabCarteWagon[6];
                case ORANGE:return tabCarteWagon[7];
                case JOKER:return tabCarteWagon[8];
                default : return tabCarteWagon[8];
            }
        }else{
            return null;
        }
    }
    /**
     * @return Renvoie un iterator de toutes les couleurs pour dessiner les cases joker arc en ciel
     */
    public static Iterator<Color> itCouleur(){
        ArrayList<Color>listCouleur=new ArrayList<Color>();
        listCouleur.add(new Color(204, 0, 0));//Rouge
        listCouleur.add(new Color(255, 102, 0));//Orange
        listCouleur.add(new Color(255,255,0));//Jaune
        listCouleur.add(new Color(0,204,0));//Vert 
        listCouleur.add(new Color(51,153,255));//Bleu clair
        listCouleur.add(new Color(0,0,153));//BLEU FONCE
        listCouleur.add(new Color(102,0,153));//VIOLET
        return listCouleur.iterator();
    }
    /**
     * Renvoie un tableau de point de quantite quantity entre p1 et p2
     * @param quantity le nombre de points dans le tableau
     * @param p1
     * @param p2 
     * @return un tableau de point avec p1 et p2 non inclus
     */
    public static Point[] getPoints(int quantity,Point p1,Point p2) {
        Point[] points = new Point[quantity];
        int ydiff = p2.y - p1.y, xdiff = p2.x - p1.x;
        double slope = (double)(p2.y - p1.y) / (p2.x - p1.x);
        double x, y;

        --quantity;

        for (double i = 0; i < quantity; i++) {
            y = slope == 0 ? 0 : ydiff * (i / quantity);
            x = slope == 0 ? xdiff * (i / quantity) : y / slope;
            points[(int)i] = new Point((int)Math.round(x) + p1.x, (int)Math.round(y) + p1.y);
        }

        points[quantity] = p2;
        return points;
    }
    /**
     * Renvoie les coordonnes du point d'ou dessine les wagon
     * @param p1 Point 1
     * @param p2 Point 2
     * @param flag Si flag est true renvoie le coin gauche du rectangle si flag est false renvoie le coin droit du rectangle
     * @return Renvoie le point decaler 
     */
    public static Point pointAfficheWagon(Point p1,Point p2,int ind,boolean sens){
        Point p3;
        Point p4;
        int diff=p1.y-p2.y;
        boolean condition = sens ? diff>0 :  diff<0;
        //Modification de p1 et p2 pour mettre le plu petit point
        if(condition){
            p3=(Point)p1.clone();
            p4=(Point)p2.clone();
        }else{
            p3=(Point)p2.clone();
            p4=(Point)p1.clone();
        }
        double x = p4.x-p3.x;
        double y = p4.y-p3.y;
        double norme = Math.sqrt((x)*(x)+(y)*(y));
        int inverse = sens ? 1 : -1;
        switch (ind) {
            case -2:
                return new Point(p4.x-inverse*(int)((y/norme)*Math.ceil(getDoubleLineSize()/2.0)),p4.y+inverse*(int)((x/norme)*Math.ceil(getDoubleLineSize()/2.0)));
            case -1:
                return new Point(p4.x+inverse*(int)((y/norme)*Math.ceil(getDoubleLineSize()/2.0)),p4.y-inverse*(int)((x/norme)*Math.ceil(getDoubleLineSize()/2.0)));
            case 0:
                return new Point(Math.min(p3.x,p4.x), p4.y-(int)Math.abs(((x/norme)*Math.ceil(getDoubleLineSize()/2.0))));
            case 1:
                return new Point(Math.min(p3.x,p4.x), p4.y-inverse*(int)((x/norme)*Math.ceil(getDoubleLineSize()/2.0)));
            case 2 :
                return new Point(p4.x-Math.abs((int)((y/norme)*Math.ceil(getDoubleLineSize()/2.0))), p4.y-(int)Math.abs(((x/norme)*Math.ceil(getDoubleLineSize()/2.0))));
            case 5 :
                return new Point(p4.x+inverse*(int)((y/norme)*Math.ceil(getDoubleLineSize()/2.0)), p4.y+inverse*(int)((x/norme)*Math.ceil(getDoubleLineSize()/2.0)));        
            default:
                return new Point(p4.x+inverse*(int)((y/norme)*Math.ceil(getDoubleLineSize()/2.0)), p4.y+inverse*(int)((x/norme)*Math.ceil(getDoubleLineSize()/2.0)));
        }
       
    }
    /**
     * Trie la liste des point de sorte a ce que pAff[i]>=pAff[i+1]
     * @param pAff tableau de point trie
     */
    protected static void reverseSort(Point []pAff){
        Arrays.sort(pAff, new Comparator<Point>() {
            public int compare(Point a, Point b) {
                int xComp = Integer.compare(a.x, b.x);
                if(xComp == 0)
                    return Integer.compare(a.y, b.y);
                else
                    return xComp;
            }
        });
        reverseArray(pAff);
    }
    private static <T> void reverseArray(T[]tab){
        for(int i=0;i<tab.length/2;i++){
            echange(tab, i, tab.length-i-1);
        }
    }
    private static <T> void echange(T[]tab,int i,int j){
        T tmp = tab[i];
        tab[i]=tab[j];
        tab[j]=tmp;
    }
}
