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
        return makeBulkMixtureInputs();
    }

    private static List<Input> makeBulkMixtureInputs() {
        return makeBulkMixtureInputs(1);
    }

    private static List<Input> makeBulkMixtureInputs(int jobNumber) {
        final double[] verticalRescaleFactors = {.05, .1, 3};
        final double[] horizontalRescaleFactors = {2, 4, 10};

        final List<Input> noSpringInputs = new ArrayList<>();

        for (int i = 0; i < verticalRescaleFactors.length; i++) {
            for (int j = 0; j < horizontalRescaleFactors.length; j++) {
                final InputBuilder inputBuilder = makeRescaleInputBuilderWithHorizontalRescaling(verticalRescaleFactors[i], horizontalRescaleFactors[j], jobNumber);
                noSpringInputs.add(inputBuilder.buildInput());
                jobNumber++;
            }
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
        InputBuilder inputBuilder;
        inputBuilder = getDefaultInputDensityBuilder();
        final double aspectRatio = inputBuilder.getSystemParametersBuilder().getAspectRatio();
        inputBuilder.getSystemParametersBuilder().setAspectRatio(aspectRatio * horizontalScale / verticalScale);
        PolymerCluster polymerCluster = getRescaledPolymerCluster(verticalScale, horizontalScale);
        inputBuilder.getSystemParametersBuilder().setPolymerCluster(polymerCluster);
        inputBuilder.getJobParametersBuilder().setJobNumber(jobNumber);
        inputBuilder.getJobParametersBuilder().setNumAnneals(5);
        inputBuilder.getJobParametersBuilder().setNumSimulationTrials(5);
        inputBuilder.getSystemParametersBuilder().autosetCoreParameters();
        final EnergeticsConstantsBuilder energeticsConstantsBuilder = inputBuilder.getSystemParametersBuilder().getEnergeticsConstantsBuilder();
        energeticsConstantsBuilder.setBBOverlapCoefficient(3 * energeticsConstantsBuilder.getBBOverlapCoefficient());
        energeticsConstantsBuilder.setAAOverlapCoefficient(3 * energeticsConstantsBuilder.getAAOverlapCoefficient());
        energeticsConstantsBuilder.setHardOverlapCoefficient(3 * energeticsConstantsBuilder.getHardOverlapCoefficient());
        return inputBuilder;
    }

    private static PolymerCluster getRescaledPolymerCluster(final double verticalScale, final double horizontalScale) {
        final int numChains = (int) (defaultNumChains * verticalScale * horizontalScale);
        return getPolymerCluster(defaultHydrophobicFraction, numChains, defaultNumBeadsPerChain, 1);
    }

    //<editor-fold defaultstate="collapsed" desc="default input">
    static private InputBuilder getDefaultInputDensityBuilder() {
        SystemParametersBuilder systemParametersBuilder = getDefaultSystemParametersBuilder();
        JobParametersBuilder jobParametersBuilder = JobParametersBuilder.getDefaultJobParametersBuilder();
        Input.InputBuilder inputBuilder = new SGEManagement.Input.InputBuilder();
        inputBuilder.setSystemParametersBuilder(systemParametersBuilder);
        inputBuilder.setJobParametersBuilder(jobParametersBuilder);
        return inputBuilder;
    }

    static private final double defaultAspectRatio = .0286;
    static private final double defaultOverlapCoefficient = -.126;
    static private final double defaultInteractionLength = 4.;
    static private final int defaultNumBeadsPerChain = 15;
    static private final int defaultNumChains = 75;
    static private final double defaultDensity = .35;
    private static final double defaultHydrophobicFraction = .5;//.15

    private static SystemParametersBuilder getDefaultSystemParametersBuilder() {
        SystemParametersBuilder systemParametersBuilder = new SystemParametersBuilder();
        systemParametersBuilder.setAspectRatio(defaultAspectRatio);
        EnergeticsConstantsBuilder energeticsConstantsBuilder = EnergeticsConstantsBuilder.zeroEnergeticsConstantsBuilder();
        energeticsConstantsBuilder.setBBOverlapCoefficient(defaultOverlapCoefficient);
        energeticsConstantsBuilder.setAAOverlapCoefficient(defaultOverlapCoefficient);
        energeticsConstantsBuilder.setABOverlapCoefficient(-defaultOverlapCoefficient / 2);
        systemParametersBuilder.setEnergeticsConstantsBuilder(energeticsConstantsBuilder);
        systemParametersBuilder.setInteractionLength(defaultInteractionLength);
        systemParametersBuilder.setPolymerCluster(getDefaultPolymerCluster());
        return systemParametersBuilder;
    }

    private static PolymerCluster getDefaultPolymerCluster() {
        return getDefaultPolymerCluster(defaultHydrophobicFraction);
    }

    private static PolymerCluster getDefaultPolymerCluster(double hydrophobicFraction) {
        return getPolymerCluster(hydrophobicFraction, defaultNumChains, defaultNumBeadsPerChain, 1);
    }

    private static PolymerCluster getPolymerCluster(double hydrophobicFraction, final int numChains, int numBeadsPerChain, int numSubblocks) {
        if (numSubblocks < 0) {
            throw new IllegalArgumentException("numSubblocks must be non-negative");
        } else if (numSubblocks == 0) {
            final int numHydrophobicChains = (int) Math.ceil(hydrophobicFraction * numChains);
            final int numHydrophilicChains = numChains - numHydrophobicChains;

            PolymerChain hydrophobicPolymerChain = PolymerChain.makeChainStartingWithA(0, numBeadsPerChain);
            PolymerChain hydrophilicPolymerChain = PolymerChain.makeChainStartingWithA(numBeadsPerChain);

            PolymerCluster polymerCluster = PolymerCluster.makeRepeatedChainCluster(hydrophobicPolymerChain, numHydrophobicChains);
            polymerCluster.addChainMultipleTimes(hydrophilicPolymerChain, numHydrophilicChains);
            polymerCluster.setConcentrationInWater(defaultDensity);
            return polymerCluster;
        } else {
            final PolymerChain polymerChain = makeMultiblockPolymerChain(numBeadsPerChain, numSubblocks, hydrophobicFraction);
            return PolymerCluster.makeRepeatedChainCluster(polymerChain, numChains);
        }
    }

    private static PolymerChain makeMultiblockPolymerChain(int numBeads, int numBlocks, double hydrophobicFraction) {
        final double numHydrophobicBeadsPerBlock = hydrophobicFraction * numBeads / numBlocks;
        final double numHydrophilicBeadsPerBlock = (1 - hydrophobicFraction) * numBeads / numBlocks;

        int numHydrophilicBeadsAddedSoFar = 0;
        int numHydrophobicBeadsAddedSoFar = 0;
        PolymerChain polymerChain = new PolymerChain();
        for (int currentBlock = 1; currentBlock <= numBlocks; currentBlock++) {
            final int numHydrophilicBeadsToBeAdded = (int) Math.round(numHydrophilicBeadsPerBlock * currentBlock - .000001) - numHydrophilicBeadsAddedSoFar; //avoid case when both numbers of blocks are rounded up
            final int numHydrophobicBeadsToBeAdded = (int) Math.round(numHydrophobicBeadsPerBlock * currentBlock) - numHydrophobicBeadsAddedSoFar;

            polymerChain.addBeads(true, numHydrophilicBeadsToBeAdded);
            numHydrophilicBeadsAddedSoFar += numHydrophilicBeadsToBeAdded;
            polymerChain.addBeads(false, numHydrophobicBeadsToBeAdded);
            numHydrophobicBeadsAddedSoFar += numHydrophobicBeadsToBeAdded;
        }
        if (polymerChain.getNumBeads() != numBeads) {
            throw new AssertionError("polymer chain has the wrong length, length is supposed to be " + numBeads + ", but actual length was " + polymerChain.getNumBeads());
        }
        return polymerChain;
    }

    static private final int defaultNumAnneals = 50;//50
    static private final int defaultNumSurfaceTensionTrials = 70;
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
