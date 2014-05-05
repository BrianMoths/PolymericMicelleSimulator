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
        final String caption = "Number density found";
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

    void printIdealGasPressure(DoubleWithUncertainty idealGasPressure) {
        final String caption = "Measured ideal gas pressure";
        printAndSoutCaptionedObject(caption, idealGasPressure);
    }

    void printMeasuredOverlapEnergyPerBead(DoubleWithUncertainty measuredOverlapEnergyPerBead) {
        final String caption = "Measured overlap energy per bead";
        printAndSoutCaptionedObject(caption, measuredOverlapEnergyPerBead);
    }

    void printMeasuredSpringEnergyPerBead(DoubleWithUncertainty measuredSpringEnergyPerBead) {
        final String caption = "Measured spring energy per bead";
        printAndSoutCaptionedObject(caption, measuredSpringEnergyPerBead);
    }

}
