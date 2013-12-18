/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FocusedSimulation;

import Engine.Energetics.EnergeticsConstants;
import Engine.Energetics.EnergeticsConstants.EnergeticsConstantsBuilder;
import Engine.Energetics.ExternalEnergyCalculator;
import Engine.Energetics.ExternalEnergyCalculator.ExternalEnergyCalculatorBuilder;
import Engine.PolymerTopology.PolymerChain;
import Engine.PolymerTopology.PolymerCluster;
import Engine.PolymerSimulator;
import Engine.SystemAnalyzer;
import Engine.PolymerState.SystemGeometry.Implementations.AbstractGeometry.AbstractGeometryBuilder;
import Engine.PolymerState.SystemGeometry.GeometricalParameters;
import Engine.PolymerState.SystemGeometry.Implementations.PeriodicGeometry;
import Engine.PolymerState.SystemGeometry.Interfaces.SystemGeometry;
import Gui.SystemViewer;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

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

    static public final class InputParameters {

        public final int numChains;
        public final ExternalEnergyCalculator externalEnergyCalculator;
        public final double density;

        public InputParameters(int numChains, ExternalEnergyCalculator externalEnergyCalculator, double density) {
            this.numChains = numChains;
            this.externalEnergyCalculator = externalEnergyCalculator;
            this.density = density;
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
            final InputParameters otherInputParameters = (InputParameters) obj;
            final boolean externalEnergyCalculatorEquals = externalEnergyCalculator == null
                    ? otherInputParameters.externalEnergyCalculator == null
                    : externalEnergyCalculator.equals(otherInputParameters.externalEnergyCalculator);
            return numChains == otherInputParameters.numChains
                    && externalEnergyCalculatorEquals
                    && density == otherInputParameters.density;
        }
        //</editor-fold>        

    }

    static private final int numBeadsPerChain = 15;

    public static void main(String[] args) {

        final InputParameters inputParameters;
        inputParameters = parseInput(args);

        final SurfaceTensionFinder surfaceTensionFinder;
        try {
//            surfaceTensionFinder = new SurfaceTensionFinder(input.externalEnergyCalculator, input.density, input.numChains);
            surfaceTensionFinder = new SurfaceTensionFinder(inputParameters);
            surfaceTensionFinder.findSurfaceTension();
            surfaceTensionFinder.closeOutputWriter();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SurfaceTensionFinder.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("File not able to be opened");
        }
    }

    static private InputParameters parseInput(String[] args) {
        final int numChains;
        final ExternalEnergyCalculator externalEnergyCalculator;
        final double density;

        if (args.length == 4) {
            numChains = Integer.parseInt(args[0]);
            final double xSpringConstant = Double.parseDouble(args[1]);
            final double xEquilibriumPosition = Double.parseDouble(args[2]);
            ExternalEnergyCalculatorBuilder externalEnergyCalculatorBuilder = new ExternalEnergyCalculatorBuilder();
            externalEnergyCalculatorBuilder.setXPositionAndSpringConstant(xEquilibriumPosition, xSpringConstant);
            externalEnergyCalculator = externalEnergyCalculatorBuilder.build();
            density = Double.parseDouble(args[3]);
        } else {
            numChains = 100;//100
            final ExternalEnergyCalculatorBuilder externalEnergyCalculatorBuilder = new ExternalEnergyCalculatorBuilder();
            externalEnergyCalculatorBuilder.setXPositionAndSpringConstant(66, 3.); //66 and .2 or 1.8
            externalEnergyCalculator = externalEnergyCalculatorBuilder.build();
            density = .05; //.15
        }

        return new InputParameters(numChains, externalEnergyCalculator, density);
    }

    static public DescriptiveStatistics generateLengthStatistics(int numSamples, PolymerSimulator polymerSimulator) {
        final int iterationsPerSample = 100000;
        int numSamplesTaken = 0;
        SystemAnalyzer systemAnalyzer = polymerSimulator.getSystemAnalyzer();

        DescriptiveStatistics lengthStatistics = new DescriptiveStatistics(numSamples);
        while (numSamplesTaken < numSamples) {
//            System.out.println(Integer.toString(100 * numSamplesTaken / numSamples) + "% done collecting statisitcs.");
            polymerSimulator.doIterations(iterationsPerSample);
            lengthStatistics.addValue(systemAnalyzer.getSystemGeometry().getSizeOfDimension(0));
            numSamplesTaken++;
        }

        return lengthStatistics;
    }

    static public MeasuredSurfaceTension calculateSurfaceTension(DescriptiveStatistics lengthStatistics, PolymerSimulator polymerSimulator) {
        final long numLengthSamples = lengthStatistics.getN();

        final double averageLength = lengthStatistics.getMean();
        final double lengthStandardDeviation = lengthStatistics.getStandardDeviation();

        final ExternalEnergyCalculator externalEnergyCalculator = polymerSimulator.getSystemAnalyzer().getEnergeticsConstants().getExternalEnergyCalculator();
        final double xEquilibriumPosition = externalEnergyCalculator.getxEquilibriumPosition();
        final double xSpringConstant = externalEnergyCalculator.getxSpringConstant();

        final double surfaceTension = xSpringConstant * (xEquilibriumPosition - averageLength); //should divide by two since there are two surfaces
        final double surfaceTensionStandardDeviation = xSpringConstant * lengthStandardDeviation;
        final double surfaceTensionStandardError = surfaceTensionStandardDeviation / Math.sqrt(numLengthSamples - 1);
        return new MeasuredSurfaceTension(surfaceTension, surfaceTensionStandardError);
    }

    //<editor-fold defaultstate="expanded" desc="makePolymerSimulator">
    static private PolymerSimulator makePolymerSimulator(InputParameters inputParameters) {
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
        PolymerChain polymerChain = PolymerChain.makeChainStartingWithA(0, numBeadsPerChain);
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

    public static int getNumBeadsPerChain() {
        return numBeadsPerChain;
    }

    private final int numAnneals = 10; //50
    private final int numSurfaceTensionTrials = 70; //70
    private final InputParameters inputParameters;
    private final OutputWriter outputWriter;
    private final List<MeasuredSurfaceTension> measuredSurfaceTensions;

    private SurfaceTensionFinder(InputParameters input) throws FileNotFoundException {
        this.inputParameters = input;
        outputWriter = new OutputWriter(this);
        measuredSurfaceTensions = new ArrayList<>();
    }

    public void findSurfaceTension() {
        outputWriter.printParameters();
        PolymerSimulator polymerSimulator = makePolymerSimulator(inputParameters);
        polymerSimulator.columnRandomizePositions();
        try {
            SystemViewer systemViewer = new SystemViewer(polymerSimulator);
            systemViewer.setVisible(true);
        } catch (java.awt.HeadlessException e) {
            System.out.println("Headless exception thrown when creating system viewer. I am unable to create system viewer.");
        }

        System.out.println("System is initialized.");

        for (int i = 0; i < numAnneals; i++) {
            polymerSimulator.doIterations(3000000);//3000000
            polymerSimulator.anneal();
            System.out.println("equilibrate anneal iteration done.");
        }

        for (int i = 0; i < numSurfaceTensionTrials; i++) {
            doSurfaceTensionIteration(polymerSimulator);
        }

        while (!isConverged(measuredSurfaceTensions)) {
            doSurfaceTensionIteration(polymerSimulator);
        }

        outputWriter.printFinalOutput(polymerSimulator);
    }

    private void doSurfaceTensionIteration(PolymerSimulator polymerSimulator) {
        System.out.println("Equilibrating System");

        polymerSimulator.anneal();
        polymerSimulator.equilibrate();

        System.out.println("System equilibrated.");
        System.out.println("Gathering statistics to find equilibrium length.");

        final int numSamples = 100;
        DescriptiveStatistics lengthStatistics = generateLengthStatistics(numSamples, polymerSimulator);
        MeasuredSurfaceTension measuredSurfaceTension = calculateSurfaceTension(lengthStatistics, polymerSimulator);
        outputWriter.printSurfaceTension(measuredSurfaceTension);
        measuredSurfaceTensions.add(measuredSurfaceTension);
    }

    private boolean isConverged(List<MeasuredSurfaceTension> measuredSurfaceTensions) {
        final int windowSize = 10;
        final int numMeasurements = measuredSurfaceTensions.size();
        if (numMeasurements < windowSize) {
            return false;
        }
        int comparisonCount = 0;
        for (int offset = 1; offset < windowSize; offset++) {
            final int comparison = measuredSurfaceTensions.get(numMeasurements - offset).surfaceTension > measuredSurfaceTensions.get(numMeasurements - offset - 1).surfaceTension ? 1 : -1;
            comparisonCount += comparison;
        }
        return Math.abs(comparisonCount) < windowSize / 3;
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

    public InputParameters getInputParameters() {
        return inputParameters;
    }
    //</editor-fold>

}
