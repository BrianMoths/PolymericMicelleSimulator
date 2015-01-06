/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FocusedSimulation.diblockenergybalance;

import Engine.SimulationStepping.StepGenerators.CompoundStepGenerators.GeneralStepGenerator;
import Engine.SimulationStepping.StepGenerators.StepGenerator;
import Engine.SimulationStepping.StepTypes.StepType;
import FocusedSimulation.bulkmixture.BulkMixtureFinder;
import static FocusedSimulation.diblockenergybalance.DiblockJobMaker.makeHorizontallyRescaledInputBuilder;
import FocusedSimulation.simulationrunner.SimulationRunnerParameters.SimulationRunnerParametersBuilder;
import SGEManagement.Input;
import SGEManagement.Input.InputBuilder;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.EnumMap;

/**
 *
 * @author bmoths
 */
public class DiblockFinder extends BulkMixtureFinder<DiblockResultsWriter> {

    public static void main(String[] args) {
        final Input input = readInput(args);
        try {
            final DiblockFinder diblockFinder;
            diblockFinder = makeDiblockFinderWithDefaultWriter(input);
            diblockFinder.doSimulation();
            diblockFinder.closeOutputWriter();
        } catch (FileNotFoundException ex) {
            System.out.println("File not able to be opened");
        }
    }

    private static Input readInput(String[] args) {
        if (args.length == 0) {
//            final double verticalScaleFactor = 1;//.3
//            final double horizontalScaleFactor = 1;//6
//
//            InputBuilder inputBuilder = makeHorizontallyRescaledInputBuilder(horizontalScaleFactor, 0);
//            SimulationRunnerParametersBuilder simulationRunnerParametersBuilder = inputBuilder.getJobParametersBuilder().getSimulationRunnerParametersBuilder();
//            inputBuilder.getJobParametersBuilder().setNumAnneals(20);
//            simulationRunnerParametersBuilder.setNumIterationsPerSample(100000);
//            simulationRunnerParametersBuilder.setNumSamples(10);
//            inputBuilder.getJobParametersBuilder().setNumSimulationTrials(1000);
//            inputBuilder.getSystemParametersBuilder().getEnergeticsConstantsBuilder().getExternalEnergyCalculatorBuilder().setxTensionAndQuadratic(-.3, 0);
//            inputBuilder.getJobParametersBuilder().setJobString("EnergyScaling");
//            return inputBuilder.buildInput();
            return DiblockJobMaker.makeRescalingInput(1, 0, 2, -2 * 1.2 / 5).buildInput();
        } else if (args.length == 1) {
            final String fileName = args[0];
            return Input.readInputFromFile(fileName);
        } else {
            throw new IllegalArgumentException("At most one input allowed");
        }
    }

    private static DiblockFinder makeDiblockFinderWithDefaultWriter(Input input) throws FileNotFoundException {
        return new DiblockFinder(input, new DiblockResultsWriter(input));
    }

    private DiblockFinder(Input input, DiblockResultsWriter bulkMixtureResultsWriter) throws FileNotFoundException {
        super(input, bulkMixtureResultsWriter);
    }

    @Override
    protected StepGenerator makeInitialStepGenerator() {
        EnumMap<StepType, Double> stepweights = new EnumMap<>(StepType.class);
        stepweights.put(StepType.SINGLE_BEAD, 1.);
        return new GeneralStepGenerator(stepweights);
    }

    @Override
    protected void initializePositions() {
        polymerSimulator.linearInitialize();
//        polymerSimulator.reasonableRandomize();
    }

}
