package edu.cse.osu.voiceIdentifier;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

public class DataSet implements Iterable<DataPoint> {

    private final ArrayList<DataPoint> mPoints;

    public DataSet() {
        mPoints = new ArrayList<DataPoint>();
    }

    public DataSet(ArrayList<DataPoint> pointList) {
        mPoints = pointList;
    }

    public DataPoint get(int index) {
        return mPoints.get(index);
    }

    public void add(DataPoint point) {
        mPoints.add(point);
    }

    public void addAll(Collection<DataPoint> points) {
        mPoints.addAll(points);
    }

    public int size() {
        return mPoints.size();
    }

    @Override
    public Iterator<DataPoint> iterator() {
        return mPoints.iterator();
    }

    @Override
    public String toString() {
        String output = "";
        for (DataPoint point : mPoints) {
            output += point + "\n";
        }
        return output;
    }

    public double[][] getRawData() {

        double[][] output = new double[mPoints.size()][mPoints.get(0)
                .getValueArray().length];

        for (int i = 0; i < output.length; i++) {
            output[i] = mPoints.get(i).getValueArray();
        }

        return output;

    }

    public void exportDataFileWeka(File outputFile) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(
                    outputFile));

            writer.write("@RELATION voice\n\n");

            // Add headers
            for (int i = 0; i < mPoints.get(0).getDimensionality(); i++) {
                writer.write("@ATTRIBUTE " + i + " NUMERIC\n");
            }


            String classLabels = "";
            for (Integer classLabel : getAllClassLabels()) {
                classLabels += classLabel + ",";
            }
            classLabels = classLabels.substring(0, classLabels.length() - 1);

            writer.write("@ATTRIBUTE class {" + classLabels + "}\n");

            // write data

            writer.write("\n@DATA\n");

            for (DataPoint point : mPoints) {
                for (int j = 0; j < point.getDimensionality(); j++) {
                    writer.write(point.getValue(j) + ",");
                }
                writer.write(point.getclassLabel() + "\n");
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public Set<Integer> getAllClassLabels() {
        HashSet<Integer> output = new HashSet<Integer>();

        for (DataPoint p : mPoints) {
            output.add(p.getclassLabel());
        }

        return output;
    }

    public ArrayList<DataSet> splitToTrainingTestPair(double ratio) {

        ArrayList<DataSet> output = new ArrayList<DataSet>();
        Random rand = new Random(System.currentTimeMillis());

        DataSet training = new DataSet();
        DataSet test = new DataSet();

        for (DataPoint point : mPoints) {
            if (rand.nextDouble() > ratio) {
                test.add(point);
            } else {
                training.add(point);
            }
        }

        output.add(training);
        output.add(test);

        return output;

    }

}
