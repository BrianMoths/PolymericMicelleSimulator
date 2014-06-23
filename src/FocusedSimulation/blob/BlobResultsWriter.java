/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FocusedSimulation.blob;

import FocusedSimulation.DoubleWithUncertainty;
import FocusedSimulation.output.AbstractResultsWriter;
import SGEManagement.Input;
import java.io.FileNotFoundException;

/**
 *
 * @author bmoths
 */
public class BlobResultsWriter extends AbstractResultsWriter {

    public BlobResultsWriter(Input input) throws FileNotFoundException {
        super(input);
    }

    void printMeasuredDensity(DoubleWithUncertainty measuredDensity) {
        printAndSoutCaptionedObject("Measured Density", measuredDensity);
    }

    void printEstimatedRadius(DoubleWithUncertainty radius) {
        printAndSoutCaptionedObject("Effective radius", radius);
    }

    void printExpectedMechanicalPressure(DoubleWithUncertainty expectedMechanicalPressure) {
        printAndSoutCaptionedObject("Expected mechanical pressure", expectedMechanicalPressure);
    }

    void printMiddleDensity(DoubleWithUncertainty measuredMiddleDensity) {
        printAndSoutCaptionedObject("Middle Density", measuredMiddleDensity);
    }

    void printOverpressure(DoubleWithUncertainty overpressure) {
        printAndSoutCaptionedObject("Expected overpressure", overpressure);
    }

    void printExpectedMiddleDensity(DoubleWithUncertainty expectedMiddleDensity) {
        printAndSoutCaptionedObject("Expected Middle Density", expectedMiddleDensity);
    }

}
