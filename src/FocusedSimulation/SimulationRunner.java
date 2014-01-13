/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FocusedSimulation;

import Engine.PolymerSimulator;
import FocusedSimulation.StatisticsTracker.TrackableVariable;
import Gui.SystemViewer;
import SystemAnalysis.SimulationHistory;
import SystemAnalysis.SimulationHistory.TrackedVariable;
import com.sun.xml.internal.ws.message.saaj.SAAJHeader;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author bmoths
 */
public class SimulationRunner {

    static public class DoubleWithUncertainty {

        private final double value;
        private final double uncertainty;

        public DoubleWithUncertainty(double value, double uncertainty) {
            this.value = value;
            this.uncertainty = uncertainty;
        }

        public double getValue() {
            return value;
        }

        public double getUncertainty() {
            return uncertainty;
        }

    }

    static public class SimulationRunnerParameters {

        private final int numIterationsPerSample;
        private final int numSamples;
        private final int numIterationsPerAnneal;
        private final int windowSize;

        public SimulationRunnerParameters(int numIterationsPerSample, int numSamples, int numIterationsPerAnneal, int windowSize) {
            this.numIterationsPerSample = numIterationsPerSample;
            this.numSamples = numSamples;
            this.numIterationsPerAnneal = numIterationsPerAnneal;
            this.windowSize = windowSize;
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

        public int getWindowSize() {
            return windowSize;
        }

    }

    private final PolymerSimulator polymerSimulator;
    private final StatisticsTracker statisticsTracker;
    private final SimulationRunnerParameters simulationRunnerParameters;

    public SimulationRunner(PolymerSimulator polymerSimulator, SimulationRunnerParameters simulationRunnerParameters) {
        this.polymerSimulator = polymerSimulator;
        this.simulationRunnerParameters = simulationRunnerParameters;
        statisticsTracker = new StatisticsTracker(simulationRunnerParameters.getWindowSize());
    }

    public void trackVariable(TrackableVariable trackableVariable) {
        statisticsTracker.addTrackableVariable(trackableVariable);
    }

//    public void ignoreVariable(TrackedVariable trackedVariable) {
//        trackedVariables.remove(trackedVariable);
//    }
    public void doEquilibrateAnnealIteration() {
        polymerSimulator.anneal();
        polymerSimulator.doIterations(simulationRunnerParameters.getNumIterationsPerAnneal());
    }

    public void doEquilibrateAnnealIterations(int numIterations) {
        for (int i = 0; i < numIterations; i++) {
            doEquilibrateAnnealIteration();
        }
    }

    public void generateStatistics() {
        int numSamplesTaken = 0;
        while (numSamplesTaken < simulationRunnerParameters.getNumSamples()) {
            polymerSimulator.doIterations(simulationRunnerParameters.numIterationsPerSample);
            statisticsTracker.addSnapshotForPolymerSimulator(polymerSimulator);
            numSamplesTaken++;
        }
    }

    public void generateStatisticsUntilConverged() {
        while (!isConverged(TrackedVariable.PERIMETER)) {
            polymerSimulator.doIterations(simulationRunnerParameters.numIterationsPerSample);
            statisticsTracker.addSnapshotForPolymerSimulator(polymerSimulator);
        }
    }

    public DoubleWithUncertainty getAverageWithUncertainty(TrackableVariable trackableVariable) {
        final double average = statisticsTracker.getAverage(trackableVariable);
        final double standardError = statisticsTracker.getStandardDeviation(trackableVariable) / Math.sqrt(statisticsTracker.getNumSamples(trackableVariable));
        return new DoubleWithUncertainty(average, standardError);
    }

    private boolean isConverged(TrackedVariable trackedVariable) {
        return true;
    }

    public void showViewer() {
        try {
            SystemViewer systemViewer = new SystemViewer(polymerSimulator);
            systemViewer.setVisible(true);
        } catch (java.awt.HeadlessException e) {
            System.out.println("Headless exception thrown when creating system viewer. I am unable to create system viewer.");
        }
    }

}
