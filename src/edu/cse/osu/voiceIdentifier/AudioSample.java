package edu.cse.osu.voiceIdentifier;

import java.util.ArrayList;

import com.musicg.wave.Wave;
import com.musicg.wave.extension.Spectrogram;

public class AudioSample {

    private String mFilepath;
    private final Wave mWave;
    private final Spectrogram mSpec;
    private final double[] mFeatures;

    public AudioSample(String filepath) {

        mFilepath = filepath;
        mWave = new Wave(filepath);
        mSpec = new Spectrogram(mWave);
        double[][] freqTimeData = mSpec.getNormalizedSpectrogramData();
        mFeatures = normalizedSum(freqTimeData);

    }

    private AudioSample(Wave wave) {
        mFilepath = "";
        mWave = wave;
        mSpec = new Spectrogram(mWave);
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

    /**
     * Returns a new sample consisting of a segment of the original
     *
     * @param startSeconds
     * @param endSeconds
     * @return
     */
    AudioSample getSubSample(double startSeconds, double endSeconds) {
        Wave newWave = new Wave(mFilepath);
        newWave.trim(startSeconds, newWave.length() - endSeconds);
        AudioSample output = new AudioSample(newWave);
        output.mFilepath = mFilepath;
        return output;
    }

    /**
     * Returns a list of samples split into samples of the provided length
     * 
     * @param sampleLengthSeconds
     * @return
     */
    ArrayList<AudioSample> split(double sampleLengthSeconds) {

        ArrayList<AudioSample> output = new ArrayList<AudioSample>();

        int numberSamples = (int) Math.floor(mWave.length()
                / sampleLengthSeconds);

        for (int i = 0; i < numberSamples; i++) {
            output.add(getSubSample(i * sampleLengthSeconds, (i + 1)
                    * sampleLengthSeconds));
        }

        return output;
    }

    /**
     * Returns a list of datapoints relating to the segments of provided length
     * in this sample
     *
     * @param sampleLengthSeconds
     * @param classLabel
     * @return
     */
    ArrayList<DataPoint> splitToDataPoints(double sampleLengthSeconds,
            int classLabel) {

        ArrayList<DataPoint> points = new ArrayList<DataPoint>();
        ArrayList<AudioSample> split = split(sampleLengthSeconds);

        for (AudioSample sample : split) {
            points.add(new DataPoint(sample.getFeatures(), classLabel));
        }

        return points;

    }

    /**
     * Returns the length in seconds of the sample
     *
     * @return
     */
    public double length() {
        return mWave.length();
    }


}
