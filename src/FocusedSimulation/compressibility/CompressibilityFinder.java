/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FocusedSimulation.compressibility;

import Engine.PolymerSimulator;
import Engine.SimulatorParameters;
import FocusedSimulation.DoubleWithUncertainty;
import FocusedSimulation.JobParameters;
import FocusedSimulation.SimulationRunner;
import FocusedSimulation.SimulationRunner.SimulationRunnerParameters;
import FocusedSimulation.StatisticsTracker.TrackableVariable;
import FocusedSimulation.bulkproperties.BulkPropertiesFinder;
import SGEManagement.Input;
import SGEManagement.Input.InputBuilder;
import java.io.FileNotFoundException;

/**
 *
 * @author bmoths
 */
public class CompressibilityFinder extends BulkPropertiesFinder<CompressibilityResultsWriter> {

    public static void main(String[] args) {
        final Input input = readInput(args);
        try {
            final CompressibilityFinder compressibilityFinder;
            compressibilityFinder = new CompressibilityFinder(input);
            compressibilityFinder.doSimulation();
            compressibilityFinder.closeOutputWriter();
        } catch (FileNotFoundException ex) {
            System.out.println("File not able to be opened");
        }
    }

    private static Input readInput(String[] args) {
        if (args.length == 0) {
            final double verticalScaleFactor = .1;
            final double horizontalScaleFactor = 4;

            InputBuilder inputBuilder = CompressibilityJobMaker.makeRescaleInputBuilderWithHorizontalRescaling(verticalScaleFactor, horizontalScaleFactor, 0);
            inputBuilder.getJobParametersBuilder().setNumAnneals(5);
            return inputBuilder.buildInputAutomaticHardOverlap();
        } else if (args.length == 1) {
            final String fileName = args[0];
            return Input.readInputFromFile(fileName);
        } else {
            throw new IllegalArgumentException("At most one input allowed");
        }
    }

    public CompressibilityFinder(Input input) throws FileNotFoundException {
        super(input, new CompressibilityResultsWriter(input));
    }

    @Override
    protected void analyzeAndPrintResults() {
        super.analyzeAndPrintResults(); //To change body of generated methods, choose Tools | Templates.
        analyzeAndPrintCompressibilty();
    }

    private void analyzeAndPrintCompressibilty() {
        final DoubleWithUncertainty equilibriumDensity = new DoubleWithUncertainty(.2970, .0003);
        final DoubleWithUncertainty compressedDensity = simulationRunner.getRecentMeasurementForTrackedVariable(TrackableVariable.NUMBER_DENSITY);
        final double pressure = simulationRunner.getPolymerSimulator().getEnergeticsConstants().getExternalEnergyCalculator().getPressure();

        final DoubleWithUncertainty compressiblity = compressedDensity.plus(equilibriumDensity.negation()).times(1 / (pressure * equilibriumDensity.getValue()));
        outputWriter.printCompressiblity(compressiblity);
    }

}
