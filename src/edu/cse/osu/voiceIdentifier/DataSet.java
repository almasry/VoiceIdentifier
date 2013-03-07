package edu.cse.osu.voiceIdentifier;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class DataSet implements Iterable<DataPoint> {

    private final ArrayList<DataPoint> mPoints;

    public DataSet() {
        mPoints = new ArrayList<DataPoint>();
    }

    public DataSet(ArrayList<DataPoint> pointList) {
        mPoints = pointList;
    }

    public DataSet(File dataFile, File classificationFile) {

        mPoints = new ArrayList<DataPoint>();

        try {
            BufferedReader valueReader = new BufferedReader(new FileReader(
                    dataFile));
            BufferedReader classReader = new BufferedReader(new FileReader(
                    classificationFile));

            String valueLine = valueReader.readLine();
            String classLine = classReader.readLine();

            while (valueLine != null) {

                String[] valueStrings = valueLine.trim().split("\\s+");

                double[] values = new double[valueStrings.length];
                for (int i = 0; i < values.length; i++) {
                    values[i] = Double.parseDouble(valueStrings[i]);
                }

                int classValue = Integer.parseInt(classLine.trim());

                mPoints.add(new DataPoint(values, classValue));

                valueLine = valueReader.readLine();
                classLine = classReader.readLine();

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public DataPoint get(int index) {
        return mPoints.get(index);
    }

    public int size() {
        return mPoints.size();
    }

    @Override
    public Iterator iterator() {
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

}
