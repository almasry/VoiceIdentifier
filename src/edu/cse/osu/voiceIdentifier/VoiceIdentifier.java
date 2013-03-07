package edu.cse.osu.voiceIdentifier;

import java.awt.Color;
import java.util.Random;

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

        // args[0] should be the path to your data folder
        String dataPath = args[0];

        String filename = dataPath + "sample-david.wav";

        // create a wave object
        Wave wave = new Wave(filename);
        Spectrogram spectrogram = new Spectrogram(wave);

        double[][] freqTimeData1 = spectrogram.getNormalizedSpectrogramData();

        filename = dataPath + "sample-tomas.wav";

        wave = new Wave(filename);
        spectrogram = new Spectrogram(wave);

        double[][] freqTimeData2 = spectrogram
                .getNormalizedSpectrogramData();

        double[] x = new double[freqTimeData1[0].length];
        for (int i = 0; i < freqTimeData1[0].length; i++) {
            x[i] = i;
        }

        double[][] yData = { normalizedSum(freqTimeData1),
                normalizedSum(freqTimeData2) };

        plotGraph(x, yData);

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

    /**
     * Plot a graph of the data provided with multiple lines
     *
     * @param x
     * @param y
     */
    public static void plotGraph(double[] x, double[][] y) {

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

        for (double[] row : y) {

            Layer layer = ef.createLayer();

            Random rand = new Random(System.currentTimeMillis());

            XYGraph graph = ef.createXYGraph(x, row);

            graph.setColor(new Color(rand.nextInt(150), rand.nextInt(120), rand
                    .nextInt(120)));

            layer.addGraph(graph);
            plot.addLayer(layer, xAxis.getTickManager().getAxisTransform(),
                    yAxis.getTickManager().getAxisTransform());

        }

        JFrame frame = new JPlot2DFrame(plot);
        frame.setSize(800, 600);
        frame.setVisible(true);

    }

}