/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FocusedSimulation.surfacetension;

import Engine.Energetics.EnergeticsConstants.EnergeticsConstantsBuilder;
import Engine.Energetics.ExternalEnergyCalculator;
import Engine.Energetics.ExternalEnergyCalculator.ExternalEnergyCalculatorBuilder;
import Engine.PolymerTopology.PolymerChain;
import Engine.PolymerTopology.PolymerCluster;
import Engine.SimulatorParameters.SystemParametersBuilder;
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
public class SurfaceTensionJobMaker {

    public static void main(String[] args) {
        final List<Input> inputs = makeInputs();
        JobSubmitter.submitJobs(inputs);
    }

    static private List<Input> makeInputs() {
        return makeNarrowVerticalScalingInputs();
    }

    public static Input makeRescaleInput(final double scaleFactor, int jobNumber) {
        InputBuilder inputBuilder = makeRescaleInputBuilder(scaleFactor, jobNumber);
        final Input input = inputBuilder.buildInput();
        return input;
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
        inputBuilder = getDefaultInputSurfaceTensionBuilder();
        final double aspectRatio = inputBuilder.getSystemParametersBuilder().getAspectRatio();
        inputBuilder.getSystemParametersBuilder().setAspectRatio(aspectRatio * horizontalScale / verticalScale);
        PolymerCluster polymerCluster = getPolymerCluster(verticalScale, horizontalScale);
        inputBuilder.getSystemParametersBuilder().setPolymerCluster(polymerCluster);
        final ExternalEnergyCalculatorBuilder externalEnergyCalculatorBuilder = new ExternalEnergyCalculatorBuilder();
        externalEnergyCalculatorBuilder.setXPositionAndSpringConstant(16 * horizontalScale, 50);
        inputBuilder.getSystemParametersBuilder().getEnergeticsConstantsBuilder().setExternalEnergyCalculator(externalEnergyCalculatorBuilder.build());
        inputBuilder.getJobParametersBuilder().setJobNumber(jobNumber);
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
    static private final double defaultOverlapCoefficient = -.06;
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
        List<Input> inputs = new ArrayList<>();

        int jobNumber = 1;
        Input input;
        double verticalRescaleFactor;
        final double horizontalRescaleFactor = 3;

        verticalRescaleFactor = .07;
        input = makeRescaleInput(verticalRescaleFactor, horizontalRescaleFactor, jobNumber);
        inputs.add(input);
        jobNumber++;

        verticalRescaleFactor = .18;
        input = makeRescaleInput(verticalRescaleFactor, horizontalRescaleFactor, jobNumber);
        inputs.add(input);
        jobNumber++;

        verticalRescaleFactor = .3;
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

        verticalRescaleFactor = 10;
        input = makeRescaleInput(verticalRescaleFactor, horizontalRescaleFactor, jobNumber);
        inputs.add(input);
        jobNumber++;
        return inputs;
    }

    private static List<Input> makeNarrowVerticalScalingInputs() {
        List<Input> inputs = new ArrayList<>();

        int jobNumber = 1;
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

//        verticalRescaleFactor = 10;
//        input = makeRescaleInput(verticalRescaleFactor, horizontalRescaleFactor, jobNumber);
//        inputs.add(input);
//        jobNumber++;
//        verticalRescaleFactor = 15;
//        input = makeRescaleInput(verticalRescaleFactor, horizontalRescaleFactor, jobNumber);
//        inputs.add(input);
//        jobNumber++;
//        verticalRescaleFactor = 20;
//        input = makeRescaleInput(verticalRescaleFactor, horizontalRescaleFactor, jobNumber);
//        inputs.add(input);
//        jobNumber++;
//        verticalRescaleFactor = 25;
//        input = makeRescaleInput(verticalRescaleFactor, horizontalRescaleFactor, jobNumber);
//        inputs.add(input);
//        jobNumber++;
//
//        verticalRescaleFactor = 30;
//        input = makeRescaleInput(verticalRescaleFactor, horizontalRescaleFactor, jobNumber);
//        inputs.add(input);
//        jobNumber++;
        return inputs;
    }

    private static List<Input> makeRepeatabilityInputs() {
        /////////////////Repeatability
        InputBuilder inputBuilder;
        int jobNumber = 0;
        List<Input> inputs = new ArrayList<>();

        inputBuilder = makeRescaleInputBuilder(.5, jobNumber);
        inputBuilder.getSystemParametersBuilder().getPolymerCluster().setConcentrationInWater(1);
        inputBuilder.getSystemParametersBuilder().getEnergeticsConstantsBuilder().setExternalEnergyCalculator(new ExternalEnergyCalculator());
        inputBuilder.getSystemParametersBuilder().setAspectRatio(1);
        inputBuilder.getJobParametersBuilder().setNumAnneals(10);
        inputBuilder.getJobParametersBuilder().setNumSurfaceTensionTrials(10);
        inputBuilder.getJobParametersBuilder().setShouldIterateUntilConvergence(false);
        inputs.add(inputBuilder.buildInput());
        jobNumber++;

        inputBuilder = makeRescaleInputBuilder(.5, jobNumber);
        inputBuilder.getSystemParametersBuilder().getPolymerCluster().setConcentrationInWater(1);
        inputBuilder.getSystemParametersBuilder().getEnergeticsConstantsBuilder().setExternalEnergyCalculator(new ExternalEnergyCalculator());
        inputBuilder.getSystemParametersBuilder().setAspectRatio(1);
        inputBuilder.getJobParametersBuilder().setNumAnneals(10);
        inputBuilder.getJobParametersBuilder().setNumSurfaceTensionTrials(10);
        inputBuilder.getJobParametersBuilder().setShouldIterateUntilConvergence(false);
        inputs.add(inputBuilder.buildInput());
        jobNumber++;

        inputBuilder = makeRescaleInputBuilder(.5, jobNumber);
        inputBuilder.getSystemParametersBuilder().getPolymerCluster().setConcentrationInWater(1);
        inputBuilder.getSystemParametersBuilder().getEnergeticsConstantsBuilder().setExternalEnergyCalculator(new ExternalEnergyCalculator());
        inputBuilder.getSystemParametersBuilder().setAspectRatio(1);
        inputBuilder.getJobParametersBuilder().setNumAnneals(10);
        inputBuilder.getJobParametersBuilder().setNumSurfaceTensionTrials(10);
        inputBuilder.getJobParametersBuilder().setShouldIterateUntilConvergence(false);
        inputs.add(inputBuilder.buildInput());
        jobNumber++;

        inputBuilder = makeRescaleInputBuilder(.5, jobNumber);
        inputBuilder.getSystemParametersBuilder().getPolymerCluster().setConcentrationInWater(1);
        inputBuilder.getSystemParametersBuilder().getEnergeticsConstantsBuilder().setExternalEnergyCalculator(new ExternalEnergyCalculator());
        inputBuilder.getSystemParametersBuilder().setAspectRatio(1);
        inputBuilder.getJobParametersBuilder().setNumAnneals(10);
        inputBuilder.getJobParametersBuilder().setNumSurfaceTensionTrials(10);
        inputBuilder.getJobParametersBuilder().setShouldIterateUntilConvergence(false);
        inputs.add(inputBuilder.buildInput());
        jobNumber++;

        inputBuilder = makeRescaleInputBuilder(.5, jobNumber);
        inputBuilder.getSystemParametersBuilder().getPolymerCluster().setConcentrationInWater(1);
        inputBuilder.getSystemParametersBuilder().getEnergeticsConstantsBuilder().setExternalEnergyCalculator(new ExternalEnergyCalculator());
        inputBuilder.getSystemParametersBuilder().setAspectRatio(1);
        inputBuilder.getJobParametersBuilder().setNumAnneals(10);
        inputBuilder.getJobParametersBuilder().setNumSurfaceTensionTrials(10);
        inputBuilder.getJobParametersBuilder().setShouldIterateUntilConvergence(false);
        inputs.add(inputBuilder.buildInput());
        jobNumber++;

        inputBuilder = makeRescaleInputBuilder(.5, jobNumber);
        inputBuilder.getSystemParametersBuilder().getPolymerCluster().setConcentrationInWater(1);
        inputBuilder.getSystemParametersBuilder().getEnergeticsConstantsBuilder().setExternalEnergyCalculator(new ExternalEnergyCalculator());
        inputBuilder.getSystemParametersBuilder().setAspectRatio(1);
        inputBuilder.getJobParametersBuilder().setNumAnneals(10);
        inputBuilder.getJobParametersBuilder().setNumSurfaceTensionTrials(10);
        inputBuilder.getJobParametersBuilder().setShouldIterateUntilConvergence(false);
        inputs.add(inputBuilder.buildInput());
        jobNumber++;

        return inputs;
    }

    private static List<Input> testAspectRatioEffectOnDensity() {
        ///////////Effect of aspect ratio on natural density.

        InputBuilder inputBuilder;
        int jobNumber = 0;
        List<Input> inputs = new ArrayList<>();
        inputBuilder = makeRescaleInputBuilder(.5, jobNumber);
        inputBuilder.getSystemParametersBuilder().getPolymerCluster().setConcentrationInWater(1);
        inputBuilder.getSystemParametersBuilder().getEnergeticsConstantsBuilder().setExternalEnergyCalculator(new ExternalEnergyCalculator());
        inputBuilder.getSystemParametersBuilder().setAspectRatio(.1);
        inputBuilder.getJobParametersBuilder().setNumAnneals(10);
        inputBuilder.getJobParametersBuilder().setNumSurfaceTensionTrials(10);
        inputBuilder.getJobParametersBuilder().setShouldIterateUntilConvergence(false);
        inputs.add(inputBuilder.buildInput());
        jobNumber++;

        inputBuilder = makeRescaleInputBuilder(.5, jobNumber);
        inputBuilder.getSystemParametersBuilder().getPolymerCluster().setConcentrationInWater(1);
        inputBuilder.getSystemParametersBuilder().getEnergeticsConstantsBuilder().setExternalEnergyCalculator(new ExternalEnergyCalculator());
        inputBuilder.getSystemParametersBuilder().setAspectRatio(.1);
        inputBuilder.getJobParametersBuilder().setNumAnneals(10);
        inputBuilder.getJobParametersBuilder().setNumSurfaceTensionTrials(10);
        inputBuilder.getJobParametersBuilder().setShouldIterateUntilConvergence(false);
        inputs.add(inputBuilder.buildInput());
        jobNumber++;

        inputBuilder = makeRescaleInputBuilder(.5, jobNumber);
        inputBuilder.getSystemParametersBuilder().getPolymerCluster().setConcentrationInWater(1);
        inputBuilder.getSystemParametersBuilder().getEnergeticsConstantsBuilder().setExternalEnergyCalculator(new ExternalEnergyCalculator());
        inputBuilder.getSystemParametersBuilder().setAspectRatio(.1);
        inputBuilder.getJobParametersBuilder().setNumAnneals(10);
        inputBuilder.getJobParametersBuilder().setNumSurfaceTensionTrials(10);
        inputBuilder.getJobParametersBuilder().setShouldIterateUntilConvergence(false);
        inputs.add(inputBuilder.buildInput());
        jobNumber++;

        inputBuilder = makeRescaleInputBuilder(.5, jobNumber);
        inputBuilder.getSystemParametersBuilder().getPolymerCluster().setConcentrationInWater(1);
        inputBuilder.getSystemParametersBuilder().getEnergeticsConstantsBuilder().setExternalEnergyCalculator(new ExternalEnergyCalculator());
        inputBuilder.getSystemParametersBuilder().setAspectRatio(.1);
        inputBuilder.getJobParametersBuilder().setNumAnneals(10);
        inputBuilder.getJobParametersBuilder().setNumSurfaceTensionTrials(10);
        inputBuilder.getJobParametersBuilder().setShouldIterateUntilConvergence(false);
        inputs.add(inputBuilder.buildInput());
        jobNumber++;

        inputBuilder = makeRescaleInputBuilder(.5, jobNumber);
        inputBuilder.getSystemParametersBuilder().getPolymerCluster().setConcentrationInWater(1);
        inputBuilder.getSystemParametersBuilder().getEnergeticsConstantsBuilder().setExternalEnergyCalculator(new ExternalEnergyCalculator());
        inputBuilder.getSystemParametersBuilder().setAspectRatio(.3);
        inputBuilder.getJobParametersBuilder().setNumAnneals(10);
        inputBuilder.getJobParametersBuilder().setNumSurfaceTensionTrials(10);
        inputBuilder.getJobParametersBuilder().setShouldIterateUntilConvergence(false);
        inputs.add(inputBuilder.buildInput());
        jobNumber++;

        inputBuilder = makeRescaleInputBuilder(.5, jobNumber);
        inputBuilder.getSystemParametersBuilder().getPolymerCluster().setConcentrationInWater(1);
        inputBuilder.getSystemParametersBuilder().getEnergeticsConstantsBuilder().setExternalEnergyCalculator(new ExternalEnergyCalculator());
        inputBuilder.getSystemParametersBuilder().setAspectRatio(.3);
        inputBuilder.getJobParametersBuilder().setNumAnneals(10);
        inputBuilder.getJobParametersBuilder().setNumSurfaceTensionTrials(10);
        inputBuilder.getJobParametersBuilder().setShouldIterateUntilConvergence(false);
        inputs.add(inputBuilder.buildInput());
        jobNumber++;

        return inputs;
    }

    private static List<Input> makeFinalOutputTestInputs() {
        List<Input> inputs = new ArrayList<>();

        int jobNumber = 1;
        InputBuilder inputBuilder;
        double verticalRescaleFactor = .1;
        final double horizontalRescaleFactor = 1;

        inputBuilder = makeRescaleInputBuilderWithHorizontalRescaling(verticalRescaleFactor, horizontalRescaleFactor, jobNumber);
        inputBuilder.getJobParametersBuilder().setNumAnneals(1);
        inputBuilder.getJobParametersBuilder().setNumSurfaceTensionTrials(1);
        inputs.add(inputBuilder.buildInput());
        jobNumber++;

        inputBuilder = makeRescaleInputBuilderWithHorizontalRescaling(verticalRescaleFactor, horizontalRescaleFactor, jobNumber);
        inputBuilder.getJobParametersBuilder().setNumAnneals(1);
        inputBuilder.getJobParametersBuilder().setNumSurfaceTensionTrials(1);
        inputs.add(inputBuilder.buildInput());
        jobNumber++;

        inputBuilder = makeRescaleInputBuilderWithHorizontalRescaling(verticalRescaleFactor, horizontalRescaleFactor, jobNumber);
        inputBuilder.getJobParametersBuilder().setNumAnneals(1);
        inputBuilder.getJobParametersBuilder().setNumSurfaceTensionTrials(1);
        inputs.add(inputBuilder.buildInput());
        jobNumber++;

        inputBuilder = makeRescaleInputBuilderWithHorizontalRescaling(verticalRescaleFactor, horizontalRescaleFactor, jobNumber);
        inputBuilder.getJobParametersBuilder().setNumAnneals(1);
        inputBuilder.getJobParametersBuilder().setNumSurfaceTensionTrials(1);
        inputs.add(inputBuilder.buildInput());
        jobNumber++;

        inputBuilder = makeRescaleInputBuilderWithHorizontalRescaling(verticalRescaleFactor, horizontalRescaleFactor, jobNumber);
        inputBuilder.getJobParametersBuilder().setNumAnneals(1);
        inputBuilder.getJobParametersBuilder().setNumSurfaceTensionTrials(1);
        inputs.add(inputBuilder.buildInput());
        jobNumber++;

        inputBuilder = makeRescaleInputBuilderWithHorizontalRescaling(verticalRescaleFactor, horizontalRescaleFactor, jobNumber);
        inputBuilder.getJobParametersBuilder().setNumAnneals(1);
        inputBuilder.getJobParametersBuilder().setNumSurfaceTensionTrials(1);
        inputs.add(inputBuilder.buildInput());
        jobNumber++;

        inputBuilder = makeRescaleInputBuilderWithHorizontalRescaling(verticalRescaleFactor, horizontalRescaleFactor, jobNumber);
        inputBuilder.getJobParametersBuilder().setNumAnneals(1);
        inputBuilder.getJobParametersBuilder().setNumSurfaceTensionTrials(1);
        inputs.add(inputBuilder.buildInput());
        jobNumber++;

        inputBuilder = makeRescaleInputBuilderWithHorizontalRescaling(verticalRescaleFactor, horizontalRescaleFactor, jobNumber);
        inputBuilder.getJobParametersBuilder().setNumAnneals(1);
        inputBuilder.getJobParametersBuilder().setNumSurfaceTensionTrials(1);
        inputs.add(inputBuilder.buildInput());
        jobNumber++;

        inputBuilder = makeRescaleInputBuilderWithHorizontalRescaling(verticalRescaleFactor, horizontalRescaleFactor, jobNumber);
        inputBuilder.getJobParametersBuilder().setNumAnneals(1);
        inputBuilder.getJobParametersBuilder().setNumSurfaceTensionTrials(1);
        inputs.add(inputBuilder.buildInput());
        jobNumber++;

        inputBuilder = makeRescaleInputBuilderWithHorizontalRescaling(verticalRescaleFactor, horizontalRescaleFactor, jobNumber);
        inputBuilder.getJobParametersBuilder().setNumAnneals(1);
        inputBuilder.getJobParametersBuilder().setNumSurfaceTensionTrials(1);
        inputs.add(inputBuilder.buildInput());
        jobNumber++;


        return inputs;
    }

}
