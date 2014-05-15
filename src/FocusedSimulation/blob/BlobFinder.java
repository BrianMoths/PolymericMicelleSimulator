/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FocusedSimulation.blob;

import Engine.SimulationStepping.StepGenerators.CompoundStepGenerators.GeneralStepGenerator;
import Engine.SimulationStepping.StepGenerators.StepGenerator;
import Engine.SimulationStepping.StepTypes.StepType;
import FocusedSimulation.AbstractFocusedSimulation;
import FocusedSimulation.StatisticsTracker.TrackableVariable;
import FocusedSimulation.surfacetension.SurfaceTensionResultsWriter;
import SGEManagement.Input;
import SystemAnalysis.StressTrackable;
import java.io.FileNotFoundException;
import java.util.EnumMap;

/**
 *
 * @author bmoths
 */
public class BlobFinder extends AbstractFocusedSimulation<BlobResultsWriter> {

    static private final double stressTrackableSizeFraction = .1;
    private final StressTrackable middleStressTrackable;

    public BlobFinder(Input input, BlobResultsWriter outputWriter) throws FileNotFoundException {
        super(input, outputWriter);
        middleStressTrackable = new StressTrackable(stressTrackableSizeFraction);
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
//        analyzeAndPrintDensity();
//        analyzeAndPrintVolume();
//        analyzeAndPrintIdealGasPressure();
//        analyzeAndPrintStress();
    }

    @Override
    protected boolean isConverged() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void printFinalOutput() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
