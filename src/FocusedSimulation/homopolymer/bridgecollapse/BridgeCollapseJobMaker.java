/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FocusedSimulation.homopolymer.bridgecollapse;

import Engine.Energetics.EnergeticsConstants.EnergeticsConstantsBuilder;
import Engine.Energetics.ExternalEnergyCalculator.ExternalEnergyCalculatorBuilder;
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
public class BridgeCollapseJobMaker {

    public static final String pathToFocusedSimulationClass = AbstractFocusedSimulation.pathToFocusedSimulation + "bridgecollapse/BridgeCollapseFinder";

    public static void main(String[] args) {
        int jobNumber;
        final List<Input> inputs = new ArrayList<>();

        jobNumber = inputs.size() + 1;
        final List<Input> noSpringInputs = makeNoSpringInputs(jobNumber);
        inputs.addAll(noSpringInputs);

        jobNumber = inputs.size() + 1;
        final List<Input> nonCollapsingInputs = testNonCollapsingInputs(jobNumber);
        inputs.addAll(nonCollapsingInputs);

        JobSubmitter.submitJobs(pathToFocusedSimulationClass, inputs);
    }

    //<editor-fold defaultstate="collapsed" desc="default input">
    static private InputBuilder getDefaultInputSurfaceTensionBuilder() {
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
    static private final double defaultDensity = .175;

    private static SystemParametersBuilder getDefaultSystemParametersBuilder() {
        SystemParametersBuilder systemParametersBuilder = new SystemParametersBuilder();
        systemParametersBuilder.setAspectRatio(defaultAspectRatio);
        EnergeticsConstantsBuilder energeticsConstantsBuilder = EnergeticsConstantsBuilder.zeroEnergeticsConstantsBuilder();
        energeticsConstantsBuilder.setBBOverlapCoefficient(defaultOverlapCoefficient);
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

    public static InputBuilder makeRescaleInputBuilderWithHorizontalRescaling(final double verticalScale, final double horizontalScale, int jobNumber) {
        InputBuilder inputBuilder;
        inputBuilder = getDefaultInputSurfaceTensionBuilder();
        final double aspectRatio = inputBuilder.getSystemParametersBuilder().getAspectRatio();
        inputBuilder.getSystemParametersBuilder().setAspectRatio(aspectRatio * horizontalScale / verticalScale);
        PolymerCluster polymerCluster = getPolymerCluster(verticalScale, horizontalScale);
        inputBuilder.getSystemParametersBuilder().setPolymerCluster(polymerCluster);
        inputBuilder.getJobParametersBuilder().setJobNumber(jobNumber);
        inputBuilder.getJobParametersBuilder().setNumAnneals(5);
        inputBuilder.getJobParametersBuilder().setNumSimulationTrials(20);
        final EnergeticsConstantsBuilder energeticsConstantsBuilder = inputBuilder.getSystemParametersBuilder().getEnergeticsConstantsBuilder();
        energeticsConstantsBuilder.setBBOverlapCoefficient(3 * energeticsConstantsBuilder.getBBOverlapCoefficient());
        energeticsConstantsBuilder.setHardOverlapCoefficient(3 * energeticsConstantsBuilder.getHardOverlapCoefficient());
        inputBuilder.getSystemParametersBuilder().autosetCoreParameters();
        return inputBuilder;
    }

    private static PolymerCluster getPolymerCluster(final double verticalScale, final double horizontalScale) {
        final PolymerChain polymerChain = PolymerChain.makeChainOfType(false, defaultNumBeadsPerChain);
        final PolymerCluster polymerCluster = PolymerCluster.makeRepeatedChainCluster(polymerChain, (int) (defaultNumChains * verticalScale * horizontalScale));
        polymerCluster.setConcentrationInWater(defaultDensity);
        return polymerCluster;
    }

    private static List<Input> makeNoSpringInputs() {
        return makeNoSpringInputs(1);
    }

    private static List<Input> makeNoSpringInputs(int jobNumber) {
        final double[] verticalRescaleFactors = {.1, .5, 1};
        final double[] horizontalRescaleFactors = {3, 5, 10};

        final List<Input> noSpringInputs = new ArrayList<>();

        for (int i = 0; i < verticalRescaleFactors.length; i++) {
            for (int j = 0; j < horizontalRescaleFactors.length; j++) {
                final Input input = makeRescaleInput(verticalRescaleFactors[i], horizontalRescaleFactors[j], jobNumber);
                noSpringInputs.add(input);
                jobNumber++;
            }
        }

        return noSpringInputs;
    }

    private static List<Input> makeRescaledInputs(int jobNumber) {
        final List<Input> noSpringInputs = new ArrayList<>();
        Input input;


        input = makeRescaleInput(.1 * 2.3, 3 * 2.3, jobNumber);
        noSpringInputs.add(input);
        jobNumber++;

        input = makeRescaleInput(.5 * .5, 3 * .5, jobNumber);
        noSpringInputs.add(input);
        jobNumber++;

        input = makeRescaleInput(1 * .25, 3 * .25, jobNumber);
        noSpringInputs.add(input);
        jobNumber++;

        return noSpringInputs;
    }

    private static List<Input> makeLongerRunInputs(int jobNumber) {
        final List<Input> noSpringInputs = new ArrayList<>();
        InputBuilder inputBuilder;


        inputBuilder = makeRescaleInputBuilderWithHorizontalRescaling(.5, 10, jobNumber);
        inputBuilder.getJobParametersBuilder().setNumSimulationTrials(210);
        noSpringInputs.add(inputBuilder.buildInputAutomaticHardOverlap());
        jobNumber++;

        inputBuilder = makeRescaleInputBuilderWithHorizontalRescaling(1, 3, jobNumber);
        inputBuilder.getJobParametersBuilder().setNumSimulationTrials(210);
        noSpringInputs.add(inputBuilder.buildInputAutomaticHardOverlap());
        jobNumber++;



        return noSpringInputs;
    }

    private static List<Input> testNonCollapsingInputs(int jobNumber) {
        final List<Input> noSpringInputs = new ArrayList<>();
        InputBuilder inputBuilder;


        inputBuilder = makeRescaleInputBuilderWithHorizontalRescaling(.5, 10, jobNumber);
        inputBuilder.getJobParametersBuilder().setNumSimulationTrials(20);
        noSpringInputs.add(inputBuilder.buildInput());
        jobNumber++;

        inputBuilder = makeRescaleInputBuilderWithHorizontalRescaling(1, 3, jobNumber);
        inputBuilder.getJobParametersBuilder().setNumSimulationTrials(20);
        noSpringInputs.add(inputBuilder.buildInput());
        jobNumber++;



        return noSpringInputs;
    }

    private static Input makeRescaleInput(final double verticalRescaleFactor, final double horizontalRescaleFactor, int jobNumber) {
        InputBuilder inputBuilder = makeRescaleInputBuilderWithHorizontalRescaling(verticalRescaleFactor, horizontalRescaleFactor, jobNumber);
        return inputBuilder.buildInput();
    }

}
