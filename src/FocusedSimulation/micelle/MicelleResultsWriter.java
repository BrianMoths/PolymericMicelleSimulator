/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FocusedSimulation.micelle;

import FocusedSimulation.DoubleWithUncertainty;
import FocusedSimulation.output.AbstractResultsWriter;
import SGEManagement.Input;
import java.io.FileNotFoundException;

/**
 *
 * @author bmoths
 */
public class MicelleResultsWriter extends AbstractResultsWriter {

    public MicelleResultsWriter(Input input) throws FileNotFoundException {
        super(input);
    }

    @Override
    public void printSimulationType() {
        printAndSoutString("Micelle\n");
    }

    void printFreeEnergyPerBead(DoubleWithUncertainty freeEnergyPerBead) {
        printAndSoutCaptionedObject("free energy per bead", freeEnergyPerBead);
    }

}
