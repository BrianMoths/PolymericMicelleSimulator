/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FocusedSimulation;

import Engine.ExternalEnergyCalculator;
import Engine.PhysicalConstants;
import Engine.PolymerChain;
import Engine.PolymerCluster;
import Engine.PolymerSimulator;
import Engine.SimulationParameters;
import Engine.SystemAnalyzer;
import Engine.SystemGeometry.AbstractGeometry.AbstractGeometryBuilder;
import Engine.SystemGeometry.PeriodicGeometry;
import Engine.SystemGeometry.SystemGeometry;
import SystemAnalysis.GeometryAnalyzer.AreaPerimeter;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 *
 * @author bmoths
 */
public class FindSurfaceTension {

    public static void main(String[] args) {
        System.out.println("Finding surface tension.");

        PolymerSimulator polymerSimulator = makePolymerSimulator();
        polymerSimulator.columnRandomizePositions();

        System.out.println("System is initialized.");

        System.out.println("Equilibrating System");

        SystemAnalyzer systemAnalyzer = polymerSimulator.getSystemAnalyzer();
        while (!systemAnalyzer.isEquilibrated()) {
            polymerSimulator.doIterations(10000);
            final double energy = polymerSimulator.getEnergy();
            final AreaPerimeter areaPerimeter = systemAnalyzer.findAreaAndPerimeter();
            systemAnalyzer.addPerimeterAreaEnergySnapshot(areaPerimeter.perimeter, areaPerimeter.area, energy);
        }

        System.out.println("System equilibrated.");

        final int iterationsPerSample = 100000;
        final int numSamples = 100;

        System.out.println("Gathering statistics to find equilibrium length.");

        DescriptiveStatistics lengthStatistics = new DescriptiveStatistics(numSamples);
        int numSamplesTaken = 0;
        while (numSamplesTaken < numSamples) {
            System.out.println(Integer.toString(100 * numSamplesTaken / numSamples) + "% done collecting statisitcs.");
            polymerSimulator.doIterations(iterationsPerSample);
            lengthStatistics.addValue(systemAnalyzer.getSystemGeometry().getRMax()[0]);
            numSamplesTaken++;
        }

        final double averageLength = lengthStatistics.getMean();
        final double lengthStandardDeviation = lengthStatistics.getStandardDeviation();

        System.out.println("Average length found: " + Double.toString(averageLength));

        final ExternalEnergyCalculator externalEnergyCalculator = polymerSimulator.getPhysicalConstants().getExternalEnergyCalculator();
        final double xTension = externalEnergyCalculator.getxTension();
        final double xQuadratic = externalEnergyCalculator.getxQuadratic();

        final double surfaceTension = -xTension - 2 * xQuadratic * averageLength;
        final double surfaceTensionStandardDeviation = 2 * xQuadratic * lengthStandardDeviation;
        final double surfaceTensionStandardError = surfaceTensionStandardDeviation / Math.sqrt(numSamples - 1);

        System.out.println("Surface Tension found: " + Double.toString(surfaceTension) + "+/-" + Double.toString(surfaceTensionStandardError));
    }

    //<editor-fold defaultstate="collapsed" desc="makePolymerSimulator">
    static private PolymerSimulator makePolymerSimulator() {
        PolymerCluster polymerCluster = makePolymerCluster();
        PhysicalConstants physicalConstants = makePhysicalConstants();

        SimulationParameters simulationParameters = new SimulationParameters(physicalConstants.idealStepLength(), 4);

        simulationParameters = simulationParameters.makeParametersFromPhysicalConstants(physicalConstants);
        physicalConstants = physicalConstants.getPhysicalConstantsFromParameters(simulationParameters);

        SystemGeometry systemGeometry = makeSystemGeometry(polymerCluster, simulationParameters);

        return new PolymerSimulator(
                systemGeometry,
                polymerCluster,
                physicalConstants);
    }

    static private PolymerCluster makePolymerCluster() {
        PolymerChain polymerChain = PolymerChain.makeChainStartingWithA(0, 15);
        PolymerCluster polymerCluster = PolymerCluster.makeRepeatedChainCluster(polymerChain, 100);
        polymerCluster.setConcentrationInWater(.15);
        return polymerCluster;
    }

    static private PhysicalConstants makePhysicalConstants() {
        PhysicalConstants.PhysicalConstantsBuilder physicalConstantsBuilder = new PhysicalConstants.PhysicalConstantsBuilder();

        physicalConstantsBuilder.setTemperature(1);
        physicalConstantsBuilder.setAAOverlapCoefficient(0);
        physicalConstantsBuilder.setBBOverlapCoefficient(-.06);
        physicalConstantsBuilder.setSpringCoefficient(1);

        return physicalConstantsBuilder.buildPhysicalConstants();
    }

    static private SystemGeometry makeSystemGeometry(PolymerCluster polymerCluster, SimulationParameters simulationParameters) {
        AbstractGeometryBuilder systemGeometryBuilder = new PeriodicGeometry.PeriodicGeometryBuilder();

        systemGeometryBuilder.setDimension(2);
        systemGeometryBuilder.makeConsistentWith(polymerCluster, simulationParameters);
        return systemGeometryBuilder.buildGeometry();
    }
//</editor-fold>
}
