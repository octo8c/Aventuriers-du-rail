package src.main.java.model;
import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Music {
    private static String FilePath = "src/main/resources/TitleTheme.wav";
    private static Clip clip;
    private static boolean isRunning = false;

    public static void playMusic() {
        if (isRunning) {
            return;
        }
        try {
            AudioInputStream audioInput = AudioSystem.getAudioInputStream(new File(FilePath));
            clip = AudioSystem.getClip();
            clip.open(audioInput);
            clip.start();
            clip.loop(-1);
            isRunning = !isRunning;
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void stopMusic() {
        if (isRunning) {
            try {
                clip.stop();
                isRunning = !isRunning;
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    public static boolean getIsRunning(){
        return isRunning;
    }
}
