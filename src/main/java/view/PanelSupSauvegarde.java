package src.main.java.view;
import javax.swing.*;

import src.main.java.model.SauvegardePlateau;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
public class PanelSupSauvegarde extends JPanel{
    private Image image;
    public PanelSupSauvegarde(){
        String[]saveOccupe = SauvegardePlateau.saveOccupe();//On suppose que l'appel se fait sur
        if(saveOccupe.length>0){ 
            for(String save:saveOccupe){
                JCheckBox checkBox = new JCheckBox(save);
                checkBox.setSelected(false);
                checkBox.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent ae){
                        if(checkBox.isSelected()){
                            SauvegardePlateau.ajoutSauvegardeSup(save);
                        }else{
                            SauvegardePlateau.supSauvegardeSup(save);
                        }
                    }
                });
                add(checkBox);
            }
        }else{
            JLabel label=new JLabel("Il n'y a pas de sauvegarde a supprime");
            add(label);
        }
        
        //Si un jour envie de faire un easter egg
        image = null;
    }
    @Override
    public void paintComponent(Graphics g){
        if(image!=null){
            g.drawImage(image, 0, 0, null);
        }
    }
}