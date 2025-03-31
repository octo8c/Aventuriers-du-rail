package src.main.java.view;

import src.main.java.model.InterPlateau;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class SelectionJoueur extends JPanel implements DocumentListener {
    private final JTextField[] textFields;
    private final InterPlateau inter;
    private final String[] avatars = {"BLEU", "JAUNE", "ROSE", "ROUGE", "VERT"};
    private final int nmbPlayers;

    public SelectionJoueur(int numberOfPlayers, InterPlateau inter) {
        this.setLayout(new GridLayout(6, 2));
        this.textFields = new JTextField[5];
        this.inter = inter;
        this.nmbPlayers = numberOfPlayers;

        // Ajouter les avatars et les champs de texte pour les pseudonymes
        for (int i = 0; i < 5; i++) {
            JLabel avatarLabel = new JLabel();
            try {
                // Charger l'image et la redimensionner
                BufferedImage originalImage = ImageIO.read(new File("src/main/resources/images/avatars/avatar-" + avatars[i] + ".png"));
                BufferedImage resizedImage = resizeImage(originalImage, 100, 100);
                avatarLabel.setIcon(new ImageIcon(resizedImage));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            JTextField textField = new JTextField();
            textField.getDocument().addDocumentListener(this);
            textFields[i] = textField;
            this.add(avatarLabel);
            this.add(textField);
        }

        // Ajouter un label d'instruction
        JLabel instructionLabel = new JLabel("Appuyez sur 'VALIDER' après avoir saisi tous les pseudos");
        instructionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(instructionLabel);

        JButton okButton = new JButton("VALIDER");
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!checkPseudos()) {
                    showErrorMessage();
                } else {showSuccessMessage();}
            }
        });
        this.add(okButton);
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        updatePseudos();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        updatePseudos();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        updatePseudos();
    }

    private boolean checkPseudos() {
        int filledFields = 0;

        for (JTextField textField : textFields) {
            if (!textField.getText().trim().isEmpty()) {
                filledFields++; // Incrémente le compteur si le champ de texte n'est pas vide
            }
        }

        return filledFields == nmbPlayers;
    }

    private void updatePseudos() {
        int count = 0;
        if (checkPseudos()) {
            for (int i = 0; i < textFields.length; i++) {
                String pseudo = textFields[i].getText().trim();
                if (!pseudo.isEmpty()) {
                    inter.setPseudo(count, pseudo);
                    inter.setAvatarColor(count, avatars[i]);
                    count++;
                }
            }
        }
    }

    private void showErrorMessage() {
        JOptionPane.showMessageDialog(this, "Veuillez remplir exactement " + nmbPlayers + " champs de texte pour les pseudos.", "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccessMessage() {
        JOptionPane.showMessageDialog(this, "Les pseudos ont été correctement initialisés.", "Succès", JOptionPane.INFORMATION_MESSAGE);
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        graphics2D.dispose();
        return resizedImage;
    }
}
