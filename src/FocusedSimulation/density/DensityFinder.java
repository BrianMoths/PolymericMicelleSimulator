/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FocusedSimulation.density;

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
import FocusedSimulation.surfacetension.SurfaceTensionJobMaker;
import Gui.SystemViewer;
import SGEManagement.Input;
import SGEManagement.Input.InputBuilder;
import SystemAnalysis.StressTrackable;
import java.io.FileNotFoundException;
import java.util.EnumMap;

/**
 *
 * @author bmoths
 */
public class DensityFinder {

    public static void main(String[] args) {
        final Input input = readInput(args);
        try {
            final DensityFinder densityFinder;
            densityFinder = new DensityFinder(input);
            densityFinder.findDensity();
            densityFinder.closeOutputWriter();
        } catch (FileNotFoundException ex) {
            System.out.println("File not able to be opened");
        }
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
    private final DensityResultsWriter densityResultsWriter;
    private final PolymerSimulator polymerSimulator;
    private final SimulationRunner simulationRunner;

    private DensityFinder(Input input) throws FileNotFoundException {
        jobParameters = input.getJobParameters();
        systemParameters = input.getSystemParameters();
        polymerSimulator = systemParameters.makePolymerSimulator();
        simulationRunner = new SimulationRunner(polymerSimulator, SimulationRunnerParameters.defaultSimulationRunnerParameters());
        densityResultsWriter = new DensityResultsWriter(input);
    }

    private void findDensity() {
        initialize();
        doInitialEquilibrateAndAnneal();
        doMeasurementTrials();
        doTrialsUntilConvergence();
        printFinalOutput();
    }

    private void initialize() {
        randomizePositions();
        registerTrackablesToSimulationRunner();
        tryInitializeSystemViewer();
        printInitialOutput();
    }

    //<editor-fold defaultstate="collapsed" desc="initialize">
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
        densityResultsWriter.printParameters();
        densityResultsWriter.printInitializationInfo(polymerSimulator);
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

    //<editor-fold defaultstate="collapsed" desc="make step generators">
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
    //</editor-fold>

    private void doMeasurementTrials() {
        for (int i = 0; i < jobParameters.getNumSurfaceTensionTrials(); i++) {
            doMeasurementTrial(TrackableVariable.SYSTEM_WIDTH);
        }
    }

    private void doMeasurementTrial(TrackableVariable trackableVariable) {
        simulationRunner.doMeasurementRun();
        DoubleWithUncertainty measuredWidth = simulationRunner.getRecentMeasurementForTrackedVariable(trackableVariable);
//        DoubleWithUncertainty measuredSurfaceTension = getMeasuredSurfaceTensionFromWidth(measuredWidth, polymerSimulator);
//        densityResultsWriter.printSurfaceTension(measuredSurfaceTension);
        densityResultsWriter.printStress(simulationRunner);
    }

    private void doTrialsUntilConvergence() {
        while (jobParameters.getShouldIterateUntilConvergence() && !simulationRunner.isConverged(TrackableVariable.SYSTEM_WIDTH)) {
            doMeasurementTrial(TrackableVariable.SYSTEM_WIDTH);
        }
    }

    private void printFinalOutput() {
        densityResultsWriter.printFinalOutput(polymerSimulator);
    }

    private void closeOutputWriter() {
        densityResultsWriter.closeWriter();
    }

}
