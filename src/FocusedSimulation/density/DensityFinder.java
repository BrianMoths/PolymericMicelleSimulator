/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FocusedSimulation.density;

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
public class DensityFinder extends AbstractFocusedSimulation {

    public static void main(String[] args) {
        final Input input = readInput(args);
        try {
            final DensityFinder densityFinder;
            densityFinder = new DensityFinder(input);
            densityFinder.doSimulation();
            densityFinder.closeOutputWriter();
        } catch (FileNotFoundException ex) {
            System.out.println("File not able to be opened");
        }
    }

    private static Input readInput(String[] args) {
        if (args.length == 0) {
            final double verticalScaleFactor = .1;
            final double horizontalScaleFactor = 1;

            InputBuilder inputBuilder = SurfaceTensionJobMaker.makeRescaleInputBuilderWithHorizontalRescaling(verticalScaleFactor, horizontalScaleFactor, 0);
            inputBuilder.getJobParametersBuilder().setNumAnneals(1);
            inputBuilder.getJobParametersBuilder().setNumSurfaceTensionTrials(1);
            return inputBuilder.buildInput();
        } else if (args.length == 1) {
            final String fileName = args[0];
            return Input.readInputFromFile(fileName);
        } else {
            throw new IllegalArgumentException("At most one input allowed");
        }
    }

    private final DensityResultsWriter densityResultsWriter;

    private DensityFinder(Input input) throws FileNotFoundException {
        super(input);
        densityResultsWriter = new DensityResultsWriter(input);
    }

    //<editor-fold defaultstate="collapsed" desc="initialize">
    @Override
    protected void initializePositions() {
        polymerSimulator.reasonableColumnRandomize();
    }

    @Override
    protected void registerTrackablesToSimulationRunner() {
        simulationRunner.trackVariable(TrackableVariable.SYSTEM_WIDTH);
        simulationRunner.trackVariable((StressTrackable.TOTAL_STRESS_TRACKABLE).getStress11Trackable());
        simulationRunner.trackVariable((StressTrackable.TOTAL_STRESS_TRACKABLE).getStress12Trackable());
        simulationRunner.trackVariable((StressTrackable.TOTAL_STRESS_TRACKABLE).getStress22Trackable());
    }

    @Override
    protected void printInitialOutput() {
        densityResultsWriter.printInitializationInfo(polymerSimulator);
    }
    //</editor-fold>

    @Override
    protected void analyzeAndPrintResults() {
        DoubleWithUncertainty measuredWidth = simulationRunner.getRecentMeasurementForTrackedVariable(TrackableVariable.SYSTEM_WIDTH);
    }

    @Override
    protected boolean isConverged() {
        return simulationRunner.isConverged(TrackableVariable.SYSTEM_WIDTH);
    }

    @Override
    protected void printFinalOutput() {
        densityResultsWriter.printFinalOutput(polymerSimulator);
    }

}
