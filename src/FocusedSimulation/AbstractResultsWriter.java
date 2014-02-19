/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FocusedSimulation;

import Engine.PolymerSimulator;
import Engine.PolymerState.SystemGeometry.Interfaces.ImmutableSystemGeometry;
import Engine.SystemAnalyzer;
import SGEManagement.Input;
import java.io.FileNotFoundException;

/**
 *
 * @author bmoths
 */
public class AbstractResultsWriter {

    protected static String makeFinalOutputString(PolymerSimulator polymerSimulator) {
        final SystemAnalyzer systemAnalyzer = polymerSimulator.getSystemAnalyzer();
        final int numBeads = systemAnalyzer.getNumBeads();
        final double beadArea = systemAnalyzer.findArea();
        final ImmutableSystemGeometry systemGeometry = systemAnalyzer.getSystemGeometry();
        final double totalArea = systemGeometry.getVolume();
        final double width = systemGeometry.getSizeOfDimension(0);
        final double height = systemGeometry.getSizeOfDimension(1);
        StringBuilder parametersStringBuilder = new StringBuilder();
        parametersStringBuilder.append("\n").append("fraction of area covered at end of simulation: ").append(Double.toString(beadArea / totalArea)).append("\n").append("number density of blob at end of simulation: ").append(Double.toString(numBeads / beadArea)).append("\n").append("horizontal size of system at end of simulation: ").append(Double.toString(width)).append("\n").append("vertical size of system at end of simulation: ").append(Double.toString(height)).append("\n");
        return parametersStringBuilder.toString();
    }

    protected static String makeParametersString(Input input) {
        final int numBeadsPerChain = (int) input.getSystemParameters().getPolymerCluster().getNumBeadsPerChain();
        final int numAnneals = input.getJobParameters().getNumAnneals();
        final int numSurfaceTensionTrials = input.getJobParameters().getNumSurfaceTensionTrials();
        final double beadSideLength = input.getSystemParameters().getSystemGeometry().getParameters().getInteractionLength();
        final int numChains = input.getSystemParameters().getPolymerCluster().getNumChains();
        final double springConstant = input.getSystemParameters().getEnergeticsConstants().getExternalEnergyCalculator().getxSpringConstant();
        final double equilibriumPosition = input.getSystemParameters().getEnergeticsConstants().getExternalEnergyCalculator().getxEquilibriumPosition();
        final double density = input.getSystemParameters().getPolymerCluster().getConcentrationInWater();
        StringBuilder parametersStringBuilder = new StringBuilder();
        parametersStringBuilder.append("Number of Chains: ").append(Integer.toString(numChains)).append("\n").append("Number of Beads per Chain: ").append(Integer.toString(numBeadsPerChain)).append("\n").append("Density: ").append(Double.toString(density)).append("\n").append("Side length of beads: ").append(Double.toString(beadSideLength)).append("\n").append("number  of anneals: ").append(Integer.toString(numAnneals)).append("\n").append("number of iterations finding surface tension: ").append(Integer.toString(numSurfaceTensionTrials)).append("\n").append("E=(1/2)a(L-b)^2 with a: ").append(Double.toString(springConstant)).append("\n").append("b: ").append(Double.toString(equilibriumPosition)).append("\n").append("=====================").append("\n").append("\n");
        return parametersStringBuilder.toString();
    }

    private final OutputWriter outputWriter;
    protected final Input input;

    public AbstractResultsWriter(Input input) throws FileNotFoundException {
        outputWriter = new OutputWriter(input.getJobNumber());
        this.input = input;
    }

    public void printFinalOutput(PolymerSimulator polymerSimulator) {
        String finalOutputString = makeFinalOutputString(polymerSimulator);
        outputWriter.printAndSoutString(finalOutputString);
    }

    public void printParameters() {
        String parametersString = makeParametersString(input);
        outputWriter.printAndSoutString(parametersString);
    }

    protected void printAndSoutString(String outputString) {
        outputWriter.printAndSoutString(outputString);
    }

    public void closeWriter() {
        outputWriter.closeWriter();
    }

}
