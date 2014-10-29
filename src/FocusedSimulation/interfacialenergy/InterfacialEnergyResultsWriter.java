/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package FocusedSimulation.interfacialenergy;

import FocusedSimulation.DoubleWithUncertainty;
import FocusedSimulation.output.AbstractResultsWriter;
import SGEManagement.Input;
import java.io.FileNotFoundException;

/**
 *
 * @author brian
 */
public class InterfacialEnergyResultsWriter extends AbstractResultsWriter {

    public InterfacialEnergyResultsWriter(Input input) throws FileNotFoundException {
        super(input);
    }

    @Override
    public void printSimulationType() {
        printAndSoutString("Interfacial Energy\n");
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
    }

    void printInterfacialEnergy(DoubleWithUncertainty measuredInterfacialEnergy) {
        printAndSoutCaptionedObject("interfacial energy", measuredInterfacialEnergy);
    }

    void printInitialWidth(double width) {
        printAndSoutCaptionedObject("initial width", width);
    }

    void printInitialHeight(double height) {
        printAndSoutCaptionedObject("initial height", height);
    }

}
