/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FocusedSimulation.output;

import Engine.Energetics.EnergeticsConstants;
import Engine.PolymerSimulator;
import Engine.PolymerState.SystemGeometry.Interfaces.ImmutableSystemGeometry;
import Engine.SimulationStepping.StepTypes.StepType;
import Engine.SystemAnalyzer;
import FocusedSimulation.DoubleWithUncertainty;
import FocusedSimulation.SimulationRunner;
import SGEManagement.Input;
import SystemAnalysis.StressTrackable;
import java.io.FileNotFoundException;

/**
 *
 * @author bmoths
 */
public class AbstractResultsWriter {

    protected static String makeFinalOutputString(PolymerSimulator polymerSimulator) {
        try {
            final SystemAnalyzer systemAnalyzer = polymerSimulator.getSystemAnalyzer();
            final int numBeads = systemAnalyzer.getNumBeads();
            final double beadArea = systemAnalyzer.findArea();
            final ImmutableSystemGeometry systemGeometry = systemAnalyzer.getSystemGeometry();
            final double totalArea = systemGeometry.getVolume();
            final double width = systemGeometry.getSizeOfDimension(0);
            final double height = systemGeometry.getSizeOfDimension(1);
            StringBuilder parametersStringBuilder = new StringBuilder();
            parametersStringBuilder.append("\n")
                    .append("fraction of area covered at end of simulation: ").append(Double.toString(beadArea / totalArea)).append("\n")
                    .append("number density of blob at end of simulation: ").append(Double.toString(numBeads / beadArea)).append("\n")
                    .append("horizontal size of system at end of simulation: ").append(Double.toString(width)).append("\n")
                    .append("vertical size of system at end of simulation: ").append(Double.toString(height)).append("\n");
            for (StepType stepType : StepType.values()) {
                parametersStringBuilder.append("Acceptance rate of ").append(stepType.toString()).append(": ").append(polymerSimulator.getAcceptanceRateForStepOfType(stepType)).append("\n");
            }
            return parametersStringBuilder.toString();
        } catch (Exception e) {
            return "Exception raised in final output string: " + e.toString();
        }
    }

    protected static String makeParametersString(Input input) {
        try {
            final int numBeadsPerChain = (int) input.getSystemParameters().getPolymerCluster().getNumBeadsPerChain();
            final int numAnneals = input.getJobParameters().getNumAnneals();
            final int numSurfaceTensionTrials = input.getJobParameters().getNumSurfaceTensionTrials();
            final int numChains = input.getSystemParameters().getPolymerCluster().getNumChains();
            final EnergeticsConstants energeticsConstants = input.getSystemParameters().getEnergeticsConstants();
            final double springConstant = energeticsConstants.getExternalEnergyCalculator().getxSpringConstant();
            final double equilibriumPosition = energeticsConstants.getExternalEnergyCalculator().getxEquilibriumPosition();
            final double softOverlapCoefficient = energeticsConstants.getBBOverlapCoefficient();
            final double hardOverlapCoefficient = energeticsConstants.getHardOverlapCoefficient();
            final double interactionLength = input.getSystemParameters().getSystemGeometry().getGeometricalParameters().getInteractionLength();
            final double hardCoreLength = input.getSystemParameters().getSystemGeometry().getGeometricalParameters().getCoreLength();
            final double density = input.getSystemParameters().getPolymerCluster().getConcentrationInWater();
            StringBuilder parametersStringBuilder = new StringBuilder();
            parametersStringBuilder.append("Number of Chains: ").append(Integer.toString(numChains)).append("\n")
                    .append("Number of Beads per Chain: ").append(Integer.toString(numBeadsPerChain)).append("\n")
                    .append("Density: ").append(Double.toString(density)).append("\n")
                    .append("number  of anneals: ").append(Integer.toString(numAnneals)).append("\n")
                    .append("number of iterations finding results: ").append(Integer.toString(numSurfaceTensionTrials)).append("\n")
                    .append("number of samples per surface tension measurement: ").append(Integer.toString(input.getJobParameters().getSimulationRunnerParameters().getNumSamples())).append("\n")
                    .append("number of simulator iterations per sample: ").append(Integer.toString(input.getJobParameters().getSimulationRunnerParameters().getNumIterationsPerSample())).append("\n")
                    .append("number of simulator iterations per anneal: ").append(Integer.toString(input.getJobParameters().getSimulationRunnerParameters().getNumIterationsPerAnneal())).append("\n")
                    .append("Interaction length of beads: ").append(Double.toString(interactionLength)).append("\n")
                    .append("hard core length of beads: ").append(Double.toString(hardCoreLength)).append("\n")
                    .append("soft overlap coefficient: ").append(Double.toString(softOverlapCoefficient)).append("\n")
                    .append("hard overlap coefficient: ").append(Double.toString(hardOverlapCoefficient)).append("\n")
                    .append("E=(1/2)a(L-b)^2 with a: ").append(Double.toString(springConstant)).append("\n")
                    .append("b: ").append(Double.toString(equilibriumPosition))
                    .append("\n").append("=====================")
                    .append("\n").append("\n");
            return parametersStringBuilder.toString();
        } catch (Exception e) {
            return "Exception: " + e.toString();
        }
    }

    static private String makeStressString(SimulationRunner simulationRunner) {
        StringBuilder stringBuilder = new StringBuilder();
        final DoubleWithUncertainty stress11 = simulationRunner.getRecentMeasurementForTrackedVariable(StressTrackable.TOTAL_STRESS_TRACKABLE.getStress11Trackable());
        final DoubleWithUncertainty stress12 = simulationRunner.getRecentMeasurementForTrackedVariable(StressTrackable.TOTAL_STRESS_TRACKABLE.getStress12Trackable());
        final DoubleWithUncertainty stress22 = simulationRunner.getRecentMeasurementForTrackedVariable(StressTrackable.TOTAL_STRESS_TRACKABLE.getStress22Trackable());
        stringBuilder.append("[").append(stress11.getValue()).append("  ").append(stress12.getValue()).append("]  +/-  [").append(stress11.getUncertainty()).append(" ").append(stress12.getUncertainty()).append("]\n");
        stringBuilder.append("[").append(stress12.getValue()).append("  ").append(stress22.getValue()).append("]  +/-  [").append(stress12.getUncertainty()).append(" ").append(stress22.getUncertainty()).append("]\n");
        stringBuilder.append("\n");
        final String outputString = stringBuilder.toString();
        return outputString;
    }

    private final OutputWriter outputWriter;
    protected final Input input;

    public AbstractResultsWriter(Input input) throws FileNotFoundException {
        outputWriter = new OutputWriter(input.getJobNumber());
        this.input = input;
    }

    public void printAnnealDone(int numAnnealsDone) {
        String annealsDoneString = "Number of anneals done so far: " + numAnnealsDone + "\n";
        outputWriter.printAndSoutString(annealsDoneString);
    }

    public void printFinalOutput(PolymerSimulator polymerSimulator) {
        String finalOutputString = makeFinalOutputString(polymerSimulator);
        outputWriter.printAndSoutString(finalOutputString);
    }

    public void printParameters() {
        String parametersString = makeParametersString(input);
        outputWriter.printAndSoutString(parametersString);
    }

    public void printStress(SimulationRunner simulationRunner) {
        String outputString = makeStressString(simulationRunner);
        printAndSoutString(outputString);
    }

    public void printAndSoutString(CharSequence outputString) {
        outputWriter.printAndSoutString(outputString);
    }

    public void printAndSoutCaptionedObject(CharSequence caption, Object object) {
        CharSequence captionedDoubleWithUncertaintyCharSequence = makeCaptionedDoubleWithUncertaintyCharSequence(caption, object);
        printAndSoutString(captionedDoubleWithUncertaintyCharSequence);
    }

    public void closeWriter() {
        outputWriter.closeWriter();
    }

    private CharSequence makeCaptionedDoubleWithUncertaintyCharSequence(CharSequence caption, Object object) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append(caption)
                .append(": ")
                .append(object.toString())
                .append("\n");
        return stringBuilder;
    }

}
