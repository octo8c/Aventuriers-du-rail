package src.main.java.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import src.main.java.controller.Joueur;
import src.main.java.model.*;
import javax.swing.*;
import javax.swing.event.MouseInputListener;

public class PanelJeu extends JPanel implements ActionListener, MouseInputListener {
    private final JButton boutonPioche;
    private final JButton boutonInventaire;
    private final JButton boutonSauvegarde;
    private InterPlateau interPlateau;
    private final JLabel labelTour;
    private final JLabel labelTime;
    private final JLabel labelJoueur;
    private final JPanel panelLabel;
    private final JPanel panelBouton;
    private final JPanel mainPanel;
    private final JButton boutonNextJoueur;
    private final Jeu jeu;
    private final MainFrame mf;
    private boolean play;
    private boolean selectionLigneGare;
    private Image img;
    private Joueur dernierJoueur = null;
    private Timer timer;
    private final int tourDuration = 20000; // 20 secondes max
    private boolean rush;

    public PanelJeu(Plateau plateau, MainFrame mf) {
        this.play = true;
        this.mf = mf;
        this.rush = false;
        this.timer = new Timer(tourDuration, this);
        this.labelTime = new JLabel();
        this.labelTour = new JLabel("NOMBRE DE TOUR : 1");
        labelTime.setForeground(Color.WHITE);
        labelTour.setForeground(Color.WHITE);
        this.labelJoueur = new JLabel();

        this.panelLabel = new JPanel();

        this.mainPanel = new JPanel();
        this.mainPanel.setLayout(new BorderLayout());
        this.mainPanel.setOpaque(false);

        this.panelBouton = new JPanel();
        this.panelBouton.setOpaque(false);
        this.panelBouton.setLayout(new GridBagLayout());

        this.boutonNextJoueur = new JButton("Next Joueur");
        this.boutonNextJoueur.setOpaque(false);

        this.boutonSauvegarde = new JButton("Sauvegarde");
        this.boutonSauvegarde.setOpaque(false);

        this.boutonInventaire = new JButton("INVENTAIRE");
        this.boutonInventaire.setOpaque(false);

        this.boutonPioche = new JButton("PIOCHE");
        this.boutonPioche.setOpaque(false);

        startTimer();

        JButton boutonFinDeJeu = new JButton("FIN DE JEU(BOUTON TEMPORAIRE)");
        labelJoueur.setName("Avatar");
        // Ajout de l'image en fond
        try {
            this.img = javax.imageio.ImageIO.read(new File("src/main/resources/EuropeSatelite.png"));
        } catch (IOException ioe) {
            ioe.printStackTrace();
            img = null;
        }
        // Creation du plateau
        this.interPlateau = plateau;
        jeu = new Jeu(plateau, mf, ModeLoader.getFonctionFin(10));

        this.boutonNextJoueur.addActionListener(ae -> {
            UpdateJoueur(interPlateau.getCurrentJoueur());
            if(ModeLoader.isOnline()){
                ModeLoader.getClient().sendPackage(plateau);
                noPlay();
            } else {
                interPlateau.nextJoueur();
                UpdateJoueur(interPlateau.getCurrentJoueur());
                play();
            }
        });
        GridBagConstraints gc = new GridBagConstraints();
        // Ajout de tout les boutons dans le panelButton
        gc.gridx = 0;
        gc.gridy = 1;
        gc.anchor = GridBagConstraints.NORTHWEST;
        gc.insets = new Insets(2, 0, 0, 2);
        panelBouton.add(boutonInventaire, gc);
        gc.gridx = 0;
        gc.gridy = 2;
        gc.anchor = GridBagConstraints.NORTHWEST;
        gc.insets = new Insets(2, 0, 0, 2);
        gc.weightx = 1;
        gc.weighty = 1;
        panelBouton.add(boutonPioche, gc);
        gc.gridx = 0;
        gc.gridy = 3;
        gc.anchor = GridBagConstraints.NORTHWEST;
        gc.insets = new Insets(2, 0, 0, 2);
        gc.weightx = 2;
        gc.weighty = 2;
        panelBouton.add(boutonFinDeJeu, gc);
        gc.gridx = 0;
        gc.gridy = 4;
        gc.anchor = GridBagConstraints.NORTHWEST;
        gc.insets = new Insets(2, 0, 0, 2);
        gc.weightx = 3;
        gc.weighty = 3;
        panelBouton.add(boutonNextJoueur, gc);
        gc.gridx = 0;
        gc.gridy = 5;
        gc.anchor = GridBagConstraints.NORTHWEST;
        gc.insets = new Insets(2, 0, 0, 2);
        gc.weightx = 4;
        gc.weighty = 4;
        if (!ModeLoader.isOnline()) {
            // On desactive la sauvegarde lorsqu'on est en ligne
            panelBouton.add(boutonSauvegarde, gc);
        } else {
            if (ModeLoader.getClient() != null) {
                JButton buttonDeco = new JButton("Quitter la partie");
                buttonDeco.setOpaque(false);
                buttonDeco.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent ae) {
                        int rep = JOptionPane.showConfirmDialog(PanelJeu.this,
                                "Etes vous sur de vouloir quitter la partie ?", "Fenetre confirmation quitter",
                                JOptionPane.YES_NO_OPTION);
                        if (rep == 0) {
                            ModeLoader.getClient().sendQuitting();
                            ModeLoader.getClient().disconnect();
                            jeu.stopThread();
                            PanelJeu.this.mf.dispose();
                        }
                    }
                });
                panelBouton.add(buttonDeco, gc);
            } else {
                JButton boutton = new JButton("Fermer le serveur");
                boutton.setOpaque(false);
                boutton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent arg0) {
                        mf.getServeur().stopRunning();
                        mf.getServeur().disconnect();
                        jeu.stopThread();
                        mf.dispose();
                    }

                });
                panelBouton.add(boutton, gc);
            }
        }
        // Ajout des action implementes par les bouton
        boutonInventaire.addActionListener(this);
        boutonPioche.addActionListener(this);
        boutonFinDeJeu.addActionListener(ae -> {
            Plateau p = mf.getPlateau();
            FrameClassement frameClassement = new FrameClassement(mf, p);
            frameClassement.setVisible(true);
        });
        
        this.boutonSauvegarde.addActionListener(ae ->{savePlateau();});
        // Ajout de tout les labels
        panelLabel.add(labelTour);
        panelLabel.add(labelTime);
        // Ajout du panel bouton
        panelBouton.setAlignmentX(LEFT_ALIGNMENT);
        panelBouton.setAlignmentY(TOP_ALIGNMENT);
        mainPanel.add(panelBouton, BorderLayout.WEST);
        mainPanel.setOpaque(false);
        panelLabel.setOpaque(false);
        // Panel a show !!!
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(mainPanel,BorderLayout.NORTH);
        panel.add(panelLabel,BorderLayout.WEST);
        panel.setOpaque(false);
        this.setLayout(new BorderLayout());
        add(panel, BorderLayout.NORTH);
        addMouseMotionListener(this);
        addMouseListener(this);
        mf.revalidate();
    }

    /**
     * Mets a jour l'image du current joueur pour indiquer a quel joueur ces le tour
     * @param currentJoueur le joueur a affiche
     */
    public void UpdateJoueur(Joueur currentJoueur) {
        if (currentJoueur != null && currentJoueur != dernierJoueur) {
            dernierJoueur = currentJoueur;
            ImageIcon icon = new ImageIcon("src/main/resources/images/images/symbole-"+ currentJoueur.getAvatarColor() +".png");
            labelJoueur.setText(currentJoueur.getPseudo() + " " + currentJoueur.getAvatarColor());
            JOptionPane.showMessageDialog(null, "C'est à " + currentJoueur.getPseudo() + " de jouer.", "Message", JOptionPane.INFORMATION_MESSAGE, icon);
            restartTimer();
        }
    }

    public void setRush() {
        rush = true;
    }

    private void startTimer() {
        timer.start();
    }

    /**
     * Remets a 0 le timer
     */
    private void restartTimer() {
        timer.restart();
        
    }

    /**
     * Desactive la pioche
     */
    public void setPiocheOff() {
        this.boutonPioche.setEnabled(false);
    }

    /**
     * Desactive tout les bouton pour empecher le joueur de jouer
     */
    public void noPlay() {
        this.play = false;
        this.boutonPioche.setEnabled(false);
    }

    /**
     * Permets au joueur de jouer
     */
    public void play() {
        this.play = true;
        this.boutonPioche.setEnabled(true);
        this.boutonInventaire.setEnabled(true);
    }

    public void setSelectionLigneGare() {
        this.selectionLigneGare = true;
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == boutonInventaire) {
            mf.getPanelInventaire().UpdateInventaire(interPlateau, interPlateau.getCurrentJoueur());
            mf.showInventaire();
        } else if (ae.getSource() == boutonPioche) {
            mf.getPanelPioche().UpdatePioche(interPlateau, interPlateau.getCurrentJoueur());
            mf.showPioche();
        } else if (ae.getSource() == timer && rush) {
            interPlateau.nextJoueur();
            UpdateJoueur(interPlateau.getCurrentJoueur());
            play();
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (this.img != null) {
            g.drawImage(img, 0, 0, this.getWidth(), this.getHeight(), null);
        }
        draw((Graphics2D) g);
    }

    /**
     * La methode qui dessine tout les villes et les lignes en 2 en les parcourant toutes via un iterator
     * @param g2d
     */
    public void draw(Graphics2D g2d) {
        Iterator<Ligne> itLigne = interPlateau.iteratorLigne();
        g2d.setStroke(new BasicStroke(ViewLoader.getLineSize()));
        while (itLigne.hasNext()) {
            Ligne tmpLigne = itLigne.next();
            g2d.setColor(tmpLigne.getCouleur().couleurToColor());
            if (tmpLigne instanceof DoubleLigne) {
                drawDoubleLigne(g2d, (DoubleLigne) tmpLigne);
            } else {
                drawLigne(tmpLigne, g2d);
            }
        }
        g2d.setStroke(new BasicStroke(1));
        Iterator<Ville> itVille = interPlateau.iteratorVille();
        while (itVille.hasNext()) {
            Ville tmpVille = itVille.next();
            drawVille(tmpVille, g2d);
            drawText(tmpVille, g2d);
        }
    }

    /**
     * Dessine les villes sur l'interface graphique
     * @param ville la ville dessine
     * @param g2d
     */
    private void drawVille(Ville ville, Graphics2D g2d) {
        g2d.setColor(Color.GRAY);
        g2d.draw(ville);
        g2d.fill(ville);
        g2d.setStroke(new BasicStroke(2));
        g2d.setColor(Color.BLACK);
        g2d.draw(ville);
        g2d.setStroke(new BasicStroke(1));
        if (ville.hasGare()) {
            BufferedImage gare = ville.getJoueur().getGare();
            if (gare != null) {
                g2d.drawImage(gare, ville.x(), ville.y(), ViewLoader.getRaduisVille(), ViewLoader.getRaduisVille(),
                        null);
            }
        }

    }

    /**
     * Dessine le nom des ville sur l'interface graphique
     * @param ville
     * @param g
     */
    private void drawText(Ville ville, Graphics g) {
        g.setColor(Color.WHITE);
        String text = ville.getNom();
        g.setFont(new Font(null, 0, 11));
        g.drawString(text, ville.x() - ViewLoader.getRaduisVille() / 2, ville.y() - ViewLoader.getRaduisVille() / 2);
    }

    /**
     * Dessine les lignes simple sur le panelJeu
     * @param ligne
     * @param g2d
     */
    private void drawLigne(Ligne ligne, Graphics2D g2d) {
        int distance = ligne.distance();
        int diviseSegment = distance / ViewLoader.getRaduisVille();
        Point[] p = ligne.getPoints(diviseSegment);

        Point[] pAff = p.length != 3 ? ViewLoader.getPoints(ligne.getNmbCases() * 2, p[1], p[p.length - 2])
                : ViewLoader.getPoints(ligne.getNmbCases() * 2, p[0], p[p.length - 1]);
        int nmbLocomotives = ligne.getNmbLocomotives();
        if (ligne.isTunnel()) {
            drawContour(g2d, pAff, ligne.getCouleur(), Color.BLACK, ligne.getNmbCases());
        } else {
            for (int i = 0; i < ligne.getNmbCases() * 2; i += 2) {
                if (nmbLocomotives > 0) {
                    drawCaseContour(g2d, pAff[i], pAff[i + 1], Color.RED, ligne.getCouleur());
                    nmbLocomotives--;
                } else {
                    g2d.drawLine(pAff[i].x, pAff[i].y, pAff[i + 1].x, pAff[i + 1].y);
                }
            }
        }
        if (ligne.getJoueur() != null) {
            drawWagon(g2d, pAff, ViewLoader.getIndBuffImage(ligne), ViewLoader.getWagon(ligne), ligne.getNmbCases());
        }

    }
    /**
     * Dessine une double ligne ainsi que ces wagon si un joueur la possede
     * @param g2d
     * @param doubleLigne
     */
    private void drawDoubleLigne(Graphics2D g2d,DoubleLigne doubleLigne){
        Point point1 = ViewLoader.pointAfficheWagon(doubleLigne.getVille1().getPoint(), doubleLigne.getVille2().getPoint(),-doubleLigne.getCote() ,true);
        Point point2 = ViewLoader.pointAfficheWagon(doubleLigne.getVille1().getPoint(), doubleLigne.getVille2().getPoint(),-doubleLigne.getCote(), false);
        Point[]points = ViewLoader.getPoints(doubleLigne.getNmbCases()*2, point1, point2);
        Point[]pAff = points.length!=3?ViewLoader.getPoints(doubleLigne.getNmbCases()*2,points[1], points[points.length-2]):ViewLoader.getPoints(doubleLigne.getNmbCases()*2, points[0], points[points.length-1]);
        int nmbLocomotives = doubleLigne.getNmbLocomotives();
        if (doubleLigne.isTunnel()) {
            drawContour(g2d, pAff, doubleLigne.getCouleur(), Color.BLACK, doubleLigne.getNmbCases());
        } else {
            for (int i = 0; i < doubleLigne.getNmbCases() * 2; i += 2) {
                if (nmbLocomotives > 0) {
                    drawCaseContour(g2d, pAff[i], pAff[i + 1], Color.RED, doubleLigne.getCouleur());
                    nmbLocomotives--;
                } else {
                    g2d.drawLine(pAff[i].x, pAff[i].y, pAff[i + 1].x, pAff[i + 1].y);
                }
            }
        }
        if (doubleLigne.getJoueur() != null) {
            drawWagon(g2d, pAff, ViewLoader.getIndBuffImage(doubleLigne), ViewLoader.getWagon(doubleLigne),
                    doubleLigne.getNmbCases());
        }
    }
    /**
     * Dessine les wagon si la ligne en possede
     * @param g2d 
     * @param pAff les points de tout les rectangle
     * @param indImage l'indice de l'image a affiche 
     * @param bufferedImage l'image
     * @param nmbCases le nombre de cases de la ligne
     */
    private void drawWagon(Graphics2D g2d, Point[] pAff, int indImage, BufferedImage bufferedImage, int nmbCases) {
        switch (indImage) {
            case 0:
                // L'image horrizontal
                ViewLoader.reverseSort(pAff);
                // affichePoint(pAff);
                for (int i = 0; i < nmbCases * 2; i += 2) {
                    Point point = ViewLoader.pointAfficheWagon(pAff[i], pAff[i + 1], indImage, true);
                    double widhtMax = Math.max(Math.abs(pAff[i].x - pAff[i + 1].x) * 4 / 3,
                            ViewLoader.getDoubleLineSize());
                    double heighMax = Math.max(Math.abs(pAff[i].y - pAff[i + 1].y) * 4 / 3,
                            ViewLoader.getDoubleLineSize());
                    g2d.drawImage(bufferedImage, point.x, point.y, (int) widhtMax, (int) heighMax, null);

                }
                break;
            case 1:
                ViewLoader.reverseSort(pAff);
                for (int i = 0; i < nmbCases * 2; i += 2) {
                    double widhtMax = Math.max(Math.abs(pAff[i].x - pAff[i + 1].x) * 4 / 3,
                            ViewLoader.getDoubleLineSize());
                    double heighMax = Math.max(Math.abs(pAff[i].y - pAff[i + 1].y) * 4 / 3,
                            ViewLoader.getDoubleLineSize());
                    Point point = ViewLoader.pointAfficheWagon(pAff[i], pAff[i + 1], indImage, true);
                    g2d.drawImage(bufferedImage, point.x, point.y, (int) widhtMax, (int) heighMax, null);
                }
                break;
            case 2:
                // Toutes les images en diagnoal de la gauche vers la droite
                ViewLoader.reverseSort(pAff);
                for (int i = 0; i < nmbCases * 2; i += 2) {
                    double widhtMax = Math.max(Math.abs(pAff[i].x - pAff[i + 1].x) * 4 / 3,
                            ViewLoader.getDoubleLineSize());
                    double heighMax = Math.max(Math.abs(pAff[i].y - pAff[i + 1].y) * 4 / 3,
                            ViewLoader.getDoubleLineSize());
                    Point point = ViewLoader.pointAfficheWagon(pAff[i], pAff[i + 1], indImage, false);
                    g2d.drawImage(bufferedImage, point.x, point.y - (int) heighMax, (int) widhtMax, (int) heighMax,
                            null);
                }
                break;
            case 3:
                // Image vertical
                ViewLoader.reverseSort(pAff);
                for (int i = 0; i < nmbCases * 2; i += 2) {
                    double widhtMax = Math.max(Math.abs(pAff[i].x - pAff[i + 1].x) * 4 / 3,
                            ViewLoader.getDoubleLineSize());
                    double heighMax = Math.max(Math.abs(pAff[i].y - pAff[i + 1].y) * 4 / 3,
                            ViewLoader.getDoubleLineSize());
                    Point point = ViewLoader.pointAfficheWagon(pAff[i], pAff[i + 1], indImage, true);
                    g2d.drawImage(bufferedImage, point.x, point.y, (int) widhtMax, (int) heighMax, null);
                }
                break;
            case 4:
                ViewLoader.reverseSort(pAff);

                for (int i = 0; i < nmbCases * 2; i += 2) {
                    double widhtMax = Math.max(Math.abs(pAff[i].x - pAff[i + 1].x) * 4 / 3,
                            ViewLoader.getDoubleLineSize());
                    double heighMax = Math.max(Math.abs(pAff[i].y - pAff[i + 1].y) * 4 / 3,
                            ViewLoader.getDoubleLineSize());
                    Point point = ViewLoader.pointAfficheWagon(pAff[i], pAff[i + 1], indImage, true);
                    g2d.drawImage(bufferedImage, point.x, point.y, (int) widhtMax, (int) heighMax, null);

                }
                break;
            case 5:
                for (int i = 0; i < nmbCases * 2; i += 2) {
                    double widhtMax = Math.max(Math.abs(pAff[i].x - pAff[i + 1].x) * 4 / 3,
                            ViewLoader.getDoubleLineSize());
                    double heighMax = Math.max(Math.abs(pAff[i].y - pAff[i + 1].y) * 4 / 3,
                            ViewLoader.getDoubleLineSize());
                    Point point = ViewLoader.pointAfficheWagon(pAff[i], pAff[i + 1], indImage, true);
                    g2d.drawImage(bufferedImage, point.x, point.y, (int) widhtMax, (int) heighMax, null);
                }
                break;
        }
    }

    /**
     * Methode pour draw les ligne qui serait des tunnel
     * 
     * @param g2d
     * @param ligne
     */
    private void drawContour(Graphics2D g2d, Point[] pA, Couleur couleurLigne, Color color, int nmbCases) {

        for (int i = 0; i < nmbCases * 2; i += 2) {
            drawCaseContour(g2d, pA[i], pA[i + 1], color, couleurLigne);
        }
    }

    /**
     * Dessine les contour d'une case de la couleur color entre les points p1 et p2
     * @param g2d
     * @param p1
     * @param p2
     * @param color
     * @param couleurLigne
     */
    private void drawCaseContour(Graphics2D g2d, Point p1, Point p2, Color color, Couleur couleurLigne) {
        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(ViewLoader.getLineSize()));
        g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
        g2d.setColor(couleurLigne.couleurToColor());
        g2d.setStroke(new BasicStroke(ViewLoader.getLineSize() - 3));
        g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
    }
    public void disableOfflineButton(boolean bool) {
        this.boutonNextJoueur.setEnabled(bool);
    }

    public JLabel getLabelTour() {
        return labelTour;
    }

    public JLabel getLabelTime() {
        return labelTime;
    }

    public JLabel getLabelJoueur() {
        return labelJoueur;
    }

    /**
     * S'occupe de gerer l'action du joueur en fonction de si ces son premier click
     * et de verifier a quelle ville correspond le click
     * @param me
     */
    private void selectionLigne(MouseEvent me) {
        Iterator<Ville> itVille = interPlateau.iteratorVille();
        while (itVille.hasNext()) {
            Ville ville = itVille.next();
            if (ville.contains(me.getPoint())) {
                if (this.interPlateau.getCurrentJoueur().getVilleClick() == null) {
                    this.interPlateau.getCurrentJoueur().setVilleCLick(ville);
                } else {
                    Ligne ligne = interPlateau.getCurrentJoueur().getVilleClick().ligneBetweenVille(ville);
                    if (ligne != null) {
                        if (!ligne.isBuyable(interPlateau.getCurrentJoueur())) {
                            JOptionPane.showMessageDialog(this,
                                    "Vous ne pouvez pas achetez la ligne si dessous ! verifiez votre inventaire/que vous ne possedez pas l'autre ligne si ces une double ligne",
                                    "Erreur Selection", JOptionPane.OK_OPTION);
                        }
                        String[] textPossible = { "Oui", "Non" };
                        int retour = JOptionPane.showOptionDialog(this,
                                "Etes vous sur de vouloir prendre la ligne de " + ligne, "Fenetre choix GARE",
                                JOptionPane.OK_OPTION, JOptionPane.PLAIN_MESSAGE, null, textPossible, textPossible[0]);
                        if (retour == 0) {
                            if (ligne.getVille1().getJoueur() == interPlateau.getCurrentJoueur()) {
                                ligne.getVille1().setLigneGare(ligne);
                            } else if (ligne.getVille2().getJoueur() == interPlateau.getCurrentJoueur()) {
                                ligne.getVille2().setLigneGare(ligne);
                            } else {
                                JOptionPane.showMessageDialog(this,
                                        "Vous n'avez pas de gare sur aucune des 2 ville que vous avez selectionnes",
                                        "Erreur Selection", JOptionPane.OK_OPTION);
                            }
                        } else {
                            interPlateau.getCurrentJoueur().setVilleCLick(null);
                        }
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "Il n'existe aucune ligne entre les 2 villes que vous avez selectionne" +
                                        ville + "/" + interPlateau.getCurrentJoueur().getVilleClick(),
                                "Erreur Ligne", JOptionPane.OK_OPTION);
                    }

                }
            }
        }
    }

    /**
     * Permets de placer la gare et de l'indiquer au current joueur
     * @param villeClick
     * @param currentJoueur
     */
    public void placeGare(Ville villeClick, Joueur currentJoueur) {
        String[] textPossible = { "Oui", "Non" };
        int retour = JOptionPane.showOptionDialog(this, "Etes vous sur de vouloir prendre la gare de " + villeClick,
                "Fenetre choix GARE",
                JOptionPane.OK_OPTION, JOptionPane.PLAIN_MESSAGE, null, textPossible, textPossible[0]);
        if (retour == 0) {
            interPlateau.placeGare(currentJoueur, villeClick);
            noPlay();
        } else {
            currentJoueur.setVilleCLick(null);
        }
    }

    /**
     * Permet d'acheter toutes les lignes tunnel
     * @param ligne
     */
    public void achatTunnel(Ligne ligne) {
        UIManager.put("OptionPane.minimumSize", new Dimension(650, 350));
        JOptionPane.showMessageDialog(this, new PanelCarteTunnel(interPlateau, ligne),
                "Carte choisi dans le tunnel", JOptionPane.YES_NO_OPTION);
        UIManager.put("OptionPane.minimumSize", null);
        noPlay();// Desactive les action du joueur
    }

    /**
     * S'occupe de gerer toutes les selctions de ligne 
     * @param ligne
     * @param j
     */
    public void achatLigne(Ligne ligne, Joueur j) {
        Integer[] tabInteger = new Integer[Math.max(Math.min(j.nmbCarteOfCouleur(Couleur.JOKER),(ligne.getNmbCases())),1)];
        for (int i = 0; i < tabInteger.length; i++){
            tabInteger[i] = i;
        }
        // Si la ligne est une ligne simple
        Integer nmbLocomotives = (Integer) JOptionPane.showInputDialog(this,
                "Choissez le nombre de carte Locomotives a utilise",
                "choixJoker", JOptionPane.QUESTION_MESSAGE, null, tabInteger, 0);
        if(nmbLocomotives==null){
            return;
        }else{
            j.jouerCarte(Couleur.JOKER, nmbLocomotives);
            ArrayList<Couleur> listCouleur = new ArrayList<Couleur>();
            for (Couleur c : Couleur.values()) {
                if (j.hasCarte(c, (ligne.getNmbCases() - nmbLocomotives))) {
                    listCouleur.add(c);
                }
            }
            Couleur c = (ligne.getCouleur());
            if (ligne.getCouleur() == Couleur.JOKER) {
                // Si la ligne est une ligne JOKER
                int choix = JOptionPane.showOptionDialog(this,
                        "Comment voulez vous achetez ?(Toutes les couleurs sont celle auquel vous avez assez)",
                        "Choix carte", JOptionPane.YES_OPTION,
                        JOptionPane.PLAIN_MESSAGE, null, listCouleur.toArray(), Couleur.RED);
                c = Couleur.values()[choix];
            }
            interPlateau.achatLigne(interPlateau.getCurrentJoueur(), ligne, c,
                    (ligne.getNmbCases() - nmbLocomotives));
            noPlay();
        }
    }

    @Override
    public void mouseClicked(MouseEvent me) {
        if(!this.play){                  
            return;
        }
        if (selectionLigneGare) {
            selectionLigne(me);
        } else {
            Iterator<Ville> itVille = interPlateau.iteratorVille();
            while (itVille.hasNext()) {
                Ville tmp = itVille.next();
                if (tmp.contains(me.getPoint())) {
                    Joueur currentJoueur = interPlateau.getCurrentJoueur();
                    Ville villeClick = currentJoueur.getVilleClick();
                    if (currentJoueur.getVilleClick() == null) {
                        currentJoueur.setVilleCLick(tmp);
                    } else {
                        if (villeClick == tmp) {
                            if (interPlateau.getCurrentJoueur().hasGare() && !villeClick.hasGare()) {
                                placeGare(villeClick, currentJoueur);
                            } else {
                                currentJoueur.setVilleCLick(null);
                                if (currentJoueur.hasGare()) {
                                    JOptionPane.showMessageDialog(this, "Il y a deja une gare sur cette ville",
                                            "Selection Gare", JOptionPane.OK_OPTION);
                                } else {
                                    JOptionPane.showMessageDialog(this, "Vous n'avez plu de gare a mettre",
                                            "Selection Gare", JOptionPane.OK_OPTION);
                                }
                            }
                        } else {
                            String[] textPossible = { "Oui", "Non" };
                            Ligne ligne = villeClick.ligneBetweenVille(tmp);
                            if (ligne == null) {
                                JOptionPane.showMessageDialog(this, "Il n'y as pas de ligne direct entre les 2 villes");
                                currentJoueur.setVilleCLick(null);
                                return;
                            }
                            if (ligne.isBuyable(currentJoueur)) {
                                int retour = JOptionPane.showOptionDialog(this,
                                        "Etes vous sur de vouloir prendre la ligne de " + ligne, "Fenetre choix GARE",
                                        JOptionPane.OK_OPTION, JOptionPane.PLAIN_MESSAGE, null, textPossible,
                                        textPossible[0]);
                                if (retour == 0) {
                                    if (ligne.isTunnel()) {// Si la ligne est un tunnel
                                        achatTunnel(ligne);
                                        // interPlateau.achatTunnel(interPlateau.getCurrentJoueur(),(ligne);
                                    } else {
                                        achatLigne(ligne, currentJoueur);
                                    }
                                }
                            } else if(ligne.isTunnel()){
                                int retour = JOptionPane.showOptionDialog(this,
                                        "Etes vous sur de vouloir prendre la ligne de " + ligne, "Fenetre choix GARE",
                                        JOptionPane.OK_OPTION, JOptionPane.PLAIN_MESSAGE, null, textPossible,
                                        textPossible[0]);
                                if(retour==0){
                                    achatTunnel(ligne);
                                }
                            }else{
                                String raison  = ligne.isBuyable(currentJoueur) ? "" : " vous n'avez pas assez de wagon";
                                raison = raison.equals("")&&!ligne.isEmpty() ? "un joueur a deja cette ligne" : raison;
                                raison = raison.equals("")&&ligne instanceof DoubleLigne ?"Vous avez deja l'autre ligne ne soyez pas trop gourmand ": raison;
                                JOptionPane.showMessageDialog(this, "Vous ne pouvez pas achetez cette ligne ,"+raison);
                                interPlateau.getCurrentJoueur().setVilleCLick(null);
                            }
                        }
                    }
                }
            }
        }
        repaint();
    }

    /**
     * Fonction de sauvegarde
     */
    public void savePlateau() {
        String[] saveDispo = SauvegardePlateau.saveDisponible();
        if (saveDispo.length == 0) {
            JOptionPane.showMessageDialog(this, "Vous n'avez pas d'endroit ou sauvegardez", "fenetre sauvegarde erreur",
                    JOptionPane.ERROR_MESSAGE);
            // Les joueur n'ayant pas d'espace ne peuvent pas sauvegarder a voir si on
            // change
        } else {
            String choix = (String) JOptionPane.showInputDialog(this,
                    "Choisissez la sauvegarde ou vous voulez sauvegarde",
                    "Choix Sauvegarde ", JOptionPane.QUESTION_MESSAGE, null, saveDispo, saveDispo[0]);
            SauvegardePlateau sauvegardePlateau = new SauvegardePlateau(jeu.getPlateau(),
                    System.currentTimeMillis(), jeu.getTimeElapsed(), jeu.getNmbTour(), jeu.getFonctionJeu());
            SauvegardePlateau.sauvegardePlateau(sauvegardePlateau, choix);
            close();
        }

    }

    public void setPlay(){
        play = !play;
    }

    private void close() {
        jeu.stopThread();
        mf.dispose();
        javax.swing.SwingUtilities.invokeLater(() -> new EcranPresentation());
    }

    @Override
    public void mouseEntered(MouseEvent arg0) {
        // System.out.println("AAAAAAAAA");
    }

    @Override
    public void mouseExited(MouseEvent arg0) {
        // System.out.println("IOA");
    }

    @Override
    public void mousePressed(MouseEvent arg0) {
        // System.out.println("OUI");
    }

    @Override
    public void mouseReleased(MouseEvent arg0) {
        // System.out.println("NON");
    }

    @Override
    public void mouseDragged(MouseEvent arg0) {
        // System.out.println("ERRR");
    }

    @Override
    public void mouseMoved(MouseEvent arg0) {
        // System.out.println("OIA");
    }

    public void setPlateau(Plateau plateau2) {
        this.interPlateau = plateau2;
    }

    public Jeu getJeu() {
        return this.jeu;
    }

    public void setInventaire(boolean b) {
        this.boutonInventaire.setEnabled(b);
    }
}
