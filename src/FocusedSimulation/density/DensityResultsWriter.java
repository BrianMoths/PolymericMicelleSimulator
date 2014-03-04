/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FocusedSimulation.density;

import Engine.PolymerSimulator;
import FocusedSimulation.output.AbstractResultsWriter;
import SGEManagement.Input;
import java.io.FileNotFoundException;

/**
 *
 * @author bmoths
 */
public class DensityResultsWriter extends AbstractResultsWriter {

    public DensityResultsWriter(Input input) throws FileNotFoundException {
        super(input);
    }

    void printInitializationInfo(PolymerSimulator polymerSimulator) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
