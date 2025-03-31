package src.main.java.model;

import java.awt.Color;

public enum Couleur {
    RED,BLUE,BLACK,YELLOW,GREEN,PURPLE,WHITE,ORANGE,
    /**
     *@param JOKER representant soit les locomotives soit les tunnel
     */
    JOKER;
    public String toString(){
        switch(this){
            case BLUE:return "BLUE";
            case RED:return "RED";
            case BLACK:return "BLACK";
            case YELLOW:return "YELLOW";
            case GREEN:return "GREEN";
            case JOKER:return "JOKER";
            case ORANGE : return "ORANGE";
            case PURPLE:return "PURPLE";
            case WHITE:return "WHITE";
            default:return "NONDEFINI";
        }
    }
    /**
     * Renvoie la couleur passe en argument en color awt
     * @param couleur Prends en parametre la couleur a convertir
     * @return Renvoie la couleur equivalent de la classe Color de awt
     */
    public static Color couleurToColor(Couleur couleur){
        switch (couleur) {
            case GREEN:
                return new Color(0, 102, 0);
            case BLUE:
                return Color.BLUE;
            case YELLOW:
                return Color.YELLOW;
            case WHITE:
                return Color.WHITE;
            case BLACK:
                return Color.BLACK;
            case RED:
                return Color.RED;
            case PURPLE :
                return Color.MAGENTA;
            case JOKER:
                return Color.GRAY;
            case ORANGE:
                return Color.ORANGE;
            default:
                return Color.BLACK;
        }
    }
    public Color couleurToColor(){
        switch (this) {
            case GREEN:
                return new Color(102, 255, 102);
            case BLUE:
                return Color.BLUE;
            case YELLOW:
                return new Color(255, 255, 153);
            case WHITE:
                return Color.WHITE;
            case BLACK:
                return  new Color(73,73,73);
            case RED:
                return new Color(255,0,0);
            case PURPLE :
                return new Color(102, 0, 153);
            case JOKER:
                return Color.GRAY;
            case ORANGE :
                return new Color(255, 102, 0);
            default:
                return Color.BLACK;
        }
    }
    public String getColorFr() {
        switch (this) {
            case GREEN:
                return "Verte";
            case BLUE:
                return "Bleue";
            case YELLOW:
                return "Jaune";
            case WHITE:
                return "Blanche";
            case BLACK:
                return  "Noire";
            case RED:
                return "Rouge";
            case PURPLE :
                return "Rose";
            case JOKER:
                return "Locomotives";
            case ORANGE :
                return "Orange";
            default:
                return "Error";
        }
    }
}
