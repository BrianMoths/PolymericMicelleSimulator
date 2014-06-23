/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SystemAnalysis;

import Engine.PolymerSimulator;
import FocusedSimulation.StatisticsTracker.TrackableVariable;

/**
 *
 * @author bmoths
 */
public class CenterDensityTrackable implements TrackableVariable {

    final double sizeFraction;
    final NumCenterBeadsTrackable numCenterBeadsTrackable;

    public CenterDensityTrackable(double sizeFraction) {
        this.sizeFraction = sizeFraction;
        numCenterBeadsTrackable = new NumCenterBeadsTrackable(sizeFraction);
    }

    @Override
    public double getValue(PolymerSimulator polymerSimulator) {
        final double centerVolume = getCenterVolume(polymerSimulator);
        return numCenterBeadsTrackable.getValue(polymerSimulator) / centerVolume;
    }

    private double getCenterVolume(PolymerSimulator polymerSimulator) {
        final double totalVolume = polymerSimulator.getGeometry().getVolume();
        final double volumeFraction = getSizeFraction(polymerSimulator);
        return totalVolume * volumeFraction;
    }

    private double getSizeFraction(PolymerSimulator polymerSimulator) {
        double volumeFraction = 1;
        int numDimensions = polymerSimulator.getGeometry().getNumDimensions();
        for (int dimension = 0; dimension < numDimensions; dimension++) {
            volumeFraction *= sizeFraction;
        }
        return volumeFraction;
    }

}
