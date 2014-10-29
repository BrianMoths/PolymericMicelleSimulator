/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FocusedSimulation.micelle;

import Engine.PolymerTopology.PolymerChain;
import Engine.PolymerTopology.PolymerCluster;
import Engine.SimulationStepping.StepGenerators.CompoundStepGenerators.GeneralStepGenerator;
import Engine.SimulationStepping.StepGenerators.StepGenerator;
import Engine.SimulationStepping.StepTypes.StepType;
import Engine.SimulatorParameters.SystemParametersBuilder;
import FocusedSimulation.AbstractFocusedSimulation;
import FocusedSimulation.DoubleWithUncertainty;
import FocusedSimulation.bulkmixture.BulkMixtureJobMaker;
import FocusedSimulation.simulationrunner.StatisticsTracker.TrackableVariable;
import SGEManagement.Input;
import SGEManagement.Input.InputBuilder;
import com.sun.org.apache.regexp.internal.REUtil;
import java.io.FileNotFoundException;
import java.util.EnumMap;

/**
 *
 * @author bmoths
 */
public class MicelleFinder extends AbstractFocusedSimulation<MicelleResultsWriter> {

    static private final int numChains = 1;

    static public void main(String[] args) {
        final Input input = readInput(args);
        try {
            final MicelleFinder micelleFinder = makeMicelleFinderWithDefaultWriter(input);
            micelleFinder.doSimulation();
            micelleFinder.closeOutputWriter();
        } catch (FileNotFoundException ex) {
            System.out.println("File not able to be opened");
        }
    }

    private static Input readInput(String[] args) {
        if (args.length == 0) {
            final double verticalScaleFactor = 1;//.3
            final double horizontalScaleFactor = 1;//6
            final double hydrophobicFraction = .7;//.5
            final int numBeadsPerChain = 200;
            final int numSubBlocks = 10;
            final double defaultDensity = .02;


            InputBuilder inputBuilder = MicelleJobMaker.getDefaultInputDensityBuilder(.75);
            PolymerChain polymerChain = PolymerChain.makeMultiblockPolymerChain(numBeadsPerChain, numSubBlocks, hydrophobicFraction);
            PolymerChain polymerChainPeanut = PolymerChain.makeMultiblockPolymerChain(numBeadsPerChain / 3, 8, .6);
            polymerChainPeanut.appendChain(PolymerChain.makeMultiblockPolymerChain(numBeadsPerChain / 10, 2, 0));
            polymerChainPeanut.appendChain(PolymerChain.makeMultiblockPolymerChain(numBeadsPerChain / 3, 8, .6));
            PolymerCluster newPolymerCluster = PolymerCluster.makeClusterFromChain(polymerChainPeanut);
            newPolymerCluster.setConcentrationInWater(defaultDensity);
            inputBuilder.getSystemParametersBuilder().setPolymerCluster(newPolymerCluster);
            return inputBuilder.buildInput();
        } else if (args.length == 1) {
            final String fileName = args[0];
            return Input.readInputFromFile(fileName);
        } else {
            throw new IllegalArgumentException("At most one input allowed");
        }
    }

    private static PolymerCluster makePeanutCluster() {
        PolymerChain bridgeChain = PolymerChain.makeMultiblockPolymerChain(20, 2, .75);
        PolymerChain blobChain = PolymerChain.makeMultiblockPolymerChain(40, 2, .92);
        PolymerChain fullChain = new PolymerChain();
        fullChain.appendChains(bridgeChain, blobChain, bridgeChain, blobChain);
//        fullChain.appendChains(bridgeChain, bridgeChain, bridgeChain, bridgeChain);
        PolymerCluster polymerCluster = PolymerCluster.makeClusterFromChain(fullChain);
        polymerCluster.setConcentrationInWater(.02);
        return polymerCluster;
    }

    private static MicelleFinder makeMicelleFinderWithDefaultWriter(Input input) throws FileNotFoundException {
        return new MicelleFinder(input, new MicelleResultsWriter(input));
    }

    public MicelleFinder(Input input, MicelleResultsWriter outputWriter) throws FileNotFoundException {
        super(input, outputWriter);
    }

    @Override
    protected StepGenerator makeMainStepGenerator() {
        EnumMap<StepType, Double> weights = new EnumMap<>(StepType.class);
        weights.put(StepType.SINGLE_BEAD, 1.);

        StepGenerator stepGenerator = new GeneralStepGenerator(weights);
        return stepGenerator;
    }

    @Override
    protected void initializePositions() {
        polymerSimulator.reasonableMiddleRandomize();
    }

    @Override
    protected void registerTrackablesToSimulationRunner() {
        simulationRunner.trackVariable(TrackableVariable.FREE_ENERGY_PER_BEAD);
    }

    @Override
    protected void printInitialOutput() {
        outputWriter.printInitialOutput();
    }

    @Override
    protected void analyzeAndPrintResults() {
        DoubleWithUncertainty freeEnergyPerBead = simulationRunner.getRecentMeasurementForTrackedVariable(TrackableVariable.FREE_ENERGY_PER_BEAD);
        outputWriter.printFreeEnergyPerBead(freeEnergyPerBead);
    }

    @Override
    protected boolean isConverged() {
        return true;
    }

    @Override
    protected void printFinalOutput() {
    }

}
