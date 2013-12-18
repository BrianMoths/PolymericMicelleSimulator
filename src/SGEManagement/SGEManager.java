/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SGEManagement;

import Engine.Energetics.ExternalEnergyCalculator;
import Engine.Energetics.ExternalEnergyCalculator.ExternalEnergyCalculatorBuilder;
import java.io.IOException;
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

        inputs.add(new Input(100, makeCalculatorAB(1., 100), .05));
        inputs.add(new Input(100, makeCalculatorAB(5., 100), .05));
        inputs.add(new Input(100, makeCalculatorAB(10., 100), .05));
        inputs.add(new Input(100, makeCalculatorAB(50., 100), .05));
        inputs.add(new Input(100, makeCalculatorAB(100., 100), .05));
        inputs.add(new Input(100, makeCalculatorAB(1000., 100), .05));

        inputs.add(new Input(100, makeCalculatorAB(5., 80), .05));
        inputs.add(new Input(100, makeCalculatorAB(10., 80), .05));
        inputs.add(new Input(100, makeCalculatorAB(30., 80), .05));
        inputs.add(new Input(100, makeCalculatorAB(100., 80), .05));
        inputs.add(new Input(100, makeCalculatorAB(300., 80), .05));
        inputs.add(new Input(100, makeCalculatorAB(1000., 80), .05));

        inputs.add(new Input(100, makeCalculatorAB(1., 50), .05));
        inputs.add(new Input(100, makeCalculatorAB(3., 50), .05));
        inputs.add(new Input(100, makeCalculatorAB(10., 50), .05));
        inputs.add(new Input(100, makeCalculatorAB(30., 50), .05));
        inputs.add(new Input(100, makeCalculatorAB(100., 50), .05));
        inputs.add(new Input(100, makeCalculatorAB(300., 50), .05));
        inputs.add(new Input(100, makeCalculatorAB(1000., 50), .05));

//        inputs.add(new Input(100, makeCalculatorAB(0, 66), .01));
//        inputs.add(new Input(50, makeCalculatorAB(0, 66), .01));
//        inputs.add(new Input(80, makeCalculatorAB(0, 66), .01));
//        inputs.add(new Input(10, makeCalculatorAB(0, 66), .01));
//        inputs.add(new Input(100, makeCalculatorAB(0, 66), .01));
//        inputs.add(new Input(100, makeCalculatorAB(0, 66), .1));
//        inputs.add(new Input(100, makeCalculatorAB(0, 66), .03));
//        inputs.add(new Input(100, makeCalculatorAB(0, 66), .001));
//        inputs.add(new Input(100, makeCalculatorAB(0, 66), .003));



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
                .append(input.externalEnergyCalculator.getxSpringConstant())
                .append(" ")
                .append(input.externalEnergyCalculator.getxEquilibriumPosition())
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
