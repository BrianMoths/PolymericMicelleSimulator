/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SystemAnalysis;

import java.io.Serializable;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 *
 * @author bmoths
 */
public class SimulationHistory implements Serializable {

    public static enum TrackedVariable {

        PERIMETER() {
            @Override
            public DescriptiveStatistics getStatistics(SimulationHistory simulationHistory) {
                return simulationHistory.perimeterStatistics;
            }

        },
        AREA() {
            @Override
            public DescriptiveStatistics getStatistics(SimulationHistory simulationHistory) {
                return simulationHistory.areaStatistics;
            }

        },
        ENERGY() {
            @Override
            public DescriptiveStatistics getStatistics(SimulationHistory simulationHistory) {
                return simulationHistory.energyStatistics;
            }

        };

        public DescriptiveStatistics getStatistics(SimulationHistory simulationHistory) {
            throw new AssertionError();
        }

    }

    private DescriptiveStatistics perimeterStatistics;
    private DescriptiveStatistics areaStatistics;
    private DescriptiveStatistics energyStatistics;

    public SimulationHistory(int windowSize) {
        perimeterStatistics = new DescriptiveStatistics(windowSize);
        areaStatistics = new DescriptiveStatistics(windowSize);
        energyStatistics = new DescriptiveStatistics(windowSize);
    }

    public SimulationHistory(SimulationHistory simulationHistory) {
        perimeterStatistics = new DescriptiveStatistics(simulationHistory.perimeterStatistics);
        areaStatistics = new DescriptiveStatistics(simulationHistory.areaStatistics);
        energyStatistics = new DescriptiveStatistics(simulationHistory.energyStatistics);
    }

    public void addValue(TrackedVariable trackedVariable, double value) {
        getStatisticsFor(trackedVariable).addValue(value);
    }

    public double getAverage(TrackedVariable trackedVariable) {
        return getStatisticsFor(trackedVariable).getMean();
    }

    public double getStandardDeviation(TrackedVariable trackedVariable) {
        return getStatisticsFor(trackedVariable).getStandardDeviation();
    }

    public double[] getStoredValues(TrackedVariable trackedVariable) {
        return getStatisticsFor(trackedVariable).getValues();
    }

    public void clearStatistics(TrackedVariable trackedVariable) {
        getStatisticsFor(trackedVariable).clear();
    }

    public void clearAll() {
        for (TrackedVariable trackedVariable : TrackedVariable.values()) {
            clearStatistics(trackedVariable);
        }
    }

    public boolean isEquilibrated() {
        if (energyStatistics.getN() < 500) {
            return false;
        }
        boolean isEquilibrated;
//        boolean isEquilibrated = true;
//        for (TrackedVariable trackedVariable : TrackedVariable.values()) {
//            isEquilibrated &= isVariableEquilibrated(trackedVariable);
//        }
        isEquilibrated = isVariableEquilibrated(TrackedVariable.ENERGY);
        return isEquilibrated;
    }

    public boolean isVariableEquilibrated(TrackedVariable trackedVariable) {
        final DescriptiveStatistics descriptiveStatistics = getStatisticsFor(trackedVariable);
        final double slope = getSlope(descriptiveStatistics);
        final double slopeLimit = getSlopeLimit(descriptiveStatistics);

//        System.out.println("Slope: " + Double.toString(slope));
//        System.out.println("Slope Limit: " + Double.toString(slopeLimit));

        final boolean isVariableEquilibrated;
        isVariableEquilibrated = Math.abs(slope) <= slopeLimit;
        return isVariableEquilibrated;
    }

    private double getSlope(DescriptiveStatistics descriptiveStatistics) {
        final double[] points = descriptiveStatistics.getValues();
        final int numPoints = points.length;

        final double f0 = numPoints * descriptiveStatistics.getMean();
        final double f1 = getF1(points);

        final double slope;
        slope = 6 * (double) (numPoints - 1) / (double) (numPoints * (numPoints + 1)) * (2 * f1 - f0);
        return slope;
    }

    private double getF1(double[] points) {
        final int numPoints = points.length;
        double f1 = 0;
        final double step = 1. / (numPoints - 1);
        double currentX = 0;
        for (int i = 0; i < numPoints; i++) {
            f1 += currentX * points[i];
            currentX += step;
        }
        return f1;
    }

    private double getSlopeLimit(DescriptiveStatistics descriptiveStatistics) {
        final double standardDeviation = descriptiveStatistics.getStandardDeviation();
        final double numPoints = (double) descriptiveStatistics.getN();
        return standardDeviation * 24. / Math.sqrt(numPoints);
    }

    private DescriptiveStatistics getStatisticsFor(TrackedVariable trackedVariable) {
        return trackedVariable.getStatistics(this);
    }

}
