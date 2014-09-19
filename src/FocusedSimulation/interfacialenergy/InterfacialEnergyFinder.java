/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package FocusedSimulation.interfacialenergy;

import Engine.Energetics.ExternalEnergyCalculator;
import Engine.PolymerSimulator;
import Engine.PolymerState.BoxPositionGenerator;
import Engine.PolymerState.PositionGenerator;
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
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

/**
 *
 * @author brian
 */
public class InterfacialEnergyFinder extends AbstractFocusedSimulation<InterfacialEnergyResultsWriter> {

    public static void main(String[] args) {
        final Input input = readInput(args);
        try {
            final InterfacialEnergyFinder interfacialEnergyFinder;
            interfacialEnergyFinder = makeBulkMixtureFinderWithDefaultWriter(input);
            interfacialEnergyFinder.doSimulation();
            interfacialEnergyFinder.closeOutputWriter();
        } catch (FileNotFoundException ex) {
            System.out.println("File not able to be opened");
        }
    }

    private static Input readInput(String[] args) {
        if (args.length == 0) {
            final double verticalScaleFactor = 1;//.3
            final double horizontalScaleFactor = 1;//6

            InputBuilder inputBuilder = InterfacialEnergyJobMaker.makeRescaledInputBuilder(verticalScaleFactor, horizontalScaleFactor, 0);
            inputBuilder.getJobParametersBuilder().setNumAnneals(1);
            inputBuilder.getJobParametersBuilder().setNumSimulationTrials(10);
//            inputBuilder.getJobParametersBuilder().getSimulationRunnerParametersBuilder().setNumIterationsPerAnneal(1000);
            inputBuilder.getJobParametersBuilder().getSimulationRunnerParametersBuilder().setNumIterationsPerSample(1000);//20000
            inputBuilder.getJobParametersBuilder().getSimulationRunnerParametersBuilder().setNumSamples(2000);
            return inputBuilder.buildInput();
        } else if (args.length == 1) {
            final String fileName = args[0];
            return Input.readInputFromFile(fileName);
        } else {
            throw new IllegalArgumentException("At most one input allowed");
        }
    }

    private static InterfacialEnergyFinder makeBulkMixtureFinderWithDefaultWriter(Input input) throws FileNotFoundException {
        return new InterfacialEnergyFinder(input, new InterfacialEnergyResultsWriter(input));
    }

    private InterfacialEnergyFinder(Input input, InterfacialEnergyResultsWriter interfacialEnergyResultsWriter) throws FileNotFoundException {
        super(input, interfacialEnergyResultsWriter);
    }

    @Override
    protected StepGenerator makeMainStepGenerator() {
        EnumMap<StepType, Double> weights = new EnumMap<>(StepType.class);
        weights.put(StepType.SINGLE_BEAD, 1.);
        weights.put(StepType.SINGLE_CHAIN, .01);
        weights.put(StepType.SINGLE_WALL_HORIZONTAL_RESIZE, .001);
        weights.put(StepType.SINGLE_WALL_VERTICAL_RESIZE, .001);

        StepGenerator stepGenerator = new GeneralStepGenerator(weights);
        return stepGenerator;
    }

    @Override
    protected void initializePositions() {
        final double[][] initialPositions = makeInitialPositions();
        polymerSimulator.setBeadPositions(initialPositions);
    }

    private double[][] makeInitialPositions() {
        final int numBeads = polymerSimulator.getNumBeads();
        final int numABeads = polymerSimulator.getSystemAnalyzer().getNumABeads();
        final int numDimensions = polymerSimulator.getSystemAnalyzer().getSystemGeometry().getNumDimensions();
        double[][] initialPositions = new double[numBeads][numDimensions];

        final double[][] upperRegionLimits = {{0, 0}, {1, .5}};
        final double[][] lowerRegionLimits = {{0, .5}, {1, 1}};
        final PositionGenerator upperPositionGenerator = new BoxPositionGenerator(upperRegionLimits[0], upperRegionLimits[1], polymerSimulator.getGeometry());
        final PositionGenerator lowerPositionGenerator = new BoxPositionGenerator(lowerRegionLimits[0], lowerRegionLimits[1], polymerSimulator.getGeometry());

        List<Boolean> isRandomized = new ArrayList<>(numBeads);
        for (int bead = 0; bead < numBeads; bead++) {
            isRandomized.add(false);
        }

        for (int bead = 0; bead < numABeads; bead++) {
            if (!isRandomized.get(bead)) {
                List<Integer> chainOfBead = polymerSimulator.getPolymerState().getImmutableDiscretePolymerState().getChainOfBead(bead);
                polymerSimulator.getPolymerState().getImmutablePolymerPosition().reasonableChainRandomize(chainOfBead, lowerPositionGenerator, initialPositions);
                for (Integer randomizedBead : chainOfBead) {
                    isRandomized.set(randomizedBead, true);
                }
            }
        }

        for (int bead = numABeads; bead < numBeads; bead++) {
            if (!isRandomized.get(bead)) {
                List<Integer> chainOfBead = polymerSimulator.getPolymerState().getImmutableDiscretePolymerState().getChainOfBead(bead);
                polymerSimulator.getPolymerState().getImmutablePolymerPosition().reasonableChainRandomize(chainOfBead, upperPositionGenerator, initialPositions);
                for (Integer randomizedBead : chainOfBead) {
                    isRandomized.set(randomizedBead, true);
                }
            }
        }

        return initialPositions;
    }

    @Override
    protected void registerTrackablesToSimulationRunner() {
        simulationRunner.trackVariable(TrackableVariable.SYSTEM_WIDTH);
        simulationRunner.trackVariable(TrackableVariable.SYSTEM_HEIGHT);
        simulationRunner.trackVariable(TrackableVariable.FREE_ENERGY_PER_BEAD);
    }

    @Override
    protected void printInitialOutput() {
        outputWriter.printInitialOutput();
    }

    @Override
    protected void analyzeAndPrintResults() {
        DoubleWithUncertainty measuredWidth = simulationRunner.getRecentMeasurementForTrackedVariable(TrackableVariable.SYSTEM_WIDTH);
        outputWriter.printWidthOfBox(measuredWidth);
        DoubleWithUncertainty measuredHeight = simulationRunner.getRecentMeasurementForTrackedVariable(TrackableVariable.SYSTEM_HEIGHT);
        outputWriter.printHeightOfBox(measuredHeight);
        DoubleWithUncertainty measuredInterfacialEnergy = getMeasuredInterfacialEnergyFromWidth(measuredWidth, polymerSimulator);
        outputWriter.printInterfacialEnergy(measuredInterfacialEnergy);

        DoubleWithUncertainty freeEnergyPerBead = simulationRunner.getRecentMeasurementForTrackedVariable(TrackableVariable.FREE_ENERGY_PER_BEAD);
        outputWriter.printFreeEnergyPerBead(freeEnergyPerBead);
        outputWriter.printAndSoutString("\n");
    }

    static private DoubleWithUncertainty getMeasuredInterfacialEnergyFromWidth(DoubleWithUncertainty width, PolymerSimulator polymerSimulator) {
        final ExternalEnergyCalculator externalEnergyCalculator = polymerSimulator.getSystemAnalyzer().getEnergeticsConstants().getExternalEnergyCalculator();
        final double xEquilibriumPosition = externalEnergyCalculator.getxEquilibriumPosition();
        final double xSpringConstant = externalEnergyCalculator.getxSpringConstant();

        final double surfaceTension = xSpringConstant * (xEquilibriumPosition - width.getValue()) / 2;
        final double surfaceTensionError = Math.abs(xSpringConstant * width.getUncertainty()) / 2;
        return new DoubleWithUncertainty(surfaceTension, surfaceTensionError);
    }

    @Override
    protected boolean isConverged() {
        return true;
    }

    @Override
    protected void printFinalOutput() {
    }

}
