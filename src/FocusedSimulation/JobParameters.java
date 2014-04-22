/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FocusedSimulation;

import FocusedSimulation.SimulationRunner.SimulationRunnerParameters;
import java.io.Serializable;

/**
 *
 * @author bmoths
 */
public class JobParameters implements Serializable {

    static public class JobParametersBuilder {

        static public JobParametersBuilder getDefaultJobParametersBuilder() {
            JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
            jobParametersBuilder.setNumAnneals(defaultNumAnneals);
            jobParametersBuilder.setNumSurfaceTensionTrials(defaultNumSurfaceTensionTrials);
            jobParametersBuilder.setJobNumber(defaultJobNumber);
            return jobParametersBuilder;
        }

        private static final long serialVersionUID = 0L;
        private int numAnneals; //50
        private int numSurfaceTensionTrials; //70
        private boolean shouldIterateUntilConvergence;
        private int jobNumber;
        private SimulationRunnerParameters simulationRunnerParameters;

        public JobParametersBuilder() {
        }

        public FocusedSimulation.JobParameters buildJobParameters() {
            return new FocusedSimulation.JobParameters(this);
        }

        public int getNumAnneals() {
            return numAnneals;
        }

        public JobParametersBuilder setNumAnneals(int numAnneals) {
            this.numAnneals = numAnneals;
            return this;
        }

        public int getNumSurfaceTensionTrials() {
            return numSurfaceTensionTrials;
        }

        public JobParametersBuilder setNumSurfaceTensionTrials(int numSurfaceTensionTrials) {
            this.numSurfaceTensionTrials = numSurfaceTensionTrials;
            return this;
        }

        public boolean getShouldIterateUntilConvergence() {
            return shouldIterateUntilConvergence;
        }

        public JobParametersBuilder setShouldIterateUntilConvergence(boolean shouldIterateUntilConvergence) {
            this.shouldIterateUntilConvergence = shouldIterateUntilConvergence;
            return this;
        }

        public int getJobNumber() {
            return jobNumber;
        }

        public JobParametersBuilder setJobNumber(int jobNumber) {
            this.jobNumber = jobNumber;
            return this;
        }

        public SimulationRunnerParameters getSimulationRunnerParameters() {
            return simulationRunnerParameters;
        }

        public JobParametersBuilder setSimulationRunnerParameters(SimulationRunnerParameters simulationRunnerParameters) {
            this.simulationRunnerParameters = simulationRunnerParameters;
            return this;
        }

    }

    static private final int defaultNumAnneals = 50;
    static private final int defaultNumSurfaceTensionTrials = 70;
    static private final int defaultJobNumber = 0;

    static public FocusedSimulation.JobParameters getDefaultJobParameters() {
        return new FocusedSimulation.JobParameters(defaultNumAnneals, defaultNumSurfaceTensionTrials, true, defaultJobNumber, SimulationRunnerParameters.defaultSimulationRunnerParameters());
    }

    private final int numAnneals; //50
    private final int numSurfaceTensionTrials; //70
    private final boolean shouldIterateUntilConvergence;
    private final int jobNumber;
    private final SimulationRunnerParameters simulationRunnerParameters;

    public JobParameters(int numAnneals, int numSurfaceTensionTrials, boolean shouldIterateUntilConvergence, int jobNumber, SimulationRunnerParameters simulationRunnerParameters) {
        this.numAnneals = numAnneals;
        this.numSurfaceTensionTrials = numSurfaceTensionTrials;
        this.shouldIterateUntilConvergence = shouldIterateUntilConvergence;
        this.jobNumber = jobNumber;
        this.simulationRunnerParameters = simulationRunnerParameters;
    }

    private JobParameters(JobParametersBuilder jobParametersBuilder) {
        numAnneals = jobParametersBuilder.getNumAnneals();
        numSurfaceTensionTrials = jobParametersBuilder.getNumSurfaceTensionTrials();
        shouldIterateUntilConvergence = jobParametersBuilder.getShouldIterateUntilConvergence();
        jobNumber = jobParametersBuilder.getJobNumber();
        simulationRunnerParameters = jobParametersBuilder.simulationRunnerParameters;
    }

    public int getNumAnneals() {
        return numAnneals;
    }

    public int getNumSurfaceTensionTrials() {
        return numSurfaceTensionTrials;
    }

    public boolean getShouldIterateUntilConvergence() {
        return shouldIterateUntilConvergence;
    }

    public int getJobNumber() {
        return jobNumber;
    }

    public SimulationRunnerParameters getSimulationRunnerParameters() {
        return simulationRunnerParameters;
    }

}
