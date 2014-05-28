/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FocusedSimulation;

import FocusedSimulation.SimulationRunnerParameters;
import FocusedSimulation.SimulationRunnerParameters.SimulationRunnerParametersBuilder;
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
            jobParametersBuilder.setNumSimulationTrials(defaultNumSurfaceTensionTrials);
            jobParametersBuilder.setJobNumber(defaultJobNumber);
            jobParametersBuilder.setSimulationRunnerParameters(SimulationRunnerParametersBuilder.deSimulationRunnerParametersBuilder());

            return jobParametersBuilder;
        }

        private static final long serialVersionUID = 0L;
        private int numAnneals; //50
        private int numSimulationTrials; //70
        private boolean shouldIterateUntilConvergence;
        private int jobNumber;
        private SimulationRunnerParametersBuilder simulationRunnerParametersBuilder;

        public JobParametersBuilder() {
        }

        public FocusedSimulation.JobParameters buildJobParameters() {
            return new FocusedSimulation.JobParameters(this);
        }

        public int getNumAnneals() {
            return numAnneals;
        }

        public void setNumAnneals(int numAnneals) {
            this.numAnneals = numAnneals;
        }

        public int getNumSimulationTrials() {
            return numSimulationTrials;
        }

        public void setNumSimulationTrials(int numSimulationTrials) {
            this.numSimulationTrials = numSimulationTrials;
        }

        public boolean getShouldIterateUntilConvergence() {
            return shouldIterateUntilConvergence;
        }

        public void setShouldIterateUntilConvergence(boolean shouldIterateUntilConvergence) {
            this.shouldIterateUntilConvergence = shouldIterateUntilConvergence;
        }

        public int getJobNumber() {
            return jobNumber;
        }

        public void setJobNumber(int jobNumber) {
            this.jobNumber = jobNumber;
        }

        public SimulationRunnerParametersBuilder getSimulationRunnerParametersBuilder() {
            return simulationRunnerParametersBuilder;
        }

        public void setSimulationRunnerParameters(SimulationRunnerParametersBuilder simulationRunnerParametersBuilder) {
            this.simulationRunnerParametersBuilder = simulationRunnerParametersBuilder;
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
    private final SimulationRunnerParameters simulationRunnerParametersBuilder;

    public JobParameters(int numAnneals, int numSurfaceTensionTrials, boolean shouldIterateUntilConvergence, int jobNumber, SimulationRunnerParameters simulationRunnerParameters) {
        this.numAnneals = numAnneals;
        this.numSurfaceTensionTrials = numSurfaceTensionTrials;
        this.shouldIterateUntilConvergence = shouldIterateUntilConvergence;
        this.jobNumber = jobNumber;
        this.simulationRunnerParametersBuilder = simulationRunnerParameters;
    }

    private JobParameters(JobParametersBuilder jobParametersBuilder) {
        numAnneals = jobParametersBuilder.getNumAnneals();
        numSurfaceTensionTrials = jobParametersBuilder.getNumSimulationTrials();
        shouldIterateUntilConvergence = jobParametersBuilder.getShouldIterateUntilConvergence();
        jobNumber = jobParametersBuilder.getJobNumber();
        simulationRunnerParametersBuilder = jobParametersBuilder.simulationRunnerParametersBuilder.build();
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
        return simulationRunnerParametersBuilder;
    }

}
