/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FocusedSimulation.surfacetension;

import Engine.PolymerSimulator;
import FocusedSimulation.DoubleWithUncertainty;
import FocusedSimulation.output.AbstractResultsWriter;
import SGEManagement.Input;
import java.io.FileNotFoundException;

/**
 *
 * @author brian
 */
public class SurfaceTensionResultsWriter extends AbstractResultsWriter {

    static private String makeSurfaceTensionString(DoubleWithUncertainty measuredSurfaceTension) {
        StringBuilder parametersStringBuilder = new StringBuilder();
        parametersStringBuilder
                .append("Surface Tension found: ")
                .append(Double.toString(measuredSurfaceTension.getValue()))
                .append(" +/- ")
                .append(Double.toString(measuredSurfaceTension.getUncertainty()))
                .append("\n");
        return parametersStringBuilder.toString();
    }

    public SurfaceTensionResultsWriter(Input input) throws FileNotFoundException {
        super(input);
    }

    public void printInitializationInfo(PolymerSimulator polymerSimulator) {
        final String initializationString = "Initial Horizontal Size of System: " + polymerSimulator.getGeometry().getSizeOfDimension(0) + "\n";
        printAndSoutString(initializationString);
    }

    public void printSurfaceTension(DoubleWithUncertainty measuredSurfaceTension) {
        String surfaceTensionString = makeSurfaceTensionString(measuredSurfaceTension);
        printAndSoutString(surfaceTensionString);
    }

}
