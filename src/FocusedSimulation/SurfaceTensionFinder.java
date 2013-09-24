/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FocusedSimulation;

import Engine.ExternalEnergyCalculator;
import Engine.ExternalEnergyCalculator.ExternalEnergyCalculatorBuilder;
import Engine.PhysicalConstants;
import Engine.PolymerChain;
import Engine.PolymerCluster;
import Engine.PolymerSimulator;
import Engine.SimulationParameters;
import Engine.SystemAnalyzer;
import Engine.SystemGeometry.AbstractGeometry.AbstractGeometryBuilder;
import Engine.SystemGeometry.PeriodicGeometry;
import Engine.SystemGeometry.SystemGeometry;
import Gui.SystemViewer;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 *
 * @author bmoths
 */
public class SurfaceTensionFinder {

    public static void main(String[] args) {
        System.out.println("Finding surface tension.");

        PolymerSimulator polymerSimulator = makePolymerSimulator();
        polymerSimulator.columnRandomizePositions();
        SystemViewer systemViewer = new SystemViewer(polymerSimulator);
        systemViewer.setVisible(true);

        System.out.println("System is initialized.");

        for (int i = 0; i < 20; i++) {
            System.out.println("Equilibrating System");

            polymerSimulator.equilibrateSystem();

            System.out.println("System equilibrated.");
            System.out.println("Gathering statistics to find equilibrium length.");

            final int numSamples = 100;
            DescriptiveStatistics lengthStatistics = generateLengthStatistics(numSamples, polymerSimulator);
            outputSurfaceTension(lengthStatistics, polymerSimulator);
            polymerSimulator.anneal();
        }
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
        PolymerCluster polymerCluster = PolymerCluster.makeRepeatedChainCluster(polymerChain, 200);// originally 100
        polymerCluster.setConcentrationInWater(.03);//originally .15
        return polymerCluster;
    }

    static private PhysicalConstants makePhysicalConstants() {
        PhysicalConstants.PhysicalConstantsBuilder physicalConstantsBuilder = new PhysicalConstants.PhysicalConstantsBuilder();

        physicalConstantsBuilder.setTemperature(1);
        physicalConstantsBuilder.setAAOverlapCoefficient(0);
        physicalConstantsBuilder.setBBOverlapCoefficient(-.06);
        physicalConstantsBuilder.setSpringCoefficient(1);

        final ExternalEnergyCalculator externalEnergyCalculator = makeExternalEnergyCalculator();
        physicalConstantsBuilder.setExternalEnergyCalculator(externalEnergyCalculator);

        return physicalConstantsBuilder.buildPhysicalConstants();
    }

    static private ExternalEnergyCalculator makeExternalEnergyCalculator() {
        final ExternalEnergyCalculatorBuilder externalEnergyCalculatorBuilder = new ExternalEnergyCalculatorBuilder();
        externalEnergyCalculatorBuilder.setxTension(50.);
        externalEnergyCalculatorBuilder.setxQuadratic(.2);
        return externalEnergyCalculatorBuilder.build();
    }

    static private SystemGeometry makeSystemGeometry(PolymerCluster polymerCluster, SimulationParameters simulationParameters) {
        AbstractGeometryBuilder systemGeometryBuilder = new PeriodicGeometry.PeriodicGeometryBuilder();

        systemGeometryBuilder.setDimension(2);
        systemGeometryBuilder.makeConsistentWith(polymerCluster, simulationParameters);
        return systemGeometryBuilder.buildGeometry();
    }
//</editor-fold>

    static public DescriptiveStatistics generateLengthStatistics(int numSamples, PolymerSimulator polymerSimulator) {
        final int iterationsPerSample = 100000;
        int numSamplesTaken = 0;
        SystemAnalyzer systemAnalyzer = polymerSimulator.getSystemAnalyzer();

        DescriptiveStatistics lengthStatistics = new DescriptiveStatistics(numSamples);
        while (numSamplesTaken < numSamples) {
//            System.out.println(Integer.toString(100 * numSamplesTaken / numSamples) + "% done collecting statisitcs.");
            polymerSimulator.doIterations(iterationsPerSample);
            lengthStatistics.addValue(systemAnalyzer.getSystemGeometry().getRMax()[0]);
            numSamplesTaken++;
        }

        return lengthStatistics;
    }

    static public void outputSurfaceTension(DescriptiveStatistics lengthStatistics, PolymerSimulator polymerSimulator) {
        final long numSamples = lengthStatistics.getN();


        System.out.println("numSamples: " + Double.toString(numSamples));

//        double[] lengths = lengthStatistics.getValues();
//        double average = 0;
//        int numSample = lengths.length;
//        for (int i = 0; i < lengths.length; i++) {
//            double length = lengths[i];
//            average += length;            
//        }        
//        average /= numSample;
//        
//        System.out.println("numSample: " + Integer.toString(numSample));
//        System.out.println("average: " + Double.toString(average));

        final double averageLength = lengthStatistics.getMean();
        final double lengthStandardDeviation = lengthStatistics.getStandardDeviation();

        final ExternalEnergyCalculator externalEnergyCalculator = polymerSimulator.getPhysicalConstants().getExternalEnergyCalculator();
        final double xTension = externalEnergyCalculator.getxTension();
        final double xQuadratic = externalEnergyCalculator.getxQuadratic();

        final double surfaceTension = -xTension - 2 * xQuadratic * averageLength; //should divide by two since there are two surfaces
        final double surfaceTensionStandardDeviation = 2 * xQuadratic * lengthStandardDeviation;
        final double surfaceTensionStandardError = surfaceTensionStandardDeviation / Math.sqrt(numSamples - 1);
        System.out.println("Surface Tension found: " + Double.toString(surfaceTension) + "+/-" + Double.toString(surfaceTensionStandardError));
    }

}
