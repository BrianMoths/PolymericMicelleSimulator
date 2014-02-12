/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SGEManagement;

import Engine.SimulatorParameters;
import Engine.SimulatorParameters.SystemParametersBuilder;
import FocusedSimulation.JobParameters;
import FocusedSimulation.JobParameters.JobParametersBuilder;

/**
 *
 * @author bmoths
 */
public class Input {

    static public class InputBuilder {

        static public Input.InputBuilder getDefaultInputBuilder() {
            SystemParametersBuilder systemParametersBuilder = SimulatorParameters.SystemParametersBuilder.getDefaultSystemParametersBuilder();
            JobParametersBuilder jobParametersBuilder = JobParametersBuilder.getDefaultJobParametersBuilder();
            Input.InputBuilder inputBuilder = new SGEManagement.Input.InputBuilder();
            inputBuilder.setSystemParametersBuilder(systemParametersBuilder);
            inputBuilder.setJobParametersBuilder(jobParametersBuilder);
            return inputBuilder;
        }

        public SystemParametersBuilder systemParametersBuilder;
        public JobParametersBuilder jobParametersBuilder;

        public Input buildInput() {
            return new Input(systemParametersBuilder.buildSystemParameters(), jobParametersBuilder.buildJobParameters());
        }

        public SystemParametersBuilder getSystemParametersBuilder() {
            return systemParametersBuilder;
        }

        public void setSystemParametersBuilder(SystemParametersBuilder systemParametersBuilder) {
            this.systemParametersBuilder = systemParametersBuilder;
        }

        public JobParametersBuilder getJobParametersBuilder() {
            return jobParametersBuilder;
        }

        public void setJobParametersBuilder(JobParametersBuilder jobParametersBuilder) {
            this.jobParametersBuilder = jobParametersBuilder;
        }

    }

    public SimulatorParameters systemParameters;
    public JobParameters jobParameters;

    public Input(SimulatorParameters systemParameters, JobParameters jobParameters) {
        this.systemParameters = systemParameters;
        this.jobParameters = jobParameters;
    }

    public SimulatorParameters getSystemParameters() {
        return systemParameters;
    }

    public JobParameters getJobParameters() {
        return jobParameters;
    }

    public int getJobNumber() {
        return jobParameters.getJobNumber();
    }

}
