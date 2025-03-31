package src.main.java.model;
import javax.swing.*;

import src.main.java.view.EcranPresentation;
public class AventuriersDuRail {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(EcranPresentation::new);
    }
}
