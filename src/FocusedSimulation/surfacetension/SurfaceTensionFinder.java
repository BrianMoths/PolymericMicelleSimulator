/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FocusedSimulation.surfacetension;

import Engine.Energetics.ExternalEnergyCalculator;
import Engine.Energetics.ExternalEnergyCalculator.ExternalEnergyCalculatorBuilder;
import Engine.PolymerSimulator;
import Engine.SimulationStepping.StepGenerators.CompoundStepGenerators.GeneralStepGenerator;
import Engine.SimulationStepping.StepGenerators.StepGenerator;
import Engine.SimulationStepping.StepTypes.StepType;
import FocusedSimulation.AbstractFocusedSimulation;
import FocusedSimulation.DoubleWithUncertainty;
import FocusedSimulation.StatisticsTracker.TrackableVariable;
import SGEManagement.Input;
import SGEManagement.Input.InputBuilder;
import SystemAnalysis.HistogramMaker;
import SystemAnalysis.StressTrackable;
import java.io.FileNotFoundException;
import java.util.EnumMap;
import java.util.List;

/**
 *
 * @author bmoths
 */
public class SurfaceTensionFinder extends AbstractFocusedSimulation<SurfaceTensionResultsWriter> {

    public static void main(String[] args) {
        final Input input = readInput(args);
        try {
            final SurfaceTensionFinder surfaceTensionFinder;
            surfaceTensionFinder = new SurfaceTensionFinder(input);
            surfaceTensionFinder.doSimulation();
            surfaceTensionFinder.closeOutputWriter();
        } catch (FileNotFoundException ex) {
            System.out.println("File not able to be opened");
        }
    }

    private static Input readInput(String[] args) {
        if (args.length == 0) {
            final double verticalScaleFactor = .2;
            final double horizontalScaleFactor = 1;

            InputBuilder inputBuilder = SurfaceTensionJobMaker.makeRescaleInputBuilderWithHorizontalRescaling(verticalScaleFactor, horizontalScaleFactor, 0);
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

    static private DoubleWithUncertainty getMeasuredSurfaceTensionFromWidth(DoubleWithUncertainty width, PolymerSimulator polymerSimulator) {
        final ExternalEnergyCalculator externalEnergyCalculator = polymerSimulator.getSystemAnalyzer().getEnergeticsConstants().getExternalEnergyCalculator();
        final double xEquilibriumPosition = externalEnergyCalculator.getxEquilibriumPosition();
        final double xSpringConstant = externalEnergyCalculator.getxSpringConstant();

        final double surfaceTension = xSpringConstant * (xEquilibriumPosition - width.getValue()) / 2;
        final double surfaceTensionError = Math.abs(xSpringConstant * width.getUncertainty()) / 2;
        return new DoubleWithUncertainty(surfaceTension, surfaceTensionError);
    }

    private SurfaceTensionFinder(Input input) throws FileNotFoundException {
        super(input, new SurfaceTensionResultsWriter(input));
    }

    //<editor-fold defaultstate="collapsed" desc="initialize">
    @Override
    protected void initializePositions() {
        polymerSimulator.reasonableColumnRandomize();
    }

    @Override
    protected void registerTrackablesToSimulationRunner() {
        simulationRunner.trackVariable(TrackableVariable.SYSTEM_WIDTH);
        simulationRunner.trackVariable((StressTrackable.TOTAL_STRESS_TRACKABLE).getStress11Trackable());
        simulationRunner.trackVariable((StressTrackable.TOTAL_STRESS_TRACKABLE).getStress12Trackable());
        simulationRunner.trackVariable((StressTrackable.TOTAL_STRESS_TRACKABLE).getStress22Trackable());
    }

    @Override
    protected void printInitialOutput() {
        outputWriter.printInitializationInfo(polymerSimulator);
    }
    //</editor-fold>

    @Override
    protected void analyzeAndPrintResults() {
        DoubleWithUncertainty measuredWidth = simulationRunner.getRecentMeasurementForTrackedVariable(TrackableVariable.SYSTEM_WIDTH);
        DoubleWithUncertainty measuredSurfaceTension = getMeasuredSurfaceTensionFromWidth(measuredWidth, polymerSimulator);
        outputWriter.printSurfaceTension(measuredSurfaceTension);
        outputWriter.printStress(simulationRunner);
    }

    @Override
    protected boolean isConverged() {
        return simulationRunner.isConverged(TrackableVariable.SYSTEM_WIDTH);
    }

    private void outputEndToEndDisplacements() {
        final List<double[]> endToEndDisplacements = polymerSimulator.getSystemAnalyzer().getEndToEndDisplacements();
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Begin end to end displacements\n");
        for (double[] displacment : endToEndDisplacements) {
            for (int i = 0; i < displacment.length - 1; i++) {
                stringBuilder.append(displacment[i]).append(", ");
            }
            stringBuilder.append(displacment[displacment.length - 1]).append("\n");
        }
        stringBuilder.append("End end to end displacements\n");
        final String displacmentsString = stringBuilder.toString();
        outputWriter.printAndSoutString(displacmentsString);
    }

    private void soutYPositionHistogram() {
        final List<Integer> histogram = HistogramMaker.makeHistogram(simulationRunner.getPolymerSimulator().getSystemAnalyzer());
        System.out.println("Histogram");
        for (Integer integer : histogram) {
            System.out.println(integer);
        }
        System.out.println("End histogram");
    }

    @Override
    protected void printFinalOutput() {
//        soutYPositionHistogram();
    }

}
