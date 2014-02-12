/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FocusedSimulation.surfacetension;

import Engine.Energetics.EnergeticsConstants;
import Engine.Energetics.EnergeticsConstants.EnergeticsConstantsBuilder;
import Engine.Energetics.ExternalEnergyCalculator;
import Engine.Energetics.ExternalEnergyCalculator.ExternalEnergyCalculatorBuilder;
import Engine.PolymerSimulator;
import Engine.PolymerState.SystemGeometry.GeometricalParameters;
import Engine.PolymerState.SystemGeometry.Implementations.AbstractGeometry.AbstractGeometryBuilder;
import Engine.PolymerState.SystemGeometry.Implementations.PeriodicGeometry;
import Engine.PolymerState.SystemGeometry.Interfaces.SystemGeometry;
import Engine.PolymerTopology.PolymerChain;
import Engine.PolymerTopology.PolymerCluster;
import Engine.SimulationStepping.StepGenerators.CompoundStepGenerators.GeneralStepGenerator;
import Engine.SimulationStepping.StepGenerators.StepGenerator;
import Engine.SimulationStepping.StepTypes.StepType;
import Engine.SimulatorParameters;
import FocusedSimulation.DoubleWithUncertainty;
import FocusedSimulation.JobParameters;
import FocusedSimulation.OutputWriter;
import FocusedSimulation.SimulationRunner;
import FocusedSimulation.SimulationRunner.SimulationRunnerParameters;
import FocusedSimulation.StatisticsTracker.TrackableVariable;
import Gui.SystemViewer;
import SGEManagement.SGEManager;
import SGEManagement.SGEManager.Input;
import SGEManagement.SGEManager.Input.InputBuilder;
import SystemAnalysis.StressTrackable;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.EnumMap;

/**
 *
 * @author bmoths
 */
public class SurfaceTensionFinder {

    static public final class MeasuredSurfaceTension {

        public final double surfaceTension;
        public final double surfaceTensionStandardError;

        public MeasuredSurfaceTension(double surfaceTension, double surfaceTensionStandardError) {
            this.surfaceTension = surfaceTension;
            this.surfaceTensionStandardError = surfaceTensionStandardError;
        }

    }

    //<editor-fold defaultstate="collapsed" desc="default system parameters">
    static public SimulatorParameters getTensionDefaultParamters() {
        final double interactionLength = 4;

        EnergeticsConstantsBuilder energeticsConstantsBuilder = makeDefaultEnergeticsConstants();
        GeometricalParameters geometricalParameters = new GeometricalParameters(interactionLength, energeticsConstantsBuilder);
        energeticsConstantsBuilder.setHardOverlapCoefficientFromParameters(geometricalParameters);

        final PolymerCluster polymerCluster = makeDefaultPolymerCluster();
        final SystemGeometry systemGeometry = makeDefaultSystemGeometry(polymerCluster, geometricalParameters);
        final EnergeticsConstants energeticsConstants = energeticsConstantsBuilder.buildEnergeticsConstants();
        return new SimulatorParameters(systemGeometry, polymerCluster, energeticsConstants);
    }

    private static PolymerCluster makeDefaultPolymerCluster() {
        final int numBeadsPerChain = 15;
        final int numChains = 100;
        final double density = .05;
        PolymerChain polymerChain = PolymerChain.makeChainStartingWithA(0, numBeadsPerChain);
        PolymerCluster polymerCluster = PolymerCluster.makeRepeatedChainCluster(polymerChain, numChains);
        polymerCluster.setConcentrationInWater(density);
        return polymerCluster;
    }

    private static EnergeticsConstantsBuilder makeDefaultEnergeticsConstants() {
        final double BBOverlapCoefficient = -.06;
        final int xPosition = 50;
        final int xSpringConstant = 10;

        EnergeticsConstants.EnergeticsConstantsBuilder energeticsConstantsBuilder = EnergeticsConstantsBuilder.zeroEnergeticsConstantsBuilder();
        energeticsConstantsBuilder.setBBOverlapCoefficient(BBOverlapCoefficient);
        final ExternalEnergyCalculatorBuilder externalEnergyCalculatorBuilder = new ExternalEnergyCalculatorBuilder().setXPositionAndSpringConstant(xPosition, xSpringConstant);
        energeticsConstantsBuilder.setExternalEnergyCalculator(externalEnergyCalculatorBuilder.build());
        return energeticsConstantsBuilder;
    }

    private static SystemGeometry makeDefaultSystemGeometry(PolymerCluster polymerCluster, GeometricalParameters geometricalParameters) {
        AbstractGeometryBuilder systemGeometryBuilder = new PeriodicGeometry.PeriodicGeometryBuilder();
        final double aspectRatio = .1;
        final int numDimensions = 2;
        systemGeometryBuilder.setDimension(numDimensions);
        systemGeometryBuilder.makeConsistentWith(polymerCluster.getNumBeadsIncludingWater(), geometricalParameters, aspectRatio);
        SystemGeometry systemGeometry = systemGeometryBuilder.buildGeometry();
        return systemGeometry;
    }
    //</editor-fold>

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

    static private MeasuredSurfaceTension getMeasuredSurfaceTensionFromWidth(DoubleWithUncertainty width, PolymerSimulator polymerSimulator) {
        final ExternalEnergyCalculator externalEnergyCalculator = polymerSimulator.getSystemAnalyzer().getEnergeticsConstants().getExternalEnergyCalculator();
        final double xEquilibriumPosition = externalEnergyCalculator.getxEquilibriumPosition();
        final double xSpringConstant = externalEnergyCalculator.getxSpringConstant();

        final double surfaceTension = xSpringConstant * (xEquilibriumPosition - width.getValue()); //should divide by two since there are two surfaces
        final double surfaceTensionError = Math.abs(xSpringConstant * width.getUncertainty()); //should divide by two since there are two surfaces
        return new MeasuredSurfaceTension(surfaceTension, surfaceTensionError);
    }

    private static Input readInput(String[] args) {
        if (args.length == 0) {
            final double verticalScaleFactor = .25;
            final double horizontalScaleFactor = 10;

            InputBuilder inputBuilder = SGEManager.makeRescaleInputBuilderWithHorizontalRescaling(verticalScaleFactor, horizontalScaleFactor, 0);
            inputBuilder.getJobParametersBuilder().setNumAnneals(5);
            return inputBuilder.buildInput();
        } else if (args.length == 1) {
            final String fileName = args[0];
            return readInputFromFile(fileName);
        } else {
            throw new AssertionError("At most one input allowed", null);
        }
    }

    static private Input readInputFromFile(String fileName) {
        ObjectInputStream objectInputStream = getObjectOutputStream(fileName);
        try {
            return (Input) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            throw new AssertionError("could not load input from file: " + fileName, ex);
        }
    }

    static public Input makeDefaultInput() {
        JobParameters jobParameters = JobParameters.getDefaultJobParameters();
        SimulatorParameters systemParameters = getTensionDefaultParamters();
        return new Input(systemParameters, jobParameters);
    }

    private static ObjectInputStream getObjectOutputStream(String fileName) {
        try {
            final String absolutePath = OutputWriter.getProjectPath() + fileName;
            FileInputStream fileInputStream = new FileInputStream(absolutePath);
            final ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            return objectInputStream;
        } catch (FileNotFoundException ex) {
            throw new AssertionError("file not found: " + fileName, null);
        } catch (IOException ex) {
            throw new AssertionError("could not load input from file: " + fileName, ex);
        }
    }

    private final JobParameters jobParameters;
    private final SimulatorParameters systemParameters;
    private final OutputWriter outputWriter;

    private SurfaceTensionFinder(Input input) throws FileNotFoundException {
        jobParameters = input.getJobParameters();
        systemParameters = input.getSystemParameters();
        outputWriter = new OutputWriter(this);
    }

    public void findSurfaceTension() {
        outputWriter.printParameters();
        PolymerSimulator polymerSimulator = systemParameters.makePolymerSimulator();
//        polymerSimulator.columnRandomizePositions();
        polymerSimulator.reasonableColumnRandomize();
        SimulationRunner simulationRunner = new SimulationRunner(polymerSimulator, SimulationRunnerParameters.defaultSimulationRunnerParameters());
        final TrackableVariable systemWidth = new TrackableVariable() {
            @Override
            public double getValue(PolymerSimulator polymerSimulator) {
                return polymerSimulator.getSystemAnalyzer().getSystemGeometry().getSizeOfDimension(0);
            }

        };
        simulationRunner.trackVariable(systemWidth);

        StressTrackable stressTrackable = new StressTrackable(polymerSimulator);
        simulationRunner.trackVariable(stressTrackable.getStress11Trackable());
        simulationRunner.trackVariable(stressTrackable.getStress12Trackable());
        simulationRunner.trackVariable(stressTrackable.getStress22Trackable());

        try {
            SystemViewer systemViewer = new SystemViewer(polymerSimulator);
            systemViewer.setVisible(true);
        } catch (java.awt.HeadlessException e) {
            System.out.println("Headless exception thrown when creating system viewer. I am unable to create system viewer.");
        }

        System.out.println("System is initialized.");

        outputWriter.printInitializationInfo(polymerSimulator);

        simulationRunner.setStepGenerator(makeInitialStepGenerator());

        simulationRunner.doEquilibrateAnnealIterations(Math.min(jobParameters.getNumAnneals(), 1));

        simulationRunner.setStepGenerator(makeMainStepGenerator());

        if (jobParameters.getNumAnneals() > 1) {
            simulationRunner.doEquilibrateAnnealIterations(jobParameters.getNumAnneals() - 1);
        }

        for (int i = 0; i < jobParameters.getNumSurfaceTensionTrials(); i++) {
            doMeasurementTrial(simulationRunner, systemWidth, polymerSimulator);
            outputStress(simulationRunner, stressTrackable);
        }

        while (jobParameters.getShouldIterateUntilConvergence() && !simulationRunner.isConverged(systemWidth)) {
            doMeasurementTrial(simulationRunner, systemWidth, polymerSimulator);
        }

        outputWriter.printFinalOutput(polymerSimulator);
    }

    private void doMeasurementTrial(SimulationRunner simulationRunner, TrackableVariable trackableVariable, PolymerSimulator polymerSimulator) {
        simulationRunner.doMeasurementRun();
        DoubleWithUncertainty measuredWidth = simulationRunner.getRecentMeasurementForTrackedVariable(trackableVariable);
        MeasuredSurfaceTension measuredSurfaceTension = getMeasuredSurfaceTensionFromWidth(measuredWidth, polymerSimulator);
        outputWriter.printSurfaceTension(measuredSurfaceTension);
    }

    private void outputStress(SimulationRunner simulationRunner, StressTrackable stressTrackable) {
        final DoubleWithUncertainty stress11 = simulationRunner.getRecentMeasurementForTrackedVariable(stressTrackable.getStress11Trackable());
        final DoubleWithUncertainty stress12 = simulationRunner.getRecentMeasurementForTrackedVariable(stressTrackable.getStress12Trackable());
        final DoubleWithUncertainty stress22 = simulationRunner.getRecentMeasurementForTrackedVariable(stressTrackable.getStress22Trackable());
        outputWriter.printStress(stress11, stress12, stress22);
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

    //</editor-fold>
}
