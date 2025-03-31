package src.main.java.model;

import java.io.*;
import java.util.ArrayList;
public class SauvegardePlateau implements Serializable{
    //On suppose que les sauvegardes ne sont que en offline ou alors simplement au lancement des parties

    private final Plateau plateau;
    private final long heureSauvegarde;
    private final long tempsPlateau;
    private final int nmbTour;
    private final FonctionJeu fonctionJeu;
    public Plateau getPlateau() {
        return plateau;
    }
    public long getHeureSauvegarde() {
        return heureSauvegarde;
    }
    public long getTempsPlateau() {
        return tempsPlateau;
    }
    public int getNmbTour() {
        return nmbTour;
    }
    public FonctionJeu getFonctionJeu(){
        return fonctionJeu;
    }
    /**
     * Constructeurs pour créer une sauvegarde 
     * @param plateau
     * @param heureSauvegarde
     * @param tempsPlateau
     * @param nmbTour
     */
    public SauvegardePlateau(Plateau plateau,long heureSauvegarde,long tempsPlateau,int nmbTour,FonctionJeu fonctionJeu){
        this.plateau=plateau;
        this.heureSauvegarde=heureSauvegarde;
        this.tempsPlateau=tempsPlateau;
        this.nmbTour=nmbTour;
        this.fonctionJeu=fonctionJeu;
    }

    private static ArrayList<String>listSauvegardeSup=new ArrayList<String>();
    private static boolean [] tabSaveDispo = null;
    private static final int nmbSave=5;
    public static void loadSaveDispo(){
        File file = new File("src/main/resources/save/saveDispo");
        if(file.exists()&&file.isFile()){
            tabSaveDispo = new boolean[5];//On pars sur un nombre limite de sauvegarde 
            try (FileInputStream fileInputStream = new FileInputStream(file)) {
                ObjectInputStream ois= new ObjectInputStream(fileInputStream);//On suppose qu'on a 5 sauvegarde
                for(int i=0;i<nmbSave;i++){
                    tabSaveDispo[i]=ois.readBoolean();
                }
                ois.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch(Exception e){
                e.printStackTrace();
                System.err.println("Erreur de convertion attention");
                tabSaveDispo=null;
            }
        }else{
            System.out.println("IL Y A UNE ERREUR ");
        }
    }
    /**
     * Renvoie toutes les saves disponible
     * @return
     */
    public static String[] saveDisponible() {
        if(tabSaveDispo==null){
            loadSaveDispo();
            return saveDisponible();
        }else{
            String []res=null;
            int cptSaveOccupe = 0;
            for(int i=0;i<tabSaveDispo.length;i++){
                if(tabSaveDispo[i]){
                    cptSaveOccupe++;
                }
            }
            int ind=0;
            res = new String[cptSaveOccupe];
            for(int i=0;i<tabSaveDispo.length;i++){
                if(tabSaveDispo[i]){
                    res[ind++]="Sauvegarde"+(i+1);
                }
            }
            return res;
        }
    }
    /**
     * @return Renvoie un tableau de string avec toutes les save occupes depuis lequels on peut lancer une partie
     */
    public static String[] saveOccupe(){
        String[]res=null;
        if(tabSaveDispo==null){
            loadSaveDispo();
            return saveOccupe();
        }else{
            int cptSaveOccupe = 0;
            for(int i=0;i<tabSaveDispo.length;i++){
                if(!tabSaveDispo[i]){
                    cptSaveOccupe++;
                }
            }
            int ind=0;
            res = new String[cptSaveOccupe];
            for(int i=0;i<tabSaveDispo.length;i++){
                if(!tabSaveDispo[i]){
                    res[ind++]="Sauvegarde"+(i+1);
                }
            }
            return res;
        }
    }
    /**
     * Enregistre la sauvegarde plateau dans le fichier save
     * @param sauvegardePlateau
     * @param save
     */
    public static void sauvegardePlateau(SauvegardePlateau sauvegardePlateau,String save){
        try{
            File file = new File("src/main/resources/save/"+save);
            if(file.exists()&&file.isFile()){
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                ObjectOutputStream oos = new ObjectOutputStream(fileOutputStream);
                oos.writeObject(sauvegardePlateau);
                oos.flush();
                oos.close();
                fileOutputStream.close();
                int i = Integer.parseInt(save.substring(save.length()-1));
                tabSaveDispo[--i]=false;
                resetFile(new File("src/main/resources/save/saveDispo"));
                writeSaveDispo();
            }else{
                throw new FileNotFoundException("Fichier pas trouvé");
            }
        }catch(FileNotFoundException fileNotFoundException){
            System.err.println("Le fichier n'est pas trouve ,  pas de sauvegarde effectue.");
            return;
        }catch(IOException ioe){
            System.err.println("Probleme dans l'ecriture pas de sauvegarde effectue.");
            ioe.printStackTrace();
            return;
        }
    }
    /**
     * Reinitialise le contenu du fichier pour le remettre a vide
     * @param file Fichier qu'est remis a vide
     */
    public static void resetFile(File file){
        try (PrintWriter pw = new PrintWriter(file)) {
            pw.print("");
            pw.flush();
            pw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    private static void writeSaveDispo(){
        try{
            File file=new File("src/main/resources/save/saveDispo");
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            ObjectOutputStream oos= new ObjectOutputStream(fileOutputStream);
            for(boolean value:tabSaveDispo){
                oos.writeBoolean(value);
                oos.flush();
            }
            oos.close();
        }catch(IOException ioe){
            ioe.printStackTrace();
        }
    }
    public static void ajoutSauvegardeSup(String save){
        if(!listSauvegardeSup.contains(save)){
            listSauvegardeSup.add(save);
        }
    }
    public static void supSauvegardeSup(String save){
        if(listSauvegardeSup.contains(save)){
            listSauvegardeSup.remove(save);
        }
    }
    public static void supSauvegarde(){
        for(String sauvegarde:listSauvegardeSup){
            resetFile(new File("src/main/resources/save/"+sauvegarde));
            tabSaveDispo[Integer.parseInt(sauvegarde.substring(sauvegarde.length()-1))-1]=true;
        }
        resetFile(new File("src/main/resources/saveDispo"));
        writeSaveDispo();
    }
    /*public static void main(String[] args) {
        resetFile(new File("src/main/resources/save/saveDispo"));
        tabSaveDispo = new boolean[5];
        for(int i=0;i<tabSaveDispo.length;i++){
            tabSaveDispo[i]=true;
        }
        writeSaveDispo();
    }*/

}
