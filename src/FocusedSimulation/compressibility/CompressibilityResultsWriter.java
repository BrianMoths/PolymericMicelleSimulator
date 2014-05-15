/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FocusedSimulation.compressibility;

import FocusedSimulation.DoubleWithUncertainty;
import FocusedSimulation.bulkproperties.BulkPropertiesResultsWriter;
import SGEManagement.Input;
import java.io.FileNotFoundException;

/**
 *
 * @author bmoths
 */
public class CompressibilityResultsWriter extends BulkPropertiesResultsWriter {

    public CompressibilityResultsWriter(Input input) throws FileNotFoundException {
        super(input);
    }

    void printCompressiblity(DoubleWithUncertainty compressibility) {
        final String caption = "Measured compressibility from pressure";
        printAndSoutCaptionedObject(caption, compressibility);
    }

    void printPressure(Double pressure) {
        final String caption = "Pressure";
        printAndSoutCaptionedObject(caption, pressure);
    }

}
