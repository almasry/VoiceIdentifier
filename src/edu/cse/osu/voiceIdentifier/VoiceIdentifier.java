package edu.cse.osu.voiceIdentifier;

import com.musicg.wave.Wave;
import com.musicg.wave.extension.Spectrogram;

public class VoiceIdentifier {

    public static void main(String[] args) {
        
        // http://www.wavlist.com/movies/317/ofsp-suprman3.wav
        
        String filename = "data/ofsp-suprman3.wav";

        // create a wave object
        Wave wave = new Wave(filename);
        Spectrogram spectrogram = new Spectrogram(wave);

        double[][] freqTimeData = spectrogram.getNormalizedSpectrogramData();

        for(int i = 0; i < freqTimeData.length; i++){
            for(int j = 0; j < freqTimeData[0].length; j++){
                System.out.format("%f.2 ", freqTimeData[i][j]);
            }
            System.out.println();
        }

    }

}