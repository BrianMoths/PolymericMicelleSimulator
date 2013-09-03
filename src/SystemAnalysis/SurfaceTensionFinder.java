/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SystemAnalysis;

import Engine.SystemAnalyzer;
import java.awt.Color;
import java.awt.Graphics;
import java.io.Serializable;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.moment.Mean;

/**
 *
 * @author bmoths
 */
public class SurfaceTensionFinder implements Serializable {

    static private final int windowSize = 1000000;
    static private final int numSamples = 100;

    static private double dotProduct(double[] firstArray, double[] secondArray) {
        final int length = firstArray.length;
        double dotProduct = 0;
        for (int i = 0; i < length; i++) {
            dotProduct += firstArray[i] * secondArray[i];
        }
        return dotProduct;
    }

    static private void removeAverage(double[] doubleArray) {
        Mean meanStatistic = new Mean();
        final double mean = meanStatistic.evaluate(doubleArray);

        for (int i = 0; i < doubleArray.length; i++) {
            doubleArray[i] -= mean;
        }
    }
    private SystemAnalyzer systemAnalyzer;
    private DescriptiveStatistics surfaceTensionStatistics;
    private final double[] sampledYs;
    private final double[] modelXs;
    final double modelXsSquared;

    public SurfaceTensionFinder(SystemAnalyzer systemAnalyzer) {
        this.systemAnalyzer = systemAnalyzer;
        surfaceTensionStatistics = new DescriptiveStatistics(windowSize);
        sampledYs = getSampledYs();
        modelXs = getModelXs();
        modelXsSquared = dotProduct(modelXs, modelXs);
    }
//<editor-fold desc="ctor helpers" defaultstate="collapsed">

    private double[] getSampledYs() {
        double[] sampledYsLocal = new double[numSamples];
        double sampledY = 0;
        double yStep = getSystemHeight() / (numSamples + 1);
        for (int i = 0; i < numSamples; i++) {
            sampledYsLocal[i] = sampledY;
            sampledY += yStep;
        }
        return sampledYsLocal;
    }

    private double[] getModelXs() {
        double[] modelXsLocal = new double[numSamples];
        final double wavelength = getSystemHeight();
        final double yStep = getSystemHeight() / (numSamples + 1);
        final double wavenumber = 2 * Math.PI / wavelength;
        double sampledY = 0;
        for (int i = 0; i < numSamples; i++) {
            modelXsLocal[i] = Math.sin(wavenumber * sampledY);
            sampledY += yStep;
        }

        return modelXsLocal;
    }

    private double getSystemHeight() {
        return systemAnalyzer.getSystemGeometry().getRMax()[1];
    }
//</editor-fold>

    public SurfaceTensionFinder(SurfaceTensionFinder surfaceTensionFinder) {
        systemAnalyzer = surfaceTensionFinder.systemAnalyzer;
        surfaceTensionStatistics = surfaceTensionFinder.surfaceTensionStatistics.copy();
        sampledYs = surfaceTensionFinder.sampledYs;
        modelXs = surfaceTensionFinder.modelXs;
        modelXsSquared = surfaceTensionFinder.modelXsSquared;
    }

    public void recordSurfaceTension() {
        double[] rightXs = getRightXs();
        removeAverage(rightXs);
        double amplitude = getFundamentalAmplitude(rightXs);
//        System.out.println(amplitude);
        surfaceTensionStatistics.addValue(amplitude * amplitude);
    }

    public double findSurfaceTension() {
        final double msAmplitude = surfaceTensionStatistics.getMean();
        final double extraLength = getExtraLength(msAmplitude);
        return systemAnalyzer.getPhysicalConstants().getTemperature() / 2 / extraLength;
    }

    private double getExtraLength(double msAmplitude) {
        return Math.PI * Math.PI * msAmplitude / getSystemHeight();
    }

    private double[] getRightXs() {
        double[] rightXs = new double[numSamples];
        for (int i = 0; i < numSamples; i++) {
            rightXs[i] = systemAnalyzer.findRightEdge(sampledYs[i]);
        }
        return rightXs;
    }

    private double getFundamentalAmplitude(double[] rightXs) {
        double dotProduct = dotProduct(rightXs, modelXs);
        return dotProduct / modelXsSquared;
    }

    public void drawDots(Graphics graphics) { //draw fitted curve as well to ensure accuracy
        double[] rightXs = getRightXs();
        Mean meanStatistic = new Mean();
        final double meanX = meanStatistic.evaluate(rightXs);
        final int radius = 4;
        double amplitude = getFundamentalAmplitude(rightXs);
        graphics.setColor(Color.red);
        for (int i = 0; i < numSamples; i++) {
            graphics.fillOval((int) Math.round(rightXs[i] / systemAnalyzer.getSystemGeometry().getRMax()[0] * 600), (int) Math.round(sampledYs[i] / systemAnalyzer.getSystemGeometry().getRMax()[0] * 600), radius, radius);
        }
        graphics.setColor(Color.green);
        for (int i = 0; i < numSamples; i++) {
            graphics.fillOval((int) Math.round((amplitude * modelXs[i] + meanX) / systemAnalyzer.getSystemGeometry().getRMax()[0] * 600), (int) Math.round(sampledYs[i] / systemAnalyzer.getSystemGeometry().getRMax()[0] * 600), radius, radius);
        }

    }
}
