/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FocusedSimulation;

import Engine.ExternalEnergyCalculator;
import Engine.EnergeticsConstants;
import Engine.PolymerChain;
import Engine.PolymerCluster;
import Engine.PolymerSimulator;
import Engine.SystemGeometry.GeometricalParameters;
import Engine.SystemGeometry.AbstractGeometry;
import Engine.SystemGeometry.PeriodicGeometry;
import Engine.SystemGeometry.SystemGeometry;
import Gui.SystemViewer;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 *
 * @author brian
 */
public class EnergyOfL {

    public static void main(String[] args) {
        System.out.println("Finding energies for different lengths.");

        for (double concentration = .1; concentration < .25; concentration += .01) {

            PolymerSimulator polymerSimulator = makePolymerSimulator(concentration);
            polymerSimulator.columnRandomizePositions();
            SystemViewer systemViewer = new SystemViewer(polymerSimulator);
            systemViewer.setVisible(true);

            System.out.println("System is initialized.");
            polymerSimulator.equilibrate();

            System.out.println("System equilibrated.");
            System.out.println("Gathering statistics to find equilibrium length.");

            final int numSamples = 100;
            DescriptiveStatistics energyStatistics = generateEnergyStatistics(numSamples, polymerSimulator);
            outputAverageEnergy(energyStatistics, polymerSimulator);
        }
    }

    //<editor-fold defaultstate="collapsed" desc="makePolymerSimulator">
    static private PolymerSimulator makePolymerSimulator(double concentration) {
        PolymerCluster polymerCluster = makePolymerCluster(concentration);
        EnergeticsConstants physicalConstants = makePhysicalConstants();

        GeometricalParameters simulationParameters = new GeometricalParameters(physicalConstants.idealStepLength(), 4);

        simulationParameters = simulationParameters.makeParametersFromPhysicalConstants(physicalConstants);
        physicalConstants = physicalConstants.getPhysicalConstantsFromParameters(simulationParameters);

        SystemGeometry systemGeometry = makeSystemGeometry(polymerCluster, simulationParameters);

        return new PolymerSimulator(
                systemGeometry,
                polymerCluster,
                physicalConstants);
    }

    static private PolymerCluster makePolymerCluster(double concentration) {
        PolymerChain polymerChain = PolymerChain.makeChainStartingWithA(0, 15);
        PolymerCluster polymerCluster = PolymerCluster.makeRepeatedChainCluster(polymerChain, 100);// originally 100
        polymerCluster.setConcentrationInWater(concentration);//originally .15
        return polymerCluster;
    }

    static private EnergeticsConstants makePhysicalConstants() {
        EnergeticsConstants.PhysicalConstantsBuilder physicalConstantsBuilder = new EnergeticsConstants.PhysicalConstantsBuilder();

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
        externalEnergyCalculatorBuilder.setxTension(0.); //was -50
        externalEnergyCalculatorBuilder.setxQuadratic(.0); //was .2
        return externalEnergyCalculatorBuilder.build();
    }

    static private SystemGeometry makeSystemGeometry(PolymerCluster polymerCluster, GeometricalParameters simulationParameters) {
        AbstractGeometry.AbstractGeometryBuilder systemGeometryBuilder = new PeriodicGeometry.PeriodicGeometryBuilder();

        systemGeometryBuilder.setDimension(2);
        systemGeometryBuilder.makeConsistentWith(polymerCluster, simulationParameters);
        return systemGeometryBuilder.buildGeometry();
    }
//</editor-fold>

    static public DescriptiveStatistics generateEnergyStatistics(int numSamples, PolymerSimulator polymerSimulator) {
        final int iterationsPerSample = 100;//10000
        int numSamplesTaken = 0;

        DescriptiveStatistics energyStatistics = new DescriptiveStatistics(numSamples);
        while (numSamplesTaken < numSamples) {
            polymerSimulator.doIterations(iterationsPerSample);
            energyStatistics.addValue(polymerSimulator.getEnergy());
            numSamplesTaken++;
        }

        return energyStatistics;
    }

    static public void outputAverageEnergy(DescriptiveStatistics energyStatistics, PolymerSimulator polymerSimulator) {
        final double averageEnergy = energyStatistics.getMean();
        System.out.println("Average Energy found: " + Double.toString(averageEnergy));
        System.out.println("Length of System:  " + Double.toString(polymerSimulator.getGeometry().getRMax()[0]));
    }

}
