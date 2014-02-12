/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SGEManagement;

import FocusedSimulation.OutputWriter;
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

    static public void submitJobs(List<Input> inputs) {
        final String commandExceptInput = makeCommandExceptInput();
        for (Input input : inputs) {
            submitJob(commandExceptInput, input);
        }
    }

    //<editor-fold defaultstate="collapsed" desc="makeCommandExceptInput">
    static private String makeCommandExceptInput() {
        final String path = getPath();
        final String jarName = getJarName();
        StringBuilder commandBuilder = new StringBuilder();
        commandBuilder.append("java -jar ")
                .append(path)
                .append("/")
                .append(jarName);

        return commandBuilder.toString();
    }

    static private String getPath() {
        return "/home/bmoths/Desktop/projects/polymerMicelles/simulation/PolymericMicelles/dist";
    }

    static private String getJarName() {
        return "PolymericMicelles.jar";
    }
    //</editor-fold>

    static private void submitJob(String commandExceptInput, Input input) {
        final String fileName = makeFileName(input);
        final String path = makePath(fileName);
        makeInputFIle(path, input);
        final String completeCommand = makeCompleteCommand(commandExceptInput, path);
        QSubAdapter.runCommandForQsub(completeCommand);
    }

    static private String makeCompleteCommand(String commandExceptInput, String inputString) {
        StringBuilder completeCommandBuilder = new StringBuilder();
        completeCommandBuilder.append(commandExceptInput)
                .append(" ")
                .append(inputString);
        return completeCommandBuilder.toString();
    }

    static private String makeFileName(Input input) {
        return OutputWriter.makeDatePrefix() + "_" + OutputWriter.makeDoubleDigitString(input.getJobNumber());
    }

    static private String makePath(String fileName) {
        return "../simulationInputs/" + fileName;
    }

    private static void makeInputFIle(String relativePath, Input input) {
        try {
            final String absolutePath = OutputWriter.getProjectPath() + relativePath;
            FileOutputStream fileOutputStream = new FileOutputStream(absolutePath);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(input);
        } catch (FileNotFoundException ex) {
            throw new AssertionError("input file could not be made", ex);
        } catch (IOException ex) {
            Logger.getLogger(SGEManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
