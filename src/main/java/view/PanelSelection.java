package src.main.java.view;

import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;

import javax.imageio.ImageIO;
import javax.swing.*;

import src.main.java.model.Plateau;
import src.main.java.model.SauvegardePlateau;

public class PanelSelection extends JPanel {
    private Image backgroundImage;
    private DicePanel dicePanel;
    private JPanel boutonPanel;
    private JLabel labelJouer;
    private JButton boutonClassique, boutonRush, boutonQuitter,boutonTemps,boutonTour,boutonSauvegarde;
    private Plateau plateau;

    public PanelSelection(MainFrame mainFrame) {
        // Chargement de l'image de fond
        try {
            backgroundImage = ImageIO.read(new File("src/main/resources/images/images/dialogue-box_Big.png"));
        } catch (IOException e) {
            System.err.println("Image non trouvée");
        }

        // Création des composants
        plateau = mainFrame.getPlateau();
        labelJouer = new JLabel("Jouer :");
        boutonClassique = new JButton("Classique");
        boutonRush = new JButton("Rush");
        boutonQuitter = new JButton("Quitter");
        boutonTemps = new JButton("Temps");
        boutonTour = new JButton("Tour");
        boutonSauvegarde = new JButton("Sauvegarde");
        dicePanel = new DicePanel(plateau);
        dicePanel.setOpaque(false);

        boutonPanel = new JPanel();
        boutonPanel.add(boutonClassique);
        boutonPanel.add(boutonRush);
        boutonPanel.add(boutonTemps);
        boutonPanel.add(boutonTour);
        boutonPanel.add(boutonSauvegarde);
        boutonPanel.setOpaque(false);
        
        // Mise en place de la disposition verticale
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Ajout des composants au panel
        add(Box.createVerticalStrut(50));
        add(labelJouer);
        add(boutonPanel);
        add(dicePanel);
        add(Box.createVerticalStrut(70));
        add(boutonQuitter);

        // Gestion des événements des boutons
        boutonQuitter.addActionListener(e -> System.exit(0));
        boutonClassique.addActionListener(e -> {
            if (dicePanel.getRolled()) {
                plateau.setTurnOrder(dicePanel.getFtab_int());
                mainFrame.getPanelJeu().UpdateJoueur(plateau.getCurrentJoueur());
                mainFrame.showJeu();
                mainFrame.getPanelJeu().getJeu().ResetTimer();
            } else {
                JOptionPane.showMessageDialog(null, "Lancez les dés pour déterminer l'ordre de passage des joueurs.", "Erreur d'initialisation", JOptionPane.ERROR_MESSAGE);

            }
        });
        boutonRush.addActionListener(e -> {
            if (dicePanel.getRolled()) {
                plateau.setTurnOrder(dicePanel.getFtab_int());
                mainFrame.getPanelJeu().setRush();
                mainFrame.showJeu();
                mainFrame.getPanelJeu().getJeu().ResetTimer();
                if(ModeLoader.isOnline()&&ModeLoader.getClient()!=null){
                    mainFrame.getServeur().setTurnOrder(dicePanel.getFtab_int());
                }
            } else {
                JOptionPane.showMessageDialog(null, "Lancez les dés pour déterminer l'ordre de passage des joueurs.", "Erreur d'initialisation", JOptionPane.ERROR_MESSAGE);
            }
        });

        boutonTemps.addActionListener(e->{
            try{
                if(dicePanel.getRolled()){
                    String inputString = (String)JOptionPane.showInputDialog(this, "Combien de minutes voulez vous que la partie dure ? "
                    , "Choix nombre minutes", JOptionPane.QUESTION_MESSAGE, null, null, null);
                    Integer input = Integer.parseInt(inputString);
                    if(input!=null&&input>0){
                        ModeLoader.setTimeMax(input);
                        mainFrame.getPanelJeu().getJeu().setFonctionJeu(ModeLoader.getFonctionFin(ModeLoader.FONCTION_TEMPS));
                        plateau.setTurnOrder(dicePanel.getFtab_int());
                        mainFrame.getPanelJeu().UpdateJoueur(plateau.getCurrentJoueur());
                        mainFrame.showJeu();
                        mainFrame.getPanelJeu().getJeu().ResetTimer();
                    }else{
                        throw new Exception();
                    }

                }else{
                    JOptionPane.showMessageDialog(null, "Lancez les dés pour déterminer l'ordre de passage des joueurs.", "Erreur d'initialisation", JOptionPane.ERROR_MESSAGE);
                }
            }catch(Exception exception){
                JOptionPane.showMessageDialog(this, "Faites attention a bien tapes des entiers sur le nombre de minutes max","Erreur d'entre",JOptionPane.ERROR_MESSAGE);
            }
        });

        boutonTour.addActionListener(e ->{
            try{
                if(dicePanel.getRolled()){
                    String inputSring = (String)JOptionPane.showInputDialog(this, "Combien de tour voulez vous ????"
                    , "Choix nombre tour", JOptionPane.QUESTION_MESSAGE, null, null, 0);
                    Integer input = Integer.parseInt(inputSring);
                    if(input!=null && input>0){
                        ModeLoader.setNmbTourMax(input);
                        mainFrame.getPanelJeu().getJeu().setFonctionJeu(ModeLoader.getFonctionFin(ModeLoader.FONCTION_TOUR));
                        plateau.setTurnOrder(dicePanel.getFtab_int());
                        mainFrame.getPanelJeu().UpdateJoueur(plateau.getCurrentJoueur());
                        mainFrame.showJeu();
                        mainFrame.getPanelJeu().getJeu().ResetTimer();
                    }else{
                        throw new Exception("Probleme d'input");
                    }
                }else{
                    JOptionPane.showMessageDialog(null, "Lancez les dés pour déterminer l'ordre de passage des joueurs.", "Erreur d'initialisation", JOptionPane.ERROR_MESSAGE);
                }
            }catch(Exception exception){
                exception.printStackTrace();
                JOptionPane.showMessageDialog(this, "Faites attention a bien tapes des entiers superieurs a 0 sur le nombre de minutes max");
            }
        });

        boutonSauvegarde.addActionListener(ae -> {
            if(dicePanel.getRolled()){
                String []saveDispo=SauvegardePlateau.saveOccupe();
                String save = (String) JOptionPane.showInputDialog(this,
                        "Choissisez la sauvegarde que vous voulez relancer ",
                        "Fenetre choix save", JOptionPane.OK_OPTION, null, saveDispo, "");
                SauvegardePlateau sauvegardePlateau = loadSave(save);
                if(sauvegardePlateau != null){//On ne fait rien si ces null
                    mainFrame.setPlateau(sauvegardePlateau.getPlateau());
                    mainFrame.getPanelJeu().getJeu().ResetTimer();
                    mainFrame.getPanelJeu().getJeu().setFonctionJeu(sauvegardePlateau.getFonctionJeu());
                    this.plateau = sauvegardePlateau.getPlateau();
                    plateau.setTurnOrder(dicePanel.getFtab_int());
                    mainFrame.getPanelJeu().UpdateJoueur(plateau.getCurrentJoueur());
                    mainFrame.getPanelJeu().getJeu().setNmbTour(sauvegardePlateau.getNmbTour());
                    mainFrame.getPanelJeu().getJeu().setTimePaused(sauvegardePlateau.getTempsPlateau());
                    mainFrame.showJeu();
                    mainFrame.getPanelJeu().getJeu().ResetTimer();
                }
            }else{
                JOptionPane.showMessageDialog(null, "Lancez les dés pour déterminer l'ordre de passage des joueurs.", "Erreur d'initialisation", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
     private SauvegardePlateau loadSave(String save){
        if(save.equals("")||save.equals("Pas de sauvegarde possible")){
            JOptionPane.showMessageDialog(this, "Vous n'avez pas de sauvegarde disponible ", "Fenetre erreur sauvegarde", JOptionPane.OK_OPTION);
            return null;
        }else{
            File file = new File("src/main/resources/save/"+save);
            if(file.exists()&&file.isFile()){
                FileInputStream fis;
                try {
                    fis = new FileInputStream(file);
                    ObjectInputStream ois = new ObjectInputStream(fis);
                    SauvegardePlateau sauvegardePlateau = (SauvegardePlateau)ois.readObject();
                    ois.close();
                    return sauvegardePlateau;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException ioe){
                    ioe.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                return null;
            }else{
                return null;
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
