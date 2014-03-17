/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FocusedSimulation.density;

import Engine.PolymerSimulator;
import FocusedSimulation.DoubleWithUncertainty;
import FocusedSimulation.output.AbstractResultsWriter;
import SGEManagement.Input;
import java.io.FileNotFoundException;

/**
 *
 * @author bmoths
 */
public class DensityResultsWriter extends AbstractResultsWriter {

    private String makeMeasuredDensityString(DoubleWithUncertainty measuredDensity) {
        StringBuilder parametersStringBuilder = new StringBuilder();
        parametersStringBuilder
                .append("Density found: ")
                .append(makeDoubleWithUncertaintyCharSequence(measuredDensity))
                .append("\n");
        return parametersStringBuilder.toString();
    }

    public DensityResultsWriter(Input input) throws FileNotFoundException {
        super(input);
    }

    void printMeasuredDensity(DoubleWithUncertainty measuredDensity) {
        final String measuredDensityString = makeMeasuredDensityString(measuredDensity);
        printAndSoutString(measuredDensityString);
    }

}
