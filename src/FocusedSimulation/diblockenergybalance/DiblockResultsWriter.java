/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FocusedSimulation.diblockenergybalance;

import FocusedSimulation.DoubleWithUncertainty;
import FocusedSimulation.bulkmixture.BulkMixtureResultsWriter;
import SGEManagement.Input;
import java.io.FileNotFoundException;

/**
 *
 * @author bmoths
 */
public class DiblockResultsWriter extends BulkMixtureResultsWriter {

    public DiblockResultsWriter(Input input) throws FileNotFoundException {
        super(input);
    }

    @Override
    public void printInitialOutput() {
        super.printInitialOutput(); //To change body of generated methods, choose Tools | Templates.
        printAndSoutCaptionedObject("force", -input.getSystemParameters().getEnergeticsConstants().getExternalEnergyCalculator().getxTension());
    }

}
