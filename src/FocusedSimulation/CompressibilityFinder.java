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
import Engine.SystemGeometry.AbstractGeometry;
import Engine.SystemGeometry.PeriodicGeometry;
import Engine.SystemGeometry.SystemGeometry;
import static FocusedSimulation.SurfaceTensionFinder.generateLengthStatistics;
import static FocusedSimulation.SurfaceTensionFinder.outputSurfaceTension;
import Gui.SystemViewer;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 *
 * @author bmoths
 */
public class CompressibilityFinder {

    public static void main(String[] args) {
        System.out.println("Finding compressibility");

        PolymerSimulator polymerSimulator = makePolymerSimulator();
        polymerSimulator.randomizePositions();
        SystemViewer systemViewer = new SystemViewer(polymerSimulator);
        systemViewer.setVisible(true);

        System.out.println("System is initialized.");

//        for (int i = 0; i < 20; i++) {
        System.out.println("Equilibrating System");

        polymerSimulator.equilibrateSystem();
        while (polymerSimulator.getSystemAnalyzer().findArea() < .95 * polymerSimulator.getGeometry().getVolume()) {
            polymerSimulator.equilibrateSystem();
            System.out.println("Area occupied by beads: " + String.format("%.4f", polymerSimulator.getSystemAnalyzer().findAreaAndPerimeter().area));
            System.out.println("Total area available: " + String.format("%.4f", polymerSimulator.getGeometry().getVolume()));
            polymerSimulator.anneal();
        }

//        while (true) {
//            polymerSimulator.doIterations(1000000);
//        }

//        System.out.println("System equilibrated.");
//        System.out.println("Gathering statistics to find equilibrium length.");
//        
//        final int numSamples = 100;
//        DescriptiveStatistics lengthStatistics = generateLengthStatistics(numSamples, polymerSimulator);
//        outputSurfaceTension(lengthStatistics, polymerSimulator);
//        polymerSimulator.anneal();
//        }
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
        PolymerCluster polymerCluster = PolymerCluster.makeRepeatedChainCluster(polymerChain, 100);// originally 100
        polymerCluster.setConcentrationInWater(.3);//originally .15
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
        final ExternalEnergyCalculator.ExternalEnergyCalculatorBuilder externalEnergyCalculatorBuilder = new ExternalEnergyCalculator.ExternalEnergyCalculatorBuilder();
        externalEnergyCalculatorBuilder.setxTension(-50.);
        externalEnergyCalculatorBuilder.setxQuadratic(.2);
        return externalEnergyCalculatorBuilder.build();
    }

    static private SystemGeometry makeSystemGeometry(PolymerCluster polymerCluster, SimulationParameters simulationParameters) {
        AbstractGeometry.AbstractGeometryBuilder systemGeometryBuilder = new PeriodicGeometry.PeriodicGeometryBuilder();

        systemGeometryBuilder.setDimension(2);
        systemGeometryBuilder.makeConsistentWith(polymerCluster, simulationParameters);
        return systemGeometryBuilder.buildGeometry();
    }
//</editor-fold>

}
