/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FocusedSimulation.bulkproperties;

import Engine.Energetics.EnergeticsConstants.EnergeticsConstantsBuilder;
import Engine.Energetics.ExternalEnergyCalculator.ExternalEnergyCalculatorBuilder;
import Engine.PolymerTopology.PolymerChain;
import Engine.PolymerTopology.PolymerCluster;
import Engine.SimulatorParameters.SystemParametersBuilder;
import FocusedSimulation.AbstractFocusedSimulation;
import FocusedSimulation.JobParameters.JobParametersBuilder;
import static FocusedSimulation.surfacetension.SurfaceTensionJobMaker.makeRescaleInput;
import static FocusedSimulation.surfacetension.SurfaceTensionJobMaker.makeRescaleInputBuilder;
import static FocusedSimulation.surfacetension.SurfaceTensionJobMaker.makeRescaleInputBuilderWithHorizontalRescaling;
import SGEManagement.Input;
import SGEManagement.Input.InputBuilder;
import SGEManagement.JobSubmitter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author bmoths
 */
public class BulkPropertiesJobMaker {

    public static final String pathToFocusedSimulationClass = AbstractFocusedSimulation.pathToFocusedSimulation + "density/DensityFinder";

    public static void main(String[] args) {
        final List<Input> inputs = makeInputs();
        JobSubmitter.submitJobs(pathToFocusedSimulationClass, inputs);
    }

    static private List<Input> makeInputs() {
        return makeWideVerticalScalingInputs();
    }

    public static Input makeRescaleInput(final double scaleFactor, double pressure, int jobNumber) {
        InputBuilder inputBuilder = makeRescaleInputBuilder(scaleFactor, pressure, jobNumber);
        return inputBuilder.buildInput();
    }

    public static InputBuilder makeRescaleInputBuilder(final double scaleFactor, double pressure, int jobNumber) {
        return makeRescaleInputBuilderWithHorizontalRescaling(scaleFactor, 1, pressure, jobNumber);
    }

    public static Input makeRescaleInput(final double verticalScale, final double horizontalScale, double pressure, final int jobNumber) {
        final InputBuilder inputBuilder = makeRescaleInputBuilderWithHorizontalRescaling(verticalScale, horizontalScale, pressure, jobNumber);
        return inputBuilder.buildInput();
    }

    public static InputBuilder makeRescaleInputBuilderWithHorizontalRescaling(final double verticalScale, final double horizontalScale, final double pressure, int jobNumber) {
        InputBuilder inputBuilder;
        inputBuilder = getDefaultInputDensityBuilder();
        final double aspectRatio = inputBuilder.getSystemParametersBuilder().getAspectRatio();
        inputBuilder.getSystemParametersBuilder().setAspectRatio(aspectRatio * horizontalScale / verticalScale);
        final ExternalEnergyCalculatorBuilder externalEnergyCalculatorBuilder = new ExternalEnergyCalculatorBuilder();
        externalEnergyCalculatorBuilder.setPressure(pressure);
        inputBuilder.getSystemParametersBuilder().getEnergeticsConstantsBuilder().setExternalEnergyCalculatorBuilder(externalEnergyCalculatorBuilder);
        PolymerCluster polymerCluster = getPolymerCluster(verticalScale, horizontalScale);
        inputBuilder.getSystemParametersBuilder().setPolymerCluster(polymerCluster);
        inputBuilder.getJobParametersBuilder().setJobNumber(jobNumber);
        inputBuilder.getJobParametersBuilder().setNumAnneals(50);
        inputBuilder.getJobParametersBuilder().setNumSimulationTrials(70);
        return inputBuilder;
    }

    private static PolymerCluster getPolymerCluster(final double verticalScale, final double horizontalScale) {
        final PolymerChain polymerChain = PolymerChain.makeChainOfType(false, defaultNumBeadsPerChain);
        final PolymerCluster polymerCluster = PolymerCluster.makeRepeatedChainCluster(polymerChain, (int) (defaultNumChains * verticalScale * horizontalScale));
        polymerCluster.setConcentrationInWater(defaultDensity);
        return polymerCluster;
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
    static private final double defaultOverlapCoefficient = -.06;
    static private final double defaultInteractionLength = 4.;
    static private final int defaultNumBeadsPerChain = 15;
    static private final int defaultNumChains = 75;
    static private final double defaultDensity = .35;

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

    private static List<Input> makeWideVerticalScalingInputs() {
        List<Input> inputs = new ArrayList<>();

        int jobNumber = 1;
        Input input;
        double verticalRescaleFactor;
        final double horizontalRescaleFactor = 9;

        verticalRescaleFactor = .05;
        input = makeRescaleInput(verticalRescaleFactor, horizontalRescaleFactor, 0, jobNumber);
        inputs.add(input);
        jobNumber++;

        verticalRescaleFactor = .1;
        input = makeRescaleInput(verticalRescaleFactor, horizontalRescaleFactor, 0, jobNumber);
        inputs.add(input);
        jobNumber++;

        verticalRescaleFactor = .5;
        input = makeRescaleInput(verticalRescaleFactor, horizontalRescaleFactor, 0, jobNumber);
        inputs.add(input);
        jobNumber++;

        verticalRescaleFactor = 1;
        input = makeRescaleInput(verticalRescaleFactor, horizontalRescaleFactor, 0, jobNumber);
        inputs.add(input);
        jobNumber++;

        verticalRescaleFactor = 2;
        input = makeRescaleInput(verticalRescaleFactor, horizontalRescaleFactor, 0, jobNumber);
        inputs.add(input);
        jobNumber++;
        return inputs;
    }

    private static List<Input> makeCompressibilityInputs() {
        List<Input> inputs = new ArrayList<>();

        int jobNumber = 1;
        Input input;
        double pressure = 0;
        final double horizontalRescaleFactor = 9;
        final double verticalRescaleFactor = .3;
        final double pressureStep = .05;


        for (int i = 0; i < 5; i++) {
            input = makeRescaleInput(verticalRescaleFactor, horizontalRescaleFactor, pressure, jobNumber);
            inputs.add(input);
            jobNumber++;
            pressure += pressureStep;
        }


        return inputs;
    }

}
