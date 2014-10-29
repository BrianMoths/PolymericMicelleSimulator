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
        return makeEquilibrationTestInputs();
    }

    private static List<Input> makeHorizontalRescalingInputs() {
        final double[] horizontalScaleFactors = {.5, 1, 2, 4};
        final double[] forceFactors = {0, 1, 3, 10};

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

    private static List<Input> makeEquilibrationTestInputs() {
        final double[] horizontalScaleFactors = {.5, 1};
        final double[] forceFactors = {0, 1, 3};

        final List<Input> inputs = new ArrayList<>();
        int jobNumber = 1;
        for (double horizontalScaleFactor : horizontalScaleFactors) {
            for (double forceFactor : forceFactors) {
                InputBuilder inputBuilder = makeHorizontallyRescaledInputBuilder(horizontalScaleFactor, jobNumber);
                SimulationRunnerParametersBuilder simulationRunnerParametersBuilder = inputBuilder.getJobParametersBuilder().getSimulationRunnerParametersBuilder();
                inputBuilder.getJobParametersBuilder().setNumAnneals(200);
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
        InputBuilder inputBuilder = getDefaultInputDensityBuilder();
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
    static private final double defaultDensity = .37;//.7

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
        final double layerSpacingCoefficient = 21.45 / Math.pow(8, 2. / 3.);
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
