/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FocusedSimulation.homopolymer.bulkproperties;

import Engine.PolymerSimulator;
import Engine.PolymerState.SystemGeometry.Interfaces.SystemGeometry;
import Engine.SystemAnalyzer;
import FocusedSimulation.simulationrunner.SimulationRunner;
import FocusedSimulation.simulationrunner.StatisticsTracker.TrackableVariable;

/**
 *
 * @author bmoths
 */
public class LeftBeadsTrackable implements TrackableVariable {

    private final double volumeFraction;
    private final double leftRegionVolume;
    private final double xPosition;

    public LeftBeadsTrackable(double volumeFraction, SimulationRunner simulationRunner) {
        this.volumeFraction = volumeFraction;
        SystemGeometry systemGeometry = simulationRunner.getPolymerSimulator().getGeometry();
        leftRegionVolume = systemGeometry.getVolume() * volumeFraction;
        xPosition = systemGeometry.getSizeOfDimension(0) * volumeFraction;
    }

    @Override
    public double getValue(PolymerSimulator polymerSimulator) {
        final SystemAnalyzer systemAnalyzer = polymerSimulator.getSystemAnalyzer();
        final int numBeads = systemAnalyzer.getNumBeads();
        int numLeftBeads = 0;
        for (int bead = 0; bead < numBeads; bead++) {
            if (systemAnalyzer.getBeadPositionComponent(bead, 0) < xPosition) {
                numLeftBeads++;
            }
        }
        return numLeftBeads;
    }

    public double getVolumeFraction() {
        return volumeFraction;
    }

    public double getLeftRegionVolume() {
        return leftRegionVolume;
    }

    public double getxPosition() {
        return xPosition;
    }

}
