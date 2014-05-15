/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FocusedSimulation.blob;

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

}
