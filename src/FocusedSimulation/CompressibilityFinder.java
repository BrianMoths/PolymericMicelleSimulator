/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FocusedSimulation;

import Engine.ExternalEnergyCalculator;
import Engine.EnergeticsConstants;
import Engine.EnergeticsConstants.EnergeticsConstantsBuilder;
import Engine.PolymerChain;
import Engine.PolymerCluster;
import Engine.PolymerSimulator;
import Engine.PolymerState.SystemGeometry.GeometricalParameters;
import Engine.PolymerState.SystemGeometry.Implementations.AbstractGeometry;
import Engine.PolymerState.SystemGeometry.Implementations.PeriodicGeometry;
import Engine.PolymerState.SystemGeometry.Interfaces.SystemGeometry;
import Gui.SystemViewer;

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

        polymerSimulator.equilibrate();
        while (polymerSimulator.getSystemAnalyzer().findArea() < .95 * polymerSimulator.getGeometry().getVolume()) {
            polymerSimulator.equilibrate();
            System.out.println("Area occupied by beads: " + String.format("%.4f", polymerSimulator.getSystemAnalyzer().findArea()));
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
        final double interactionLength = 4;

        final PolymerCluster polymerCluster = makePolymerCluster();

        EnergeticsConstantsBuilder energeticsConstantsBuilder = makeEnergeticsConstantsBuilder();
        final GeometricalParameters geometricalParameters = new GeometricalParameters(interactionLength, energeticsConstantsBuilder);
        energeticsConstantsBuilder.setHardOverlapCoefficientFromParameters(geometricalParameters);

        final EnergeticsConstants energeticsConstants = energeticsConstantsBuilder.buildEnergeticsConstants();
        final SystemGeometry systemGeometry = makeSystemGeometry(polymerCluster, geometricalParameters);
        return new PolymerSimulator(
                systemGeometry,
                polymerCluster,
                energeticsConstants);
    }

    static private PolymerCluster makePolymerCluster() {
        PolymerChain polymerChain = PolymerChain.makeChainStartingWithA(0, 15);
        PolymerCluster polymerCluster = PolymerCluster.makeRepeatedChainCluster(polymerChain, 100);// originally 100
        polymerCluster.setConcentrationInWater(.3);//originally .15
        return polymerCluster;
    }

    static private EnergeticsConstantsBuilder makeEnergeticsConstantsBuilder() {
        EnergeticsConstants.EnergeticsConstantsBuilder energeticsConstantsBuilder = new EnergeticsConstants.EnergeticsConstantsBuilder();

        energeticsConstantsBuilder.setTemperature(1);
        energeticsConstantsBuilder.setAAOverlapCoefficient(0);
        energeticsConstantsBuilder.setBBOverlapCoefficient(-.06);
        energeticsConstantsBuilder.setSpringCoefficient(1);

        final ExternalEnergyCalculator externalEnergyCalculator = makeExternalEnergyCalculator();
        energeticsConstantsBuilder.setExternalEnergyCalculator(externalEnergyCalculator);

        return energeticsConstantsBuilder;
    }

    static private ExternalEnergyCalculator makeExternalEnergyCalculator() {
        final ExternalEnergyCalculator.ExternalEnergyCalculatorBuilder externalEnergyCalculatorBuilder = new ExternalEnergyCalculator.ExternalEnergyCalculatorBuilder();
        externalEnergyCalculatorBuilder.setXPositionAndSpringConstant(66, 1.8);
        return externalEnergyCalculatorBuilder.build();
    }

    static private SystemGeometry makeSystemGeometry(PolymerCluster polymerCluster, GeometricalParameters geometricalParameters) {
        AbstractGeometry.AbstractGeometryBuilder systemGeometryBuilder = new PeriodicGeometry.PeriodicGeometryBuilder();

        systemGeometryBuilder.setDimension(2);
        systemGeometryBuilder.makeConsistentWith(polymerCluster.getNumBeadsIncludingWater(), geometricalParameters);
        return systemGeometryBuilder.buildGeometry();
    }
//</editor-fold>

}
