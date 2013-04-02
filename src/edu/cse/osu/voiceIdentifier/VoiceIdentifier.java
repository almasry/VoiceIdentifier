package edu.cse.osu.voiceIdentifier;

import java.io.File;

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

        DataSet testSet = new DataSet();

        for (int n = 1; n < 10; n++) {
            AudioSample ben = new AudioSample(dataPath + "ben/ben-" + n
                    + ".wav");
            testSet.addAll(ben.splitToDataPoints(30.0, 0));
        }


        for (int n = 1; n < 10; n++) {
            AudioSample david = new AudioSample(dataPath + "david/david-" + n
                    + ".wav");
            testSet.addAll(david.splitToDataPoints(30.0, 1));
        }


        Grapher.plotGraph(x, testSet.getRawData());

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