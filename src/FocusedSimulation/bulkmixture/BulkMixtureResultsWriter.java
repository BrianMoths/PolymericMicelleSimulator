/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FocusedSimulation.bulkmixture;

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

}
