/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FocusedSimulation.surfacetension;

import Engine.PolymerSimulator;
import Engine.PolymerState.SystemGeometry.Interfaces.ImmutableSystemGeometry;
import Engine.SystemAnalyzer;
import FocusedSimulation.DoubleWithUncertainty;
import FocusedSimulation.OutputWriter;
import FocusedSimulation.SimulationRunner;
import FocusedSimulation.surfacetension.SurfaceTensionFinder.MeasuredSurfaceTension;
import SystemAnalysis.StressTrackable;
import java.io.FileNotFoundException;

/**
 *
 * @author brian
 */
public class SurfaceTensionOutputWriter {

    private final OutputWriter outputWriter;
    private final SurfaceTensionFinder surfaceTensionFinder;

    public SurfaceTensionOutputWriter(final SurfaceTensionFinder surfaceTensionFinder) throws FileNotFoundException {
        this.surfaceTensionFinder = surfaceTensionFinder;
        outputWriter = new OutputWriter(surfaceTensionFinder.getJobNumber());
    }

    public void printParameters() {
        String parametersString = makeParametersString();
        outputWriter.printAndSoutString(parametersString);
    }

    private String makeParametersString() {
        final int numBeadsPerChain = surfaceTensionFinder.getNumBeadsPerChain();
        final int numAnneals = surfaceTensionFinder.getNumAnneals();
        final int numSurfaceTensionTrials = surfaceTensionFinder.getNumSurfaceTensionTrials();
        final double beadSideLength = surfaceTensionFinder.getBeadSize();

        StringBuilder parametersStringBuilder = new StringBuilder();
        parametersStringBuilder
                .append("Number of Chains: ").append(Integer.toString(surfaceTensionFinder.getNumChains())).append("\n")
                .append("Number of Beads per Chain: ").append(Integer.toString(numBeadsPerChain)).append("\n")
                .append("E=(1/2)a(L-b)^2 with a: ").append(Double.toString(surfaceTensionFinder.getExternalEnergyCalculator().getxSpringConstant())).append("\n")
                .append("b: ").append(Double.toString(surfaceTensionFinder.getExternalEnergyCalculator().getxEquilibriumPosition())).append("\n")
                .append("Density: ").append(Double.toString(surfaceTensionFinder.getDensity())).append("\n")
                .append("Side length of beads: ")
                .append(Double.toString(beadSideLength)).append("\n")
                .append("number  of anneals: ").append(Integer.toString(numAnneals)).append("\n")
                .append("number of iterations finding surface tension: ").append(Integer.toString(numSurfaceTensionTrials)).append("\n")
                .append("=====================").append("\n")
                .append("\n");
        return parametersStringBuilder.toString();
    }

    public void printSurfaceTension(MeasuredSurfaceTension measuredSurfaceTension) {
        String surfaceTensionString = makeSurfaceTensionString(measuredSurfaceTension);
        outputWriter.printAndSoutString(surfaceTensionString);
    }

    private String makeSurfaceTensionString(MeasuredSurfaceTension measuredSurfaceTension) {
        StringBuilder parametersStringBuilder = new StringBuilder();
        parametersStringBuilder
                .append("Surface Tension found: ")
                .append(Double.toString(measuredSurfaceTension.surfaceTension))
                .append(" +/- ")
                .append(Double.toString(measuredSurfaceTension.surfaceTensionStandardError))
                .append("\n");
        return parametersStringBuilder.toString();
    }

    public void printStress(SimulationRunner simulationRunner) {
        StringBuilder stringBuilder = new StringBuilder();
        final DoubleWithUncertainty stress11 = simulationRunner.getRecentMeasurementForTrackedVariable(StressTrackable.STRESS_TRACKABLE.getStress11Trackable());
        final DoubleWithUncertainty stress12 = simulationRunner.getRecentMeasurementForTrackedVariable(StressTrackable.STRESS_TRACKABLE.getStress12Trackable());
        final DoubleWithUncertainty stress22 = simulationRunner.getRecentMeasurementForTrackedVariable(StressTrackable.STRESS_TRACKABLE.getStress22Trackable());
        stringBuilder.append("[").append(stress11.getValue()).append("  ").append(stress12.getValue()).append("]  +/-  [").append(stress11.getUncertainty()).append(" ").append(stress12.getUncertainty()).append("]\n");
        stringBuilder.append("[").append(stress12.getValue()).append("  ").append(stress22.getValue()).append("]  +/-  [").append(stress12.getUncertainty()).append(" ").append(stress22.getUncertainty()).append("]\n");
        stringBuilder.append("\n");
        final String outputString = stringBuilder.toString();
        outputWriter.printAndSoutString(outputString);
    }

    public void printFinalOutput(PolymerSimulator polymerSimulator) {
        String finalOutputString = makeFinalOutputString(polymerSimulator);
        outputWriter.printAndSoutString(finalOutputString);
    }

    private String makeFinalOutputString(PolymerSimulator polymerSimulator) {
        final SystemAnalyzer systemAnalyzer = polymerSimulator.getSystemAnalyzer();
        final int numBeads = systemAnalyzer.getNumBeads();
        final double beadArea = systemAnalyzer.findArea();

        final ImmutableSystemGeometry systemGeometry = systemAnalyzer.getSystemGeometry();
        final double totalArea = systemGeometry.getVolume();
        final double width = systemGeometry.getSizeOfDimension(0);
        final double height = systemGeometry.getSizeOfDimension(1);

        StringBuilder parametersStringBuilder = new StringBuilder();
        parametersStringBuilder
                .append("\n")
                .append("fraction of area covered at end of simulation: ").append(Double.toString(beadArea / totalArea)).append("\n").append("number density of blob at end of simulation: ")
                .append(Double.toString(numBeads / beadArea)).append("\n")
                .append("horizontal size of system at end of simulation: ")
                .append(Double.toString(width)).append("\n")
                .append("vertical size of system at end of simulation: ")
                .append(Double.toString(height)).append("\n");

        return parametersStringBuilder.toString();
    }

    public void printInitializationInfo(PolymerSimulator polymerSimulator) {
        final String initializationString = "Initial Horizontal Size of System: " + polymerSimulator.getGeometry().getSizeOfDimension(0) + "\n";
        outputWriter.printAndSoutString(initializationString);
    }

    public void closeWriter() {
        outputWriter.closeWriter();
    }

}
