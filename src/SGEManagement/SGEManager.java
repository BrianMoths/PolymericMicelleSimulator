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
        QSubAdapter.runCommandForQsub(completeCommand);
    }

    static private String makeInputString(Input input) {
        StringBuilder inputStringBuilder = new StringBuilder();
        inputStringBuilder
                .append(input.jobNumber)
                .append(" ")
                .append(input.numChains)
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

}
