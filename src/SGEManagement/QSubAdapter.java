/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SGEManagement;

import FocusedSimulation.homopolymer.surfacetension.SurfaceTensionJobMaker;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bmoths
 */
public class QSubAdapter {

    static public void runCommandForQsub(String qsubCommand) {
        if (qsubCommand.contains("\"")) {
            throw new IllegalArgumentException("command submitted to qsub must not contain double quotes. Use single quotes if necessary.");
        }
        String qsubWrappedCommand = "qsub -e /dev/null -o /dev/null <<< \"" + qsubCommand + "\"";
        try {
            runCommandForBash(qsubWrappedCommand);
        } catch (IOException ex) {
            Logger.getLogger(SurfaceTensionJobMaker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    static private Process runCommandForBash(String commandForBash) throws IOException {
        Runtime runtime = Runtime.getRuntime();
        String[] bashWrappedCommand = makeBashWrappedCommand(commandForBash);
        System.out.println("attempted to run command---" + bashWrappedCommand[0] + " " + bashWrappedCommand[1] + " " + bashWrappedCommand[2]);
        return runtime.exec(bashWrappedCommand);
    }

    static private String[] makeBashWrappedCommand(String commandForBash) {
        String[] bashWrappedCommand = new String[3];
        bashWrappedCommand[0] = "bash";
        bashWrappedCommand[1] = "-c";
        bashWrappedCommand[2] = commandForBash;
        return bashWrappedCommand;
    }

}
