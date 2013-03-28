package edu.cse.osu.voiceIdentifier;

import java.io.File;
import java.util.ArrayList;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.SMO;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class VoiceIdentifier {

    public static void main(String[] args) {

        // args[0] should be the path to your data folder
        String dataPath = args[0];

        double[] x = new double[256];
        for (int i = 0; i < 256; i++) {
            x[i] = i;
        }

        AudioSample sample1 = new AudioSample(dataPath + "sample-ben1.wav");
        AudioSample sample2 = new AudioSample(dataPath + "sample-ben2.wav");
        AudioSample sample3 = new AudioSample(dataPath + "ben-long.wav");
        AudioSample sample4 = new AudioSample(dataPath + "sample-david.wav");
        AudioSample sample5 = new AudioSample(dataPath + "david-long.wav");

        ArrayList<DataPoint> pointList = new ArrayList<DataPoint>();

        pointList.add(new DataPoint(sample1.getFeatures(), 0));
        pointList.add(new DataPoint(sample2.getFeatures(), 0));
        pointList.add(new DataPoint(sample3.getFeatures(), 0));
        pointList.add(new DataPoint(sample4.getFeatures(), 1));
        pointList.add(new DataPoint(sample5.getFeatures(), 1));

        System.out.println(sample5.length());

        DataSet splitData1 = new DataSet(sample5.splitToDataPoints(3.0, 1));
        DataSet splitData2 = new DataSet(sample3.splitToDataPoints(3.0, 0));

        Grapher.plotGraph(x, splitData1.getRawData());
        Grapher.plotGraph(x, splitData2.getRawData());

        DataSet testSet = new DataSet(sample5.splitToDataPoints(3.0, 1));
        testSet.addAll(sample3.splitToDataPoints(3.0, 0));

        testSet.exportDataFileWeka(new File("data/wekatest.arff"));

        // load file into WEKA

        try {

            DataSource source = new DataSource("data/wekatest.arff");

            Classifier svm = new SMO();

            Evaluation eval = trainClassifier(source, svm, "");
            testClassifier(source, svm, eval);

            svm = new SMO();

            eval = trainClassifier(source, svm, "-R -C 10000");
            testClassifier(source, svm, eval);

            System.out.println(eval.errorRate());
            System.out.println(eval.correct());

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static Evaluation trainClassifier(DataSource trainSource,
            Classifier cls,
            String options) throws Exception {

        String[] optionsArray = weka.core.Utils.splitOptions(options);

        Instances data = trainSource.getDataSet();

        if (data.classIndex() == -1)
            data.setClassIndex(data.numAttributes() - 1);

        cls.setOptions(optionsArray);
        cls.buildClassifier(data);

        return new Evaluation(data);

    }

    public static void testClassifier(DataSource testSource, Classifier cls,
            Evaluation eval) throws Exception {

        Instances testData = testSource.getDataSet();

        if (testData.classIndex() == -1)
            testData.setClassIndex(testData.numAttributes() - 1);

        eval.evaluateModel(cls, testData);
        System.out.println(eval.toSummaryString("\nResults\n======\n", false));

    }




}