/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FocusedSimulation.diblockenergybalance;

import Engine.PolymerTopology.PolymerCluster;
import Engine.SimulatorParameters.SystemParametersBuilder;
import FocusedSimulation.bulkmixture.BulkMixtureFinder;
import FocusedSimulation.bulkmixture.BulkMixtureJobMaker;
import FocusedSimulation.bulkmixture.BulkMixtureResultsWriter;
import SGEManagement.Input;
import SGEManagement.Input.InputBuilder;
import java.io.FileNotFoundException;

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
            final double verticalScaleFactor = 1;//.3
            final double horizontalScaleFactor = 1;//6

            InputBuilder inputBuilder = DiblockJobMaker.makeHorizontallyRescaledInputBuilder(1, 0);
            inputBuilder.getJobParametersBuilder().setNumAnneals(1);
            inputBuilder.getJobParametersBuilder().setNumSimulationTrials(4);
            inputBuilder.getJobParametersBuilder().getSimulationRunnerParametersBuilder().setNumIterationsPerAnneal(10000);
            inputBuilder.getJobParametersBuilder().getSimulationRunnerParametersBuilder().setNumIterationsPerSample(10000);
            inputBuilder.getJobParametersBuilder().getSimulationRunnerParametersBuilder().setNumSamples(2000);
            return inputBuilder.buildInput();
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
    protected void initializePositions() {
//        polymerSimulator.linearInitialize();
        polymerSimulator.reasonableRandomize();
    }

}
