/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FocusedSimulation.bulkproperties;

import FocusedSimulation.AbstractFocusedSimulation;
import FocusedSimulation.DoubleWithUncertainty;
import FocusedSimulation.StatisticsTracker.TrackableVariable;
import FocusedSimulation.surfacetension.SurfaceTensionJobMaker;
import SGEManagement.Input;
import SGEManagement.Input.InputBuilder;
import SystemAnalysis.StressTrackable;
import java.io.FileNotFoundException;

/**
 *
 * @author bmoths
 */
public class BulkPropertiesFinder extends AbstractFocusedSimulation<BulkPropertiesResultsWriter> {

    public static void main(String[] args) {
        final Input input = readInput(args);
        try {
            final BulkPropertiesFinder densityFinder;
            densityFinder = new BulkPropertiesFinder(input);
            densityFinder.doSimulation();
            densityFinder.closeOutputWriter();
        } catch (FileNotFoundException ex) {
            System.out.println("File not able to be opened");
        }
    }

    private static Input readInput(String[] args) {
        if (args.length == 0) {
            final double verticalScaleFactor = .3;
            final double horizontalScaleFactor = 9;

            InputBuilder inputBuilder = BulkPropertiesJobMaker.makeRescaleInputBuilderWithHorizontalRescaling(verticalScaleFactor, horizontalScaleFactor, 0, 0);
            inputBuilder.getJobParametersBuilder().setNumAnneals(1);
            inputBuilder.getJobParametersBuilder().setNumSimulationTrials(5);
            return inputBuilder.buildInputAutomaticHardOverlap();
        } else if (args.length == 1) {
            final String fileName = args[0];
            return Input.readInputFromFile(fileName);
        } else {
            throw new IllegalArgumentException("At most one input allowed");
        }
    }

    private BulkPropertiesFinder(Input input) throws FileNotFoundException {
        super(input, new BulkPropertiesResultsWriter(input));
    }

    //<editor-fold defaultstate="collapsed" desc="initialize">
    @Override
    protected void initializePositions() {
        polymerSimulator.reasonableRandomize();
    }

    @Override
    protected void registerTrackablesToSimulationRunner() {
        simulationRunner.trackVariable(TrackableVariable.SYSTEM_VOLUME);
        simulationRunner.trackVariable(TrackableVariable.SYSTEM_ENERGY);
        simulationRunner.trackVariable(TrackableVariable.SYSTEM_ENTROPY);
        simulationRunner.trackVariable((StressTrackable.TOTAL_STRESS_TRACKABLE).getStress11Trackable());
        simulationRunner.trackVariable((StressTrackable.TOTAL_STRESS_TRACKABLE).getStress12Trackable());
        simulationRunner.trackVariable((StressTrackable.TOTAL_STRESS_TRACKABLE).getStress22Trackable());
    }

    @Override
    protected void printInitialOutput() {
        outputWriter.printPressure();
    }
    //</editor-fold>

    @Override
    protected void analyzeAndPrintResults() {
        DoubleWithUncertainty measuredVolume = simulationRunner.getRecentMeasurementForTrackedVariable(TrackableVariable.SYSTEM_VOLUME);
        final double numBeads = polymerSimulator.getNumBeads();
        DoubleWithUncertainty measuredDensity = measuredVolume.reciprocalTimes(numBeads);
        outputWriter.printMeasuredDensity(measuredDensity);

        DoubleWithUncertainty measuredEnergy = simulationRunner.getRecentMeasurementForTrackedVariable(TrackableVariable.SYSTEM_ENERGY);
        DoubleWithUncertainty measuredEnergyPerBead = measuredEnergy.dividedBy(polymerSimulator.getNumBeads());
        outputWriter.printMeasuredEnergyPerBead(measuredEnergyPerBead);

        DoubleWithUncertainty measuredEntropy = simulationRunner.getRecentMeasurementForTrackedVariable(TrackableVariable.SYSTEM_VOLUME);
        DoubleWithUncertainty measuredEntropyPerBead = measuredEntropy.reciprocalTimes(numBeads);
        outputWriter.printMeasuredEntropyPerBead(measuredEntropyPerBead);

        outputWriter.printIdealGasPressure(polymerSimulator.getSystemAnalyzer().getIdealGasPressure());
        outputWriter.printStress(simulationRunner);
    }

    @Override
    protected boolean isConverged() {
        return simulationRunner.isConverged(TrackableVariable.SYSTEM_VOLUME);
    }

    @Override
    protected void printFinalOutput() {
    }

}
