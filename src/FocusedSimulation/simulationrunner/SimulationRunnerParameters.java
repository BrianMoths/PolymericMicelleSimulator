/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FocusedSimulation.simulationrunner;

import java.io.Serializable;

/**
 *
 * @author bmoths
 */
public class SimulationRunnerParameters implements Serializable {

    static public class SimulationRunnerParametersBuilder {

        private int numIterationsPerSample;
        private int numSamples;
        private int numIterationsPerAnneal;

        static public SimulationRunnerParametersBuilder deSimulationRunnerParametersBuilder() {
            return new SimulationRunnerParametersBuilder(defaultNumIterationsPerSample, defaultNumSamples, defaultNumIterationsPerAnneal);
        }

        public SimulationRunnerParametersBuilder(int numIterationsPerSample, int numSamples, int numIterationsPerAnneal) {
            this.numIterationsPerSample = numIterationsPerSample;
            this.numSamples = numSamples;
            this.numIterationsPerAnneal = numIterationsPerAnneal;
        }

        public SimulationRunnerParameters build() {
            return new SimulationRunnerParameters(this);
        }

        public void setNumIterationsPerSample(int numIterationsPerSample) {
            this.numIterationsPerSample = numIterationsPerSample;
        }

        public void setNumSamples(int numSamples) {
            this.numSamples = numSamples;
        }

        public void setNumIterationsPerAnneal(int numIterationsPerAnneal) {
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

    private static final int defaultNumIterationsPerSample = 10_000; // should depend on number of beads and size of resize steps 100_000
    private static final int defaultNumSamples = 10_000; //should depend on desired relative precision of result 10_000 high precision; 1_000 low precision
    private static final int defaultNumIterationsPerAnneal = 300_000; //300_000
    private static final long serialVersionUID = 0L;

    public static SimulationRunnerParameters defaultSimulationRunnerParameters() {
        final SimulationRunnerParameters defaultSimulationRunnerParameters;
        defaultSimulationRunnerParameters = new SimulationRunnerParameters(defaultNumIterationsPerSample, defaultNumSamples, defaultNumIterationsPerAnneal);
        return defaultSimulationRunnerParameters;
    }

    final int numIterationsPerSample;
    private final int numSamples;
    private final int numIterationsPerAnneal;

    public SimulationRunnerParameters(int numIterationsPerSample, int numSamples, int numIterationsPerAnneal) {
        this.numIterationsPerSample = numIterationsPerSample;
        this.numSamples = numSamples;
        this.numIterationsPerAnneal = numIterationsPerAnneal;
    }

    private SimulationRunnerParameters(SimulationRunnerParametersBuilder simulationRunnerParametersBuilder) {
        numIterationsPerSample = simulationRunnerParametersBuilder.getNumIterationsPerSample();
        numSamples = simulationRunnerParametersBuilder.getNumSamples();
        numIterationsPerAnneal = simulationRunnerParametersBuilder.getNumIterationsPerAnneal();
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
