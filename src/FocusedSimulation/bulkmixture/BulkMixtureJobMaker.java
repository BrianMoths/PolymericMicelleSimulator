/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FocusedSimulation.bulkmixture;

import Engine.Energetics.EnergeticsConstants.EnergeticsConstantsBuilder;
import Engine.PolymerTopology.PolymerChain;
import Engine.PolymerTopology.PolymerCluster;
import Engine.SimulatorParameters.SystemParametersBuilder;
import FocusedSimulation.AbstractFocusedSimulation;
import FocusedSimulation.JobParameters.JobParametersBuilder;
import SGEManagement.Input;
import SGEManagement.Input.InputBuilder;
import SGEManagement.JobSubmitter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author bmoths
 */
public class BulkMixtureJobMaker {

    public static final String pathToFocusedSimulationClass = AbstractFocusedSimulation.pathToFocusedSimulation + "bulkmixture/BulkMixtureFinder";

    public static void main(String[] args) {
        final List<Input> inputs = makeInputs();
        JobSubmitter.submitJobs(pathToFocusedSimulationClass, inputs);
    }

    static private List<Input> makeInputs() {
        return makeBulkMixtureInputs(1);
    }

    private static List<Input> makeBulkMixtureInputs() {
        return makeBulkMixtureInputsSmallAsymmetry(1);
    }

    private static List<Input> makeBulkMixtureInputs(int jobNumber) {
        final double[] verticalRescaleFactors = {.4, .6, .8, 1.2};
        final double[] horizontalRescaleFactors = {.5, .75, 1, 1.5};
        final double[] polymericityFactors = {.5, 1, 2};

        final List<Input> noSpringInputs = new ArrayList<>();

        for (int i = 0; i < verticalRescaleFactors.length; i++) {
            for (int j = 0; j < horizontalRescaleFactors.length; j++) {
                for (int k = 0; k < polymericityFactors.length; k++) {
                    final InputBuilder inputBuilder = makeRescaleInputBuilderWithHorizontalRescaling(verticalRescaleFactors[i], horizontalRescaleFactors[j], jobNumber);
                    inputBuilder.getJobParametersBuilder().getSimulationRunnerParametersBuilder().setNumIterationsPerAnneal(10000);
                    inputBuilder.getJobParametersBuilder().getSimulationRunnerParametersBuilder().setNumIterationsPerSample(30000);
                    inputBuilder.getJobParametersBuilder().getSimulationRunnerParametersBuilder().setNumSamples(2000);
                    inputBuilder.getJobParametersBuilder().setNumSimulationTrials(5);
                    final SystemParametersBuilder systemParametersBuilder = inputBuilder.systemParametersBuilder;
                    final int numChains = systemParametersBuilder.getPolymerCluster().getNumChains();
                    PolymerCluster newPolymerCluster = getPolymerCluster(.5, (int) (numChains * polymericityFactors[k]), (int) (16 / polymericityFactors[k]), 1);
                    systemParametersBuilder.setPolymerCluster(newPolymerCluster);
                    inputBuilder.getJobParametersBuilder().setJobString("SymmetricLayerSpacingShorterAgain");
                    noSpringInputs.add(inputBuilder.buildInput());
                    jobNumber++;
                }
            }
        }

        return noSpringInputs;
    }

    private static List<Input> makeBulkMixtureInputsSmallAsymmetry(int jobNumber) {
        final double[] verticalRescaleFactors = {.1, .15, .2, .3};
        final double[] horizontalRescaleFactors = {2, 3, 4, 6};
        final double[] polymericityFactors = {1};

        final List<Input> noSpringInputs = new ArrayList<>();

        for (int i = 0; i < verticalRescaleFactors.length; i++) {
            for (int j = 0; j < horizontalRescaleFactors.length; j++) {
                for (int k = 0; k < polymericityFactors.length; k++) {
                    final InputBuilder inputBuilder = makeRescaleInputBuilderWithHorizontalRescaling(verticalRescaleFactors[i], horizontalRescaleFactors[j], jobNumber);
                    inputBuilder.getJobParametersBuilder().getSimulationRunnerParametersBuilder().setNumIterationsPerAnneal(10000);
                    inputBuilder.getJobParametersBuilder().getSimulationRunnerParametersBuilder().setNumIterationsPerSample(30000);
                    inputBuilder.getJobParametersBuilder().getSimulationRunnerParametersBuilder().setNumSamples(2000);
                    final SystemParametersBuilder systemParametersBuilder = inputBuilder.systemParametersBuilder;
                    final int numChains = systemParametersBuilder.getPolymerCluster().getNumChains();
                    PolymerCluster newPolymerCluster = getPolymerCluster(.38, (int) (numChains * polymericityFactors[k]), (int) (16 / polymericityFactors[k]), 1);
                    systemParametersBuilder.setPolymerCluster(newPolymerCluster);
                    noSpringInputs.add(inputBuilder.buildInput());
                    jobNumber++;
                }
            }
        }

        return noSpringInputs;
    }

    private static List<Input> makeAsymmetryInputs(int jobNumber) {
        final double[] verticalRescaleFactors = {.15};
        final double[] horizontalRescaleFactors = {4};
        final double[] asymmetry = {.2578, .2734, .289, .3047, .32, .336, .352};

        final List<Input> noSpringInputs = new ArrayList<>();

        for (int i = 0; i < verticalRescaleFactors.length; i++) {
            for (int j = 0; j < horizontalRescaleFactors.length; j++) {
                for (int k = 0; k < asymmetry.length; k++) {
                    final InputBuilder inputBuilder = makeRescaleInputBuilderWithHorizontalRescaling(verticalRescaleFactors[i], horizontalRescaleFactors[j], jobNumber);
                    inputBuilder.getJobParametersBuilder().getSimulationRunnerParametersBuilder().setNumIterationsPerAnneal(10000);
                    inputBuilder.getJobParametersBuilder().getSimulationRunnerParametersBuilder().setNumIterationsPerSample(20000);
                    inputBuilder.getJobParametersBuilder().getSimulationRunnerParametersBuilder().setNumSamples(2000);
                    final SystemParametersBuilder systemParametersBuilder = inputBuilder.systemParametersBuilder;
                    final int numChains = systemParametersBuilder.getPolymerCluster().getNumChains();
                    PolymerCluster newPolymerCluster = getPolymerCluster(asymmetry[k], numChains / 2, 32, 1);
                    systemParametersBuilder.setPolymerCluster(newPolymerCluster);
                    noSpringInputs.add(inputBuilder.buildInput());
                    jobNumber++;
                    inputBuilder.getJobParametersBuilder().setJobNumber(jobNumber);
                    newPolymerCluster = getPolymerCluster(asymmetry[k], numChains / 4, 64, 1);
                    systemParametersBuilder.setPolymerCluster(newPolymerCluster);
                    noSpringInputs.add(inputBuilder.buildInput());
                    jobNumber++;
                }
            }
        }

        return noSpringInputs;
    }

    static private List<Input> makeResizingInputs(int jobNumber) {
        final double verticalRescaleFactor = .15;
        final double horizontalRescaleFactor = 4;
        final double asymmetry = .32;

        final List<Input> noSpringInputs = new ArrayList<>();


        for (int numChainsDifference = 0; numChainsDifference < 9; numChainsDifference++) {
            final InputBuilder inputBuilder = makeRescaleInputBuilderWithHorizontalRescaling(verticalRescaleFactor, horizontalRescaleFactor, jobNumber);
            inputBuilder.getJobParametersBuilder().setNumSimulationTrials(15);
            inputBuilder.getJobParametersBuilder().getSimulationRunnerParametersBuilder().setNumIterationsPerAnneal(10000);
            inputBuilder.getJobParametersBuilder().getSimulationRunnerParametersBuilder().setNumIterationsPerSample(120000);
            inputBuilder.getJobParametersBuilder().getSimulationRunnerParametersBuilder().setNumSamples(2000);
            final SystemParametersBuilder systemParametersBuilder = inputBuilder.systemParametersBuilder;
            final int numChains = systemParametersBuilder.getPolymerCluster().getNumChains();
            PolymerCluster newPolymerCluster = getPolymerCluster(asymmetry, numChains / 2 + numChainsDifference, 32, 1);
            systemParametersBuilder.setPolymerCluster(newPolymerCluster);
            noSpringInputs.add(inputBuilder.buildInput());
            jobNumber++;
        }

        return noSpringInputs;

    }

    static private List<Input> makeDropletResizingInputs(int jobNumber) {
        final double verticalRescaleFactor = .15;
        final double horizontalRescaleFactor = 4;
        final double asymmetry = .16;

        final List<Input> noSpringInputs = new ArrayList<>();


        for (int numChainsDifference = -8; numChainsDifference < 9; numChainsDifference++) {
            final InputBuilder inputBuilder = makeRescaleInputBuilderWithHorizontalRescaling(verticalRescaleFactor, horizontalRescaleFactor, jobNumber);
            inputBuilder.getJobParametersBuilder().setNumSimulationTrials(15);
            inputBuilder.getJobParametersBuilder().getSimulationRunnerParametersBuilder().setNumIterationsPerAnneal(10000);
            inputBuilder.getJobParametersBuilder().getSimulationRunnerParametersBuilder().setNumIterationsPerSample(20000);
            inputBuilder.getJobParametersBuilder().getSimulationRunnerParametersBuilder().setNumSamples(2000);
            final SystemParametersBuilder systemParametersBuilder = inputBuilder.systemParametersBuilder;
            final int numChains = systemParametersBuilder.getPolymerCluster().getNumChains();
            PolymerCluster newPolymerCluster = getPolymerCluster(asymmetry, numChains / 2 + numChainsDifference, 32, 1);
            systemParametersBuilder.setPolymerCluster(newPolymerCluster);
            noSpringInputs.add(inputBuilder.buildInput());
            jobNumber++;
        }

        return noSpringInputs;

    }

    static private List<Input> makeLargeSystemResizingInputs(int jobNumber) {
        final double verticalRescaleFactor = .15;
        final double horizontalRescaleFactor = 4;
        final double asymmetry = .32;

        final List<Input> noSpringInputs = new ArrayList<>();


        for (int numChainsDifference = 20; numChainsDifference < 40; numChainsDifference += 2) {
            final InputBuilder inputBuilder = makeRescaleInputBuilderWithHorizontalRescaling(verticalRescaleFactor, horizontalRescaleFactor, jobNumber);
            inputBuilder.getJobParametersBuilder().setNumSimulationTrials(15);
            inputBuilder.getJobParametersBuilder().getSimulationRunnerParametersBuilder().setNumIterationsPerAnneal(10000);
            inputBuilder.getJobParametersBuilder().getSimulationRunnerParametersBuilder().setNumIterationsPerSample(60000);
            inputBuilder.getJobParametersBuilder().getSimulationRunnerParametersBuilder().setNumSamples(2000);
            final SystemParametersBuilder systemParametersBuilder = inputBuilder.systemParametersBuilder;
            final int numChains = systemParametersBuilder.getPolymerCluster().getNumChains();
            PolymerCluster newPolymerCluster = getPolymerCluster(asymmetry, numChains / 2 + numChainsDifference, 32, 1);
            systemParametersBuilder.setPolymerCluster(newPolymerCluster);
            noSpringInputs.add(inputBuilder.buildInput());
            jobNumber++;
        }

        return noSpringInputs;

    }

    public static Input makeRescaleInput(final double scaleFactor, int jobNumber) {
        InputBuilder inputBuilder = makeRescaleInputBuilder(scaleFactor, jobNumber);
        return inputBuilder.buildInput();
    }

    public static InputBuilder makeRescaleInputBuilder(final double scaleFactor, int jobNumber) {
        return makeRescaleInputBuilderWithHorizontalRescaling(scaleFactor, 1, jobNumber);
    }

    public static Input makeRescaleInput(final double verticalScale, final double horizontalScale, final int jobNumber) {
        final InputBuilder inputBuilder = makeRescaleInputBuilderWithHorizontalRescaling(verticalScale, horizontalScale, jobNumber);
        return inputBuilder.buildInput();
    }

    public static InputBuilder makeRescaleInputBuilderWithHorizontalRescaling(final double verticalScale, final double horizontalScale, int jobNumber) {
        InputBuilder inputBuilder = getDefaultInputDensityBuilder();
        inputBuilder.rescale(verticalScale, horizontalScale);
        inputBuilder.getJobParametersBuilder().setJobNumber(jobNumber);

        return inputBuilder;
    }

    //<editor-fold defaultstate="collapsed" desc="default input">
    static private InputBuilder getDefaultInputDensityBuilder() {
        SystemParametersBuilder systemParametersBuilder = getDefaultSystemParametersBuilder();
        JobParametersBuilder jobParametersBuilder = getDefaultJobParametersBuilder();
        Input.InputBuilder inputBuilder = new SGEManagement.Input.InputBuilder();
        inputBuilder.setSystemParametersBuilder(systemParametersBuilder);
        inputBuilder.setJobParametersBuilder(jobParametersBuilder);
        return inputBuilder;
    }

    static private final double defaultAspectRatio = .5;
    static private final double defaultOverlapCoefficient = -.378;
    static private final double defaultInteractionLength = 4.;
    static private final int defaultNumBeadsPerChain = 16;
    static private final int defaultNumChains = 75;
    static private final double defaultDensity = 2;//.7
    private static final double defaultHydrophobicFraction = .5;//.15

    private static SystemParametersBuilder getDefaultSystemParametersBuilder() {
        SystemParametersBuilder systemParametersBuilder = new SystemParametersBuilder();
        systemParametersBuilder.setAspectRatio(defaultAspectRatio);
        EnergeticsConstantsBuilder energeticsConstantsBuilder = EnergeticsConstantsBuilder.zeroEnergeticsConstantsBuilder();
        energeticsConstantsBuilder.setBBOverlapCoefficient(defaultOverlapCoefficient);
        energeticsConstantsBuilder.setAAOverlapCoefficient(defaultOverlapCoefficient);
        energeticsConstantsBuilder.setABOverlapCoefficient(defaultOverlapCoefficient / 2);
        systemParametersBuilder.setEnergeticsConstantsBuilder(energeticsConstantsBuilder);
        systemParametersBuilder.setInteractionLength(defaultInteractionLength);
        systemParametersBuilder.autosetCoreParameters();
        systemParametersBuilder.setPolymerCluster(getDefaultPolymerCluster());
        return systemParametersBuilder;
    }

    private static PolymerCluster getDefaultPolymerCluster() {
        return getDefaultPolymerCluster(defaultHydrophobicFraction);
    }

    private static PolymerCluster getDefaultPolymerCluster(double hydrophobicFraction) {
        return getPolymerCluster(hydrophobicFraction, defaultNumChains, defaultNumBeadsPerChain, 1);
    }

    public static PolymerCluster getPolymerCluster(double hydrophobicFraction, final int numChains, int numBeadsPerChain, int numSubblocks) {
        return PolymerCluster.makePolymerCluster(hydrophobicFraction, numChains, numBeadsPerChain, numSubblocks, defaultDensity);
    }

    static private final int defaultNumAnneals = 5;
    static private final int defaultNumSurfaceTensionTrials = 20;
    static private final int defaultJobNumber = 0;

    static public JobParametersBuilder getDefaultJobParametersBuilder() {
        JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
        jobParametersBuilder.setNumAnneals(defaultNumAnneals);
        jobParametersBuilder.setNumSimulationTrials(defaultNumSurfaceTensionTrials);
        jobParametersBuilder.setJobNumber(defaultJobNumber);
        return jobParametersBuilder;
    }
    //</editor-fold>

}
