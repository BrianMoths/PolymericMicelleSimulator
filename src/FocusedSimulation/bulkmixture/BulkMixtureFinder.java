/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FocusedSimulation.bulkmixture;

import FocusedSimulation.AbstractFocusedSimulation;
import SGEManagement.Input;
import SGEManagement.Input.InputBuilder;
import java.io.FileNotFoundException;

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
            final double verticalScaleFactor = .05;
            final double horizontalScaleFactor = 2;

            InputBuilder inputBuilder = BulkMixtureJobMaker.makeRescaleInputBuilderWithHorizontalRescaling(verticalScaleFactor, horizontalScaleFactor, 0);
            inputBuilder.getJobParametersBuilder().setNumAnneals(1);
            inputBuilder.getJobParametersBuilder().setNumSimulationTrials(2);
            inputBuilder.getJobParametersBuilder().getSimulationRunnerParametersBuilder().setNumIterationsPerAnneal(10000);
            inputBuilder.getJobParametersBuilder().getSimulationRunnerParametersBuilder().setNumIterationsPerSample(10000);
            inputBuilder.getJobParametersBuilder().getSimulationRunnerParametersBuilder().setNumSamples(200);
            inputBuilder.getJobParametersBuilder().setShouldIterateUntilConvergence(true);
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

    private static BulkMixtureFinder makeBulkMixtureFinderWithDefaultWriter(Input input) throws FileNotFoundException {
        return new BulkMixtureFinder(input, new BulkMixtureResultsWriter(input));
    }

    @Override
    protected void initializePositions() {
        polymerSimulator.reasonableRandomize();
    }

    @Override
    protected void registerTrackablesToSimulationRunner() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void printInitialOutput() {
    }

    @Override
    protected void analyzeAndPrintResults() {
    }

    @Override
    protected boolean isConverged() {
        return true;
    }

    @Override
    protected void printFinalOutput() {
    }

}
