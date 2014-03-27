/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FocusedSimulation;

import Engine.Energetics.ExternalEnergyCalculator;
import Engine.PolymerSimulator;
import Engine.SimulationStepping.StepGenerators.CompoundStepGenerators.GeneralStepGenerator;
import Engine.SimulationStepping.StepGenerators.StepGenerator;
import Engine.SimulationStepping.StepTypes.StepType;
import Engine.SimulatorParameters;
import FocusedSimulation.SimulationRunner.SimulationRunnerParameters;
import FocusedSimulation.output.AbstractResultsWriter;
import FocusedSimulation.output.PolymerSimulatorWriter;
import Gui.SystemViewer;
import SGEManagement.Input;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.EnumMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bmoths
 */
public abstract class AbstractFocusedSimulation<T extends AbstractResultsWriter> {

    protected static Input readInput(String[] args, Input defaultInput) {
        if (args.length == 0) {
            return defaultInput;
        } else if (args.length == 1) {
            final String fileName = args[0];
            return Input.readInputFromFile(fileName);
        } else {
            throw new IllegalArgumentException("At most one input allowed");
        }
    }

    private final JobParameters jobParameters;
    private final SimulatorParameters systemParameters;
    private PolymerSimulatorWriter polymerSimulatorWriter;
    protected final T outputWriter;
    protected final PolymerSimulator polymerSimulator;
    protected final SimulationRunner simulationRunner;

    protected AbstractFocusedSimulation(Input input, T outputWriter) throws FileNotFoundException {
        jobParameters = input.getJobParameters();
        systemParameters = input.getSystemParameters();
        polymerSimulator = systemParameters.makePolymerSimulator();
        simulationRunner = new SimulationRunner(polymerSimulator, SimulationRunnerParameters.defaultSimulationRunnerParameters());
        this.outputWriter = outputWriter;
        try {
            polymerSimulatorWriter = new PolymerSimulatorWriter(polymerSimulator, input.getJobNumber());
        } catch (IOException ex) {
            Logger.getLogger(AbstractFocusedSimulation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public final void doSimulation() {
        initialize();
        doInitialEquilibrateAndAnneal();
        doMeasurementTrials();
        if (jobParameters.getShouldIterateUntilConvergence()) {
            doTrialsUntilConvergence();
        }
        printFinalOutputGeneral();
    }

    //<editor-fold defaultstate="collapsed" desc="initialize">
    private void initialize() {
        initializePositions();
        registerTrackablesToSimulationRunner();
        tryInitializeSystemViewer();
        printInitialOutputGeneric();
    }

    protected abstract void initializePositions();

    protected abstract void registerTrackablesToSimulationRunner();

    private void tryInitializeSystemViewer() {
        try {
            SystemViewer systemViewer = new SystemViewer(polymerSimulator);
            systemViewer.setVisible(true);
        } catch (java.awt.HeadlessException e) {
            System.out.println("Headless exception thrown when creating system viewer. I am unable to create system viewer.");
        }
    }

    private void printInitialOutputGeneric() {
        outputWriter.printParameters();
        printInitialOutput();
    }

    protected abstract void printInitialOutput();
    //</editor-fold>

    private void doInitialEquilibrateAndAnneal() {
        simulationRunner.setStepGenerator(makeInitialStepGenerator());
        simulationRunner.doEquilibrateAnnealIterations(Math.min(jobParameters.getNumAnneals(), 1));

        simulationRunner.setStepGenerator(makeMainStepGenerator());

        if (jobParameters.getNumAnneals() > 1) {
            simulationRunner.doEquilibrateAnnealIterations(jobParameters.getNumAnneals() - 1);
        }
    }

    private StepGenerator makeInitialStepGenerator() {
        EnumMap<StepType, Double> stepweights = new EnumMap<>(StepType.class);
        stepweights.put(StepType.SINGLE_WALL_RESIZE, .0001);
        stepweights.put(StepType.SINGLE_BEAD, 1.);
        return new GeneralStepGenerator(stepweights);

    }

    protected StepGenerator makeMainStepGenerator() {
        EnumMap<StepType, Double> stepweights = new EnumMap<>(StepType.class);
        stepweights.put(StepType.SINGLE_WALL_RESIZE, .0001);
        stepweights.put(StepType.SINGLE_BEAD, 1.);
        stepweights.put(StepType.REPTATION, .1);
        stepweights.put(StepType.SINGLE_CHAIN, .01);
        return new GeneralStepGenerator(stepweights);

    }

    private void doMeasurementTrials() {
        for (int i = 0; i < jobParameters.getNumSurfaceTensionTrials(); i++) {
            doMeasurementTrial();
        }
    }

    private void doTrialsUntilConvergence() {
        while (!isConverged()) {
            doMeasurementTrial();
        }
    }

    private void doMeasurementTrial() {
        simulationRunner.doMeasurementRun();
        analyzeAndPrintResults();
    }

    protected abstract void analyzeAndPrintResults();

    protected abstract boolean isConverged();

    private void printFinalOutputGeneral() {
        printFinalOutput();
        outputWriter.printFinalOutput(polymerSimulator);
    }

    protected abstract void printFinalOutput();

    public final void closeOutputWriter() {
        outputWriter.closeWriter();
        polymerSimulatorWriter.stopWriting();
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
