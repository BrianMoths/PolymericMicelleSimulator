/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FocusedSimulation.blob;

import Engine.SimulationStepping.StepGenerators.CompoundStepGenerators.GeneralStepGenerator;
import Engine.SimulationStepping.StepGenerators.StepGenerator;
import Engine.SimulationStepping.StepTypes.StepType;
import FocusedSimulation.AbstractFocusedSimulation;
import FocusedSimulation.DoubleWithUncertainty;
import FocusedSimulation.StatisticsTracker.TrackableVariable;
import FocusedSimulation.compressibility.CompressibilityFinder;
import FocusedSimulation.compressibility.CompressibilityJobMaker;
import FocusedSimulation.surfacetension.SurfaceTensionResultsWriter;
import SGEManagement.Input;
import SGEManagement.Input.InputBuilder;
import SystemAnalysis.FractionalVolumeStressTrackable;
import java.io.FileNotFoundException;
import java.util.EnumMap;

/**
 *
 * @author bmoths
 */
public class BlobFinder extends AbstractFocusedSimulation<BlobResultsWriter> {

    static private final double stressTrackableSizeFraction = .1;

    public static void main(String[] args) {
        final Input input = readInput(args);
        try {
            final BlobFinder blobFinder;
            blobFinder = new BlobFinder(input);
            blobFinder.doSimulation();
            blobFinder.closeOutputWriter();
        } catch (FileNotFoundException ex) {
            System.out.println("File not able to be opened");
        }
    }

    private static Input readInput(String[] args) {
        if (args.length == 0) {
            final double verticalScaleFactor = .1;
            final double horizontalScaleFactor = 4;

            InputBuilder inputBuilder = BlobJobMaker.makeRescaleInputBuilderWithHorizontalRescaling(verticalScaleFactor, horizontalScaleFactor, 0);
            inputBuilder.getJobParametersBuilder().setNumAnneals(5);
            inputBuilder.getJobParametersBuilder().getSimulationRunnerParametersBuilder().setNumSamples(10); //1000
            return inputBuilder.buildInput();
        } else if (args.length == 1) {
            final String fileName = args[0];
            return Input.readInputFromFile(fileName);
        } else {
            throw new IllegalArgumentException("At most one input allowed");
        }
    }

    private final FractionalVolumeStressTrackable middleStressTrackable;

    public BlobFinder(Input input) throws FileNotFoundException {
        super(input, new BlobResultsWriter(input));
        middleStressTrackable = new FractionalVolumeStressTrackable(stressTrackableSizeFraction);
    }

    //<editor-fold defaultstate="collapsed" desc="initialize">
    @Override
    protected void initializePositions() {
        polymerSimulator.reasonableMiddleRandomize();
    }

    @Override
    protected StepGenerator makeMainStepGenerator() {
        EnumMap<StepType, Double> stepweights = new EnumMap<>(StepType.class);
        stepweights.put(StepType.SINGLE_BEAD, 1.);
        stepweights.put(StepType.REPTATION, .1);
        stepweights.put(StepType.SINGLE_CHAIN, .01);
        return new GeneralStepGenerator(stepweights);
    }

    @Override
    protected StepGenerator makeInitialStepGenerator() {
        EnumMap<StepType, Double> stepweights = new EnumMap<>(StepType.class);
        stepweights.put(StepType.SINGLE_BEAD, 1.);
        stepweights.put(StepType.REPTATION, .01);
        stepweights.put(StepType.SINGLE_CHAIN, .01);
        return new GeneralStepGenerator(stepweights);
    }

    @Override
    protected void registerTrackablesToSimulationRunner() {
        simulationRunner.trackVariable(middleStressTrackable.getStress11Trackable());
        simulationRunner.trackVariable(middleStressTrackable.getStress12Trackable());
        simulationRunner.trackVariable(middleStressTrackable.getStress22Trackable());
        simulationRunner.trackVariable(TrackableVariable.OCCUPIED_VOLUME);
    }

    @Override
    protected void printInitialOutput() {
    }
    //</editor-fold>

    @Override
    protected void analyzeAndPrintResults() {
        simulationRunner.getPolymerSimulator().recenter();
        analyzeAndPrintDensity();
        outputWriter.printStress(simulationRunner, middleStressTrackable);
    }

    private void analyzeAndPrintDensity() {
        final DoubleWithUncertainty occupiedVolume = simulationRunner.getRecentMeasurementForTrackedVariable(TrackableVariable.OCCUPIED_VOLUME);
        final DoubleWithUncertainty radius = occupiedVolume.dividedBy(Math.PI).sqrt();
        outputWriter.printEstimatedRadius(radius);
        final DoubleWithUncertainty measuredDensity = occupiedVolume.reciprocalTimes(simulationRunner.getPolymerSimulator().getNumBeads());
        outputWriter.printMeasuredDensity(measuredDensity);
    }

    @Override
    protected boolean isConverged() {
        return simulationRunner.isConverged(TrackableVariable.SYSTEM_VOLUME);
    }

    @Override
    protected void printFinalOutput() {
    }

}
