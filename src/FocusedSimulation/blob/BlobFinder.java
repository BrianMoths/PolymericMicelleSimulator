/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FocusedSimulation.blob;

import FocusedSimulation.AbstractFocusedSimulation;
import FocusedSimulation.StatisticsTracker.TrackableVariable;
import FocusedSimulation.surfacetension.SurfaceTensionResultsWriter;
import SGEManagement.Input;
import SystemAnalysis.StressTrackable;
import java.io.FileNotFoundException;

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
    protected void registerTrackablesToSimulationRunner() {
        simulationRunner.trackVariable(TrackableVariable.SYSTEM_WIDTH);
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        //want to print area occupied by beads, ideal gas pressure, stress, 
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
