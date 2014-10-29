/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FocusedSimulation.simulationrunner;

import Engine.PolymerSimulator;
import Engine.SimulationStepping.StepGenerators.StepGenerator;
import FocusedSimulation.DoubleWithUncertainty;
import FocusedSimulation.simulationrunner.StatisticsTracker.TrackableVariable;
import Gui.SystemViewer;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 *
 * @author bmoths
 */
public class SimulationRunner {

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

    public void doMeasurementRuns(int numMeasurementRuns, int numSamples) {
        for (int i = 0; i < numMeasurementRuns; i++) {
            doMeasurementRun(numSamples);
        }
    }

    public void doMeasurementRun() {
        statisticsTracker.clearAll();
        generateSamples(simulationRunnerParameters.getNumSamples());
        updateMeasuredValuesWithStatistics();
    }

    public void doMeasurementRun(int numSamples) {
        statisticsTracker.clearAll();
        generateSamples(numSamples);
        updateMeasuredValuesWithStatistics();
    }

    public void doMeasurementRun(int numSamples, int numIterationsPerSample) {
        statisticsTracker.clearAll();
        generateSamplesWithIterationsPerSample(numSamples, numIterationsPerSample);
        updateMeasuredValuesWithStatistics();
    }

    private void generateSamples(int numSamples) {
        generateSamplesWithIterationsPerSample(numSamples, simulationRunnerParameters.getNumIterationsPerSample());
    }

    private void generateSamplesWithIterationsPerSample(int numSamples, int numIterationsPerSample) {
        int numSamplesTaken = 0;
        while (numSamplesTaken < numSamples) {
            polymerSimulator.doIterations(numIterationsPerSample);
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

    public DescriptiveStatistics getStatisticsFor(TrackableVariable trackableVariable) {
        return statisticsTracker.getStatisticsFor(trackableVariable);
    }

    private DoubleWithUncertainty getAverageWithUncertainty(TrackableVariable trackableVariable) {
        return statisticsTracker.getDoubleWithUncertainty(trackableVariable);
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
