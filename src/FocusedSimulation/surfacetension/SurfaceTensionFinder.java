/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FocusedSimulation.surfacetension;

import Engine.Energetics.ExternalEnergyCalculator;
import Engine.PolymerSimulator;
import Engine.SimulationStepping.StepGenerators.CompoundStepGenerators.GeneralStepGenerator;
import Engine.SimulationStepping.StepGenerators.StepGenerator;
import Engine.SimulationStepping.StepTypes.StepType;
import Engine.SimulatorParameters;
import FocusedSimulation.DoubleWithUncertainty;
import FocusedSimulation.JobParameters;
import FocusedSimulation.SimulationRunner;
import FocusedSimulation.SimulationRunner.SimulationRunnerParameters;
import FocusedSimulation.StatisticsTracker.TrackableVariable;
import Gui.SystemViewer;
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
public class SurfaceTensionFinder {

    public static void main(String[] args) {
        final Input input = readInput(args);
        try {
            final SurfaceTensionFinder surfaceTensionFinder;
            surfaceTensionFinder = new SurfaceTensionFinder(input);
            surfaceTensionFinder.findSurfaceTension();
            surfaceTensionFinder.closeOutputWriter();
        } catch (FileNotFoundException ex) {
            System.out.println("File not able to be opened");
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

    private static Input readInput(String[] args) {
        if (args.length == 0) {
            final double verticalScaleFactor = .1;
            final double horizontalScaleFactor = 1;

            InputBuilder inputBuilder = SurfaceTensionJobMaker.makeRescaleInputBuilderWithHorizontalRescaling(verticalScaleFactor, horizontalScaleFactor, 0);
            inputBuilder.getJobParametersBuilder().setNumAnneals(1);
            inputBuilder.getJobParametersBuilder().setNumSurfaceTensionTrials(1);
            return inputBuilder.buildInput();
        } else if (args.length == 1) {
            final String fileName = args[0];
            return Input.readInputFromFile(fileName);
        } else {
            throw new IllegalArgumentException("At most one input allowed");
        }
    }

    private final JobParameters jobParameters;
    private final SimulatorParameters systemParameters;
    private final SurfaceTensionResultsWriter outputWriter;
    private final PolymerSimulator polymerSimulator;
    private final SimulationRunner simulationRunner;

    private SurfaceTensionFinder(Input input) throws FileNotFoundException {
        jobParameters = input.getJobParameters();
        systemParameters = input.getSystemParameters();
        polymerSimulator = systemParameters.makePolymerSimulator();
        simulationRunner = new SimulationRunner(polymerSimulator, SimulationRunnerParameters.defaultSimulationRunnerParameters());
        outputWriter = new SurfaceTensionResultsWriter(input);
    }

    public void findSurfaceTension() {
        initialize();
        doInitialEquilibrateAndAnneal();
        doMeasurementTrials();
        doTrialsUntilConvergence();
        printFinalOutput();
    }

    //<editor-fold defaultstate="collapsed" desc="initialize">
    private void initialize() {
        randomizePositions();
        registerTrackablesToSimulationRunner();
        tryInitializeSystemViewer();
        printInitialOutput();
    }

    private void randomizePositions() {
        polymerSimulator.reasonableColumnRandomize();
    }

    private void registerTrackablesToSimulationRunner() {
        simulationRunner.trackVariable(TrackableVariable.SYSTEM_WIDTH);
        simulationRunner.trackVariable((StressTrackable.TOTAL_STRESS_TRACKABLE).getStress11Trackable());
        simulationRunner.trackVariable((StressTrackable.TOTAL_STRESS_TRACKABLE).getStress12Trackable());
        simulationRunner.trackVariable((StressTrackable.TOTAL_STRESS_TRACKABLE).getStress22Trackable());
    }

    private void tryInitializeSystemViewer() {
        try {
            SystemViewer systemViewer = new SystemViewer(polymerSimulator);
            systemViewer.setVisible(true);
        } catch (java.awt.HeadlessException e) {
            System.out.println("Headless exception thrown when creating system viewer. I am unable to create system viewer.");
        }
    }

    private void printInitialOutput() {
        outputWriter.printParameters();
        outputWriter.printInitializationInfo(polymerSimulator);
        System.out.println("System is initialized.");
    }
    //</editor-fold>

    private void doInitialEquilibrateAndAnneal() {
        simulationRunner.setStepGenerator(makeInitialStepGenerator());
        simulationRunner.doEquilibrateAnnealIterations(Math.min(jobParameters.getNumAnneals(), 1));

        simulationRunner.setStepGenerator(makeMainStepGenerator());

        if (jobParameters.getNumAnneals() > 1) {
            simulationRunner.doEquilibrateAnnealIterations(jobParameters.getNumAnneals() - 1);
        }
    }

    private void doMeasurementTrials() {
        for (int i = 0; i < jobParameters.getNumSurfaceTensionTrials(); i++) {
            doMeasurementTrial(TrackableVariable.SYSTEM_WIDTH);
        }
    }

    private void doTrialsUntilConvergence() {
        while (jobParameters.getShouldIterateUntilConvergence() && !simulationRunner.isConverged(TrackableVariable.SYSTEM_WIDTH)) {
            doMeasurementTrial(TrackableVariable.SYSTEM_WIDTH);
        }
    }

    private void doMeasurementTrial(TrackableVariable trackableVariable) {
        simulationRunner.doMeasurementRun();
        DoubleWithUncertainty measuredWidth = simulationRunner.getRecentMeasurementForTrackedVariable(trackableVariable);
        DoubleWithUncertainty measuredSurfaceTension = getMeasuredSurfaceTensionFromWidth(measuredWidth, polymerSimulator);
        outputWriter.printSurfaceTension(measuredSurfaceTension);
        outputWriter.printStress(simulationRunner);
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

    private void printFinalOutput() {
        outputEndToEndDisplacements();
        outputWriter.printFinalOutput(polymerSimulator);
    }

    public void closeOutputWriter() {
        outputWriter.closeWriter();
    }

    private StepGenerator makeInitialStepGenerator() {
        EnumMap<StepType, Double> stepweights = new EnumMap<>(StepType.class);
        stepweights.put(StepType.SINGLE_WALL_RESIZE, .0001);
        stepweights.put(StepType.SINGLE_BEAD, 1.);
        return new GeneralStepGenerator(stepweights);

    }

    private StepGenerator makeMainStepGenerator() {
        EnumMap<StepType, Double> stepweights = new EnumMap<>(StepType.class);
        stepweights.put(StepType.SINGLE_WALL_RESIZE, .0001);
        stepweights.put(StepType.SINGLE_BEAD, 1.);
        stepweights.put(StepType.REPTATION, .1);
        stepweights.put(StepType.SINGLE_CHAIN, .01);
        return new GeneralStepGenerator(stepweights);

    }

//<editor-fold defaultstate="collapsed" desc="getters">
    public SimulatorParameters getInputParameters() {
        return systemParameters;
    }

    public int getJobNumber() {
        return jobParameters.getJobNumber();
    }

    public int getNumBeadsPerChain() {
        return (int) Math.round(systemParameters.getPolymerCluster().getNumBeadsPerChain());
    }

    public int getNumChains() {
        return systemParameters.getPolymerCluster().getNumChains();
    }

    public double getDensity() {
        return systemParameters.getPolymerCluster().getConcentrationInWater();
    }

    public int getNumAnneals() {
        return jobParameters.getNumAnneals();
    }

    public int getNumSurfaceTensionTrials() {
        return jobParameters.getNumSurfaceTensionTrials();
    }

    public ExternalEnergyCalculator getExternalEnergyCalculator() {
        return systemParameters.getEnergeticsConstants().getExternalEnergyCalculator();
    }

    public double getBeadSize() {
        return systemParameters.systemGeometry.getParameters().getInteractionLength();
    }

    SimulationRunner getSimulationRunner() {
        return simulationRunner;
    }
    //</editor-fold>

}
