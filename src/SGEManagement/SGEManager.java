/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SGEManagement;

import Engine.Energetics.ExternalEnergyCalculator;
import Engine.Energetics.ExternalEnergyCalculator.ExternalEnergyCalculatorBuilder;
import FocusedSimulation.OutputWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bmoths
 */
public class SGEManager {

    static public class Input implements Serializable {

        public int jobNumber;
        public int numChains;
        public ExternalEnergyCalculator externalEnergyCalculator;
        public double density;

        public Input(int numChains, ExternalEnergyCalculator externalEnergyCalculator, double density) {
            this(0, numChains, externalEnergyCalculator, density);
        }

        public Input(int jobNumber, int numChains, ExternalEnergyCalculator externalEnergyCalculator, double density) {
            this.jobNumber = jobNumber;
            this.numChains = numChains;
            this.externalEnergyCalculator = externalEnergyCalculator;
            this.density = density;
        }

        public int getJobNumber() {
            return jobNumber;
        }

        public int getNumChains() {
            return numChains;
        }

        public ExternalEnergyCalculator getExternalEnergyCalculator() {
            return externalEnergyCalculator;
        }

        public double getDensity() {
            return density;
        }

    }

    public static void main(String[] args) {
        final List<Input> inputs = makeInputs();
        submitJobs(inputs);
    }

    static private List<Input> makeInputs() {
        List<Input> inputs = new ArrayList<>();

        int jobNumber = 1;
        int numChains = 100 / 3;
        double a = 10;
        double b = 50 / 3;
        double density = .05;

//        inputs.add(new Input(jobNumber++, numChains / 2, makeCalculatorAB(a, b), density));
//        inputs.add(new Input(jobNumber++, numChains * 2 / 3, makeCalculatorAB(a, b), density));
//        inputs.add(new Input(jobNumber++, numChains, makeCalculatorAB(a, b), density));
//        inputs.add(new Input(jobNumber++, numChains * 3 / 2, makeCalculatorAB(a, b), density));
//        inputs.add(new Input(jobNumber++, numChains * 2, makeCalculatorAB(a, b), density));
//        inputs.add(new Input(jobNumber++, numChains * 3, makeCalculatorAB(a, b), density));
        inputs.add(new Input(jobNumber++, numChains * 5, makeCalculatorAB(a, b), density));
//        inputs.add(new Input(jobNumber++, numChains * 10, makeCalculatorAB(a, b), density));




        return inputs;
    }

    //<editor-fold defaultstate="collapsed" desc="makeInputsHelper">
    static private ExternalEnergyCalculator makeCalculatorAB(double a, double b) {
        ExternalEnergyCalculatorBuilder externalEnergyCalculatorBuilder = new ExternalEnergyCalculatorBuilder();
        externalEnergyCalculatorBuilder.setXPositionAndSpringConstant(b, a);
        return externalEnergyCalculatorBuilder.build();
    }
    //</editor-fold>

    static private void submitJobs(List<Input> inputs) {
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
