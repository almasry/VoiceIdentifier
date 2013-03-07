package edu.cse.osu.voiceIdentifier;

public class DataPoint {

    private final double[] mValues;
    private int mclassLabel;

    public DataPoint(int size) {
        mValues = new double[size];
        mclassLabel = 0;
    }

    public DataPoint(double[] values, int classLabel) {
        mValues = values;
        mclassLabel = classLabel;
    }

    public double getValue(int index) {
        return mValues[index];
    }

    public double[] getValueArray() {
        return mValues;
    }

    public int getclassLabel() {
        return mclassLabel;
    }

    public void setclassLabel(int value) {
        mclassLabel = value;
    }

    public int getDimensionality() {
        return mValues.length;
    }

    @Override
    public String toString() {
        String output = "[ ";
        for (double value : mValues) {
            output += value + " ";
        }
        output += "] : " + mclassLabel;
        return output;
    }

}
