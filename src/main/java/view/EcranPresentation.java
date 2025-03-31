package src.main.java.view;

import java.awt.*;
import java.io.*;
import javax.imageio.*;
import javax.swing.*;
import javax.swing.border.LineBorder;

import src.main.java.internet.PanelConnectionClient;
import src.main.java.internet.PanelConnectionServeur;
import src.main.java.model.Plateau;
import src.main.java.model.SauvegardePlateau;
import src.main.java.model.Music;

public class EcranPresentation extends JFrame {
    private CardLayout cardLayout;
    private JPanel panelLayout;
    private boolean isOn = false;

    public EcranPresentation() {
        super("Aventuriers du Rail");
        cardLayout = new CardLayout();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setResizable(false);

        // Définir l'icône de la fenêtre
        ImageIcon icon = new ImageIcon("src/main/resources/icon.jpg");
        setIconImage(icon.getImage());
        Image img = null;
        // Charger l'image de fond
        try {
            img = ImageIO.read(new File("src/main/resources/background.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        PanelConnectionServeur pcs = new PanelConnectionServeur(this);
        PanelImage panelImage = new PanelImage(img);// Represente le pannel de l'ecran de presentation
        PanelConnectionClient panelConnectionClient = new PanelConnectionClient(this);
        panelLayout = new JPanel();
        panelLayout.setLayout(cardLayout);
        panelLayout.add(panelImage, "Presentation");
        panelLayout.add(panelConnectionClient, "Client");
        panelLayout.add(pcs, "Serveur");
        add(panelLayout);

        // Créer un panel pour contenir le reste du contenu
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BorderLayout());

        // Ajouter l'image du titre
        ImageIcon imageIcon = new ImageIcon("src/main/resources/Titre.png");
        JLabel imageLabel = new JLabel(imageIcon);
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(imageLabel, BorderLayout.NORTH);

        // Taille fixe pour les boutons
        Dimension buttonSize = new Dimension(300, 50);

        // Ajouter le bouton jouer
        JButton startButton = new JButton("Jouer");
        startButton.setFont(new Font("Arial", Font.PLAIN, 18));
        startButton.setPreferredSize(buttonSize);
        startButton.setMaximumSize(buttonSize);
        startButton.setMinimumSize(buttonSize);
        Integer[] nmbJoueur = { 1, 2, 3, 4, 5 };

        startButton.addActionListener(e -> {
            Plateau plateau = null;
            ModeLoader.loadCurrentScreenSize(getWidth(), getHeight());
            SauvegardePlateau.loadSaveDispo();
            Integer nmb = (Integer) JOptionPane.showInputDialog(this, "Choissisez le nombre de joueur",
                    "Fenetre selection nombre de joueur", JOptionPane.OK_OPTION, null, nmbJoueur, 1);
            if (nmb == null) {
                return; // Annule le choix de selection si il ferme la fenetre
            }
            plateau = Plateau.plateauFichier(new File("src/main/resources/plateau.txt"), nmb);
            Plateau p = plateau;
            UIManager.put("OptionPane.minimumSize", new Dimension(800, 350));
            JOptionPane.showMessageDialog(this, new SelectionJoueur(nmb, p), "Choix des pseudonyme",
                    JOptionPane.INFORMATION_MESSAGE);
            UIManager.put("OptionPane.minimumSize", null);
            javax.swing.SwingUtilities.invokeLater(() -> new MainFrame(p));
            dispose();
        });
        startButton.setOpaque(true);
        startButton.setBorder(new LineBorder(Color.RED));

        // Ajouter le bouton mute
        JButton muteButton = new JButton("Musique : " + "OFF");
        muteButton.setFont(new Font("Arial", Font.PLAIN, 18));
        muteButton.setPreferredSize(buttonSize);
        muteButton.setMaximumSize(buttonSize);
        muteButton.setMinimumSize(buttonSize);
        muteButton.addActionListener(e -> {
            if (!isOn) {
                Music.playMusic();
                isOn = true;
            } else {
                Music.stopMusic();
                isOn = false;
            }
            // Mettre à jour le texte du bouton
            muteButton.setText("Musique : " + (isOn ? "ON" : "OFF"));
        });
        muteButton.setOpaque(true);
        muteButton.setBorder(new LineBorder(Color.RED));

        // Ajouter le bouton quitter
        JButton exitButton = new JButton("Quitter");
        exitButton.setFont(new Font("Arial", Font.PLAIN, 18));
        exitButton.setPreferredSize(buttonSize);
        exitButton.setMaximumSize(buttonSize);
        exitButton.setMinimumSize(buttonSize);
        exitButton.addActionListener(e -> dispose());
        exitButton.setOpaque(true);
        exitButton.setBorder(new LineBorder(Color.RED));

        // Ajouter le bouton online
        JButton onlineButton = new JButton("Online");
        onlineButton.setFont(new Font("Arial", Font.PLAIN, 18));
        onlineButton.setPreferredSize(buttonSize);
        onlineButton.setMaximumSize(buttonSize);
        onlineButton.setMinimumSize(buttonSize);
        String[] option = { "Rejoindre une partie", "Creer une partie" };
        onlineButton.addActionListener(ae -> {
            ModeLoader.loadCurrentScreenSize(getWidth(), getHeight());
            ModeLoader.setStartX(getX());
            ModeLoader.setStartY(getY());
            int retour = JOptionPane.showOptionDialog(this, "Choissisez rejoindre une partie ou créer une partie",
                    "Choix online", JOptionPane.OK_OPTION, JOptionPane.PLAIN_MESSAGE, null, option, option[0]);
            if (retour == 0) {
                showClient();
            } else {
                showServeur();
            }
        });
        onlineButton.setOpaque(true);
        onlineButton.setBorder(new LineBorder(Color.RED));

        // Ajouter le bouton sauvegarder
        JButton saveButton = new JButton("SaveButton");
        saveButton.setFont(new Font("Arial", Font.PLAIN, 18));
        saveButton.setPreferredSize(buttonSize);
        saveButton.setMaximumSize(buttonSize);
        saveButton.setMinimumSize(buttonSize);
        saveButton.setBorder(new LineBorder(Color.RED));
        saveButton.setOpaque(false);
        saveButton.addActionListener(ae -> {
            JOptionPane.showMessageDialog(this, new PanelSupSauvegarde());
            SauvegardePlateau.supSauvegarde();
        });

        // Créer un panel pour contenir les boutons avec BoxLayout Y_AXIS
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
        buttonsPanel.setOpaque(false);

        // Ajouter les boutons au panel avec des espaces
        buttonsPanel.add(Box.createVerticalStrut(125));
        buttonsPanel.add(startButton);
        buttonsPanel.add(Box.createVerticalStrut(20));
        buttonsPanel.add(onlineButton);
        buttonsPanel.add(Box.createVerticalStrut(20));
        buttonsPanel.add(saveButton);
        buttonsPanel.add(Box.createVerticalStrut(20));
        buttonsPanel.add(muteButton);
        buttonsPanel.add(Box.createVerticalStrut(20));
        buttonsPanel.add(exitButton);
        buttonsPanel.add(Box.createVerticalGlue());

        // Créer un panel pour centrer les boutons verticalement
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(buttonsPanel);

        // Ajouter le panel central à votre layout
        panel.add(centerPanel, BorderLayout.CENTER);
        panelImage.add(panel);

        // Ajouter le panel au contenu de la fenêtre
        setVisible(true);
        pack();
    }

    public void showPresentation() {
        cardLayout.show(panelLayout, "Presentation");
    }

    public void showClient() {
        cardLayout.show(panelLayout, "Client");
    }

    public void showServeur() {
        cardLayout.show(panelLayout, "Serveur");
    }

    public JPanel getPanelLayout() {
        return this.panelLayout;
    }

    public class PanelImage extends JPanel {
        private Image image;

        public PanelImage(Image image) {
            this.image = image;
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (image != null) {
                g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(EcranPresentation::new);
    }
}
