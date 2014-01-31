/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SGEManagement;

import Engine.Energetics.EnergeticsConstants.EnergeticsConstantsBuilder;
import Engine.Energetics.ExternalEnergyCalculator.ExternalEnergyCalculatorBuilder;
import Engine.PolymerTopology.PolymerChain;
import Engine.PolymerTopology.PolymerCluster;
import FocusedSimulation.OutputWriter;
import FocusedSimulation.SurfaceTensionFinder.JobParameters;
import FocusedSimulation.SurfaceTensionFinder.JobParameters.JobParametersBuilder;
import FocusedSimulation.SurfaceTensionFinder.SimulatorParameters;
import FocusedSimulation.SurfaceTensionFinder.SimulatorParameters.SystemParametersBuilder;
import SGEManagement.SGEManager.Input.InputBuilder;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bmoths
 */
public class SGEManager {

    static public class Input implements java.io.Serializable {

        static public class InputBuilder {

            static private final double defaultAspectRatio = .1;
            static private final double defaultOverlapCoefficient = -.06;
            static private final double defaultInteractionLength = 4.;
            static private final int defaultNumBeadsPerChain = 15;
            static private final int defaultNumChains = 100;
            static private final double defaultDensity = .05;
            static private final double defaultXPosition = 50;
            static private final double defaultSpringConstant = 10;

            static public InputBuilder getDefaultInputBuilder() {
                SystemParametersBuilder systemParametersBuilder = getDefaultSystemParametersBuilder();
                JobParametersBuilder jobParametersBuilder = JobParametersBuilder.getDefaultJobParametersBuilder();
                InputBuilder inputBuilder = new InputBuilder();
                inputBuilder.setSystemParametersBuilder(systemParametersBuilder);
                inputBuilder.setJobParametersBuilder(jobParametersBuilder);
                return inputBuilder;
            }

            private static SystemParametersBuilder getDefaultSystemParametersBuilder() {
                SystemParametersBuilder systemParametersBuilder = new SystemParametersBuilder();
                systemParametersBuilder.setAspectRatio(defaultAspectRatio);
                EnergeticsConstantsBuilder energeticsConstantsBuilder = EnergeticsConstantsBuilder.zeroEnergeticsConstantsBuilder();
                energeticsConstantsBuilder.setBBOverlapCoefficient(defaultOverlapCoefficient);
                ExternalEnergyCalculatorBuilder externalEnergyCalculatorBuilder = new ExternalEnergyCalculatorBuilder();
                externalEnergyCalculatorBuilder.setXPositionAndSpringConstant(defaultXPosition, defaultSpringConstant);
                energeticsConstantsBuilder.setExternalEnergyCalculator(externalEnergyCalculatorBuilder.build());
                systemParametersBuilder.setEnergeticsConstantsBuilder(energeticsConstantsBuilder);
                systemParametersBuilder.setInteractionLength(defaultInteractionLength);
                systemParametersBuilder.setPolymerCluster(getDefaultPolymerCluster());
                return systemParametersBuilder;
            }

            private static PolymerCluster getDefaultPolymerCluster() {
                PolymerChain polymerChain = PolymerChain.makeChainStartingWithA(0, defaultNumBeadsPerChain);
                PolymerCluster polymerCluster = PolymerCluster.makeRepeatedChainCluster(polymerChain, defaultNumChains);
                polymerCluster.setConcentrationInWater(defaultDensity);
                return polymerCluster;
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

    public static void main(String[] args) {
        final List<Input> inputs = makeInputs();
        submitJobs(inputs);
    }

    static private List<Input> makeInputs() {
        List<Input> inputs = new ArrayList<>();

        int jobNumber = 1;
//        int numChains = 100 / 3;
        double a = 10;
        double b = 50 / 3;
        double density = .05;
        Input input;
        double scaleFactor;

///////////Different vertical sizes of the bridge.
        scaleFactor = 1. / 10.;
        input = makeRescaleInput(scaleFactor, jobNumber);
        inputs.add(input);
        jobNumber++;

        scaleFactor = 1. / 5.;
        input = makeRescaleInput(scaleFactor, jobNumber);
        inputs.add(input);
        jobNumber++;

        scaleFactor = 1. / 3.;
        input = makeRescaleInput(scaleFactor, jobNumber);
        inputs.add(input);
        jobNumber++;

        scaleFactor = 1. / 1.5;
        input = makeRescaleInput(scaleFactor, jobNumber);
        inputs.add(input);
        jobNumber++;

        scaleFactor = 1.;
        input = makeRescaleInput(scaleFactor, jobNumber);
        inputs.add(input);
        jobNumber++;

        scaleFactor = 1.5;
        input = makeRescaleInput(scaleFactor, jobNumber);
        inputs.add(input);
        jobNumber++;

        scaleFactor = 2;
        input = makeRescaleInput(scaleFactor, jobNumber);
        inputs.add(input);
        jobNumber++;

        scaleFactor = 3;
        input = makeRescaleInput(scaleFactor, jobNumber);
        inputs.add(input);
        jobNumber++;

        return inputs;


///////////////////Repeatability
//        InputBuilder inputBuilder;
//
//        inputBuilder = SGEManager.makeRescaleInputBuilder(.5, jobNumber);
//        inputBuilder.getSystemParametersBuilder().getPolymerCluster().setConcentrationInWater(1);
//        inputBuilder.getSystemParametersBuilder().getEnergeticsConstantsBuilder().setExternalEnergyCalculator(new ExternalEnergyCalculator());
//        inputBuilder.getSystemParametersBuilder().setAspectRatio(1);
//        inputBuilder.getJobParametersBuilder().setNumAnneals(10);
//        inputBuilder.getJobParametersBuilder().setNumSurfaceTensionTrials(10);
//        inputBuilder.getJobParametersBuilder().setShouldIterateUntilConvergence(false);
//        inputs.add(inputBuilder.buildInput());
//        jobNumber++;
//
//        inputBuilder = SGEManager.makeRescaleInputBuilder(.5, jobNumber);
//        inputBuilder.getSystemParametersBuilder().getPolymerCluster().setConcentrationInWater(1);
//        inputBuilder.getSystemParametersBuilder().getEnergeticsConstantsBuilder().setExternalEnergyCalculator(new ExternalEnergyCalculator());
//        inputBuilder.getSystemParametersBuilder().setAspectRatio(1);
//        inputBuilder.getJobParametersBuilder().setNumAnneals(10);
//        inputBuilder.getJobParametersBuilder().setNumSurfaceTensionTrials(10);
//        inputBuilder.getJobParametersBuilder().setShouldIterateUntilConvergence(false);
//        inputs.add(inputBuilder.buildInput());
//        jobNumber++;
//
//        inputBuilder = SGEManager.makeRescaleInputBuilder(.5, jobNumber);
//        inputBuilder.getSystemParametersBuilder().getPolymerCluster().setConcentrationInWater(1);
//        inputBuilder.getSystemParametersBuilder().getEnergeticsConstantsBuilder().setExternalEnergyCalculator(new ExternalEnergyCalculator());
//        inputBuilder.getSystemParametersBuilder().setAspectRatio(1);
//        inputBuilder.getJobParametersBuilder().setNumAnneals(10);
//        inputBuilder.getJobParametersBuilder().setNumSurfaceTensionTrials(10);
//        inputBuilder.getJobParametersBuilder().setShouldIterateUntilConvergence(false);
//        inputs.add(inputBuilder.buildInput());
//        jobNumber++;
//
//        inputBuilder = SGEManager.makeRescaleInputBuilder(.5, jobNumber);
//        inputBuilder.getSystemParametersBuilder().getPolymerCluster().setConcentrationInWater(1);
//        inputBuilder.getSystemParametersBuilder().getEnergeticsConstantsBuilder().setExternalEnergyCalculator(new ExternalEnergyCalculator());
//        inputBuilder.getSystemParametersBuilder().setAspectRatio(1);
//        inputBuilder.getJobParametersBuilder().setNumAnneals(10);
//        inputBuilder.getJobParametersBuilder().setNumSurfaceTensionTrials(10);
//        inputBuilder.getJobParametersBuilder().setShouldIterateUntilConvergence(false);
//        inputs.add(inputBuilder.buildInput());
//        jobNumber++;
//
//        inputBuilder = SGEManager.makeRescaleInputBuilder(.5, jobNumber);
//        inputBuilder.getSystemParametersBuilder().getPolymerCluster().setConcentrationInWater(1);
//        inputBuilder.getSystemParametersBuilder().getEnergeticsConstantsBuilder().setExternalEnergyCalculator(new ExternalEnergyCalculator());
//        inputBuilder.getSystemParametersBuilder().setAspectRatio(1);
//        inputBuilder.getJobParametersBuilder().setNumAnneals(10);
//        inputBuilder.getJobParametersBuilder().setNumSurfaceTensionTrials(10);
//        inputBuilder.getJobParametersBuilder().setShouldIterateUntilConvergence(false);
//        inputs.add(inputBuilder.buildInput());
//        jobNumber++;
//
//        inputBuilder = SGEManager.makeRescaleInputBuilder(.5, jobNumber);
//        inputBuilder.getSystemParametersBuilder().getPolymerCluster().setConcentrationInWater(1);
//        inputBuilder.getSystemParametersBuilder().getEnergeticsConstantsBuilder().setExternalEnergyCalculator(new ExternalEnergyCalculator());
//        inputBuilder.getSystemParametersBuilder().setAspectRatio(1);
//        inputBuilder.getJobParametersBuilder().setNumAnneals(10);
//        inputBuilder.getJobParametersBuilder().setNumSurfaceTensionTrials(10);
//        inputBuilder.getJobParametersBuilder().setShouldIterateUntilConvergence(false);
//        inputs.add(inputBuilder.buildInput());
//        jobNumber++;




/////////////Effect of aspect ratio on natural density.
//        inputBuilder = SGEManager.makeRescaleInputBuilder(.5, jobNumber);
//        inputBuilder.getSystemParametersBuilder().getPolymerCluster().setConcentrationInWater(1);
//        inputBuilder.getSystemParametersBuilder().getEnergeticsConstantsBuilder().setExternalEnergyCalculator(new ExternalEnergyCalculator());
//        inputBuilder.getSystemParametersBuilder().setAspectRatio(.1);
//        inputBuilder.getJobParametersBuilder().setNumAnneals(10);
//        inputBuilder.getJobParametersBuilder().setNumSurfaceTensionTrials(10);
//        inputBuilder.getJobParametersBuilder().setShouldIterateUntilConvergence(false);
//        inputs.add(inputBuilder.buildInput());
//        jobNumber++;
//
//        inputBuilder = SGEManager.makeRescaleInputBuilder(.5, jobNumber);
//        inputBuilder.getSystemParametersBuilder().getPolymerCluster().setConcentrationInWater(1);
//        inputBuilder.getSystemParametersBuilder().getEnergeticsConstantsBuilder().setExternalEnergyCalculator(new ExternalEnergyCalculator());
//        inputBuilder.getSystemParametersBuilder().setAspectRatio(.1);
//        inputBuilder.getJobParametersBuilder().setNumAnneals(10);
//        inputBuilder.getJobParametersBuilder().setNumSurfaceTensionTrials(10);
//        inputBuilder.getJobParametersBuilder().setShouldIterateUntilConvergence(false);
//        inputs.add(inputBuilder.buildInput());
//        jobNumber++;
//
//        inputBuilder = SGEManager.makeRescaleInputBuilder(.5, jobNumber);
//        inputBuilder.getSystemParametersBuilder().getPolymerCluster().setConcentrationInWater(1);
//        inputBuilder.getSystemParametersBuilder().getEnergeticsConstantsBuilder().setExternalEnergyCalculator(new ExternalEnergyCalculator());
//        inputBuilder.getSystemParametersBuilder().setAspectRatio(.1);
//        inputBuilder.getJobParametersBuilder().setNumAnneals(10);
//        inputBuilder.getJobParametersBuilder().setNumSurfaceTensionTrials(10);
//        inputBuilder.getJobParametersBuilder().setShouldIterateUntilConvergence(false);
//        inputs.add(inputBuilder.buildInput());
//        jobNumber++;
//
//        inputBuilder = SGEManager.makeRescaleInputBuilder(.5, jobNumber);
//        inputBuilder.getSystemParametersBuilder().getPolymerCluster().setConcentrationInWater(1);
//        inputBuilder.getSystemParametersBuilder().getEnergeticsConstantsBuilder().setExternalEnergyCalculator(new ExternalEnergyCalculator());
//        inputBuilder.getSystemParametersBuilder().setAspectRatio(.1);
//        inputBuilder.getJobParametersBuilder().setNumAnneals(10);
//        inputBuilder.getJobParametersBuilder().setNumSurfaceTensionTrials(10);
//        inputBuilder.getJobParametersBuilder().setShouldIterateUntilConvergence(false);
//        inputs.add(inputBuilder.buildInput());
//        jobNumber++;
//
//        inputBuilder = SGEManager.makeRescaleInputBuilder(.5, jobNumber);
//        inputBuilder.getSystemParametersBuilder().getPolymerCluster().setConcentrationInWater(1);
//        inputBuilder.getSystemParametersBuilder().getEnergeticsConstantsBuilder().setExternalEnergyCalculator(new ExternalEnergyCalculator());
//        inputBuilder.getSystemParametersBuilder().setAspectRatio(.3);
//        inputBuilder.getJobParametersBuilder().setNumAnneals(10);
//        inputBuilder.getJobParametersBuilder().setNumSurfaceTensionTrials(10);
//        inputBuilder.getJobParametersBuilder().setShouldIterateUntilConvergence(false);
//        inputs.add(inputBuilder.buildInput());
//        jobNumber++;
//
//        inputBuilder = SGEManager.makeRescaleInputBuilder(.5, jobNumber);
//        inputBuilder.getSystemParametersBuilder().getPolymerCluster().setConcentrationInWater(1);
//        inputBuilder.getSystemParametersBuilder().getEnergeticsConstantsBuilder().setExternalEnergyCalculator(new ExternalEnergyCalculator());
//        inputBuilder.getSystemParametersBuilder().setAspectRatio(.3);
//        inputBuilder.getJobParametersBuilder().setNumAnneals(10);
//        inputBuilder.getJobParametersBuilder().setNumSurfaceTensionTrials(10);
//        inputBuilder.getJobParametersBuilder().setShouldIterateUntilConvergence(false);
//        inputs.add(inputBuilder.buildInput());
//        jobNumber++;
//
//        return inputs;
    }

    public static Input makeRescaleInput(final double scaleFactor, int jobNumber) {
        InputBuilder inputBuilder = makeRescaleInputBuilder(scaleFactor, jobNumber);
        final Input input = inputBuilder.buildInput();
        return input;
    }

    public static InputBuilder makeRescaleInputBuilder(final double scaleFactor, int jobNumber) {
        InputBuilder inputBuilder;
        inputBuilder = InputBuilder.getDefaultInputBuilder();
        final double aspectRatio = inputBuilder.getSystemParametersBuilder().getAspectRatio() / 3.5;
        inputBuilder.getSystemParametersBuilder().setAspectRatio(aspectRatio / scaleFactor);
        final int numChains = inputBuilder.getSystemParametersBuilder().getPolymerCluster().getNumChains();
        final int numBeadsPerChain = (int) Math.round(inputBuilder.getSystemParametersBuilder().getPolymerCluster().getNumBeadsPerChain());
        final PolymerChain polymerChain = PolymerChain.makeChainOfType(false, numBeadsPerChain);
        final PolymerCluster polymerCluster = PolymerCluster.makeRepeatedChainCluster(polymerChain, (int) (numChains * scaleFactor));
        polymerCluster.setConcentrationInWater(.05 * 3.5);
        inputBuilder.getSystemParametersBuilder().setPolymerCluster(polymerCluster);
        final ExternalEnergyCalculatorBuilder externalEnergyCalculatorBuilder = new ExternalEnergyCalculatorBuilder();
        externalEnergyCalculatorBuilder.setXPositionAndSpringConstant(16, 50);
        inputBuilder.getSystemParametersBuilder().getEnergeticsConstantsBuilder().setExternalEnergyCalculator(externalEnergyCalculatorBuilder.build());
        inputBuilder.getJobParametersBuilder().setJobNumber(jobNumber);
        inputBuilder.getJobParametersBuilder().setNumSurfaceTensionTrials(1);
        inputBuilder.getJobParametersBuilder().setNumSurfaceTensionTrials(70);
        return inputBuilder;
    }

    static private void submitJobs(List<Input> inputs) {
        final String commandExceptInput = makeCommandExceptInput();
        for (Input input : inputs) {
            submitJob(commandExceptInput, input);
        }
    }

    //<editor-fold defaultstate="collapsed" desc="makeCommandExceptInput">
    static private String makeCommandExceptInput() {
        final String path = getPath();
        final String jarName = getJarName();
        StringBuilder commandBuilder = new StringBuilder();
        commandBuilder.append("java -jar ")
                .append(path)
                .append("/")
                .append(jarName);

        return commandBuilder.toString();
    }

    static private String getPath() {
        return "/home/bmoths/Desktop/projects/polymerMicelles/simulation/PolymericMicelles/dist";
    }

    static private String getJarName() {
        return "PolymericMicelles.jar";
    }
    //</editor-fold>

    static private void submitJob(String commandExceptInput, Input input) {
        final String fileName = makeFileName(input);
        final String path = makePath(fileName);
        makeInputFIle(path, input);
        final String completeCommand = makeCompleteCommand(commandExceptInput, path);
        QSubAdapter.runCommandForQsub(completeCommand);
    }

    static private String makeCompleteCommand(String commandExceptInput, String inputString) {
        StringBuilder completeCommandBuilder = new StringBuilder();
        completeCommandBuilder.append(commandExceptInput)
                .append(" ")
                .append(inputString);
        return completeCommandBuilder.toString();
    }

    static private String makeFileName(Input input) {
        return OutputWriter.makeDatePrefix() + "_" + OutputWriter.makeDoubleDigitString(input.getJobNumber());
    }

    static private String makePath(String fileName) {
        return "../simulationInputs/" + fileName;
    }

    private static void makeInputFIle(String relativePath, Input input) {
        try {
            final String absolutePath = OutputWriter.getProjectPath() + relativePath;
            FileOutputStream fileOutputStream = new FileOutputStream(absolutePath);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(input);
        } catch (FileNotFoundException ex) {
            throw new AssertionError("input file could not be made", ex);
        } catch (IOException ex) {
            Logger.getLogger(SGEManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
