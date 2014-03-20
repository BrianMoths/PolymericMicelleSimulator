/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FocusedSimulation;

import Engine.Energetics.EnergeticsConstants;
import Engine.Energetics.EnergeticsConstants.EnergeticsConstantsBuilder;
import Engine.Energetics.ExternalEnergyCalculator.ExternalEnergyCalculatorBuilder;
import Engine.PolymerTopology.PolymerChain;
import Engine.PolymerTopology.PolymerCluster;
import Engine.PolymerSimulator;
import Engine.PolymerState.SystemGeometry.GeometricalParameters;
import Engine.PolymerState.SystemGeometry.Implementations.AbstractGeometry.AbstractGeometryBuilder;
import Engine.PolymerState.SystemGeometry.Implementations.PeriodicGeometry;
import Engine.PolymerState.SystemGeometry.Interfaces.SystemGeometry;
import Engine.SimulationStepping.StepGenerators.CompoundStepGenerators.GeneralStepGenerator;
import Engine.SimulationStepping.StepTypes.StepType;
import Engine.SystemAnalyzer;
//import static FocusedSimulation.SurfaceTensionFinder.generateLengthStatistics;
import Gui.SystemViewer;
import java.util.EnumMap;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 *
 * @author bmoths
 */
public class IdealGasSimulation {

    static private final int numBeadsPerChain = 1;
    static private final int numBeads = 4;
    static private final double density = .01;

    public static void main(String[] args) {

        final IdealGasSimulation idealGasSimulation;
        idealGasSimulation = new IdealGasSimulation();
        idealGasSimulation.findVolume(.1);

    }

    static public DescriptiveStatistics generateLengthStatistics(int numSamples, PolymerSimulator polymerSimulator) {
        final int iterationsPerSample = 10000;//1000000
        int numSamplesTaken = 0;
        SystemAnalyzer systemAnalyzer = polymerSimulator.getSystemAnalyzer();

        DescriptiveStatistics lengthStatistics = new DescriptiveStatistics(numSamples);
        while (numSamplesTaken < numSamples) {
            polymerSimulator.doIterations(iterationsPerSample);
            lengthStatistics.addValue(systemAnalyzer.getSystemGeometry().getSizeOfDimension(0));
            numSamplesTaken++;
        }

        return lengthStatistics;
    }

    //<editor-fold defaultstate="expanded" desc="makePolymerSimulator">
    static private PolymerSimulator makePolymerSimulator(double pressure) {
        EnergeticsConstants energeticsConstants = makeEnergeticsConstants(pressure);
        GeometricalParameters geometricalParameters = new GeometricalParameters(1., 1.);

        final PolymerCluster polymerCluster = makePolymerCluster(numBeads, density);
        final SystemGeometry systemGeometry = makeSystemGeometry(polymerCluster.getNumBeadsIncludingWater(), geometricalParameters);
        final PolymerSimulator polymerSimulator = new PolymerSimulator(systemGeometry, polymerCluster, energeticsConstants);
        final EnumMap<StepType, Double> stepWeights = GeneralStepGenerator.makeDefaultWeights();
        stepWeights.put(StepType.SINGLE_WALL_RESIZE, .5);
        polymerSimulator.setStepGenerator(new GeneralStepGenerator(stepWeights));
        return polymerSimulator;
    }

    static private PolymerCluster makePolymerCluster(int numChains, double density) {
        PolymerChain polymerChain = PolymerChain.makeChainStartingWithA(0, numBeadsPerChain);
        PolymerCluster polymerCluster = PolymerCluster.makeRepeatedChainCluster(polymerChain, numChains);
        polymerCluster.setConcentrationInWater(density);
        return polymerCluster;
    }

    static private SystemGeometry makeSystemGeometry(double numBeadsIncludingWater, GeometricalParameters geometricalParameters) {
        AbstractGeometryBuilder systemGeometryBuilder = new PeriodicGeometry.PeriodicGeometryBuilder();

        final double aspectRatio = 1.;
        systemGeometryBuilder.setDimension(2);
        systemGeometryBuilder.makeConsistentWith(numBeadsIncludingWater, geometricalParameters, aspectRatio);
        if (systemGeometryBuilder.getFullRMaxCopy()[0] == 0) {
            systemGeometryBuilder.setDimensionSize(0, 10);
            systemGeometryBuilder.setDimensionSize(1, 10);
        }
        return systemGeometryBuilder.buildGeometry();
    }
//</editor-fold>

    static private EnergeticsConstants makeEnergeticsConstants(double pressure) {
        EnergeticsConstantsBuilder energeticsConstantsBuilder = EnergeticsConstantsBuilder.zeroEnergeticsConstantsBuilder();
        energeticsConstantsBuilder.setTemperature(1);
        ExternalEnergyCalculatorBuilder externalEnergyCalculatorBuilder = new ExternalEnergyCalculatorBuilder();
        externalEnergyCalculatorBuilder.setPressure(pressure);
        energeticsConstantsBuilder.setExternalEnergyCalculator(externalEnergyCalculatorBuilder.build());
        return energeticsConstantsBuilder.buildEnergeticsConstants();
    }

    private int numSurfaceTensionTrials = 1000;

    private IdealGasSimulation() {
    }

    private void findVolume(double pressure) {
        PolymerSimulator polymerSimulator = makePolymerSimulator(pressure);
        polymerSimulator.columnRandomizePositions();

//        final int numIterations = 5000000;
//        final long beginTime = System.currentTimeMillis();
//        polymerSimulator.doIterations(numIterations);
//        final long timeTaken = System.currentTimeMillis() - beginTime;
//        System.out.println("Total milleseconds taken for " + numIterations + " iterations: " + timeTaken);
//        System.out.println("Microseconds per iteration: " + (1000. * (double) timeTaken / numIterations));

        try {
            SystemViewer systemViewer = new SystemViewer(polymerSimulator);
            systemViewer.setVisible(true);
        } catch (java.awt.HeadlessException e) {
            System.out.println("Headless exception thrown when creating system viewer. I am unable to create system viewer.");
        }

        System.out.println("System Height " + polymerSimulator.getSystemAnalyzer().getSystemGeometry().getSizeOfDimension(1));

        System.out.println("System is initialized.");

        for (int i = 0; i < numSurfaceTensionTrials; i++) {
            System.out.println("Equilibrating System");
            polymerSimulator.equilibrate();

            System.out.println("System equilibrated.");
            System.out.println("Gathering statistics to find equilibrium length.");

            final int numSamples = 10000;//100
            DescriptiveStatistics lengthStatistics = generateLengthStatistics(numSamples, polymerSimulator);
            System.out.println("Pressure times Volume found is: " + pressure * polymerSimulator.getSystemAnalyzer().getSystemGeometry().getSizeOfDimension(1) * lengthStatistics.getMean());
        }

    }

}
