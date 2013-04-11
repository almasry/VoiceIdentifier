package edu.cse.osu.voiceIdentifier;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.functions.SMO;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class VoiceIdentifier {

    public static void main(String[] args) {

        BufferedReader input = new BufferedReader(new InputStreamReader(
                System.in));
        System.out.println("Select program mode");
        System.out.println("0 : Compare graphs of the data");
        System.out
                .println("1 : Generate training/test set and compare classifiers");
        System.out.println("2 : Live Demo");

        int mode = 0;
        try {
            mode = Integer.parseInt(input.readLine());
            switch (mode) {
            case 0:
                graphComparison(args[0]);
                break;
            case 1:
                generateClassifiers(args[0], 10);
                break;
            case 2:
                liveDemo(args[0], 10);
                break;
            default:
                System.exit(0);
            }
        } catch (Exception e) {
            System.exit(0);
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

    public static void generateClassifiers(String dataPath, double sampleLength) {

        DataSet data = new DataSet(4);

        for (int n = 1; n < 10; n++) {
            AudioSample ben = new AudioSample(dataPath + "ben/ben-" + n
                    + ".wav");
            data.addAll(ben.splitToDataPoints(sampleLength, 0));
        }

        for (int n = 1; n < 19; n++) {
            AudioSample david = new AudioSample(dataPath + "david/david-" + n
                    + ".wav");
            data.addAll(david.splitToDataPoints(sampleLength, 1));
        }

        for (int n = 1; n < 19; n++) {
            AudioSample nicole = new AudioSample(dataPath + "nicole/nicole-"
                    + n + ".wav");
            data.addAll(nicole.splitToDataPoints(sampleLength, 2));
        }

        for (int n = 1; n < 19; n++) {
            AudioSample ethan = new AudioSample(dataPath + "ethan/ethan-" + n
                    + ".wav");
            data.add(new DataPoint(ethan.getFeatures(), 3));
        }

        System.out.println("Total size of dataset: " + data.size());

        ArrayList<DataSet> pair = data.splitToTrainingTestPair(0.75);

        pair.get(0).exportDataFileWeka(new File("data/training.arff"));
        pair.get(1).exportDataFileWeka(new File("data/test.arff"));

        // load file into WEKA

        try {

            // SVM
            System.out.println("Support Vector Machine");
            DataSource training = new DataSource("data/training.arff");
            DataSource test = new DataSource("data/test.arff");

            Classifier svm = new SMO();

            Evaluation eval = trainClassifier(training, svm, "");
            testClassifier(test, svm, eval);

            svm = new SMO();

            eval = trainClassifier(training, svm, "-R");
            testClassifier(test, svm, eval);

            // NN
            System.out.println("Neural Network / Perceptron");

            Classifier nn = new MultilayerPerceptron();

            eval = trainClassifier(training, nn, "-L 0.1 -N 2");
            testClassifier(test, nn, eval);

            nn = new MultilayerPerceptron();

            eval = trainClassifier(training, nn, "-L 0.5 -N 20");
            testClassifier(test, nn, eval);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void graphComparison(String dataPath) {

        double[] x = new double[256];
        for (int i = 0; i < 256; i++) {
            x[i] = i;
        }

        DataSet data = new DataSet(4);

        for (int n = 1; n < 10; n++) {
            AudioSample ben = new AudioSample(dataPath + "ben/ben-" + n
                    + ".wav");
            data.add(new DataPoint(ben.getFeatures(), 0));
        }

        for (int n = 1; n < 19; n++) {
            AudioSample david = new AudioSample(dataPath + "david/david-" + n
                    + ".wav");
            data.add(new DataPoint(david.getFeatures(), 1));
        }

        for (int n = 1; n < 19; n++) {
            AudioSample nicole = new AudioSample(dataPath + "nicole/nicole-"
                    + n + ".wav");
            data.add(new DataPoint(nicole.getFeatures(), 2));
        }

        for (int n = 1; n < 19; n++) {
            AudioSample ethan = new AudioSample(dataPath + "ethan/ethan-" + n
                    + ".wav");
            data.add(new DataPoint(ethan.getFeatures(), 3));
        }

        Grapher.plotDataSet(x, data);

    }

    public static void liveDemo(String dataPath, int durationInSeconds) {

        // Set up the classifier in advance

        DataSource training;
        Classifier svm, nn;
        Evaluation evalSVM, evalNN;
        try {

            training = new DataSource("data/training.arff");

            System.out.println("Training SVM, please wait..");
            svm = new SMO();
            evalSVM = trainClassifier(training, svm, "");
            System.out.println("SVM Training Complete.");

            System.out.println("Training NN, please wait..");
            nn = new MultilayerPerceptron();
            evalNN = trainClassifier(training, nn, "-L 0.5 -N 20");
            System.out.println("NN Training Complete.");

            // record a sample from the microphone

            // set up the line in

            AudioFormat format = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED, 44100.0F, 16, 2, 4,
                    44100.0F, false);
            TargetDataLine line;
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            if (!AudioSystem.isLineSupported(info)) {
                System.err.println("Line not supported");
                System.exit(0);
            }

            try {
                line = (TargetDataLine) AudioSystem.getLine(info);
                line.open(format);

                while (true) {

                    BufferedReader input = new BufferedReader(
                            new InputStreamReader(System.in));
                    System.out
                            .println("Enter the class label of the person speaking");
                    System.out.println("0 : Ben");
                    System.out.println("1 : David");
                    System.out.println("2 : Nicole");
                    System.out.println("3 : Ethan");
                    System.out.println("Press Enter to begin recording");

                    int classLabel = 0;
                    try {
                        classLabel = Integer.parseInt(input.readLine());
                    } catch (Exception e) {
                        System.exit(0);
                    }

                    try {

                        line.start();
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        int numBytesRead;
                        byte[] data = new byte[line.getBufferSize() / 5];

                        System.out.println("Recording Started");

                        line.start();

                        long startTime = System.currentTimeMillis();

                        long percentDone = 0;
                        while ((System.currentTimeMillis() - startTime) <= durationInSeconds * 1000) {
                            numBytesRead = line.read(data, 0, data.length);
                            out.write(data, 0, numBytesRead);
                            if ((percentDone < (System.currentTimeMillis() - startTime)
                                    / (durationInSeconds * 10))
                                    && (percentDone % 20 == 0)) {
                                System.out.print(" " + percentDone + "%");
                            }
                            percentDone = (System.currentTimeMillis() - startTime)
                                    / (durationInSeconds * 10);
                        }
                        System.out.println();
                        out.close();
                        System.out.println("Recording Completed");

                        ByteArrayInputStream bais = new ByteArrayInputStream(
                                out.toByteArray());
                        AudioInputStream ais = new AudioInputStream(bais,
                                format, out.size());

                        if (AudioSystem.isFileTypeSupported(
                                AudioFileFormat.Type.WAVE, ais)) {
                            AudioSystem.write(ais, AudioFileFormat.Type.WAVE,
                                    new File("data/temp.wav"));
                        }

                        line.stop();
                        ais.close();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    System.out.println("Audio file written");

                    // Create a new test dataset to evaluate the file
                    AudioSample recordedClip = new AudioSample("data/temp.wav");
                    DataSet recorded = new DataSet(4);
                    recorded.add(new DataPoint(recordedClip.getFeatures(),
                            classLabel));
                    recorded.exportDataFileWeka(new File("data/temp.arff"));

                    System.out.println("Weka data file written");

                    // Evaluate the results

                    System.out.println("SVM Results..");
                    testClassifier(new DataSource("data/temp.arff"), svm,
                            evalSVM);

                    System.out.println("NN Results..");
                    testClassifier(new DataSource("data/temp.arff"), nn, evalNN);

                }

            } catch (LineUnavailableException e1) {
                e1.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}