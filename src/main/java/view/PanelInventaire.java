package src.main.java.view;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.util.Iterator;

import src.main.java.controller.Joueur;
import src.main.java.model.CarteObjectif;
import src.main.java.model.Couleur;
import src.main.java.model.InterPlateau;
import src.main.java.model.Plateau;

/**
 * La classe PanelInventaire représente un panneau graphique affichant l'inventaire du joueur.
 * Cette classe est utilisée pour afficher les cartes wagon, les cartes objectif, 
 * le pseudo du joueur, et le nombre de wagons restants.
 */
public class PanelInventaire extends JPanel {
    private JPanel mainPanel;
    private Image img;
    private JLabel nbW;
    private Image bleuW, rougeW, noirW, vertW, orangeW, blancW, jauneW, violetW, locoW;
    private JButton retour;
    private JPanel cartesPanel, pseudoPanel, pseudoWrapperPanel, objPanel;
    private JLabel pseudoLabel;

    /**
     * Constructeur pour créer un nouveau panneau d'inventaire.
     * Initialise les composants graphiques et charge les images nécessaires.
     *
     * @param plateau le plateau de jeu contenant les informations du joueur.
     * @param mf la fenêtre principale de l'application.
     */
    public PanelInventaire(Plateau plateau, MainFrame mf) {
        try {
            img = ImageIO.read(new File("src/main/resources/images/images/dialogue-box_Big.png"));
            bleuW = resizeImage(ImageIO.read(new File("src/main/resources/images/cards/eu_WagonCard_blue.png")), 115, 115);
            rougeW = resizeImage(ImageIO.read(new File("src/main/resources/images/cards/eu_WagonCard_red.png")), 115, 115);
            noirW = resizeImage(ImageIO.read(new File("src/main/resources/images/cards/eu_WagonCard_black.png")), 115, 115);
            vertW = resizeImage(ImageIO.read(new File("src/main/resources/images/cards/eu_WagonCard_green.png")), 115, 115);
            orangeW = resizeImage(ImageIO.read(new File("src/main/resources/images/cards/eu_WagonCard_orange.png")), 115, 115);
            blancW = resizeImage(ImageIO.read(new File("src/main/resources/images/cards/eu_WagonCard_white.png")), 115, 115);
            jauneW = resizeImage(ImageIO.read(new File("src/main/resources/images/cards/eu_WagonCard_yellow.png")), 115, 115);
            violetW = resizeImage(ImageIO.read(new File("src/main/resources/images/cards/eu_WagonCard_purple.png")), 115, 115);
            locoW = resizeImage(ImageIO.read(new File("src/main/resources/images/cards/eu_WagonCard_joker.png")), 115, 115);
        } catch (IOException ioe) {
            System.out.println("Image non trouvée");
        }

        mainPanel = new JPanel();
        mainPanel.setOpaque(false);
        mainPanel.setLayout(new BorderLayout());

        pseudoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        String pseudo = plateau.getCurrentJoueur().getPseudo();
        pseudoLabel = new JLabel();
        pseudoLabel.setText("Inventaire de " + pseudo + " :");
        pseudoLabel.setName("pseudo");
        pseudoPanel.add(pseudoLabel);
        pseudoPanel.setOpaque(false);

        cartesPanel = new JPanel();
        cartesPanel.setOpaque(false);
        afficherCartesWagon(cartesPanel, plateau);

        int nbWagon = plateau.getCurrentJoueur().getNmbWagon();
        nbW = new JLabel();
        nbW.setText("Nombre de wagons restants : " + nbWagon);
        nbW.setName("nbW");

        objPanel = new JPanel();
        objPanel.setOpaque(false);
        objPanel.setLayout(new BoxLayout(objPanel, BoxLayout.Y_AXIS));
        afficherCartesObjectifs(objPanel, plateau);

        retour = new JButton("Retour");
        retour.addActionListener(e -> mf.showJeu());

        pseudoWrapperPanel = new JPanel();
        pseudoWrapperPanel.setOpaque(false);
        pseudoWrapperPanel.setLayout(new BoxLayout(pseudoWrapperPanel, BoxLayout.Y_AXIS));
        pseudoWrapperPanel.add(Box.createVerticalStrut(20));
        pseudoWrapperPanel.add(pseudoPanel);

        mainPanel.add(pseudoWrapperPanel, BorderLayout.NORTH);
        mainPanel.add(cartesPanel, BorderLayout.WEST);
        mainPanel.add(nbW, BorderLayout.EAST);
        mainPanel.add(objPanel);
        mainPanel.add(retour, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    /**
     * Affiche les cartes wagon du joueur sur le panneau fourni.
     *
     * @param cartesPanel le panneau où les cartes wagon seront affichées.
     * @param plateau le plateau de jeu contenant les informations du joueur.
     */
    private void afficherCartesWagon(JPanel cartesPanel, Plateau plateau) {
        HashMap<Couleur, Image> imagesCartes = new HashMap<>();
        imagesCartes.put(Couleur.BLUE, bleuW);
        imagesCartes.put(Couleur.RED, rougeW);
        imagesCartes.put(Couleur.BLACK, noirW);
        imagesCartes.put(Couleur.GREEN, vertW);
        imagesCartes.put(Couleur.ORANGE, orangeW);
        imagesCartes.put(Couleur.WHITE, blancW);
        imagesCartes.put(Couleur.YELLOW, jauneW);
        imagesCartes.put(Couleur.PURPLE, violetW);
        imagesCartes.put(Couleur.JOKER, locoW);
    
        for (Couleur couleur : Couleur.values()) {
            int nombreCartes = plateau.getCurrentJoueur().nmbCarteOfCouleur(couleur);
            Image imageCarte = imagesCartes.get(couleur);
            JLabel labelCarte = new JLabel(new ImageIcon(imageCarte));
            JLabel labelNombre = new JLabel(String.valueOf(nombreCartes));
            labelNombre.setName(couleur.toString());
            cartesPanel.add(labelCarte);
            cartesPanel.add(labelNombre);
        }
        cartesPanel.setLayout(new GridLayout(3, 3, 10, 10));
    }

    /**
     * Affiche les cartes objectif du joueur sur le panneau fourni.
     *
     * @param objPanel le panneau où les cartes objectif seront affichées.
     * @param plateau le plateau de jeu contenant les informations du joueur.
     */
    private void afficherCartesObjectifs(JPanel objPanel, Plateau plateau) {
        if (plateau.getObj() != null) {
            // Ajout des cartes au joueur seulement la première fois
            if (plateau.getCurrentJoueur().getNmbCartesObjectifs() == 0) {
                try {
                    for (int i = 0; i < 3; i++) {
                        CarteObjectif co = plateau.getObj().pioche();
                        plateau.addCarteJoueur(co);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
    
            // Affichage des cartes dans le JPanel
            Iterator<CarteObjectif> iterator = plateau.getCurrentJoueur().iteratorCarteObjectif();
            while (iterator.hasNext()) {
                CarteObjectif carteObjectif = iterator.next();
                JLabel labelCarteObjectif = new JLabel();
                labelCarteObjectif.setText(carteObjectif.toString());
                labelCarteObjectif.setName("obj");
                if (carteObjectif.getSuccess()) {
                    labelCarteObjectif.setForeground(Color.GREEN);
                }
                objPanel.add(labelCarteObjectif);
            }
    
            objPanel.revalidate();
            objPanel.repaint();
        }
    }    

    /**
     * Redimensionne une image à la largeur et à la hauteur cibles.
     *
     * @param originalImage l'image originale à redimensionner.
     * @param targetWidth la largeur cible.
     * @param targetHeight la hauteur cible.
     * @return l'image redimensionnée.
     */
    private Image resizeImage(Image originalImage, int targetWidth, int targetHeight) {
        int newWidth;
        int newHeight;
        if (originalImage.getWidth(null) > originalImage.getHeight(null)) {
            newWidth = targetWidth;
            newHeight = (int) ((double) originalImage.getHeight(null) / originalImage.getWidth(null) * targetWidth);
        } else {
            newHeight = targetHeight;
            newWidth = (int) ((double) originalImage.getWidth(null) / originalImage.getHeight(null) * targetHeight);
        }
    
        BufferedImage resizedBufferedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resizedBufferedImage.createGraphics();
        g2d.drawImage(originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH), 0, 0, null);
        g2d.dispose();
    
        return resizedBufferedImage;
    }

    /**
     * Met à jour l'inventaire du joueur en fonction des informations du plateau et du joueur.
     *
     * @param plateau le plateau intermédiaire contenant les informations du jeu.
     * @param j le joueur dont l'inventaire doit être mis à jour.
     */
    public void UpdateInventaire(InterPlateau plateau, Joueur j) {
        if (j != null) {
            for (Couleur couleur : Couleur.values()) {
                //On suppose que le serveur n'a pas acces au serveur pas besoin de regarder si le client n'est pas null
                int nombreCartes = ModeLoader.isOnline() ? ModeLoader.getClient().getJoueur().nmbCarteOfCouleur(couleur):plateau.getCurrentJoueur().nmbCarteOfCouleur(couleur);
                Component[] components = cartesPanel.getComponents();
                for (Component component : components) {
                    if (component instanceof JLabel) {
                        JLabel label = (JLabel) component;
                        String name = component.getName();
                          
                        if (name != null && name.equals(couleur.toString())) {
                                label.setText(String.valueOf(nombreCartes));
                        }
                    }
                }
            }
            //Meme logique pour le pseudo que pour le nombre de carte par couleur
            String pseudo = ModeLoader.isOnline() ? ModeLoader.getClient().getJoueur().getPseudo() :plateau.getCurrentJoueur().getPseudo();
            pseudoLabel.setText("Inventaire de " + pseudo + " :");

            Plateau p = (Plateau)plateau;
            Component[] components = objPanel.getComponents();
            for (Component component : components) {
                if (component instanceof JLabel) {
                    String name = component.getName();
                    if (name != null && name.equals("obj")) {
                        objPanel.removeAll();
                        afficherCartesObjectifs(objPanel, p);
                    }
                }
            }
            
            // Mise à jour du label nbW
            int nbWagon = plateau.getCurrentJoueur().getNmbWagon();
            if (nbW == null) {
                nbW = new JLabel();
            }
            nbW.setText("Nombre de wagons restants : " + nbWagon);
            nbW.setName("nbW");
        }
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        if(img!=null){
            g.drawImage(img,0,0,this.getWidth(),this.getHeight(),null);
        }
    }
}
