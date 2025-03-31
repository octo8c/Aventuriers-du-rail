package src.main.java.model;
import java.util.*;
import src.main.java.controller.*;
public class CheminPlusLong {
    /**
     * @param listVilles
     * @return Trie toutes les villes dans la listeVilles
     */
    private static ArrayList<Ville> sortVille(ArrayList<Ville>listVilles){
        ArrayList<Ville>listVilleTrie=new ArrayList<Ville>();
        ArrayList<Ville>listVilleVisited=new ArrayList<Ville>();
        for(Ville v:listVilles){
            visit(v, listVilleVisited, listVilleTrie);
        }
        Collections.reverse(listVilleTrie);
        return listVilleTrie;
    }
    
    /**
     * Visites toutes les ville et l'ajoute a la listeVilleTrie
     * @param v
     * @param listVilleVisited
     * @param villeTrie
     */
    private static void visit(Ville v,ArrayList<Ville>listVilleVisited,ArrayList<Ville>villeTrie){
        if(!listVilleVisited.contains(v)){
            listVilleVisited.add(v);
            Iterator<Ligne>itLigne=v.getLigne();
            while(itLigne.hasNext()){
                visit(itLigne.next().getVille(v), listVilleVisited,villeTrie);
            }
            villeTrie.add(v);
        }
    }
    /**
     * Initialise les distances de toutes les villes
     * @param listVille la liste des villes
     * @return Renvoie une hashmap des villes ainsi que leur distance a 0
     */
    private static HashMap<Ville,Float> initMapDist(ArrayList<Ville>listVille){
        HashMap<Ville,Float>mapDist=new HashMap<Ville,Float>();
        for(Ville v:listVille){
            mapDist.put(v, Float.NEGATIVE_INFINITY);
        }
        mapDist.replace(listVille.get(0),0.0f);
        return mapDist;
    }
    @SuppressWarnings("unchecked")
    /**
     * @return Renvoie le joueur possedants le chemin le plus long
     */
    private static Float cheminPluLong(ArrayList<Ville>listeVille,Joueur joueur){
        ArrayList<Ville>listVill=(ArrayList<Ville>)listeVille.clone();
        listVill=sortVille(listVill);
        HashMap<Ville,Float>mapDist=initMapDist(listeVille);
        for(Ville v:listVill){
            Iterator<Ligne>iterator=v.getLigne();
            while(iterator.hasNext()){
                Ligne ligne=iterator.next();
                if(ligne.getJoueur()==joueur){
                    Ville v2=ligne.getVille(v);
                    mapDist.replace(v2,Math.max(mapDist.get(v2),mapDist.get(v)+ligne.getNmbCases()));
                }
            }
            if(v.getJoueur()==joueur){
                Ville v2=v.getLigneGare().getVille(v);
                mapDist.replace(v2,Math.max(mapDist.get(v2),mapDist.get(v)+v.getLigneGare().getNmbCases()));
            }
        }
        return maxMap(mapDist);
    }
    /**
     * @param listJoueur
     * @param listVille
     * @return Renvoie le joueur possedant le plus long chemin parmis listJoueur
     */
    public static Joueur maxChemin(ArrayList<Joueur>listJoueur,ArrayList<Ville>listVille){
        Joueur max=null;Float maxF=null;
        for(Joueur j:listJoueur){
            Float tmp=cheminPluLong(listVille,j);
            if(max==null||maxF<tmp){
                max=j;
                maxF=tmp;
            }
        }
        return max;
    }
    /**
     * @param mapVille
     * @return Renvoie la valeur la plus haute de mapVille
     */
    private static float maxMap(HashMap<Ville,Float>mapVille){
        Float max=null;
        for(Float f:mapVille.values()){
            if(max==null||max<f){
                max=f;
            }
        }
        return max;
    }

    /**
     * Verifie si il existe un chemin entre 2 ville appartenant au joueur
     * @param v1 Ville d'ou on part
     * @param v2 VIlle ou on doit arriver
     * @param j Joueur a verifier si il existe un chemin
     * @return Renvoie true si il existe bien un chemin entre les 2 villes qui appartient au joueur ou false sinon
     */
    public static boolean pathBeetweenTwoVille(Ville v1,Ville v2,Joueur j,ArrayList<Ville>listVille){
        ArrayList<Ville>visited=new ArrayList<Ville>();
        ArrayList<Ville>queue=new ArrayList<Ville>();
        queue.add(v1);
        while(queue.size()>0){
            Ville tmp=queue.remove(0);
            Iterator<Ligne>itLigne=tmp.getLigne();
            while(itLigne.hasNext()){
                Ligne tmpLigne=itLigne.next();
                if(tmpLigne.isProprietaire(j)){
                    Ville tmp2Ville=tmpLigne.getVille(tmp);
                    if(tmp2Ville==v2){
                        return true;
                    }else{
                        queue.add(tmp2Ville);
                    }
                }
            }
            if(tmp.getJoueur()==j){
                Ligne tmpLigne=tmp.getLigneGare();
                if(tmpLigne.isProprietaire(j)){
                    Ville tmp2Ville=tmpLigne.getVille(tmp);
                    if(tmp2Ville==v2){
                        return true;
                    }else{
                        queue.add(tmp2Ville);
                    }
                }
            }
            visited.add(tmp);
        }
        return false;
    }
}
