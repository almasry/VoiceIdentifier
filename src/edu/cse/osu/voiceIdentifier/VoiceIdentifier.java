package edu.cse.osu.voiceIdentifier;

import javax.swing.JFrame;

import org.jplot2d.element.Axis;
import org.jplot2d.element.ElementFactory;
import org.jplot2d.element.Layer;
import org.jplot2d.element.Plot;
import org.jplot2d.element.Title;
import org.jplot2d.element.XYGraph;
import org.jplot2d.sizing.AutoPackSizeMode;
import org.jplot2d.swing.JPlot2DFrame;

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

        double[] x = new double[freqTimeData[0].length];
        for (int i = 0; i < freqTimeData[0].length; i++) {
            x[i] = i;
        }

        plotGraph(x, normalizedSum(freqTimeData));

    }

    /**
     * Takes each row of a 2D array and sums together Divides each array element
     * by the number of rows
     *
     * @param data
     *            2d array to sum
     * @return 1d sum of the values in data divided by the length of data
     */
    public static double[] normalizedSum(double[][] data) {

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
     * Plot a graph of the data provided
     *
     * @param x
     * @param y
     */
    public static void plotGraph(double[] x, double[] y) {

        ElementFactory ef = ElementFactory.getInstance();
        Plot plot = ef.createPlot();
        plot.setPreferredContentSize(800, 600);
        plot.setSizeMode(new AutoPackSizeMode());

        Title title = ef.createTitle("Intensity vs. Frequency");
        title.setFontScale(2);
        plot.addTitle(title);

        Axis xAxis = ef.createAxis();
        Axis yAxis = ef.createAxis();

        plot.addXAxis(xAxis);
        plot.addYAxis(yAxis);

        Layer layer = ef.createLayer();

        XYGraph graph = ef.createXYGraph(x, y);

        layer.addGraph(graph);

        plot.addLayer(layer, xAxis.getTickManager().getAxisTransform(), yAxis
                .getTickManager().getAxisTransform());

        JFrame frame = new JPlot2DFrame(plot);
        frame.setSize(800, 600);
        frame.setVisible(true);

    }

}