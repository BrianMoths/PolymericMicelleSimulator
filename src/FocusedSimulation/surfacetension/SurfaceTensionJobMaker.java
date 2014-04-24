/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FocusedSimulation.surfacetension;

import Engine.Energetics.EnergeticsConstants.EnergeticsConstantsBuilder;
import Engine.Energetics.ExternalEnergyCalculator.ExternalEnergyCalculatorBuilder;
import Engine.PolymerTopology.PolymerChain;
import Engine.PolymerTopology.PolymerCluster;
import Engine.SimulatorParameters.SystemParametersBuilder;
import FocusedSimulation.AbstractFocusedSimulation;
import FocusedSimulation.JobParameters.JobParametersBuilder;
import FocusedSimulation.SimulationRunner.SimulationRunnerParameters;
import SGEManagement.Input;
import SGEManagement.Input.InputBuilder;
import SGEManagement.JobSubmitter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author bmoths
 */
public class SurfaceTensionJobMaker {

    public static final String pathToFocusedSimulationClass = AbstractFocusedSimulation.pathToFocusedSimulation + "surfacetension/SurfaceTensionFinder";

    public static void main(String[] args) {
        final List<Input> inputs = smallSystemInput(1);
        JobSubmitter.submitJobs(pathToFocusedSimulationClass, inputs);
    }

    static private List<Input> makeInputs() {
        final List<Input> inputs = new ArrayList<>();
        int jobNumber = 1;
        inputs.addAll(makeWideVerticalScalingInputs(jobNumber));
        jobNumber = inputs.size() + 1;
        inputs.addAll(testSpringEffect(jobNumber));
        jobNumber = inputs.size() + 1;
        inputs.addAll(makeNarrowVerticalScalingInputs(jobNumber));
        return inputs;
    }

    public static Input makeRescaleInput(final double scaleFactor, int jobNumber) {
        InputBuilder inputBuilder = makeRescaleInputBuilder(scaleFactor, jobNumber);
        final Input input = inputBuilder.buildInputAutomaticHardOverlap();
        return input;
    }

    public static InputBuilder makeRescaleInputBuilder(final double scaleFactor, int jobNumber) {
        return makeRescaleInputBuilderWithHorizontalRescaling(scaleFactor, 1, jobNumber);
    }

    public static Input makeRescaleInput(final double verticalScale, final double horizontalScale, final int jobNumber) {
        final InputBuilder inputBuilder = makeRescaleInputBuilderWithHorizontalRescaling(verticalScale, horizontalScale, jobNumber);
        return inputBuilder.buildInputAutomaticHardOverlap();
    }

    public static InputBuilder makeRescaleInputBuilderWithHorizontalRescaling(final double verticalScale, final double horizontalScale, int jobNumber) {
        InputBuilder inputBuilder;
        inputBuilder = getDefaultInputSurfaceTensionBuilder();
        final double aspectRatio = inputBuilder.getSystemParametersBuilder().getAspectRatio();
        inputBuilder.getSystemParametersBuilder().setAspectRatio(aspectRatio * horizontalScale / verticalScale);
        PolymerCluster polymerCluster = getPolymerCluster(verticalScale, horizontalScale);
        inputBuilder.getSystemParametersBuilder().setPolymerCluster(polymerCluster);
        final ExternalEnergyCalculatorBuilder externalEnergyCalculatorBuilder = new ExternalEnergyCalculatorBuilder();
        externalEnergyCalculatorBuilder.setXPositionAndSpringConstant(16 * horizontalScale, 50);
        inputBuilder.getSystemParametersBuilder().getEnergeticsConstantsBuilder().setExternalEnergyCalculatorBuilder(externalEnergyCalculatorBuilder);
        inputBuilder.getJobParametersBuilder().setJobNumber(jobNumber);
        inputBuilder.getJobParametersBuilder().setNumAnneals(3);
        inputBuilder.getJobParametersBuilder().setNumSurfaceTensionTrials(70);
        return inputBuilder;
    }

    private static PolymerCluster getPolymerCluster(final double verticalScale, final double horizontalScale) {
        final PolymerChain polymerChain = PolymerChain.makeChainOfType(false, defaultNumBeadsPerChain);
        final PolymerCluster polymerCluster = PolymerCluster.makeRepeatedChainCluster(polymerChain, (int) (defaultNumChains * verticalScale * horizontalScale));
        polymerCluster.setConcentrationInWater(defaultDensity);
        return polymerCluster;
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
    static private final double defaultOverlapCoefficient = -.053;//-.06 -.053 looks different
    static private final double defaultInteractionLength = 4.;
    static private final double defaultXPosition = 50;
    static private final double defaultSpringConstant = 10;
    static private final int defaultNumBeadsPerChain = 15;
    static private final int defaultNumChains = 75;
    static private final double defaultDensity = .175;

    private static SystemParametersBuilder getDefaultSystemParametersBuilder() {
        SystemParametersBuilder systemParametersBuilder = new SystemParametersBuilder();
        systemParametersBuilder.setAspectRatio(defaultAspectRatio);
        EnergeticsConstantsBuilder energeticsConstantsBuilder = EnergeticsConstantsBuilder.zeroEnergeticsConstantsBuilder();
        energeticsConstantsBuilder.setBBOverlapCoefficient(defaultOverlapCoefficient);
        ExternalEnergyCalculatorBuilder externalEnergyCalculatorBuilder = new ExternalEnergyCalculatorBuilder();
        externalEnergyCalculatorBuilder.setXPositionAndSpringConstant(defaultXPosition, defaultSpringConstant);
        energeticsConstantsBuilder.setExternalEnergyCalculatorBuilder(externalEnergyCalculatorBuilder);
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
        jobParametersBuilder.setNumSurfaceTensionTrials(defaultNumSurfaceTensionTrials);
        jobParametersBuilder.setJobNumber(defaultJobNumber);
        return jobParametersBuilder;
    }
    //</editor-fold>

    private static List<Input> makeWideVerticalScalingInputs() {
        return makeWideVerticalScalingInputs(1);
    }

    private static List<Input> makeWideVerticalScalingInputs(int jobNumber) {
        List<Input> inputs = new ArrayList<>();

        Input input;
        double verticalRescaleFactor;
        final double horizontalRescaleFactor = 2;

        verticalRescaleFactor = 1;
        input = makeRescaleInput(verticalRescaleFactor, horizontalRescaleFactor, jobNumber);
        inputs.add(input);
        jobNumber++;

        verticalRescaleFactor = 2;
        input = makeRescaleInput(verticalRescaleFactor, horizontalRescaleFactor, jobNumber);
        inputs.add(input);
        jobNumber++;

        verticalRescaleFactor = 5;
        input = makeRescaleInput(verticalRescaleFactor, horizontalRescaleFactor, jobNumber);
        inputs.add(input);
        jobNumber++;

        verticalRescaleFactor = 7;
        input = makeRescaleInput(verticalRescaleFactor, horizontalRescaleFactor, jobNumber);
        inputs.add(input);
        jobNumber++;

        verticalRescaleFactor = 10;
        input = makeRescaleInput(verticalRescaleFactor, horizontalRescaleFactor, jobNumber);
        inputs.add(input);
        jobNumber++;

        verticalRescaleFactor = 15;
        input = makeRescaleInput(verticalRescaleFactor, horizontalRescaleFactor, jobNumber);
        inputs.add(input);
        jobNumber++;
        return inputs;
    }

    private static List<Input> makeNarrowVerticalScalingInputs() {
        return makeNarrowVerticalScalingInputs(1);
    }

    private static List<Input> makeNarrowVerticalScalingInputs(int jobNumber) {
        List<Input> inputs = new ArrayList<>();

        Input input;
        double verticalRescaleFactor;
        final double horizontalRescaleFactor = 1;

        verticalRescaleFactor = .5;
        input = makeRescaleInput(verticalRescaleFactor, horizontalRescaleFactor, jobNumber);
        inputs.add(input);
        jobNumber++;
        verticalRescaleFactor = 1;
        input = makeRescaleInput(verticalRescaleFactor, horizontalRescaleFactor, jobNumber);
        inputs.add(input);
        jobNumber++;
        verticalRescaleFactor = 3;
        input = makeRescaleInput(verticalRescaleFactor, horizontalRescaleFactor, jobNumber);
        inputs.add(input);
        jobNumber++;
        verticalRescaleFactor = 5;
        input = makeRescaleInput(verticalRescaleFactor, horizontalRescaleFactor, jobNumber);
        inputs.add(input);
        jobNumber++;
        verticalRescaleFactor = 8;
        input = makeRescaleInput(verticalRescaleFactor, horizontalRescaleFactor, jobNumber);
        inputs.add(input);
        jobNumber++;

        verticalRescaleFactor = 10;
        input = makeRescaleInput(verticalRescaleFactor, horizontalRescaleFactor, jobNumber);
        inputs.add(input);
        jobNumber++;
        verticalRescaleFactor = 15;
        input = makeRescaleInput(verticalRescaleFactor, horizontalRescaleFactor, jobNumber);
        inputs.add(input);
        jobNumber++;
        verticalRescaleFactor = 20;
        input = makeRescaleInput(verticalRescaleFactor, horizontalRescaleFactor, jobNumber);
        inputs.add(input);
        jobNumber++;
        verticalRescaleFactor = 25;
        input = makeRescaleInput(verticalRescaleFactor, horizontalRescaleFactor, jobNumber);
        inputs.add(input);
        jobNumber++;

        verticalRescaleFactor = 30;
        input = makeRescaleInput(verticalRescaleFactor, horizontalRescaleFactor, jobNumber);
        inputs.add(input);
        jobNumber++;
        return inputs;
    }

    private static List<Input> makeRepeatabilityInputs() {
        return makeRepeatabilityInputs(1);
    }

    private static List<Input> makeRepeatabilityInputs(int jobNumber) {
        /////////////////Repeatability
        List<Input> inputs = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            inputs.add(makeRepeatabilityInput(jobNumber));
            jobNumber++;
        }
        return inputs;
    }

    private static Input makeRepeatabilityInput(int jobNumber) {
        InputBuilder inputBuilder;
        inputBuilder = makeRescaleInputBuilder(.5, jobNumber);
        inputBuilder.getSystemParametersBuilder().getPolymerCluster().setConcentrationInWater(1);
        inputBuilder.getSystemParametersBuilder().getEnergeticsConstantsBuilder().setExternalEnergyCalculatorBuilder(new ExternalEnergyCalculatorBuilder());
        inputBuilder.getSystemParametersBuilder().setAspectRatio(1);
        inputBuilder.getJobParametersBuilder().setNumAnneals(10);
        inputBuilder.getJobParametersBuilder().setNumSurfaceTensionTrials(10);
        inputBuilder.getJobParametersBuilder().setShouldIterateUntilConvergence(false);
        final Input input = inputBuilder.buildInputAutomaticHardOverlap();
        return input;
    }

    private static List<Input> testAspectRatioEffectOnDensity() {
        return testAspectRatioEffectOnDensity(1);
    }

    private static List<Input> testAspectRatioEffectOnDensity(int jobNumber) {
        ///////////Effect of aspect ratio on natural density.

        double[] aspectRatios = {.1, .3, .5, 1, 3, 10};
        InputBuilder inputBuilder;
        List<Input> inputs = new ArrayList<>();
        for (int i = 0; i < aspectRatios.length; i++) {
            inputs.add(makeTestAspectRatioInput(jobNumber, aspectRatios[i]));
            jobNumber++;
        }
        return inputs;
    }

    public static Input makeTestAspectRatioInput(int jobNumber, double aspectRatio) {
        InputBuilder inputBuilder;
        inputBuilder = makeRescaleInputBuilder(.5, jobNumber);
        inputBuilder.getSystemParametersBuilder().getPolymerCluster().setConcentrationInWater(1);
        inputBuilder.getSystemParametersBuilder().getEnergeticsConstantsBuilder().setExternalEnergyCalculatorBuilder(new ExternalEnergyCalculatorBuilder());
        inputBuilder.getSystemParametersBuilder().setAspectRatio(aspectRatio);
        inputBuilder.getJobParametersBuilder().setNumAnneals(10);
        inputBuilder.getJobParametersBuilder().setNumSurfaceTensionTrials(10);
        inputBuilder.getJobParametersBuilder().setShouldIterateUntilConvergence(false);
        Input input = inputBuilder.buildInputAutomaticHardOverlap();
        return input;
    }

    private static List<Input> makeHistogramInputs() {
        return makeHistogramInputs(1);
    }

    private static List<Input> makeHistogramInputs(int jobNumber) {

        List<Input> inputs = new ArrayList<>();
        inputs.add(makeHistogramInput(jobNumber, .5, 2.5));
        jobNumber++;
        inputs.add(makeHistogramInput(jobNumber, 1, 5));
        jobNumber++;
        inputs.add(makeHistogramInput(jobNumber, .25, 1.25));
        jobNumber++;
        return inputs;
    }

    public static Input makeHistogramInput(int jobNumber, double verticalScaleFactor, double horizontalScaleFactor) {
        InputBuilder inputBuilder = SurfaceTensionJobMaker.makeRescaleInputBuilderWithHorizontalRescaling(verticalScaleFactor, horizontalScaleFactor, 0);
        inputBuilder.getJobParametersBuilder().setNumAnneals(1);
        inputBuilder.getJobParametersBuilder().setNumSurfaceTensionTrials(30);
        inputBuilder.getJobParametersBuilder().setJobNumber(jobNumber);
        return inputBuilder.buildInputAutomaticHardOverlap();
    }

    private static List<Input> testSpringEffect() {
        return testSpringEffect(1);
    }

    private static List<Input> testSpringEffect(int jobNumber) {
        List<Input> inputs = new ArrayList<>();

        final double verticalRescaleFactor = 8;
        final double horizontalRescaleFactor = 1;

        final ExternalEnergyCalculatorBuilder exernalEnergyCalculatorBuilder = makeRescaleInputBuilderWithHorizontalRescaling(verticalRescaleFactor, horizontalRescaleFactor, jobNumber).systemParametersBuilder.getEnergeticsConstantsBuilder().getExternalEnergyCalculatorBuilder();

        final double observedBalancePointOfSpring = 13.5;
        final double force = -exernalEnergyCalculatorBuilder.getxSpringConstant() * (observedBalancePointOfSpring - exernalEnergyCalculatorBuilder.getxEquilibriumPosition());
        double[] newSpringConstants = {5, 20, 100, 500};
        Input input;

        for (int i = 0; i < newSpringConstants.length; i++) {
            double springConstant = newSpringConstants[i];
            input = makeSpringEffectInput(verticalRescaleFactor, horizontalRescaleFactor, jobNumber, force, springConstant, observedBalancePointOfSpring);
            inputs.add(input);
            jobNumber++;
        }

        return inputs;
    }

    private static Input makeSpringEffectInput(final double verticalRescaleFactor, final double horizontalRescaleFactor, int jobNumber, final double force, double newSpringConstant, final double observedBalancePointOfSpring) {
        InputBuilder inputBuilder = makeRescaleInputBuilderWithHorizontalRescaling(verticalRescaleFactor, horizontalRescaleFactor, jobNumber);
        final double newXEquilibrium = force / newSpringConstant + observedBalancePointOfSpring;
        ExternalEnergyCalculatorBuilder newExternalEnergyCalculatorBuilder = new ExternalEnergyCalculatorBuilder().setXPositionAndSpringConstant(newXEquilibrium, newSpringConstant);
        inputBuilder.getSystemParametersBuilder().getEnergeticsConstantsBuilder().setExternalEnergyCalculatorBuilder(newExternalEnergyCalculatorBuilder);
        return inputBuilder.buildInputAutomaticHardOverlap();
    }

    private static List<Input> smallSystemInput(int jobNumber) {
        List<Input> inputs = new ArrayList<>();

        InputBuilder inputBuilder;

        inputBuilder = makeRescaleInputBuilderWithHorizontalRescaling(.1, 3, jobNumber);
        inputBuilder.getSystemParametersBuilder().getEnergeticsConstantsBuilder().getExternalEnergyCalculatorBuilder().setXPositionAndSpringConstant(35, 50);
        inputs.add(inputBuilder.buildInputAutomaticHardOverlap());
        jobNumber++;


        inputBuilder = makeRescaleInputBuilderWithHorizontalRescaling(.1, 5, jobNumber);
        inputBuilder.getSystemParametersBuilder().getEnergeticsConstantsBuilder().getExternalEnergyCalculatorBuilder().setXPositionAndSpringConstant(60, 50);
        inputs.add(inputBuilder.buildInputAutomaticHardOverlap());
        jobNumber++;


        inputBuilder = makeRescaleInputBuilderWithHorizontalRescaling(.1, 10, jobNumber);
        inputBuilder.getSystemParametersBuilder().getEnergeticsConstantsBuilder().getExternalEnergyCalculatorBuilder().setXPositionAndSpringConstant(100, 50);
        inputs.add(inputBuilder.buildInputAutomaticHardOverlap());
        jobNumber++;


        inputBuilder = makeRescaleInputBuilderWithHorizontalRescaling(.05, 1.5, jobNumber);
        inputs.add(inputBuilder.buildInputAutomaticHardOverlap());
        jobNumber++;


        inputBuilder = makeRescaleInputBuilderWithHorizontalRescaling(.1, 1.5, jobNumber);
        inputs.add(inputBuilder.buildInputAutomaticHardOverlap());
        jobNumber++;


        inputBuilder = makeRescaleInputBuilderWithHorizontalRescaling(.5, 1.5, jobNumber);
        inputs.add(inputBuilder.buildInputAutomaticHardOverlap());
        jobNumber++;



        return inputs;
    }

}
