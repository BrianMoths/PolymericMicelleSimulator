/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FocusedSimulation.diblockenergybalance;

import Engine.Energetics.EnergeticsConstants.EnergeticsConstantsBuilder;
import Engine.Energetics.ExternalEnergyCalculator.ExternalEnergyCalculatorBuilder;
import Engine.PolymerTopology.PolymerCluster;
import Engine.SimulatorParameters.SystemParametersBuilder;
import FocusedSimulation.AbstractFocusedSimulation;
import FocusedSimulation.JobParameters.JobParametersBuilder;
import FocusedSimulation.simulationrunner.SimulationRunnerParameters.SimulationRunnerParametersBuilder;
import SGEManagement.Input;
import SGEManagement.Input.InputBuilder;
import SGEManagement.JobSubmitter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author bmoths
 */
public class DiblockJobMaker {

    public static final String pathToFocusedSimulationClass = AbstractFocusedSimulation.pathToFocusedSimulation + "diblockenergybalance/DiblockFinder";

    public static void main(String[] args) {
        final List<Input> inputs = makeInputs();
        JobSubmitter.submitJobs(pathToFocusedSimulationClass, inputs);
    }

    static private List<Input> makeInputs() {
//        List<Input> inputs = new ArrayList<>();
//        inputs.add(makeHorizontallyRescaledInputBuilder(1, 1).buildInput());
        return makeLongRescalingInputsWithPressure();
    }

    private static List<Input> makeHorizontalRescalingInputs() {
        final double[] horizontalScaleFactors = {.5, 1, 2, 4};
        final double[] forceFactors = {0, 1, 3, 5};

        final List<Input> inputs = new ArrayList<>();
        int jobNumber = 1;
        for (double horizontalScaleFactor : horizontalScaleFactors) {
            for (double forceFactor : forceFactors) {
                InputBuilder inputBuilder = makeHorizontallyRescaledInputBuilder(horizontalScaleFactor, jobNumber);
                inputBuilder.getSystemParametersBuilder().getEnergeticsConstantsBuilder().getExternalEnergyCalculatorBuilder().setxTensionAndQuadratic(defaultXTension * forceFactor, 0);
                inputs.add(inputBuilder.buildInput());
                inputBuilder.getJobParametersBuilder().setJobString("EnergyScaling");
                jobNumber++;
            }
        }
        return inputs;
    }

    private static List<Input> makeLongRescalingInputsWithPressure() {
        final double horizontalScaleFactor = 1;
        final double[] forceFactors = {0, .5, 1, 1.5, 2};
        final double[] polymericityFactors = {.5, .75, 1, 1.25, 1.5, 1.75, 2};

        final List<Input> inputs = new ArrayList<>();
        int jobNumber = 1;
        for (double polymericityFactor : polymericityFactors) {
            for (double forceFactor : forceFactors) {
                inputs.add(makeRescalingInput(horizontalScaleFactor, jobNumber, polymericityFactor, forceFactor * defaultXTension).buildInput());
                jobNumber++;
            }
        }
        return inputs;
    }

    public static InputBuilder makeRescalingInput(double horizontalScaleFactor, int jobNumber, double polymericityFactor, double pressure) {
        InputBuilder inputBuilder = makeRescalingInput(horizontalScaleFactor, jobNumber, polymericityFactor);
        inputBuilder.getSystemParametersBuilder().getEnergeticsConstantsBuilder().getExternalEnergyCalculatorBuilder().setxTensionAndQuadratic(pressure, 0);
        inputBuilder.getJobParametersBuilder().setJobString("LayerSpacingWithPressure");
        return inputBuilder;
    }

    private static List<Input> makeLayerSpacingInputs() {
        final double[] horizontalScaleFactors = {.5, 1, 1.5, 2};
        final double[] polymericityFactors = {.5, 1, 2};

        final List<Input> inputs = new ArrayList<>();
        int jobNumber = 1;
        for (double polymericityFactor : polymericityFactors) {
            for (double horizontalScaleFactor : horizontalScaleFactors) {
                inputs.add(makeRescalingInput(horizontalScaleFactor, jobNumber, polymericityFactor).buildInput());
                jobNumber++;
            }
        }
        return inputs;
    }

    public static InputBuilder makeRescalingInput(double horizontalScaleFactor, int jobNumber, double polymericityFactor) {
        InputBuilder inputBuilder = makeHorizontallyRescaledInputBuilder(horizontalScaleFactor * Math.pow(polymericityFactor, 4. / 3.), jobNumber, polymericityFactor);
        inputBuilder.getSystemParametersBuilder().getEnergeticsConstantsBuilder().getExternalEnergyCalculatorBuilder().setxTensionAndQuadratic(0, 0);
        inputBuilder.getSystemParametersBuilder().setAspectRatio(inputBuilder.getSystemParametersBuilder().getAspectRatio() / Math.pow(polymericityFactor, 4. / 3.));
        inputBuilder.getJobParametersBuilder().setJobString("LayerSpacing");
        SimulationRunnerParametersBuilder simulationRunnerParametersBuilder = inputBuilder.getJobParametersBuilder().getSimulationRunnerParametersBuilder();
        inputBuilder.getJobParametersBuilder().setNumAnneals(20);
        final long hopefulNumIterations = 10_000_000L / 400 * (long) inputBuilder.getSystemParametersBuilder().getPolymerCluster().getNumBeads();
        simulationRunnerParametersBuilder.setNumIterationsPerSample((int) Math.min(hopefulNumIterations, 2_000_000_000));
        simulationRunnerParametersBuilder.setNumSamples(100);
        inputBuilder.getJobParametersBuilder().setNumSimulationTrials(1);
        return inputBuilder;
    }

    private static List<Input> makeEquilibrationTestInputs() {
        final double[] horizontalScaleFactors = {.5, 1};
        final double[] forceFactors = {0, 1, 3};

        final List<Input> inputs = new ArrayList<>();
        int jobNumber = 1;
        for (double horizontalScaleFactor : horizontalScaleFactors) {
            for (double forceFactor : forceFactors) {
                InputBuilder inputBuilder = makeHorizontallyRescaledInputBuilder(horizontalScaleFactor, jobNumber);
                inputBuilder.getJobParametersBuilder().setNumAnneals(200);
                SimulationRunnerParametersBuilder simulationRunnerParametersBuilder = inputBuilder.getJobParametersBuilder().getSimulationRunnerParametersBuilder();
                simulationRunnerParametersBuilder.setNumIterationsPerSample(100);
                simulationRunnerParametersBuilder.setNumSamples(10);
                inputBuilder.getJobParametersBuilder().setNumSimulationTrials(1000);
                inputBuilder.getSystemParametersBuilder().getEnergeticsConstantsBuilder().getExternalEnergyCalculatorBuilder().setxTensionAndQuadratic(defaultXTension * forceFactor, 0);
                inputs.add(inputBuilder.buildInput());
                inputBuilder.getJobParametersBuilder().setJobString("EnergyScaling");
                jobNumber++;
            }
        }
        return inputs;
    }

    public static InputBuilder makeHorizontallyRescaledInputBuilder(final double horizontalScale, final int jobNumber) {
        return makeHorizontallyRescaledInputBuilder(horizontalScale, jobNumber, 1);
    }

    public static InputBuilder makeHorizontallyRescaledInputBuilder(final double horizontalScale, final int jobNumber, double polymericityFactor) {
        InputBuilder inputBuilder = getDefaultInputDensityBuilder();
        final double defaultHydrophobicFraction = .5;
        PolymerCluster polymerCluster = getPolymerCluster(defaultHydrophobicFraction, (int) (defaultNumChains / polymericityFactor), (int) (defaultNumBeadsPerChain * polymericityFactor), 1);
        inputBuilder.systemParametersBuilder.setPolymerCluster(polymerCluster);
        inputBuilder.rescale(1, horizontalScale);
        inputBuilder.getJobParametersBuilder().setJobNumber(jobNumber);
        inputBuilder.getSystemParametersBuilder().getEnergeticsConstantsBuilder().setExternalEnergyCalculatorBuilder(makeExternalEnergyCalculatorBuilder());
        return inputBuilder;
    }

    //<editor-fold defaultstate="collapsed" desc="default input">
    static private InputBuilder getDefaultInputDensityBuilder() {
        SystemParametersBuilder systemParametersBuilder = getDefaultSystemParametersBuilder(defaultNumBeadsPerChain);
        JobParametersBuilder jobParametersBuilder = getDefaultJobParametersBuilder();
        jobParametersBuilder.setJobString("DiblockEnergyBalance");
        Input.InputBuilder inputBuilder = new SGEManagement.Input.InputBuilder();
        inputBuilder.setSystemParametersBuilder(systemParametersBuilder);
        inputBuilder.setJobParametersBuilder(jobParametersBuilder);
        return inputBuilder;
    }

    static private final double defaultOverlapCoefficient = -.378;
    static private final double defaultInteractionLength = 4.;
    static private final int defaultNumBeadsPerChain = 16;
    static private final int defaultNumChains = 25;
    static private final double defaultDensity = .3;//.7

    private static SystemParametersBuilder getDefaultSystemParametersBuilder(int numBeadsPerChain) {
        SystemParametersBuilder systemParametersBuilder = new SystemParametersBuilder();
        final double aspectRatio = getAspectRatio(numBeadsPerChain, defaultNumChains);
        systemParametersBuilder.setAspectRatio(aspectRatio);
        systemParametersBuilder.setEnergeticsConstantsBuilder(makeDefaultEnergeticsConstantsBuilder());
        systemParametersBuilder.setInteractionLength(defaultInteractionLength);
        systemParametersBuilder.autosetCoreParameters();
        systemParametersBuilder.setPolymerCluster(getDefaultPolymerCluster());
        return systemParametersBuilder;
    }

    private static double findHeightFromNumBeadsPerChain(int numBeadsPerChain) {
        final double layerSpacingCoefficient = 29 / Math.pow(16., 2. / 3.);
        return layerSpacingCoefficient * Math.pow(numBeadsPerChain, 2. / 3.);
    }

    private static double findWidthFromNumBeadsPerChain(int numBeadsPerChain, int numChains, double height) {
        final double naturalDensity = .3;
        final double numBeads = numBeadsPerChain * numChains;
        final double volume = numBeads / naturalDensity;
        return volume / height;
    }

    private static PolymerCluster getDefaultPolymerCluster() {
        final double defaultHydrophobicFraction = .5;
        return getPolymerCluster(defaultHydrophobicFraction, defaultNumChains, defaultNumBeadsPerChain, 1);
    }

    public static PolymerCluster getPolymerCluster(double hydrophobicFraction, final int numChains, int numBeadsPerChain, int numSubblocks) {
        return PolymerCluster.makePolymerCluster(hydrophobicFraction, numChains, numBeadsPerChain, numSubblocks, defaultDensity);
    }

    static private final int defaultNumAnneals = 5;
    static private final int defaultNumSurfaceTensionTrials = 20;
    static private final int defaultJobNumber = 0;
    static private final double interfacialEnergy = 1.2;
    static private final double defaultXTension = -interfacialEnergy / 5;

    static public JobParametersBuilder getDefaultJobParametersBuilder() {
        JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
        jobParametersBuilder.setNumAnneals(defaultNumAnneals);
        jobParametersBuilder.setNumSimulationTrials(defaultNumSurfaceTensionTrials);
        jobParametersBuilder.setJobNumber(defaultJobNumber);
        return jobParametersBuilder;
    }

    private static ExternalEnergyCalculatorBuilder makeExternalEnergyCalculatorBuilder() {
        ExternalEnergyCalculatorBuilder externalEnergyCalculatorBuilder = new ExternalEnergyCalculatorBuilder();
        externalEnergyCalculatorBuilder.setxTensionAndQuadratic(-defaultXTension, 0);
//        externalEnergyCalculatorBuilder.setxTensionAndQuadratic(0, 0);
        return externalEnergyCalculatorBuilder;
    }

    private static EnergeticsConstantsBuilder makeDefaultEnergeticsConstantsBuilder() {
        EnergeticsConstantsBuilder energeticsConstantsBuilder = EnergeticsConstantsBuilder.zeroEnergeticsConstantsBuilder();
        energeticsConstantsBuilder.setBBOverlapCoefficient(defaultOverlapCoefficient);
        energeticsConstantsBuilder.setAAOverlapCoefficient(defaultOverlapCoefficient);
        energeticsConstantsBuilder.setABOverlapCoefficient(defaultOverlapCoefficient / 2);
        energeticsConstantsBuilder.setExternalEnergyCalculatorBuilder(makeExternalEnergyCalculatorBuilder());
        return energeticsConstantsBuilder;
    }

    private static double getAspectRatio(int numBeadsPerChain, int numChains) {
        final double systemHeight = findHeightFromNumBeadsPerChain(numBeadsPerChain);
        final double systemWidth = findWidthFromNumBeadsPerChain(numBeadsPerChain, numChains, systemHeight);
        return systemWidth / systemHeight;
    }
    //</editor-fold>

}
