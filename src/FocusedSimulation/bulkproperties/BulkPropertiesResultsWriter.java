/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FocusedSimulation.bulkproperties;

import Engine.PolymerSimulator;
import FocusedSimulation.DoubleWithUncertainty;
import FocusedSimulation.output.AbstractResultsWriter;
import SGEManagement.Input;
import java.io.FileNotFoundException;

/**
 *
 * @author bmoths
 */
public class BulkPropertiesResultsWriter extends AbstractResultsWriter {

    private String makeMeasuredDensityString(DoubleWithUncertainty measuredDensity) {
        StringBuilder parametersStringBuilder = new StringBuilder();
        parametersStringBuilder
                .append("Density found: ")
                .append(makeDoubleWithUncertaintyCharSequence(measuredDensity))
                .append("\n");
        return parametersStringBuilder.toString();
    }

    public BulkPropertiesResultsWriter(Input input) throws FileNotFoundException {
        super(input);
    }

    void printMeasuredDensity(DoubleWithUncertainty measuredDensity) {
        final String measuredDensityString = makeMeasuredDensityString(measuredDensity);
        printAndSoutString(measuredDensityString);
    }

    void printPressure() {
        printAndSoutString(makePressureString());
    }

    private String makePressureString() {
        return "Pressure: " + input.getSystemParameters().energeticsConstants.getExternalEnergyCalculator().getPressure() + "\n";
    }

}
