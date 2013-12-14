/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Tests;

import Engine.Energetics.EnergeticsConstants;
import Engine.Energetics.EnergeticsConstants.EnergeticsConstantsBuilder;
import Engine.Energetics.ExternalEnergyCalculator;
import Engine.PolymerTopology.PolymerCluster;
import Engine.PolymerSimulator;
import Engine.PolymerState.SystemGeometry.Implementations.PeriodicGeometry;
import Engine.PolymerState.SystemGeometry.Interfaces.SystemGeometry;
import static FocusedSimulation.SurfaceTensionFinder.generateLengthStatistics;
import Gui.SystemViewer;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 *
 * @author bmoths
 */
public class WallStepGeneratorTest {

    static public void main(String[] args) {
        PolymerSimulator polymerSimulator = makePolymerSimulator();

        SystemViewer systemViewer = new SystemViewer(polymerSimulator);
        systemViewer.setVisible(true);

        final int numSamples = 1000;
        DescriptiveStatistics lengthStatistics = generateLengthStatistics(numSamples, polymerSimulator);
        System.out.println(lengthStatistics.getMean());
    }

    private static PolymerSimulator makePolymerSimulator() {
        PolymerCluster polymerCluster = PolymerCluster.makeEmptyPolymerCluster();

        EnergeticsConstantsBuilder energeticsConstantsBuilder = EnergeticsConstantsBuilder.defaultEnergeticsConstantsBuilder();
        energeticsConstantsBuilder.setExternalEnergyCalculator(makeExternalEnergyCalculator());
        final EnergeticsConstants energeticsConstants = energeticsConstantsBuilder.buildEnergeticsConstants();

        SystemGeometry systemGeometry = PeriodicGeometry.defaultGeometry();


        return new PolymerSimulator(systemGeometry, polymerCluster, energeticsConstants);
    }

    private static ExternalEnergyCalculator makeExternalEnergyCalculator() {
        return new ExternalEnergyCalculator() {
            @Override
            public double calculateExternalEnergy(double[] rMax) {
                final double xMax = rMax[0];
                double energy;
                if (xMax > 10 && xMax < 20) {
                    energy = xMax;
                } else {
                    energy = 1000;
                }
                return energy;
            }

        };
    }

}
