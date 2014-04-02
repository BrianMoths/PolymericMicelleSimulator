/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FocusedSimulation.bridgecollapse;

import Engine.Energetics.ExternalEnergyCalculator.ExternalEnergyCalculatorBuilder;
import FocusedSimulation.AbstractFocusedSimulation;
import FocusedSimulation.DoubleWithUncertainty;
import FocusedSimulation.StatisticsTracker.TrackableVariable;
import FocusedSimulation.surfacetension.SurfaceTensionResultsWriter;
import SGEManagement.Input;
import SGEManagement.Input.InputBuilder;
import java.io.FileNotFoundException;

/**
 *
 * @author bmoths
 */
public class BridgeCollapseFinder extends AbstractFocusedSimulation<BridgeCollapseResultsWriter> {

    public static void main(String[] args) {
        final Input input = readInput(args);
        try {
            final BridgeCollapseFinder bridgeCollapseFinder;
            bridgeCollapseFinder = new BridgeCollapseFinder(input);
            bridgeCollapseFinder.doSimulation();
            bridgeCollapseFinder.closeOutputWriter();
        } catch (FileNotFoundException ex) {
            System.out.println("File not able to be opened");
        }
    }

    private static Input readInput(String[] args) {
        if (args.length == 0) {
            final double verticalScaleFactor = .2;
            final double horizontalScaleFactor = 1;

            InputBuilder inputBuilder = BridgeCollapseJobMaker.makeRescaleInputBuilderWithHorizontalRescaling(verticalScaleFactor, horizontalScaleFactor, 0);
            inputBuilder.getJobParametersBuilder().setNumAnneals(1);
            inputBuilder.getJobParametersBuilder().setNumSurfaceTensionTrials(30);
            inputBuilder.getSystemParametersBuilder().getEnergeticsConstantsBuilder().setExternalEnergyCalculatorBuilder(new ExternalEnergyCalculatorBuilder());
            return inputBuilder.buildInput();
        } else if (args.length == 1) {
            final String fileName = args[0];
            return Input.readInputFromFile(fileName);
        } else {
            throw new IllegalArgumentException("At most one input allowed");
        }
    }

    private BridgeCollapseFinder(Input input) throws FileNotFoundException {
        super(input, new BridgeCollapseResultsWriter(input));
    }

    @Override
    protected void initializePositions() {
        polymerSimulator.reasonableColumnRandomize();
    }

    @Override
    protected void registerTrackablesToSimulationRunner() {
        simulationRunner.trackVariable(TrackableVariable.SYSTEM_WIDTH);
    }

    @Override
    protected void printInitialOutput() {
        outputWriter.printInitializationInfo(polymerSimulator);
    }

    @Override
    protected void analyzeAndPrintResults() {
        DoubleWithUncertainty measuredWidth = simulationRunner.getRecentMeasurementForTrackedVariable(TrackableVariable.SYSTEM_WIDTH);
        outputWriter.printHorizontalSystemSize(measuredWidth);
        outputWriter.printFractionOfAreaCovered(polymerSimulator);
    }

    @Override
    protected boolean isConverged() {
        return simulationRunner.isConverged(TrackableVariable.SYSTEM_WIDTH);
    }

    @Override
    protected void printFinalOutput() {
    }

}
