/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FocusedSimulation.density;

import Engine.PolymerSimulator;
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
public class DensityFinder extends AbstractFocusedSimulation<DensityResultsWriter> {

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
            final double verticalScaleFactor = .3;
            final double horizontalScaleFactor = 9;

            InputBuilder inputBuilder = DensityJobMaker.makeRescaleInputBuilderWithHorizontalRescaling(verticalScaleFactor, horizontalScaleFactor, 0);
            inputBuilder.getJobParametersBuilder().setNumAnneals(1);
            inputBuilder.getJobParametersBuilder().setNumSurfaceTensionTrials(2);
            return inputBuilder.buildInput();
        } else if (args.length == 1) {
            final String fileName = args[0];
            return Input.readInputFromFile(fileName);
        } else {
            throw new IllegalArgumentException("At most one input allowed");
        }
    }

    private DoubleWithUncertainty getMeasuredDensityFromVolume(DoubleWithUncertainty measuredVolume, PolymerSimulator polymerSimulator) {
        final double numBeads = polymerSimulator.getNumBeads();
        final double density = numBeads / measuredVolume.getValue();
        final double uncertainty = density * measuredVolume.getRelativeError();
        return new DoubleWithUncertainty(density, uncertainty);
    }

    private DensityFinder(Input input) throws FileNotFoundException {
        super(input, new DensityResultsWriter(input));
    }

    //<editor-fold defaultstate="collapsed" desc="initialize">
    @Override
    protected void initializePositions() {
        polymerSimulator.reasonableRandomize();
    }

    @Override
    protected void registerTrackablesToSimulationRunner() {
        simulationRunner.trackVariable(TrackableVariable.SYSTEM_VOLUME);
        simulationRunner.trackVariable((StressTrackable.TOTAL_STRESS_TRACKABLE).getStress11Trackable());
        simulationRunner.trackVariable((StressTrackable.TOTAL_STRESS_TRACKABLE).getStress12Trackable());
        simulationRunner.trackVariable((StressTrackable.TOTAL_STRESS_TRACKABLE).getStress22Trackable());
    }

    @Override
    protected void printInitialOutput() {
    }
    //</editor-fold>

    @Override
    protected void analyzeAndPrintResults() {
        DoubleWithUncertainty measuredVolume = simulationRunner.getRecentMeasurementForTrackedVariable(TrackableVariable.SYSTEM_VOLUME);
        DoubleWithUncertainty measuredDensity = getMeasuredDensityFromVolume(measuredVolume, polymerSimulator);
        outputWriter.printMeasuredDensity(measuredDensity);
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
