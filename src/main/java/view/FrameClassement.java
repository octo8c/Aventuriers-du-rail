package src.main.java.view;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import src.main.java.controller.Joueur;
import src.main.java.model.Plateau;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class FrameClassement extends JFrame {
    private ArrayList<Map.Entry<Joueur, Integer>> classement;
    private JPanel classementPanel;
    private Image img;

    public FrameClassement(MainFrame mf, Plateau p) {
        super("Classement");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        try {
            img = ImageIO.read(new File("src/main/resources/images/images/dialogue-box_Big.png"));
        } catch (IOException ioe) {
            System.out.println("Image non trouvée");
        }

        classement = p.classementsJoueur();

        // Panneau principal avec l'image de fond
        BackgroundPanel mainPanel = new BackgroundPanel(img);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // Création du panneau pour afficher le classement
        classementPanel = new JPanel();
        classementPanel.setLayout(new BoxLayout(classementPanel, BoxLayout.Y_AXIS));
        classementPanel.setOpaque(false); // Rendre le panneau transparent pour voir l'image de fond
        classementPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        afficherClassement(classement);

        // Ajout du panneau de classement dans un JScrollPane
        JScrollPane scrollPane = new JScrollPane(classementPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        mainPanel.add(scrollPane);

        // Création du panneau de boutons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);

        JButton quitterButton = new JButton("Quitter");
        quitterButton.addActionListener(e -> {
            mf.dispose();
            dispose();

        });

        JButton menu = new JButton("Menu");
        menu.addActionListener(e ->{
            mf.dispose();
            dispose();
            new EcranPresentation();
        });

        buttonPanel.add(quitterButton);
        buttonPanel.add(menu);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);


        // Ajout du panneau principal à la fenêtre
        add(mainPanel);

        setPreferredSize(new Dimension(400, 600));
        pack(); // Redimensionner la fenêtre pour s'adapter au contenu
        setLocationRelativeTo(null); // Centrer sur la fenêtre
        setVisible(true);
    }

    private void afficherClassement(ArrayList<Map.Entry<Joueur, Integer>> classement) {
        int position = 1;
        for (Map.Entry<Joueur, Integer> entry : classement) {
            JPanel entryPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            entryPanel.setOpaque(false); // Rendre le panneau transparent pour voir l'image de fond
            entryPanel.setBorder(new EmptyBorder(5, 5, 5, 5)); // Ajouter un peu de marge

            JLabel positionLabel = new JLabel(convertirEnOrdinal(position)); // Affichage de la position
            JLabel pseudoLabel = new JLabel(entry.getKey().getPseudo() + " avec"); // Affichage du pseudo du joueur
            JLabel scoreLabel = new JLabel(pluriel(entry.getValue())); // Affichage du score du joueur
            
            // Ajouter les composants au panneau
            entryPanel.add(positionLabel);
            entryPanel.add(pseudoLabel);
            entryPanel.add(scoreLabel);

            classementPanel.add(entryPanel);
            position++;
        }
    }

    private String convertirEnOrdinal(int nombre) {
        if (nombre == 1) {
            return nombre + "er :";
        } else {
            return nombre + "ème :";
        }
    }

    private String pluriel(int nombre) {
        if (nombre < 2) {
            return nombre + " point";
        } else {
            return nombre + " points";
        }
    }

    // Panneau personnalisé pour afficher l'image de fond
    class BackgroundPanel extends JPanel {
        private Image img;

        public BackgroundPanel(Image img) {
            this.img = img;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (img != null) {
                g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }
}
