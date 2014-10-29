/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SGEManagement;

import FocusedSimulation.FileLocations;
import FocusedSimulation.homopolymer.surfacetension.SurfaceTensionJobMaker;
import FocusedSimulation.output.OutputWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bmoths
 */
public class JobSubmitter {

    static public void submitJobs(String focusedSimulationPath, List<Input> inputs) {
        final String commandExceptInput = makeCommandExceptInput(focusedSimulationPath);
        for (Input input : inputs) {
            submitJob(commandExceptInput, input);
        }
    }

    static public void submitNamedJobs(String focusedSimulationPath, List<Input> inputs) {
        final String commandExceptInput = makeCommandExceptInput(focusedSimulationPath);
        for (Input input : inputs) {
            submitNamedJob(commandExceptInput, input);
        }
    }

    //<editor-fold defaultstate="collapsed" desc="makeCommandExceptInput">
    static private String makeCommandExceptInput(String focusedSimulationPath) {
        StringBuilder commandBuilder = new StringBuilder();
        commandBuilder.append("java ")
                .append("-cp '/home/bmoths/Desktop/projects/polymerMicelles/simulation/PolymericMicelles/build/classes/:/home/bmoths/Desktop/projects/polymerMicelles/simulation/PolymericMicelles/dist/lib/*' ")
                .append(focusedSimulationPath);

        return commandBuilder.toString();
    }
    //</editor-fold>

    static private void submitJob(String commandExceptInput, Input input) {
        final String relativePath = makeRelativePath(input);
        makeInputFIle(relativePath, input);
        final String completeCommand = makeCompleteCommand(commandExceptInput, relativePath);
        QSubAdapter.runCommandForQsub(completeCommand);
    }

    static private void submitNamedJob(String commandExceptInput, Input input) {
        final String relativePath = makeRelativePath(input);
        makeInputFIle(relativePath, input);
        final String completeCommand = makeCompleteCommand(commandExceptInput, relativePath);
        QSubAdapter.runCommandForQsub(completeCommand, makeFileName(input));
    }

    private static String makeRelativePath(Input input) {
        return "../simulationInputs/" + makeFileName(input);
    }

    static private String makeFileName(Input input) {
        return OutputWriter.makeFileName(input.getJobNumber(), input.getJobParameters().getJobString());
    }

    private static void makeInputFIle(String relativePath, Input input) {
        try {
            final String absolutePath = FileLocations.PROJECT_PATH + relativePath;
            FileOutputStream fileOutputStream = new FileOutputStream(absolutePath);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(input);
        } catch (FileNotFoundException ex) {
            throw new AssertionError("input file could not be made", ex);
        } catch (IOException ex) {
            Logger.getLogger(SurfaceTensionJobMaker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    static private String makeCompleteCommand(String commandExceptInput, String inputString) {
        StringBuilder completeCommandBuilder = new StringBuilder();
        completeCommandBuilder.append(commandExceptInput)
                .append(" ")
                .append(inputString);
        return completeCommandBuilder.toString();
    }

    private JobSubmitter() {
    }

}
