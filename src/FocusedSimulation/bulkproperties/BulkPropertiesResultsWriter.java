/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FocusedSimulation.bulkproperties;

import FocusedSimulation.DoubleWithUncertainty;
import FocusedSimulation.output.AbstractResultsWriter;
import SGEManagement.Input;
import java.io.FileNotFoundException;

/**
 *
 * @author bmoths
 */
public class BulkPropertiesResultsWriter extends AbstractResultsWriter {

    public BulkPropertiesResultsWriter(Input input) throws FileNotFoundException {
        super(input);
    }

    void printMeasuredDensity(DoubleWithUncertainty measuredDensity) {
        final String caption = "Density found";
        printAndSoutCaptionedObject(caption, measuredDensity);
    }

    void printPressure() {
        printAndSoutCaptionedObject("Pressure", input.getSystemParameters().energeticsConstants.getExternalEnergyCalculator().getPressure());
    }

    void printMeasuredEnergyPerBead(DoubleWithUncertainty measuredEnergyPerBead) {
        final String caption = "Measured energy per bead";
        printAndSoutCaptionedObject(caption, measuredEnergyPerBead);
    }

    void printMeasuredEntropyPerBead(DoubleWithUncertainty measuredEntropyPerBead) {
        final String caption = "Measured entropy per bead";
        printAndSoutCaptionedObject(caption, measuredEntropyPerBead);
    }

    void printIdealGasPressure(double idealGasPressure) {
    }

}
