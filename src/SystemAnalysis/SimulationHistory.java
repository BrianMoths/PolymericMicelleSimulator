/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SystemAnalysis;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 *
 * @author bmoths
 */
public class SimulationHistory {

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

    public void addValue(TrackedVariable trackedVariable, double perimeter) {
        getStatisticsFor(trackedVariable).addValue(perimeter);
    }

    public double getAverage(TrackedVariable trackedVariable) {
        return getStatisticsFor(trackedVariable).getMean();
    }

    public double getStandardDeviation(TrackedVariable trackedVariable) {
        return getStatisticsFor(trackedVariable).getStandardDeviation();
    }

    public void clearStatistics(TrackedVariable trackedVariable) {
        getStatisticsFor(trackedVariable).clear();
    }

    public double[] getStoredValues(TrackedVariable trackedVariable) {
        return getStatisticsFor(trackedVariable).getValues();
    }

    public void clearAll() {
        for (TrackedVariable trackedVariable : TrackedVariable.values()) {
            clearStatistics(trackedVariable);
        }
    }

    private DescriptiveStatistics getStatisticsFor(TrackedVariable trackedVariable) {
        return trackedVariable.getStatistics(this);
    }
}
