/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FocusedSimulation.bulkmixture;

import Engine.PolymerTopology.PolymerCluster;
import Engine.SimulationStepping.StepGenerators.CompoundStepGenerators.GeneralStepGenerator;
import Engine.SimulationStepping.StepGenerators.StepGenerator;
import Engine.SimulationStepping.StepTypes.SimulationStep;
import Engine.SimulationStepping.StepTypes.StepType;
import Engine.SimulatorParameters.SystemParametersBuilder;
import Engine.SystemAnalyzer;
import FocusedSimulation.AbstractFocusedSimulation;
import FocusedSimulation.DoubleWithUncertainty;
import FocusedSimulation.simulationrunner.StatisticsTracker.TrackableVariable;
import SGEManagement.Input;
import SGEManagement.Input.InputBuilder;
import java.io.FileNotFoundException;
import java.util.EnumMap;

/**
 *
 * @author bmoths
 */
public class BulkMixtureFinder extends AbstractFocusedSimulation<BulkMixtureResultsWriter> {

    public static void main(String[] args) {
        final Input input = readInput(args);
        try {
            final BulkMixtureFinder bulkMixtureFinder;
            bulkMixtureFinder = makeBulkMixtureFinderWithDefaultWriter(input);
            bulkMixtureFinder.doSimulation();
            bulkMixtureFinder.closeOutputWriter();
        } catch (FileNotFoundException ex) {
            System.out.println("File not able to be opened");
        }
    }

    private static Input readInput(String[] args) {
        if (args.length == 0) {
            final double verticalScaleFactor = .15;//.3
            final double horizontalScaleFactor = 2;//6
            final double hydrophilicFraction = .405;//.5

            InputBuilder inputBuilder = BulkMixtureJobMaker.makeRescaleInputBuilderWithHorizontalRescaling(verticalScaleFactor, horizontalScaleFactor, 0);
            inputBuilder.getJobParametersBuilder().setNumAnneals(1);
            inputBuilder.getJobParametersBuilder().setNumSimulationTrials(4);
            inputBuilder.getJobParametersBuilder().getSimulationRunnerParametersBuilder().setNumIterationsPerAnneal(10000);
            inputBuilder.getJobParametersBuilder().getSimulationRunnerParametersBuilder().setNumIterationsPerSample(10000);
            inputBuilder.getJobParametersBuilder().getSimulationRunnerParametersBuilder().setNumSamples(2000);
            inputBuilder.getJobParametersBuilder().setShouldIterateUntilConvergence(false);
            final SystemParametersBuilder systemParametersBuilder = inputBuilder.systemParametersBuilder;
            final int numChains = systemParametersBuilder.getPolymerCluster().getNumChains();
            PolymerCluster newPolymerCluster = BulkMixtureJobMaker.getPolymerCluster(hydrophilicFraction, numChains, 16, 1);
            systemParametersBuilder.setPolymerCluster(newPolymerCluster);
            return inputBuilder.buildInput();
        } else if (args.length == 1) {
            final String fileName = args[0];
            return Input.readInputFromFile(fileName);
        } else {
            throw new IllegalArgumentException("At most one input allowed");
        }
    }

    private BulkMixtureFinder(Input input, BulkMixtureResultsWriter bulkMixtureResultsWriter) throws FileNotFoundException {
        super(input, bulkMixtureResultsWriter);
    }

    @Override
    protected StepGenerator makeMainStepGenerator() {
        EnumMap<StepType, Double> weights = new EnumMap<>(StepType.class);
        weights.put(StepType.REPTATION, .01);
        weights.put(StepType.SINGLE_BEAD, 1.);
        weights.put(StepType.SINGLE_CHAIN, .01);
        weights.put(StepType.SINGLE_WALL_HORIZONTAL_RESIZE, .001);
        weights.put(StepType.SINGLE_WALL_VERTICAL_RESIZE, .001);

        StepGenerator stepGenerator = new GeneralStepGenerator(weights);
        return stepGenerator;
    }

    private static BulkMixtureFinder makeBulkMixtureFinderWithDefaultWriter(Input input) throws FileNotFoundException {
        return new BulkMixtureFinder(input, new BulkMixtureResultsWriter(input));
    }

    @Override
    protected void initializePositions() {
        polymerSimulator.reasonableRandomize();
    }

    @Override
    protected void registerTrackablesToSimulationRunner() {
        simulationRunner.trackVariable(TrackableVariable.SYSTEM_WIDTH);
        simulationRunner.trackVariable(TrackableVariable.SYSTEM_HEIGHT);
        simulationRunner.trackVariable(TrackableVariable.FREE_ENERGY_PER_BEAD);
        simulationRunner.trackVariable(TrackableVariable.SYSTEM_ENERGY);
    }

    @Override
    protected void printInitialOutput() {
        outputWriter.printInitialOutput();
    }

    @Override
    protected void analyzeAndPrintResults() {
        DoubleWithUncertainty measuredWidth = simulationRunner.getRecentMeasurementForTrackedVariable(TrackableVariable.SYSTEM_WIDTH);
        outputWriter.printWidthOfBox(measuredWidth);
        DoubleWithUncertainty measuredHeight = simulationRunner.getRecentMeasurementForTrackedVariable(TrackableVariable.SYSTEM_HEIGHT);
        outputWriter.printHeightOfBox(measuredHeight);
        DoubleWithUncertainty freeEnergyPerBead = simulationRunner.getRecentMeasurementForTrackedVariable(TrackableVariable.FREE_ENERGY_PER_BEAD);
        outputWriter.printFreeEnergyPerBead(freeEnergyPerBead);
        DoubleWithUncertainty energyPerBead = simulationRunner.getRecentMeasurementForTrackedVariable(TrackableVariable.SYSTEM_ENERGY).dividedBy(getInputParameters().getPolymerCluster().getNumBeads());
        outputWriter.printEnergyPerBead(energyPerBead);
    }

    @Override
    protected boolean isConverged() {
        return true;
    }

    @Override
    protected void printFinalOutput() {
    }

}
