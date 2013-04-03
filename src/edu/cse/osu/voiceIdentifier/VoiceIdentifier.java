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

        DataSet data = new DataSet();

        // AudioSample ben = new AudioSample(dataPath + "ben/ben-full.wav");
        // data.addAll(ben.splitToDataPoints(5.0, 0));

        for (int n = 1; n < 10; n++) {
            AudioSample ben = new AudioSample(dataPath + "ben/ben-" + n
                    + ".wav");
            data.addAll(ben.splitToDataPoints(2.0, 0));
        }

        for (int n = 1; n < 10; n++) {
            AudioSample david = new AudioSample(dataPath + "david/david-" + n
                    + ".wav");
            data.addAll(david.splitToDataPoints(2.0, 1));
        }

        Grapher.plotGraph(x, data.getRawData());

        ArrayList<DataSet> pair = data.splitToTrainingTestPair(0.75);

        pair.get(0).exportDataFileWeka(new File("data/training.arff"));
        pair.get(1).exportDataFileWeka(new File("data/test.arff"));

        // load file into WEKA

        try {

            DataSource training = new DataSource("data/training.arff");
            DataSource test = new DataSource("data/test.arff");

            Classifier svm = new SMO();

            Evaluation eval = trainClassifier(training, svm, "");
            testClassifier(test, svm, eval);

            System.out.println(eval.errorRate());
            System.out.println(eval.correct());

            svm = new SMO();

            eval = trainClassifier(training, svm, "-R -C 10000");
            testClassifier(test, svm, eval);

            System.out.println(eval.errorRate());
            System.out.println(eval.correct());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static Evaluation trainClassifier(DataSource trainSource,
            Classifier cls, String options) throws Exception {

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