package edu.cse.osu.voiceIdentifier;

import com.musicg.wave.Wave;
import com.musicg.wave.extension.Spectrogram;

public class AudioSample {

    private Wave mWave;
    private final Spectrogram mSpec;
    private final double[] mFeatures;

    public AudioSample(Wave wave) {

        mSpec = new Spectrogram(wave);
        double[][] freqTimeData = mSpec.getNormalizedSpectrogramData();
        mFeatures = normalizedSum(freqTimeData);

    }

    public double[] getFeatures() {

        return mFeatures;

    }

    /**
     * Takes each row of a 2D array and sums together Divides each array element
     * by the number of rows
     *
     * @param data
     *            2d array to sum
     * @return 1d sum of the values in data divided by the length of data
     */
    private static double[] normalizedSum(double[][] data) {

        double[] output = new double[data[0].length];

        for (double[] row : data) {
            for (int i = 0; i < row.length; i++) {
                output[i] += row[i];
            }
        }

        for (int i = 0; i < output.length; i++) {
            output[i] /= data.length;
        }

        return output;

    }

}
