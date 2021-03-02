package com.tribesquare;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class AudioPlayer {

        Long currentFrame;
        Clip clip;
        String status = "stop";
        AudioInputStream audioInputStream;

    public AudioPlayer(String filePath) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
            var file = new File(filePath).getAbsoluteFile();
            audioInputStream = AudioSystem.getAudioInputStream(file);

//            System.out.println("Audio stream: " + audioInputStream);
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);

        }

    // Method to play the audio
        public void play()
        {
            //start the clip
            if (!status.equals("play")) {
                clip.start();
                status = "play";
            }
        }

    // Method to stop the audio
        public void stop() throws UnsupportedAudioFileException, IOException, LineUnavailableException
        {
//            System.out.println("Got to stop");
            currentFrame = 0L;
            clip.stop();
            clip.close();
            status = "stop";
        }

}
