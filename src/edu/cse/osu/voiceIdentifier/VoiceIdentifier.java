package edu.cse.osu.voiceIdentifier;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JFrame;

import org.jplot2d.element.Annotation;
import org.jplot2d.element.Axis;
import org.jplot2d.element.ElementFactory;
import org.jplot2d.element.Layer;
import org.jplot2d.element.Plot;
import org.jplot2d.element.Title;
import org.jplot2d.element.XYGraph;
import org.jplot2d.sizing.AutoPackSizeMode;
import org.jplot2d.swing.JPlot2DFrame;

import com.musicg.wave.Wave;

public class VoiceIdentifier {

    public static void main(String[] args) {

        // args[0] should be the path to your data folder
        String dataPath = args[0];

        double[] x = new double[256];
        for (int i = 0; i < 256; i++) {
            x[i] = i;
        }

        AudioSample sample1 = new AudioSample(new Wave(dataPath
                + "sample-ben1.wav"));
        AudioSample sample2 = new AudioSample(new Wave(dataPath
                + "sample-ben2.wav"));
        AudioSample sample3 = new AudioSample(new Wave(dataPath
                + "ben-long.wav"));
        AudioSample sample4 = new AudioSample(new Wave(dataPath
                + "sample-david.wav"));
        AudioSample sample5 = new AudioSample(new Wave(dataPath
                + "david-long.wav"));

        ArrayList<DataPoint> pointList = new ArrayList<DataPoint>();

        pointList.add(new DataPoint(sample1.getFeatures(), 0));
        pointList.add(new DataPoint(sample2.getFeatures(), 0));
        pointList.add(new DataPoint(sample3.getFeatures(), 0));
        pointList.add(new DataPoint(sample4.getFeatures(), 1));
        pointList.add(new DataPoint(sample5.getFeatures(), 1));

        DataSet data = new DataSet(pointList);

        data.exportDataFileWeka(new File("data/wekatest.arff"));

        plotGraph(x, data.getRawData());

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

        int i = 0;
        for (double[] row : y) {

            Layer layer = ef.createLayer();

            Random rand = new Random(System.currentTimeMillis());

            XYGraph graph = ef.createXYGraph(x, row);

            Color c = new Color(rand.nextInt(150), rand.nextInt(120),
                    rand.nextInt(120));

            graph.setColor(c);

            layer.addGraph(graph);
            plot.addLayer(layer, xAxis.getTickManager().getAxisTransform(),
                    yAxis.getTickManager().getAxisTransform());

            Annotation ann = ef
                    .createSymbolAnnotation(x[255], row[255], i + "");
            ann.setColor(c);
            ann.setFontSize(30);
            layer.addAnnotation(ann);

            i++;

        }

        JFrame frame = new JPlot2DFrame(plot);
        frame.setSize(800, 600);
        frame.setVisible(true);

    }


}