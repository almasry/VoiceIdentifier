package edu.cse.osu.voiceIdentifier;

import java.awt.Color;

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

public class Grapher {

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

            XYGraph graph = ef.createXYGraph(x, row);

            /**
             * Random rand = new Random(System.currentTimeMillis()); Color c =
             * new Color(rand.nextInt(150), rand.nextInt(120),
             * rand.nextInt(120));
             **/

            // TODO write a new function to graph a dataset with each class as a
            // color

            Color c;

            if (i < y.length / 2) {
                c = Color.RED;
            } else {
                c = Color.BLUE;
            }

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
