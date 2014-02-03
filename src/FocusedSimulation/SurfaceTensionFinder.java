/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FocusedSimulation;

import Engine.Energetics.EnergeticsConstants;
import Engine.Energetics.EnergeticsConstants.EnergeticsConstantsBuilder;
import Engine.Energetics.ExternalEnergyCalculator;
import Engine.Energetics.ExternalEnergyCalculator.ExternalEnergyCalculatorBuilder;
import Engine.PolymerSimulator;
import Engine.PolymerState.SystemGeometry.GeometricalParameters;
import Engine.PolymerState.SystemGeometry.Implementations.AbstractGeometry.AbstractGeometryBuilder;
import Engine.PolymerState.SystemGeometry.Implementations.PeriodicGeometry;
import Engine.PolymerState.SystemGeometry.Implementations.PeriodicGeometry.PeriodicGeometryBuilder;
import Engine.PolymerState.SystemGeometry.Interfaces.SystemGeometry;
import Engine.PolymerTopology.PolymerChain;
import Engine.PolymerTopology.PolymerCluster;
import Engine.SimulationStepping.StepGenerators.CompoundStepGenerators.GeneralStepGenerator;
import Engine.SimulationStepping.StepGenerators.StepGenerator;
import Engine.SimulationStepping.StepTypes.StepType;
import FocusedSimulation.SimulationRunner.DoubleWithUncertainty;
import FocusedSimulation.SimulationRunner.SimulationRunnerParameters;
import FocusedSimulation.StatisticsTracker.TrackableVariable;
import Gui.SystemViewer;
import SGEManagement.SGEManager;
import SGEManagement.SGEManager.Input;
import SGEManagement.SGEManager.Input.InputBuilder;
import SystemAnalysis.StressFinder;
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

    static public class JobParameters implements java.io.Serializable {

        static public class JobParametersBuilder {

            static private final int defaultNumAnneals = 1;//50
            static private final int defaultNumSurfaceTensionTrials = 70;
            static private final int defaultJobNumber = 0;

            static public JobParametersBuilder getDefaultJobParametersBuilder() {
                JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
                jobParametersBuilder.setNumAnneals(defaultNumAnneals);
                jobParametersBuilder.setNumSurfaceTensionTrials(defaultNumSurfaceTensionTrials);
                jobParametersBuilder.setJobNumber(defaultJobNumber);
                return jobParametersBuilder;
            }

            static public JobParameters getDefaultJobParameters() {
                return new JobParameters(defaultNumAnneals, defaultNumSurfaceTensionTrials, true, defaultJobNumber);
            }

            private int numAnneals; //50
            private int numSurfaceTensionTrials; //70
            private boolean shouldIterateUntilConvergence;
            private int jobNumber;

            public JobParametersBuilder() {
            }

            public JobParameters buildJobParameters() {
                return new JobParameters(this);
            }

            public int getNumAnneals() {
                return numAnneals;
            }

            public JobParametersBuilder setNumAnneals(int numAnneals) {
                this.numAnneals = numAnneals;
                return this;
            }

            public int getNumSurfaceTensionTrials() {
                return numSurfaceTensionTrials;
            }

            public JobParametersBuilder setNumSurfaceTensionTrials(int numSurfaceTensionTrials) {
                this.numSurfaceTensionTrials = numSurfaceTensionTrials;
                return this;
            }

            public boolean getShouldIterateUntilConvergence() {
                return shouldIterateUntilConvergence;
            }

            public void setShouldIterateUntilConvergence(boolean shouldIterateUntilConvergence) {
                this.shouldIterateUntilConvergence = shouldIterateUntilConvergence;
            }

            public int getJobNumber() {
                return jobNumber;
            }

            public JobParametersBuilder setJobNumber(int jobNumber) {
                this.jobNumber = jobNumber;
                return this;
            }

        }

        static private final int defaultNumAnneals = 50;
        static private final int defaultNumSurfaceTensionTrials = 70;
        static private final int defaultJobNumber = 0;

        static public JobParameters getDefaultJobParameters() {
            return new JobParameters(defaultNumAnneals, defaultNumSurfaceTensionTrials, true, defaultJobNumber);
        }

        private final int numAnneals; //50
        private final int numSurfaceTensionTrials; //70
        private final boolean shouldIterateUntilConvergence;
        private final int jobNumber;

        public JobParameters(int numAnneals, int numSurfaceTensionTrials, boolean shouldIterateUntilConvergence, int jobNumber) {
            this.numAnneals = numAnneals;
            this.numSurfaceTensionTrials = numSurfaceTensionTrials;
            this.shouldIterateUntilConvergence = shouldIterateUntilConvergence;
            this.jobNumber = jobNumber;
        }

        private JobParameters(JobParametersBuilder jobParametersBuilder) {
            numAnneals = jobParametersBuilder.getNumAnneals();
            numSurfaceTensionTrials = jobParametersBuilder.getNumSurfaceTensionTrials();
            shouldIterateUntilConvergence = jobParametersBuilder.getShouldIterateUntilConvergence();
            jobNumber = jobParametersBuilder.getJobNumber();
        }

        public int getNumAnneals() {
            return numAnneals;
        }

        public int getNumSurfaceTensionTrials() {
            return numSurfaceTensionTrials;
        }

        public boolean getShouldIterateUntilConvergence() {
            return shouldIterateUntilConvergence;
        }

        public int getJobNumber() {
            return jobNumber;
        }

    }

    static public final class MeasuredSurfaceTension {

        public final double surfaceTension;
        public final double surfaceTensionStandardError;

        public MeasuredSurfaceTension(double surfaceTension, double surfaceTensionStandardError) {
            this.surfaceTension = surfaceTension;
            this.surfaceTensionStandardError = surfaceTensionStandardError;
        }

    }

    static public final class SimulatorParameters implements java.io.Serializable {

        static public class SystemParametersBuilder {

            private double interactionLength;
            private EnergeticsConstantsBuilder energeticsConstantsBuilder;
            private PolymerCluster polymerCluster;
            private double aspectRatio;
            private GeneralStepGenerator generalStepGenerator;

            public SimulatorParameters buildSystemParameters() {
                GeometricalParameters geometricalParameters = new GeometricalParameters(interactionLength, energeticsConstantsBuilder);
                energeticsConstantsBuilder.setHardOverlapCoefficientFromParameters(geometricalParameters);
                SystemGeometry systemGeometry = makeSystemGeometry(geometricalParameters);
                final EnergeticsConstants energeticsConstants = energeticsConstantsBuilder.buildEnergeticsConstants();
                return new SimulatorParameters(systemGeometry, polymerCluster, energeticsConstants);
            }

            public double getInteractionLength() {
                return interactionLength;
            }

            public void setInteractionLength(double interactionLength) {
                this.interactionLength = interactionLength;
            }

            public EnergeticsConstantsBuilder getEnergeticsConstantsBuilder() {
                return energeticsConstantsBuilder;
            }

            public void setEnergeticsConstantsBuilder(EnergeticsConstantsBuilder energeticsConstantsBuilder) {
                this.energeticsConstantsBuilder = energeticsConstantsBuilder;
            }

            public PolymerCluster getPolymerCluster() {
                return polymerCluster;
            }

            public void setPolymerCluster(PolymerCluster polymerCluster) {
                this.polymerCluster = polymerCluster;
            }

            public double getAspectRatio() {
                return aspectRatio;
            }

            public void setAspectRatio(double aspectRatio) {
                this.aspectRatio = aspectRatio;
            }

            public GeneralStepGenerator getGeneralStepGenerator() {
                return generalStepGenerator;
            }

            public void setGeneralStepGenerator(GeneralStepGenerator generalStepGenerator) {
                this.generalStepGenerator = generalStepGenerator;
            }

            private SystemGeometry makeSystemGeometry(GeometricalParameters geometricalParameters) {
                final AbstractGeometryBuilder systemGeometryBuilder = new PeriodicGeometryBuilder();
                final int numDimensions = 2;
                systemGeometryBuilder.setDimension(numDimensions);
                systemGeometryBuilder.makeConsistentWith(polymerCluster.getNumBeadsIncludingWater(), geometricalParameters, aspectRatio);
                SystemGeometry systemGeometry = systemGeometryBuilder.buildGeometry();
                return systemGeometry;
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

        public final SystemGeometry systemGeometry;
        public final PolymerCluster polymerCluster;
        public final EnergeticsConstants energeticsConstants;

        public SimulatorParameters(SystemGeometry systemGeometry, PolymerCluster polymerCluster, EnergeticsConstants energeticsConstants) {
            this.systemGeometry = systemGeometry;
            this.polymerCluster = polymerCluster;
            this.energeticsConstants = energeticsConstants;
        }

        public PolymerSimulator makePolymerSimulator() {
            return new PolymerSimulator(systemGeometry, polymerCluster, energeticsConstants);
        }

        public SystemGeometry getSystemGeometry() {
            return systemGeometry;
        }

        public PolymerCluster getPolymerCluster() {
            return polymerCluster;
        }

        public EnergeticsConstants getEnergeticsConstants() {
            return energeticsConstants;
        }

    }

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
            final double scaleFactor = .2;

            InputBuilder inputBuilder = SGEManager.makeRescaleInputBuilder(scaleFactor, 0);
            inputBuilder.getJobParametersBuilder().numAnneals = 0;
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
        SimulatorParameters systemParameters = SimulatorParameters.getTensionDefaultParamters();
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
        this.jobParameters = input.getJobParameters();
        this.systemParameters = input.getSystemParameters();
        outputWriter = new OutputWriter(this);
    }

    public void findSurfaceTension() {
        outputWriter.printParameters();
        PolymerSimulator polymerSimulator = systemParameters.makePolymerSimulator();
        polymerSimulator.columnRandomizePositions();
        SimulationRunner simulationRunner = new SimulationRunner(polymerSimulator, SimulationRunnerParameters.defaultSimulationRunnerParameters());
        final TrackableVariable systemWidth = new TrackableVariable() {
            @Override
            public double getValue(PolymerSimulator polymerSimulator) {
                return polymerSimulator.getSystemAnalyzer().getSystemGeometry().getSizeOfDimension(0);
            }

        };
        simulationRunner.trackVariable(systemWidth);
        try {
            SystemViewer systemViewer = new SystemViewer(polymerSimulator);
            systemViewer.setVisible(true);
        } catch (java.awt.HeadlessException e) {
            System.out.println("Headless exception thrown when creating system viewer. I am unable to create system viewer.");
        }

        System.out.println("System is initialized.");

        outputWriter.printInitializationInfo(polymerSimulator);

        simulationRunner.setStepGenerator(makeInitialStepGenerator());

        simulationRunner.doEquilibrateAnnealIterations(jobParameters.getNumAnneals());

        simulationRunner.setStepGenerator(makeMainStepGenerator());

        for (int i = 0; i < jobParameters.getNumSurfaceTensionTrials(); i++) {
            doMeasurementTrial(simulationRunner, systemWidth, polymerSimulator);
            double[][] stress = StressFinder.calculateSpringStress(polymerSimulator);
            for (int j = 0; j < stress.length; j++) {
                double[] stressRow = stress[j];
                for (int k = 0; k < stressRow.length; k++) {
                    System.out.print(stressRow[k] + " ");
                }
                System.out.print("\n");
            }
            System.out.println();
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
