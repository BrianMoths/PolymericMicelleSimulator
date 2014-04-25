/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FocusedSimulation;

import Engine.PolymerSimulator;
import Engine.SimulationStepping.StepGenerators.StepGenerator;
import FocusedSimulation.StatisticsTracker.TrackableVariable;
import Gui.SystemViewer;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author bmoths
 */
public class SimulationRunner {

    static public class SimulationRunnerParameters implements Serializable {

        static private final int defaultNumIterationsPerSample = 100_000; // should depend on number of beads and size of resize steps
        static private final int defaultNumSamples = 1000; //should depend on desired relative precision of result
        static private final int defaultNumIterationsPerAnneal = 300_000;

        static public SimulationRunnerParameters defaultSimulationRunnerParameters() {
            final SimulationRunnerParameters defaultSimulationRunnerParameters;
            defaultSimulationRunnerParameters = new SimulationRunnerParameters(defaultNumIterationsPerSample, defaultNumSamples, defaultNumIterationsPerAnneal);
            return defaultSimulationRunnerParameters;
        }

        private static final long serialVersionUID = 0L;
        private final int numIterationsPerSample;
        private final int numSamples;
        private final int numIterationsPerAnneal;

        public SimulationRunnerParameters(int numIterationsPerSample, int numSamples, int numIterationsPerAnneal) {
            this.numIterationsPerSample = numIterationsPerSample;
            this.numSamples = numSamples;
            this.numIterationsPerAnneal = numIterationsPerAnneal;
        }

        public int getNumIterationsPerSample() {
            return numIterationsPerSample;
        }

        public int getNumSamples() {
            return numSamples;
        }

        public int getNumIterationsPerAnneal() {
            return numIterationsPerAnneal;
        }

    }

    private final PolymerSimulator polymerSimulator;
    private final StatisticsTracker statisticsTracker;
    private final SimulationRunnerParameters simulationRunnerParameters;
    private final Map<TrackableVariable, List<DoubleWithUncertainty>> measuredValues;

    public SimulationRunner(PolymerSimulator polymerSimulator, SimulationRunnerParameters simulationRunnerParameters) {
        this.polymerSimulator = polymerSimulator;
        this.simulationRunnerParameters = simulationRunnerParameters;
        statisticsTracker = new StatisticsTracker(simulationRunnerParameters.getNumSamples());
        measuredValues = new HashMap<>();
    }

    public void trackVariable(TrackableVariable trackableVariable) {
        statisticsTracker.addTrackableVariable(trackableVariable);
        measuredValues.put(trackableVariable, new ArrayList<DoubleWithUncertainty>());
    }

//    public void ignoreVariable(TrackedVariable trackedVariable) {
//        trackedVariables.remove(trackedVariable);
//    }
    public void doEquilibrateAnnealIteration() {
        polymerSimulator.anneal();
        polymerSimulator.doIterations(simulationRunnerParameters.getNumIterationsPerAnneal());
        System.out.println("Equilibrate and anneal iteration done.");
    }

    public void doEquilibrateAnnealIterations(int numIterations) {
        for (int i = 0; i < numIterations; i++) {
            doEquilibrateAnnealIteration();
        }
    }

    public void doMeasurementRuns(int numMeasurementRuns) {
        for (int i = 0; i < numMeasurementRuns; i++) {
            doMeasurementRun();
        }
    }

    public void doMeasurementRun() {
        statisticsTracker.clearAll();
        generateStatistics();
        updateMeasuredValuesWithStatistics();
    }

    private void generateStatistics() {
        int numSamplesTaken = 0;
        while (numSamplesTaken < simulationRunnerParameters.getNumSamples()) {
            polymerSimulator.doIterations(simulationRunnerParameters.numIterationsPerSample);
            statisticsTracker.addSnapshotForPolymerSimulator(polymerSimulator);
            numSamplesTaken++;
        }
    }

    private void updateMeasuredValuesWithStatistics() {
        for (TrackableVariable trackableVariable : statisticsTracker.getTrackedVariables()) {
            final DoubleWithUncertainty doubleWithUncertainty = getAverageWithUncertainty(trackableVariable);
            final List<DoubleWithUncertainty> measuredValuesForTrackedVariable = measuredValues.get(trackableVariable);
            measuredValuesForTrackedVariable.add(doubleWithUncertainty);
        }
    }

    private DoubleWithUncertainty getAverageWithUncertainty(TrackableVariable trackableVariable) {
        return statisticsTracker.getDoubleWithUncertainty(trackableVariable);
    }

    public void doTrialsUntilConverged() {
        while (!isConverged()) {
            doMeasurementRun();
        }
    }

    private boolean isConverged() {
        boolean isConverged = true;
        for (TrackableVariable trackableVariable : statisticsTracker.getTrackedVariables()) {
            isConverged &= isConverged(trackableVariable);
        }
        return isConverged;
    }

    public boolean isConverged(TrackableVariable trackableVariable) {
        final List<DoubleWithUncertainty> measurements = getMeasurementsForTrackedVariable(trackableVariable);
        final int numMeasurements = measurements.size();
        final int windowSize = 10;

        if (numMeasurements + 1 < windowSize) {
            return false;
        }

        int comparisonCount = 0;
        for (int i = numMeasurements - windowSize; i < numMeasurements; i++) {
            final int comparison = measurements.get(i).getValue() > measurements.get(i - 1).getValue() ? 1 : -1;
            comparisonCount += comparison;
        }
        return Math.abs(comparisonCount) < windowSize / 3;
    }

    public void showViewer() {
        try {
            SystemViewer systemViewer = new SystemViewer(polymerSimulator);
            systemViewer.setVisible(true);
        } catch (java.awt.HeadlessException e) {
            System.out.println("Headless exception thrown when creating system viewer. I am unable to create system viewer.");
        }
    }

    public DoubleWithUncertainty getRecentMeasurementForTrackedVariable(TrackableVariable trackableVariable) throws IllegalArgumentException {
        List<DoubleWithUncertainty> measurements = getMeasurementsForTrackedVariable(trackableVariable);
        return measurements.get(measurements.size() - 1);
    }

    public List<DoubleWithUncertainty> getMeasurementsForTrackedVariable(TrackableVariable trackableVariable) throws IllegalArgumentException {
        List<DoubleWithUncertainty> measuredValuesForTrackableVariable = measuredValues.get(trackableVariable);
        if (measuredValuesForTrackableVariable == null) {
            throw new IllegalArgumentException("Trackable variable not found");
        }
        return measuredValuesForTrackableVariable;
    }

    public StepGenerator getStepGenerator() {
        return polymerSimulator.getStepGenerator();
    }

    public void setStepGenerator(StepGenerator stepGenerator) {
        polymerSimulator.setStepGenerator(stepGenerator);
    }

    public PolymerSimulator getPolymerSimulator() {
        return polymerSimulator;
    }

}
