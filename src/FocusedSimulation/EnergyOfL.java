/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FocusedSimulation;

import Engine.Energetics.EnergeticsConstants;
import Engine.Energetics.EnergeticsConstants.EnergeticsConstantsBuilder;
import Engine.Energetics.ExternalEnergyCalculator;
import Engine.PolymerSimulator;
import Engine.PolymerState.SystemGeometry.GeometricalParameters;
import Engine.PolymerState.SystemGeometry.Implementations.AbstractGeometry;
import Engine.PolymerState.SystemGeometry.Implementations.PeriodicGeometry;
import Engine.PolymerState.SystemGeometry.Interfaces.SystemGeometry;
import Engine.PolymerTopology.PolymerChain;
import Engine.PolymerTopology.PolymerCluster;
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
        final double interactionLength = 4;

        final PolymerCluster polymerCluster = makePolymerCluster(concentration);

        EnergeticsConstants.EnergeticsConstantsBuilder energeticsConstantsBuilder = makeEnergeticsConstantsBuilder();
        final GeometricalParameters geometricalParameters = new GeometricalParameters(interactionLength, energeticsConstantsBuilder);
        energeticsConstantsBuilder.setHardOverlapCoefficientFromParameters(geometricalParameters);

        final EnergeticsConstants energeticsConstants = energeticsConstantsBuilder.buildEnergeticsConstants();
        final SystemGeometry systemGeometry = makeSystemGeometry(polymerCluster, geometricalParameters);

        return new PolymerSimulator(
                systemGeometry,
                polymerCluster,
                energeticsConstants);
    }

    static private PolymerCluster makePolymerCluster(double concentration) {
        PolymerChain polymerChain = PolymerChain.makeChainStartingWithA(0, 15);
        PolymerCluster polymerCluster = PolymerCluster.makeRepeatedChainCluster(polymerChain, 100);// originally 100
        polymerCluster.setConcentrationInWater(concentration);//originally .15
        return polymerCluster;
    }

    static private EnergeticsConstantsBuilder makeEnergeticsConstantsBuilder() {
        EnergeticsConstants.EnergeticsConstantsBuilder energeticsConstantsBuilder = EnergeticsConstantsBuilder.defaultEnergeticsConstantsBuilder();

        energeticsConstantsBuilder.setTemperature(1);
        energeticsConstantsBuilder.setAAOverlapCoefficient(0);
        energeticsConstantsBuilder.setBBOverlapCoefficient(-.06);
        energeticsConstantsBuilder.setSpringConstant(2);

        final ExternalEnergyCalculator externalEnergyCalculator = makeExternalEnergyCalculator();
        energeticsConstantsBuilder.setExternalEnergyCalculator(externalEnergyCalculator);

        return energeticsConstantsBuilder;
    }

    static private ExternalEnergyCalculator makeExternalEnergyCalculator() {
        final ExternalEnergyCalculator.ExternalEnergyCalculatorBuilder externalEnergyCalculatorBuilder = new ExternalEnergyCalculator.ExternalEnergyCalculatorBuilder();
        externalEnergyCalculatorBuilder.setXPositionAndSpringConstant(0, 0); //was 50, .2
        return externalEnergyCalculatorBuilder.build();
    }

    static private SystemGeometry makeSystemGeometry(PolymerCluster polymerCluster, GeometricalParameters geometricalParameters) {
        AbstractGeometry.AbstractGeometryBuilder systemGeometryBuilder = new PeriodicGeometry.PeriodicGeometryBuilder();

        systemGeometryBuilder.setDimension(2);
        systemGeometryBuilder.makeConsistentWith(polymerCluster.getNumBeadsIncludingWater(), geometricalParameters);
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
        System.out.println("Length of System:  " + Double.toString(polymerSimulator.getGeometry().getSizeOfDimension(0)));
    }

}
