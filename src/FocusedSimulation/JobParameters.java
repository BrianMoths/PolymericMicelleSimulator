/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FocusedSimulation;

import FocusedSimulation.simulationrunner.SimulationRunnerParameters;
import FocusedSimulation.simulationrunner.SimulationRunnerParameters.SimulationRunnerParametersBuilder;
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
            jobParametersBuilder.setShouldIterateUntilConvergence(false);
            jobParametersBuilder.setConvergencePrecision(defaultConvergencePrecision);
            jobParametersBuilder.setSimulationRunnerParameters(SimulationRunnerParametersBuilder.deSimulationRunnerParametersBuilder());

            return jobParametersBuilder;
        }

        private static final long serialVersionUID = 0L;
        private int numAnneals; //50
        private int numSimulationTrials; //70
        private boolean shouldIterateUntilConvergence;
        private double convergencePrecision;
        private int jobNumber;
        private String jobString = "";
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

        public double getConvergencePrecision() {
            return convergencePrecision;
        }

        public void setConvergencePrecision(double convergencePrecision) {
            this.convergencePrecision = convergencePrecision;
        }

        public int getJobNumber() {
            return jobNumber;
        }

        public void setJobNumber(int jobNumber) {
            this.jobNumber = jobNumber;
        }

        public String getJobString() {
            return jobString;
        }

        public void setJobString(String jobString) {
            this.jobString = jobString;
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
    static private final double defaultConvergencePrecision = .1;
    private final int numAnneals; //50
    private final int numSurfaceTensionTrials; //70
    private final boolean shouldIterateUntilConvergence;
    private final double convergencePrecision;
    private final int jobNumber;
    private final String jobString;
    private final SimulationRunnerParameters simulationRunnerParameters;

    private JobParameters(int numAnneals, int numSurfaceTensionTrials, boolean shouldIterateUntilConvergence, double precision, int jobNumber, String jobString, SimulationRunnerParameters simulationRunnerParameters) {
        this.numAnneals = numAnneals;
        this.numSurfaceTensionTrials = numSurfaceTensionTrials;
        this.shouldIterateUntilConvergence = shouldIterateUntilConvergence;
        this.convergencePrecision = precision;
        this.jobNumber = jobNumber;
        this.jobString = jobString;
        this.simulationRunnerParameters = simulationRunnerParameters;
    }

    private JobParameters(JobParametersBuilder jobParametersBuilder) {
        numAnneals = jobParametersBuilder.getNumAnneals();
        numSurfaceTensionTrials = jobParametersBuilder.getNumSimulationTrials();
        shouldIterateUntilConvergence = jobParametersBuilder.getShouldIterateUntilConvergence();
        convergencePrecision = jobParametersBuilder.getConvergencePrecision();
        jobNumber = jobParametersBuilder.getJobNumber();
        jobString = jobParametersBuilder.getJobString();
        simulationRunnerParameters = jobParametersBuilder.simulationRunnerParametersBuilder.build();
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

    public double getConvergencePrecision() {
        return convergencePrecision;
    }

    public int getJobNumber() {
        return jobNumber;
    }

    public String getJobString() {
        return jobString;
    }

    public SimulationRunnerParameters getSimulationRunnerParameters() {
        return simulationRunnerParameters;
    }

}
