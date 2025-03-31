package src.main.java.model;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.*;
import javax.naming.NameNotFoundException;
import src.main.java.controller.Joueur;
import src.main.java.view.ModeLoader;

public class Plateau implements InterPlateau,Serializable{
    private final static int CONST_TUNNEL=3;
    /**
     *Liste de toutes les villes du plateau
     */
    private final ArrayList<Ville>listeVille;
    /**
     *Liste de toutes les lignes du plateau
     */
    private final ArrayList<Ligne>listeLigne;
    /**
     *Liste de toutes les cartes mise face decouverte
     */
    private final ArrayList<CarteWagon>listCarte;

    /**
     *Liste de tout les joueurs present sur la carte
     */
    private ArrayList<Joueur>listJoueur;
    private int indJoueur;
    private int nmbNextJoueur;//Nombre d'appel a nextJoueur
    private PiocheObjectif piocheObjectif;
    private PiocheWagon piocheWagon;
    private boolean tour;//Pour empecher que les tours se comptent constamment quand le premier joueur att 

    public Plateau(ArrayList<Ville>listeVilles,ArrayList<Ligne>listLigne,int nbmJoueur){
        this.listeVille=listeVilles;
        this.piocheObjectif=new PiocheObjectif();
        this.piocheWagon=new PiocheWagon();
        this.listeLigne=listLigne;
        this.listCarte=new ArrayList<CarteWagon>();
        this.listJoueur=new ArrayList<Joueur>();
        this.tour=true;
        for(int i=0;i<nbmJoueur;i++){
            listJoueur.add(new Joueur(45));
        }
        for(Couleur c:Couleur.values()){
            for(int i=0;i<7;i++){
                piocheWagon.ajouterCarte(new CarteWagon(c));
            }
        }
        piocheWagon.shuffle();
        for(Ligne ligne:listLigne){
            ligne.getVille1().ajoutLigne(ligne);
            ligne.getVille2().ajoutLigne(ligne);
        }
        indJoueur=0;
    }
    public void addCarteO(CarteObjectif co){
        this.piocheObjectif.addCarteObjetif(co);
    }
    /**
     * Fais piocher au current joueur une carte de la pioche wagon
     */
    public void piocheCarteWagon(){
        this.getCurrentJoueur().addCarte(piocheWagon());
    }
    /**
     * @return Renvoie le nombre de joueur dans la partie
     */
    public int getNmbJoueur(){
        return this.listJoueur.size();
    }
    public Joueur getCurrentJoueur(){
        return listJoueur.get(indJoueur);
    }
    public void nextJoueur(){
        listJoueur.get(indJoueur).setVilleCLick(null);
        indJoueur = (indJoueur+1)%listJoueur.size(); 
        tour=true;
        nmbNextJoueur++;
    }
    public int getNmbNextJoueur(){
        return this.nmbNextJoueur;
    }
    /**
     * Fais piocher au current joueur la carte a l'indice index
     * @param index l'indice de la carte a piocher
     */
    public void piocheCarteVisible(int index){
        this.getCurrentJoueur().addCarte(piocheWagon.getCarteWagon(index));
    }
    public void setTour(){
        this.tour=(!this.tour);
    }
    public boolean getTour(){
        return this.tour;
    }
    public Iterator<CarteWagon> iteratorCarteWagon(){
        return listCarte.iterator();
    }
    public Iterator<Ligne> iteratorLigne(){
        return listeLigne.iterator();
    }
    public Iterator<Ville>iteratorVille(){
        return listeVille.iterator();
    }
    public void addLigne(Ligne l){
        listeLigne.add(l);
    }
    public void addVille(Ville v){
        listeVille.add(v);
    }
    public boolean ligneAchetable(Ligne l,Joueur j){
        return j.achetable(l)&&l.isEmpty();
    }
    /**
     * Regarde toutes les lignes et renvoie toutes celle qui sont achetable par le joueur
     * @param j Prends un joueur j
     * @return Renvoie la liste de toutes les lignes achetable par le joueur j
     */
    public ArrayList<Ligne> constructable(Joueur j){
        ArrayList<Ligne>res=new ArrayList<Ligne>();
        for(Ligne l:listeLigne){
            if(ligneAchetable(l, j)){
                res.add(l);
            }
        }
        return res;
    }
    /**
     * Achete la ligne si elle est un tunnel
     * @param j Prends le joueur qui veut acheter la ligne
     * @param l La ligne que le joueur veut acheter
     */
    public void achatTunnel(Joueur j,Ligne l){
        ArrayList<CarteWagon>listCw=new ArrayList<CarteWagon>();
        if(this.canTunnel(j,listCw)){
            for(CarteWagon cw:listCw){
                j.jouerCarte(cw.getCouleur(), 1);
            }
            j.decreaseWagon(l.getNmbCases());
            l.setJoueur(j);
        }
    }
    public void achatLigne(Joueur j,Ligne l,Couleur c){
        achatLigne(j, l, c, l.getNmbCases());
    }
    public void achatLigne(Joueur j,Ligne l,Couleur c,int nmbCase){
        l.setJoueur(j);
        j.jouerCarte(c, nmbCase);
        for(int i=0;i<nmbCase;i++){//On rajoute les cartes utilise dans la pioche
            piocheWagon.ajouterCarte(new CarteWagon(c));
        }
    }
    /**
     * Fonction pour qu'un joueur achete une ligne
     * @param j Joueur qui achete la ligne
     * @param l Ligne achete 
     */
    public void achatLigne(Joueur j,Ligne l){
        achatLigne(j, l, l.getCouleur());
    }
    /**
     * Renvoie un plateau creer par fichier
     * @param f Prend un fichier en parametre
     * @return Renvoie un plateau genere a l'aide du fichier
     */
    public Iterator<Joueur> iteratorJoueur(){
        return listJoueur.iterator();
    }
    private int scoreJoueur(Joueur joueur){
    int totalScore=0;
    Iterator<CarteObjectif>itCarteO=joueur.iteratorCarteObjectif();
    while(itCarteO.hasNext()){
        totalScore+=calculLigneProprio(joueur,itCarteO.next());       
    }
    totalScore+= (3-joueur.getNmbGare())*4;
    return totalScore;
    }

    public void setCurrentJoueur(Joueur j){
        for(int i=0;i<listJoueur.size();i++){
            if(listJoueur.get(i)==j){
                this.indJoueur=i;
                return;
            }
        }
    }
    /**
     * Retire le joueur i de la partie en enlevant toutes les lignes et toutes les gares qu'il possedes
     * @param i Indice du joueur dans la liste listeJoueur
     */
    public void removeJoueur(int i){
        Joueur joueur = listJoueur.get(i);
        for(Ville ville:listeVille){
            if(ville.getJoueur()==joueur){
                ville.setGare(null);
            }
        }
        for(Ligne ligne:listeLigne){
            if(ligne.getJoueur()==joueur){
                ligne.setJoueur(null);
            }
        }
    }

    /**
     * Verifie pour le joueur j si ca carte objectif a été verifie ou non
     * @param joueur Prends un joueur
     * @param co La carte objetctif a verifie
     * @return REnvoie le score de la carte objectif si il a reussi a la valide ou le score en negatif de la carte si elle n'est pas validee
     */
    public int calculLigneProprio(Joueur joueur, CarteObjectif co) {
        if(CheminPlusLong.pathBeetweenTwoVille(co.getVille1(), co.getVille2(), joueur, listeVille)){
            co.setSuccess(true);
            return co.getPoint();
        }
        return -co.getPoint();

    }
    /**
     * @return Retourne une liste contenant tous les joueurs avec le celui qui a le plus haut score en tout premier puis le second a l'indice 1 etc...
     */
    public ArrayList<Map.Entry<Joueur,Integer>> classementsJoueur(){
        ArrayList<Map.Entry<Joueur,Integer>>listJoueurScore=new ArrayList<Map.Entry<Joueur,Integer>>();
        HashMap<Joueur,Integer>mapScore=new HashMap<Joueur,Integer>();
        for(Joueur joueur:listJoueur){
            //Calcul de toutes les cartes destination
            mapScore.put(joueur, scoreJoueur(joueur));
        }
        listLigneJoueurcompteSepareLigne(mapScore);
        Joueur maxCheminJoueur=CheminPlusLong.maxChemin(listJoueur, listeVille);
        mapScore.replace(maxCheminJoueur, mapScore.get(maxCheminJoueur)+10);
        while(!mapScore.isEmpty()){
            Map.Entry<Joueur, Integer>maxJoueur=null;
            for(Map.Entry<Joueur,Integer>mapEntry:mapScore.entrySet()){
                if(maxJoueur==null||mapEntry.getValue()>maxJoueur.getValue()){
                    maxJoueur=mapEntry;
                }
            }
            listJoueurScore.add(maxJoueur);
            mapScore.remove(maxJoueur.getKey());
        }
        return listJoueurScore;
    }
    private void listLigneJoueurcompteSepareLigne(HashMap<Joueur, Integer> mapScore) {
        for(Ligne ligne:listeLigne){
            if(!ligne.isEmpty()){
                Joueur tmp=ligne.getJoueur();
                mapScore.put(tmp,Ligne.cout(ligne)+mapScore.get(tmp));
            }
        }
    }
    public int getIndJoueur(){
        return this.indJoueur;
    }

    public void addCarteJoueur(CarteObjectif co) {
        this.getCurrentJoueur().addCarte(co);
    }
    
    @SuppressWarnings ("unchecked")
    /**
     * Parcours la liste de toutes les villes et renvoie un iterator de toutes les villes qui n'ont pas de gare
     * @return Renvoie toutes les villes qui n'ont pas de gare 
     */
    public ArrayList<Ville> itVilleGare(){
        ArrayList<Ville>listVilleGare=(ArrayList<Ville>)listeVille.clone();
        listVilleGare.removeIf(v1 -> v1.hasGare());
        return listVilleGare;
    }
    public void placeGare(Joueur j,Ville v){
        if(j.hasGare()&&!v.hasGare()){
            v.setGare(j);
            j.decreaseGare();
        }
    }
    public boolean canTunnel(Joueur j,ArrayList<CarteWagon>listCarteWagon){
        try {
            for(int i=0;i<CONST_TUNNEL;i++){
                CarteWagon cw=piocheWagon.pioche();
                if(!j.jouerCarte(cw.getCouleur(), 1)){
                    return false;
                }
                listCarteWagon.add(cw);
            }
            return true;
        } catch (NameNotFoundException e) {
            System.out.println("Pas assez de carte dans la pioche");
            return false;
        }
    }
    public CarteWagon piocheWagon(){
        try{
        return this.piocheWagon.pioche();
        }catch(NameNotFoundException name){
            return null;
        }
    }
    public void setPseudo(int i,String pseudo){
        listJoueur.get(i).setPseudo(pseudo);
    }

    public void setAvatarColor(int i, String c){
        listJoueur.get(i).setAvatarColor(c);
    }

    /**
     * Creer un plateau a partir d'un fichier de jeu 
     * @param f
     * @param nmb
     * @return
     */
    public static Plateau plateauFichier(File f,int nmb){
        ArrayList<Ville>listVille=new ArrayList<Ville>();
        ArrayList<Ligne>listLignes=new ArrayList<Ligne>();
        String buffer="";
        Scanner sc;
        int i=1;
        int j=0;
        try{
            sc=new Scanner(f);
            int width=sc.nextInt();
            int height=sc.nextInt();
            ModeLoader.loadBaseScreenSize(width, height);
            double ratioHeight=ModeLoader.getCurrentHeight()/ModeLoader.getBaseHeight();
            double ratioLength=ModeLoader.getCurrentLength()/ModeLoader.getBaseLength();
            buffer=sc.next();
            do{
                int x=(int)(sc.nextInt()*ratioLength);
                int y=(int)(sc.nextInt()*ratioHeight);
                listVille.add(new Ville(buffer, new ArrayList<Ligne>(), x, y));
                i++;
                sc.nextLine();
                buffer=sc.next();
            }while(!buffer.equals("###############"));
                String ville1;
                String ville2;
                int nbmCases;
                listVille.sort((Ville v1,Ville v2)->v1.getNom().compareTo(v2.getNom()));
                i++;
            do{
                ville1=sc.next();j++;
                ville2=sc.next();j++;
                nbmCases=sc.nextInt();j++;
                String couleur=sc.next();j+=2;
                boolean tunnel=sc.nextBoolean();
                boolean doubleLigne=sc.nextBoolean();
                int nmbLocomotives=sc.nextInt();
                if(doubleLigne){
                    //On suppose que les double ligne sont cote a cote dans le fichier
                    DoubleLigne dbLigne=new DoubleLigne(dicoSearch(ville1, listVille),dicoSearch(ville2, listVille),nbmCases,strToColor(couleur),tunnel,nmbLocomotives);
                    sc.nextLine();
                    i++;
                    ville1=sc.next();j++;
                    ville2=sc.next();j++;
                    nbmCases=sc.nextInt();j++;
                    couleur=sc.next();j+=2;
                    tunnel=sc.nextBoolean();
                    doubleLigne=sc.nextBoolean();
                    nmbLocomotives=sc.nextInt();
                    DoubleLigne dbLigne2=new DoubleLigne(dicoSearch(ville1, listVille), dicoSearch(ville2, listVille), nbmCases, strToColor(couleur), dbLigne,tunnel, nmbLocomotives);
                    listLignes.add(dbLigne);
                    listLignes.add(dbLigne2);
                }else{
                    listLignes.add(new Ligne(dicoSearch(ville1, listVille), dicoSearch(ville2, listVille), nbmCases,strToColor(couleur),tunnel,nmbLocomotives));
                }
                sc.nextLine();
                j=0;
                i++;
            }while(sc.hasNext());
            sc.close();
        }catch(FileNotFoundException fif){
            fif.printStackTrace();
            throw new Error();
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("Erreur de lecture de fichier ligne : "+i+" , "+j);
        }
        Plateau plateau=new Plateau(listVille, listLignes, nmb);
        plateau.addCarteObjectif(new File("src/main/resources/objectif.txt"));
        plateau.getObj().shuffle();
        return plateau;
    }
    public CarteObjectif piocheObjectif(){
        try{
            return this.piocheObjectif.pioche();
        }catch(Exception e){
            return null;
        }
    }
 
    private void addCarteObjectif(File file) {
        int i = 0; // Indice pour le debugging
        try (Scanner sc = new Scanner(file)) {
            while (sc.hasNextLine()) {
                i++;
                String line = sc.nextLine();
                String[] parts = line.split(" ");
                if (parts.length != 2) {
                    System.out.println("Erreur de format à la ligne " + i);
                    continue; // Passe à la ligne suivante si le format est incorrect
                }
                String[] villes = parts[0].split("_");
                if (villes.length != 2) {
                    System.out.println("Erreur de format des villes à la ligne " + i);
                    continue; // Passe à la ligne suivante si le format des villes est incorrect
                }
                String nomVille1 = villes[0];
                String nomVille2 = villes[1];
                
                Ville ville1 = villeDicoSearch(nomVille1);
                Ville ville2 = villeDicoSearch(nomVille2);
    
                if (ville1 == null) {
                    System.out.println("Ville " + nomVille1 + " non trouvée à la ligne " + i);
                    continue; // Passe à la ligne suivante si la première ville n'est pas trouvée
                }
    
                if (ville2 == null) {
                    System.out.println("Ville " + nomVille2 + " non trouvée à la ligne " + i);
                    continue; // Passe à la ligne suivante si la deuxième ville n'est pas trouvée
                }
    
                int poids = Integer.parseInt(parts[1]);
    
                CarteObjectif co = new CarteObjectif(ville1, ville2, false, poids);
                this.piocheObjectif.addCarteObjetif(co);
            }
        } catch (FileNotFoundException fileNotFoundException) {
            return;
        } catch (Exception e) {
            System.out.println("Erreur à la ligne " + i);
            e.printStackTrace();
        }
    }    
    
    private static Ville dicoSearch(String ville,ArrayList<Ville>villes){
    int debut = 0;
    int fin = villes.size() - 1;

        while (debut <= fin) {
            int milieu = (debut + fin) / 2;
            Ville villeMilieu = villes.get(milieu);
            int comparaison = ville.compareTo(villeMilieu.getNom());

            if (comparaison == 0) {
                // La ville a été trouvée
                return villeMilieu;
            } else if (comparaison < 0) {
                // La ville recherchée est avant celle du milieu, donc on recherche à gauche
                fin = milieu - 1;
            } else {
                // La ville recherchée est après celle du milieu, donc on recherche à droite
                debut = milieu + 1;
            }
        }

    // Si la ville n'est pas trouvée, on retourne null
    System.err.println("Erreur "+ville);
    return null;
    }
    public Ville villeDicoSearch(String villeNom){
        return dicoSearch(villeNom, listeVille);
    }
    private static Couleur strToColor(String str){
        switch (str) {
            case "GREEN":return Couleur.GREEN;
            case "RED": return Couleur.RED;
            case "BLUE":  return Couleur.BLUE;
            case "YELLOW": return Couleur.YELLOW;
            case "BLACK":  return Couleur.BLACK;
            case "PURPLE": return Couleur.PURPLE;
            case "WHITE": return Couleur.WHITE;
            case "ORANGE":return Couleur.ORANGE;
            default:      return Couleur.JOKER;       
        }
    }

    public PiocheObjectif getObj() {
        return piocheObjectif;
    }

    public PiocheWagon getWag() {
        return piocheWagon;
    }
    public Joueur getJoueur(int i){
        if(i<0||i>=listJoueur.size()){
            return null;
        }else{
            return listJoueur.get(i);
        }
    }

    public void setTurnOrder(int[] ftab) {
        // Vérification que ftab et listJoueur ont la même taille
        if (ftab.length != listJoueur.size()) {
            throw new IllegalArgumentException("La taille du tableau ftab doit correspondre à la taille de listJoueur.");
        }

        Integer[] sortedIndices = generateSortedIndices(ftab);

         // Création d'une liste temporaire pour stocker les joueurs dans le bon ordre
         List<Joueur> temp = new ArrayList<>(Collections.nCopies(listJoueur.size(), null));

        // Réorganisation des joueurs en fonction des indices triés
        for (int i = 0; i < listJoueur.size(); i++) {
            temp.set(i, listJoueur.get(sortedIndices[i]));            
        }

        // Remplacement de la liste de joueurs par la liste temporaire
        listJoueur.clear();
        listJoueur.addAll(temp);
    }

    public static Integer[] generateSortedIndices(int[] array) {
        // Créer un tableau d'indices
        Integer[] indices = new Integer[array.length];
        for (int i = 0; i < array.length; i++) {
            indices[i] = i ;
        }
        
        // Trier les indices en fonction des valeurs dans le tableau array
        Arrays.sort(indices, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return Integer.compare(array[o2], array[o1]);
            }
        });
        return indices;
    }
}
