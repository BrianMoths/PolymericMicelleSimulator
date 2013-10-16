/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SGEManagement;

import Engine.ExternalEnergyCalculator;
import Engine.ExternalEnergyCalculator.ExternalEnergyCalculatorBuilder;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bmoths
 */
public class SGEManager {

    static private class Input {

        public int numChains;
        public ExternalEnergyCalculator externalEnergyCalculator;
        public double density;

        public Input(int numChains, ExternalEnergyCalculator externalEnergyCalculator, double density) {
            this.numChains = numChains;
            this.externalEnergyCalculator = externalEnergyCalculator;
            this.density = density;
        }

    }

    public static void main(String[] args) {
        final List<Input> inputs = makeInputs();
        submitJobs(inputs);
    }

    static private List<Input> makeInputs() {
        List<Input> inputs = new ArrayList<>();

//        inputs.add(new Input(100, makeCalculatorAB(.2, -50), .15));
//        inputs.add(new Input(100, makeCalculatorAB(.18, -50), .15));
//        inputs.add(new Input(100, makeCalculatorAB(.15, -50), .15));
//        inputs.add(new Input(100, makeCalculatorAB(.22, -50), .15));
//        inputs.add(new Input(100, makeCalculatorAB(.25, -50), .15));
//        inputs.add(new Input(100, makeCalculatorAB(.2, -60), .15));
//        inputs.add(new Input(100, makeCalculatorAB(.2, -70), .15));
//        inputs.add(new Input(100, makeCalculatorAB(.2, -80), .15));
//        inputs.add(new Input(100, makeCalculatorAB(.2, -40), .15));
//        inputs.add(new Input(100, makeCalculatorAB(.2, -30), .15));
        inputs.add(new Input(100, makeCalculatorAB(.2, -50), .18));
//        inputs.add(new Input(100, makeCalculatorAB(.2, -50), .20));
//        inputs.add(new Input(100, makeCalculatorAB(.2, -50), .12));
//        inputs.add(new Input(100, makeCalculatorAB(.2, -50), .1));

        return inputs;
    }

    //<editor-fold defaultstate="collapsed" desc="makeInputsHelper">
    static private ExternalEnergyCalculator makeCalculatorAB(double a, double b) {
        ExternalEnergyCalculatorBuilder externalEnergyCalculatorBuilder = new ExternalEnergyCalculatorBuilder();
        externalEnergyCalculatorBuilder.setxQuadratic(a);
        externalEnergyCalculatorBuilder.setxTension(b);
        return externalEnergyCalculatorBuilder.build();
    }
    //</editor-fold>

    static private void submitJobs(List<Input> inputs) {
        String path = getPath();
        String className = getJarName();

        String commandExceptInput = makeCommandExceptInput(path, className);

        for (Input input : inputs) {
            submitJob(commandExceptInput, input);
        }
    }

    static private String getPath() {
        return "/home/bmoths/Desktop/projects/polymerMicelles/simulation/PolymericMicelles/dist";
    }

    static private String getJarName() {
        return "PolymericMicelles.jar";
    }

    static private String makeCommandExceptInput(String path, String jarName) {
        StringBuilder commandBuilder = new StringBuilder();
        commandBuilder.append("java -jar ")
                .append(path)
                .append("/")
                .append(jarName);

        return commandBuilder.toString();
    }

    static private void submitJob(String commandExceptInput, Input input) {
        final String inputString = makeInputString(input);
        final String completeCommand = makeCompleteCommand(commandExceptInput, inputString);
        runCommandForQsub(completeCommand);
    }

    static private String makeInputString(Input input) {
        StringBuilder inputStringBuilder = new StringBuilder();
        inputStringBuilder.append(input.numChains)
                .append(" ")
                .append(input.externalEnergyCalculator.getxQuadratic())
                .append(" ")
                .append(input.externalEnergyCalculator.getxTension())
                .append(" ")
                .append(input.density);
        return inputStringBuilder.toString();
    }

    static private String makeCompleteCommand(String commandExceptInput, String inputString) {
        StringBuilder completeCommandBuilder = new StringBuilder();
        completeCommandBuilder.append(commandExceptInput)
                .append(" ")
                .append(inputString);
        return completeCommandBuilder.toString();
    }

    static private void runCommandForQsub(String qsubCommand) {
        if (qsubCommand.contains("\"")) {
            throw new IllegalArgumentException("command submitted to qsub must not contain double quotes. Use single quotes if necessary.");
        }
        String qsubWrappedCommand = "qsub -e /dev/null -o /dev/null <<< \"" + qsubCommand + "\"";
        try {
            runCommandForBash(qsubWrappedCommand);
        } catch (IOException ex) {
            Logger.getLogger(SGEManager.class.getName()).log(Level.SEVERE, null, ex);
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
