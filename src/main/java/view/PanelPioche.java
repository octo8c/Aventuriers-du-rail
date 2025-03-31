package src.main.java.view;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

import src.main.java.controller.Joueur;
import src.main.java.model.*;

public class PanelPioche extends JPanel {
    private Image img;
    private PiocheWagon pw;
    private PiocheObjectif po;
    private CarteWagon[] cartesVisibles;
    private JLabel[] carteLabels;
    private int count;
    private boolean done;
    private JLabel piocheObjectifLabel;
    private CarteObjectif[] dernieresCartesPiochees = null;
    private Joueur dernierJoueur;
    private JLabel[] typeCarteLabels;
    private boolean regardePioche;

    public PanelPioche(Plateau p, MainFrame mf) {
        try {
            img = ImageIO.read(new File("src/main/resources/images/images/dialogue-box_Big.png"));
        } catch (IOException e) {
           System.out.println("Image non trouvée");
        }
        dernierJoueur = p.getCurrentJoueur();

        pw = p.getWag();
        po = p.getObj();
        pw.piocherWagon();
        cartesVisibles = pw.getCartesVisible();
        typeCarteLabels = new JLabel[cartesVisibles.length];

        carteLabels = new JLabel[cartesVisibles.length];
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        count = 2; // Nombre de carte à piocher

        // Affichage des cartes wagons visibles
        for (int i = 0; i < cartesVisibles.length; i++) {
            // Création d'un JLabel pour afficher chaque carte wagon visible
            final int index = i;
            carteLabels[i] = new JLabel(new ImageIcon(getImagePath(cartesVisibles[i], i)));
            carteLabels[i].setOpaque(false);

             // Création d'un JLabel pour afficher le type de carte au-dessus de chaque image
            if (i == cartesVisibles.length - 1) {
                typeCarteLabels[i] = new JLabel("Pioche Cartes Wagons");
            } else {
                typeCarteLabels[i] = new JLabel(cartesVisibles[i] instanceof CarteWagon ? "Carte Wagon n°" + (i+1) : "Pioche Objectif");
            }
            typeCarteLabels[i].setOpaque(false);

            JPanel cartePanel = new JPanel();
            cartePanel.setLayout(new BoxLayout(cartePanel, BoxLayout.Y_AXIS));
            cartePanel.add(typeCarteLabels[i]);
            cartePanel.add(Box.createVerticalStrut(5));
            cartePanel.add(carteLabels[i]);
            cartePanel.setOpaque(false);

            add(cartePanel);

            carteLabels[i].addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    // Action à effectuer lorsque le joueur clique sur une carte
                    CarteWagon carte = cartesVisibles[index];
                    try {
                        mf.getPanelJeu().setPlay();
                        if (!done && count > 0 && !regardePioche) {
                            // Vérifier si la carte est un Joker
                            if (carte.getCouleur() == Couleur.JOKER) {
                                count -= 2; // Si oui, décrémenter de 2
                                if (count < 0) { // Vérifier si on a le droit de la piocher
                                    JOptionPane.showMessageDialog(null, "Vous ne pouvez pas piocher de locomotive après un wagon !", "Pioche d'une locomotive impossible", JOptionPane.WARNING_MESSAGE);
                                    count = 1;
                                    return;
                                }
                            } else {
                                count--; // Sinon, décrémenter de 1
                            }
                            JOptionPane.showMessageDialog(null, "Vous avez pioché une carte " + carte.getCouleur().getColorFr() + " !");
                            p.getCurrentJoueur().addCarte(carte);
                            // Mettre à jour l'affichage en retirant la carte piochée et en ajoutant une nouvelle carte visible
                            pw.remplacerCarteVisible(index);
                            // Mettre à jour l'affichage des cartes visibles
                            updateCarteLabels();
                            updatePiocheWagon();
                        } else {
                            // Afficher un message si le joueur a déjà pioché le nombre maximal de cartes
                            JOptionPane.showMessageDialog(null, "Vous avez déjà pioché le nombre maximal de cartes !", "Limite de pioche atteinte", JOptionPane.WARNING_MESSAGE);
                        }
                        done = (count == 0) ? true : false;
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
        }

        piocheObjectifLabel = new JLabel(new ImageIcon("src/main/resources/images/cardsV/eu_TicketBack.png"));
        piocheObjectifLabel.setOpaque(false);
        
        piocheObjectifLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                // Action à effectuer lorsque le joueur clique sur la pioche d'objectifs
                try {
                    mf.getPanelJeu().setPlay();
                    if (!done && count == 2) { // Vérifier si le joueur a le droit de piocher
                        // Piocher les trois premières cartes d'objectif en appelant la méthode piocher trois fois
                        CarteObjectif[] cartesPiochees = new CarteObjectif[3];
                        if (dernieresCartesPiochees == null) {
                            for (int i = 0; i < 3; i++) {
                                cartesPiochees[i] = po.pioche();
                            }
                            dernieresCartesPiochees = cartesPiochees;
                            afficherCartesPiochees(cartesPiochees, p);
                        } else {
                            afficherCartesPiochees(dernieresCartesPiochees, p);
                        }
                        regardePioche = true;
                    } else {
                        // Afficher un message si le joueur a déjà pioché des objectifs ce tour
                        JOptionPane.showMessageDialog(null, "Vous avez déjà pioché ce tour !", "Pioche d'objectifs impossible", JOptionPane.WARNING_MESSAGE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        JLabel piocheLabel = new JLabel();
        piocheLabel.setText("Pioche Cartes Objectifs");

        // Créer un JPanel pour contenir piocheObjectifLabel
        JPanel piochePanel = new JPanel();
        piochePanel.setLayout(new BoxLayout(piochePanel, BoxLayout.Y_AXIS));
        // Ajouter piocheObjectifLabel au panneau
        piochePanel.add(piocheLabel);
        piochePanel.add(Box.createVerticalStrut(5)); // Ajouter un espace vertical
        piochePanel.add(piocheObjectifLabel);
        piochePanel.setOpaque(false);

        // Ajout d'un bouton "Retour"
        JButton retourButton = new JButton("Retour");
        retourButton.addActionListener(e -> mf.showJeu());
        retourButton.setOpaque(false);

        add(piochePanel);
        add(retourButton);
    }

    // Méthode pour obtenir le chemin d'accès à l'image en fonction de la couleur de la carte
    private String getImagePath(CarteWagon carte, int i) {
        String couleur = carte.getCouleur().toString().toLowerCase();
        if (i == cartesVisibles.length - 1) {
            return "src/main/resources/images/cardsV/carte-wagon.png";
        } else {
        return "src/main/resources/images/cardsV/eu_WagonCard_" + couleur + ".png";
        }
    }

    public void updateCarteLabels() {
        CarteWagon[] cartesVisibles = pw.getCartesVisible();
        for (int i = 0; i < cartesVisibles.length; i++) {
            carteLabels[i].setIcon(new ImageIcon(getImagePath(cartesVisibles[i], i)));
            carteLabels[i].setOpaque(false);
        }
    }

    private void afficherCartesPiochees(CarteObjectif[] cartes, Plateau p) {
        boolean selectionValide = false;
        while (!selectionValide) {
            // Création d'un tableau de JCheckBox pour chaque carte
            JCheckBox[] checkBoxes = new JCheckBox[cartes.length];
            JPanel panel = new JPanel(new GridLayout(0, 1));
    
            // Ajout de chaque carte avec une case à cocher au panneau
            for (int i = 0; i < cartes.length; i++) {
                checkBoxes[i] = new JCheckBox(cartes[i].toString());
                panel.add(checkBoxes[i]);
            }
    
            // Affichage du panneau dans une boîte de dialogue
            int result = JOptionPane.showConfirmDialog(null, panel, "Sélectionnez vos cartes", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                // Traitement des cartes sélectionnées
                int count = 0;
                for (int i = 0; i < cartes.length; i++) {
                    if (checkBoxes[i].isSelected()) {
                        count++;
                    }
                }
                // Vérification du nombre de cartes sélectionnées
                if (count < 1 || count > 3) {
                    JOptionPane.showMessageDialog(null, "Veuillez sélectionner entre 1 et 3 cartes.", "Erreur de sélection", JOptionPane.ERROR_MESSAGE);
                } else {
                    // Récupération des cartes sélectionnées et traitement
                    for (int i = 0; i < cartes.length; i++) {
                        if (checkBoxes[i].isSelected()) {
                            p.addCarteJoueur(cartes[i]);
                        } else {
                            po.ajoutFondPioche(cartes[i]);
                        }
                    }
                    selectionValide = true;
                    done = true;
                }
            } else {
                // Si le joueur clique sur "Annuler" ou ferme la boîte de dialogue, quitter la boucle
                return;
            }
        }
    }

    public void UpdatePioche(InterPlateau p, Joueur j) {
        Plateau plateau = (Plateau) p;
        if (j != dernierJoueur || plateau.getNmbJoueur() == 1) {
            done = false;
            count = 2;
            dernierJoueur = p.getCurrentJoueur();
        }
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        if(img!=null){
            g.drawImage(img,0,0,this.getWidth(),this.getHeight(),null);
        }
    }

    public void setPlateau(Plateau plateau2) {
        this.po=plateau2.getObj();
        this.pw=plateau2.getWag();
        this.cartesVisibles=pw.getCartesVisible();
        this.dernieresCartesPiochees=new CarteObjectif[3];
        try{
            for (int i = 0; i < 3; i++) {
                dernieresCartesPiochees[i]=po.pioche();
            }
        }catch(Exception e){
            //Plu de carte voir ce qu'on fait 
        }
        this.updateCarteLabels();
    }

    private void updatePiocheWagon() {
        if (pw.deuxLocomotives()) {
            JOptionPane.showMessageDialog(null, "Il y a plus d'une locomotive face visible. Elles vont toutes être défaussées.");
            for (CarteWagon carte : cartesVisibles) {
                pw.ajoutFondCarte(carte);
            }
            pw.piocherWagon();
        }
        updateCarteLabels();
    }

}
