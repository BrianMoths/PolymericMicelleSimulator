/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FocusedSimulation.bulkmixture;

import FocusedSimulation.DoubleWithUncertainty;
import FocusedSimulation.output.AbstractResultsWriter;
import SGEManagement.Input;
import java.io.FileNotFoundException;

/**
 *
 * @author bmoths
 */
public class BulkMixtureResultsWriter extends AbstractResultsWriter {

    public BulkMixtureResultsWriter(Input input) throws FileNotFoundException {
        super(input);
    }

    @Override
    public void printSimulationType() {
        printAndSoutString("Bulk Mixture\n");
    }

    public void printWidthOfBox(DoubleWithUncertainty widthOfBox) {
        printAndSoutCaptionedObject("width of box", widthOfBox);
    }

    public void printHeightOfBox(DoubleWithUncertainty heightOfBox) {
        printAndSoutCaptionedObject("height of box", heightOfBox);
    }

    public void printFreeEnergyPerBead(DoubleWithUncertainty freeEnergyPerBead) {
        printAndSoutCaptionedObject("free energy per bead", freeEnergyPerBead);
    }

    void printInitialOutput() {
        printAndSoutCaptionedObject("Number of A beads per chain", input.systemParameters.polymerCluster.getNumABeadsPerChain());
        printAndSoutCaptionedObject("Number of B beads per chain", input.systemParameters.polymerCluster.getNumBBeadsPerChain());
    }

    void printEnergyPerBead(DoubleWithUncertainty energyPerBead) {
        printAndSoutCaptionedObject("potential energy per bead", energyPerBead);
    }

}
