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
import Engine.PolymerState.SystemGeometry.Interfaces.SystemGeometry;
import Engine.PolymerTopology.PolymerChain;
import Engine.PolymerTopology.PolymerCluster;
import FocusedSimulation.SimulationRunner.DoubleWithUncertainty;
import FocusedSimulation.SimulationRunner.SimulationRunnerParameters;
import FocusedSimulation.StatisticsTracker.TrackableVariable;
import Gui.SystemViewer;
import SGEManagement.SGEManager.Input;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    static public final class SystemParameters {

        public final int numChains;
        public final ExternalEnergyCalculator externalEnergyCalculator;
        public final double density;

        public SystemParameters(int numChains, ExternalEnergyCalculator externalEnergyCalculator, double density) {
            this.numChains = numChains;
            this.externalEnergyCalculator = externalEnergyCalculator;
            this.density = density;
        }

        public SystemParameters(Input input) {
            this.numChains = input.getNumChains();
            this.externalEnergyCalculator = input.getExternalEnergyCalculator();
            this.density = input.getDensity();
        }

        //<editor-fold defaultstate="collapsed" desc="equals and hashcode">
        @Override
        public int hashCode() {
            int hash = 7;
            hash = 59 * hash + this.numChains;
            hash = 59 * hash + externalEnergyCalculator.hashCode();
            hash = 59 * hash + (int) (Double.doubleToLongBits(this.density) ^ (Double.doubleToLongBits(this.density) >>> 32));
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final SystemParameters otherInputParameters = (SystemParameters) obj;
            final boolean externalEnergyCalculatorEquals = externalEnergyCalculator == null
                    ? otherInputParameters.externalEnergyCalculator == null
                    : externalEnergyCalculator.equals(otherInputParameters.externalEnergyCalculator);
            return numChains == otherInputParameters.numChains
                    && externalEnergyCalculatorEquals
                    && density == otherInputParameters.density;
        }
        //</editor-fold>        

    }

    static private final int defaultNumBeadsPerChain = 15;

    public static void main(String[] args) {

        final Input input = readInput(args);

        final SystemParameters inputParameters;
        inputParameters = new SystemParameters(input);

        final int jobNumber;
        jobNumber = input.getJobNumber();

        final SurfaceTensionFinder surfaceTensionFinder;
        try {
            surfaceTensionFinder = new SurfaceTensionFinder(jobNumber, inputParameters);
            surfaceTensionFinder.findSurfaceTension();
            surfaceTensionFinder.closeOutputWriter();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SurfaceTensionFinder.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("File not able to be opened");
        }
    }

    //<editor-fold defaultstate="expanded" desc="makePolymerSimulator">
    static private PolymerSimulator makePolymerSimulator(SystemParameters inputParameters) {
        final double interactionLength = 4;

        EnergeticsConstantsBuilder energeticsConstantsBuilder = makeEnergeticsConstantsBuilder(inputParameters.externalEnergyCalculator);
        GeometricalParameters geometricalParameters = new GeometricalParameters(interactionLength, energeticsConstantsBuilder);
        energeticsConstantsBuilder.setHardOverlapCoefficientFromParameters(geometricalParameters);

        final PolymerCluster polymerCluster = makePolymerCluster(inputParameters.numChains, inputParameters.density);
        final EnergeticsConstants energeticsConstants = energeticsConstantsBuilder.buildEnergeticsConstants();
        final SystemGeometry systemGeometry = makeSystemGeometry(polymerCluster.getNumBeadsIncludingWater(), geometricalParameters);

        return new PolymerSimulator(systemGeometry, polymerCluster, energeticsConstants);
    }

    static private PolymerCluster makePolymerCluster(int numChains, double density) {
        PolymerChain polymerChain = PolymerChain.makeChainStartingWithA(0, defaultNumBeadsPerChain);
        PolymerCluster polymerCluster = PolymerCluster.makeRepeatedChainCluster(polymerChain, numChains);
        polymerCluster.setConcentrationInWater(density);
        return polymerCluster;
    }

    static private EnergeticsConstantsBuilder makeEnergeticsConstantsBuilder(ExternalEnergyCalculator externalEnergyCalculator) {
        EnergeticsConstants.EnergeticsConstantsBuilder energeticsConstantsBuilder = EnergeticsConstantsBuilder.defaultEnergeticsConstantsBuilder();

        energeticsConstantsBuilder.setTemperature(1);
        energeticsConstantsBuilder.setAAOverlapCoefficient(0);
        energeticsConstantsBuilder.setBBOverlapCoefficient(-.06);
        energeticsConstantsBuilder.setSpringCoefficient(1);

        energeticsConstantsBuilder.setExternalEnergyCalculator(externalEnergyCalculator);

        return energeticsConstantsBuilder;
    }

    static private SystemGeometry makeSystemGeometry(double numBeadsIncludingWater, GeometricalParameters geometricalParameters) {
        AbstractGeometryBuilder systemGeometryBuilder = new PeriodicGeometry.PeriodicGeometryBuilder();

        final double aspectRatio = .1;
        systemGeometryBuilder.setDimension(2);
        systemGeometryBuilder.makeConsistentWith(numBeadsIncludingWater, geometricalParameters, aspectRatio);
        return systemGeometryBuilder.buildGeometry();
    }
//</editor-fold>

    static private MeasuredSurfaceTension getMeasuredSurfaceTensionFromWidth(DoubleWithUncertainty width, PolymerSimulator polymerSimulator) {
        final ExternalEnergyCalculator externalEnergyCalculator = polymerSimulator.getSystemAnalyzer().getEnergeticsConstants().getExternalEnergyCalculator();
        final double xEquilibriumPosition = externalEnergyCalculator.getxEquilibriumPosition();
        final double xSpringConstant = externalEnergyCalculator.getxSpringConstant();

        final double surfaceTension = xSpringConstant * (xEquilibriumPosition - width.getValue()); //should divide by two since there are two surfaces
        final double surfaceTensionError = Math.abs(xSpringConstant * width.getUncertainty()); //should divide by two since there are two surfaces
        return new MeasuredSurfaceTension(surfaceTension, surfaceTensionError);
    }

    public static int getNumBeadsPerChain() {
        return defaultNumBeadsPerChain;
    }

    private static Input readInput(String[] args) {
        if (args.length == 0) {
            return makeDefaultInput();
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

    public static Input makeDefaultInput() {
        int numChains;
        ExternalEnergyCalculator externalEnergyCalculator;
        double density;
        numChains = 100;//100
        ExternalEnergyCalculatorBuilder externalEnergyCalculatorBuilder = new ExternalEnergyCalculatorBuilder();
        externalEnergyCalculatorBuilder.setXPositionAndSpringConstant(50, 10.); //66 and 3
        externalEnergyCalculator = externalEnergyCalculatorBuilder.build();
        density = .05; //.15
        Input input = new Input(numChains, externalEnergyCalculator, density);
        return input;
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

    private final int numAnneals = 10; //50
    private final int numSurfaceTensionTrials = 150; //70
    private final int jobNumber;
    private final SystemParameters systemParameters;
    private final OutputWriter outputWriter;

    private SurfaceTensionFinder(int jobNumber, SystemParameters input) throws FileNotFoundException {
        this.jobNumber = jobNumber;
        this.systemParameters = input;
        outputWriter = new OutputWriter(this);
    }

    public void findSurfaceTension() {
        outputWriter.printParameters();
        PolymerSimulator polymerSimulator = makePolymerSimulator(systemParameters);
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

        simulationRunner.doEquilibrateAnnealIterations(numAnneals);

        for (int i = 0; i < numSurfaceTensionTrials; i++) {
            doMeasurementTrial(simulationRunner, systemWidth, polymerSimulator);
        }

        while (!simulationRunner.isConverged(systemWidth)) {
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

    //<editor-fold defaultstate="collapsed" desc="getters">
    public int getNumAnneals() {
        return numAnneals;
    }

    public int getNumSurfaceTensionTrials() {
        return numSurfaceTensionTrials;
    }

    public SystemParameters getInputParameters() {
        return systemParameters;
    }

    public int getJobNumber() {
        return jobNumber;
    }
    //</editor-fold>

}
