/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FocusedSimulation.surfacetension;

import Engine.PolymerSimulator;
import FocusedSimulation.AbstractResultsWriter;
import FocusedSimulation.DoubleWithUncertainty;
import FocusedSimulation.SimulationRunner;
import FocusedSimulation.surfacetension.SurfaceTensionFinder.MeasuredSurfaceTension;
import SGEManagement.Input;
import SystemAnalysis.StressTrackable;
import java.io.FileNotFoundException;

/**
 *
 * @author brian
 */
public class SurfaceTensionResultsWriter extends AbstractResultsWriter {

    static private String makeSurfaceTensionString(MeasuredSurfaceTension measuredSurfaceTension) {
        StringBuilder parametersStringBuilder = new StringBuilder();
        parametersStringBuilder
                .append("Surface Tension found: ")
                .append(Double.toString(measuredSurfaceTension.surfaceTension))
                .append(" +/- ")
                .append(Double.toString(measuredSurfaceTension.surfaceTensionStandardError))
                .append("\n");
        return parametersStringBuilder.toString();
    }

    static private String makeStressString(SimulationRunner simulationRunner) {
        StringBuilder stringBuilder = new StringBuilder();
        final DoubleWithUncertainty stress11 = simulationRunner.getRecentMeasurementForTrackedVariable(StressTrackable.STRESS_TRACKABLE.getStress11Trackable());
        final DoubleWithUncertainty stress12 = simulationRunner.getRecentMeasurementForTrackedVariable(StressTrackable.STRESS_TRACKABLE.getStress12Trackable());
        final DoubleWithUncertainty stress22 = simulationRunner.getRecentMeasurementForTrackedVariable(StressTrackable.STRESS_TRACKABLE.getStress22Trackable());
        stringBuilder.append("[").append(stress11.getValue()).append("  ").append(stress12.getValue()).append("]  +/-  [").append(stress11.getUncertainty()).append(" ").append(stress12.getUncertainty()).append("]\n");
        stringBuilder.append("[").append(stress12.getValue()).append("  ").append(stress22.getValue()).append("]  +/-  [").append(stress12.getUncertainty()).append(" ").append(stress22.getUncertainty()).append("]\n");
        stringBuilder.append("\n");
        final String outputString = stringBuilder.toString();
        return outputString;
    }

    public SurfaceTensionResultsWriter(Input input) throws FileNotFoundException {
        super(input);
    }

    public void printInitializationInfo(PolymerSimulator polymerSimulator) {
        final String initializationString = "Initial Horizontal Size of System: " + polymerSimulator.getGeometry().getSizeOfDimension(0) + "\n";
        printAndSoutString(initializationString);
    }

    public void printSurfaceTension(MeasuredSurfaceTension measuredSurfaceTension) {
        String surfaceTensionString = makeSurfaceTensionString(measuredSurfaceTension);
        printAndSoutString(surfaceTensionString);
    }

    public void printStress(SimulationRunner simulationRunner) {
        String outputString = makeStressString(simulationRunner);
        printAndSoutString(outputString);
    }

}
