/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SystemAnalysis;

import Engine.SystemAnalyzer;

/**
 *
 * @author bmoths
 */
public class NonNeighborEnergy {

    static public double getAverageNonNeighborEnergy(SystemAnalyzer systemAnalyzer) {
        final double totalDensityEnergy = systemAnalyzer.densityEnergy();
        final double neighborDensityEnergy = getNeighborDensityEnergy(systemAnalyzer);
        final int numBeads = systemAnalyzer.getNumBeads();
        final double nonNeighborEnergy = (totalDensityEnergy - neighborDensityEnergy) / numBeads;
        return nonNeighborEnergy;
    }

    static private double getNeighborDensityEnergy(SystemAnalyzer systemAnalyzer) {
        double neighborEnergy = 0;
        for (int currentBead = 0; currentBead < systemAnalyzer.getNumBeads(); currentBead++) {
            final int rightNeighbor = systemAnalyzer.getNeighbor(currentBead, 1);
            if (rightNeighbor != -1) {
                neighborEnergy += systemAnalyzer.beadDensityEnergy(currentBead, rightNeighbor);
            }
        }
        return neighborEnergy;
    }

}
