package src.main.java.view;

import javax.imageio.ImageIO;
import javax.swing.*;

import src.main.java.model.InterPlateau;
import src.main.java.model.Plateau;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class DicePanel extends JPanel {
    private static final String IMAGE_PATH = "src/main/resources/dice_";
    private static final int ROLL_DURATION = 2500; // en ms
    private static final int IMAGE_SIZE = 200;

    private Integer[] ftab;
    private Integer[] diceValues;
    private int numberOfDice;
    private ArrayList<JLabel> diceImages;
    private JButton rollButton;
    private boolean rolled;

    private Integer a = 10, b = -10;

    public DicePanel(Plateau plateau) {
        setPreferredSize(new Dimension(700, 650));
        setLayout(null);

        numberOfDice = plateau.getNmbJoueur();
        ftab = new Integer[numberOfDice];
        Arrays.fill(ftab, null);
        diceValues = new Integer[numberOfDice];
        diceImages = new ArrayList<>();
        rolled = false;

        // Load dice images
        for (int i = 0; i < numberOfDice; i++) {
            JLabel diceImg = loadImage(IMAGE_PATH+ (i + 1) +".png");
            JLabel pseudoLabel = new JLabel(plateau.getCurrentJoueur().getPseudo());
            InterPlateau interP = (InterPlateau) plateau;
            interP.nextJoueur();
            pseudoLabel.setBounds(90 + i * 260, 180, 50, 20);
            pseudoLabel.setHorizontalAlignment(SwingConstants.CENTER);
            if (diceImg != null) {
                diceImg.setBounds(90 + i * 260, 200, IMAGE_SIZE, IMAGE_SIZE);
                add(diceImg);
                add(pseudoLabel);
                diceImages.add(diceImg);
            }
        }

        // Roll Button
        Random rand = new Random();
        rollButton = new JButton("Roll!");

        rollButton.setBounds(125 * numberOfDice, 550, 200, 50);
        rollButton.addActionListener(e -> {
                rollButton.setEnabled(false);

                // Roll dice
                long startTime = System.currentTimeMillis();
                Thread rollThread = new Thread(() -> {
                    long endTime = System.currentTimeMillis();
                    try {
                        while ((endTime - startTime) < ROLL_DURATION) {
                            // Roll dice for each die
                            for (int i = 0; i < diceImages.size(); i++) {
                                if (ftab[i] == null) {
                                    JLabel diceImg = diceImages.get(i);
                                    int diceValue = rand.nextInt(6) + 1;
                                    updateImage(diceImg, IMAGE_PATH + diceValue + ".png");
                                    diceValues[i] = diceValue;
                                }
                            }

                            repaint();
                            revalidate();

                            Thread.sleep(60);

                            endTime = System.currentTimeMillis();
                        }
                    } catch (InterruptedException ex) {
                        System.out.println("Threading Error: " + ex);
                    }
                });
                rollThread.start();

                // Check and update ftab
                updateFtab();

                enableRollButton();
        });
        add(rollButton);
    }

    private JLabel loadImage(String filePath) {
        BufferedImage image;
        JLabel imageContainer;
        try {
            File file = new File(filePath);
            image = ImageIO.read(file);
            imageContainer = new JLabel(new ImageIcon(image));
            return imageContainer;
        } catch (IOException e) {
            System.out.println("Error: " + e);
            return null;
        }
    }

    private void updateFtab() {
        boolean updated;
        do {
            updated = false;
            Integer min = Integer.MAX_VALUE;
            Integer max = Integer.MIN_VALUE;
    
            // Recherche simultanée du maximum et du minimum uniques dans diceValues
            for (int i = 0; i < numberOfDice; i++) {
                Integer value = diceValues[i];
                if (value != null && ftab[i] == null) {
                    // Mise à jour du maximum unique
                    if (value > max) {
                        max = value;
                    }
                    // Mise à jour du minimum unique
                    if (value < min) {
                        min = value;
                    }
                }
            }
            // Mise à jour de ftab avec le maximum unique trouvé
            if (Unique(max)) {
                for (int i = 0; i < numberOfDice; i++) {
                    if (diceValues[i] == max && ftab[i] == null) {
                        ftab[i] = a;
                        a--;
                        diceValues[i] = null;
                        updated = true;
                        break;
                    }
                }
            }
            // Mise à jour de ftab avec le minimum unique trouvé
            if (Unique(min) && !min.equals(max)) {
                for (int i = 0; i < numberOfDice; i++) {
                    if (diceValues[i] == min && ftab[i] == null) {
                        ftab[i] = b;
                        b++;
                        diceValues[i] = null;
                        updated = true;
                        break;
                    }
                }
            }
        } while (updated);
    }
    

    private boolean Unique(Integer m) {
        int count = 0;
        for (int i = 0; i < numberOfDice; i++) {
            if (diceValues[i] != null && diceValues[i] == m) {
                count++;
            }
        }
        return count == 1;
    }

    public void enableRollButton() {
        boolean enable = true;
        for (Integer val : ftab) {
            if (val == null) {
                enable = false;
                break;
            }
        }

        rollButton.setEnabled(!enable);
        rolled = enable;
    }


    private void updateImage(JLabel imageContainer, String filePath) {
        BufferedImage image;
        try {
            File file = new File(filePath);
            image = ImageIO.read(file);
            imageContainer.setIcon(new ImageIcon(image));
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }
    }

    public Integer[] getFtab() {
        return ftab;
    }

    public int[] getFtab_int(){
        int[] tableauInt = Arrays.stream(ftab).mapToInt(Integer::intValue).toArray();
        return tableauInt;
    }

    public boolean getRolled() {
        return rolled;
    }
}
