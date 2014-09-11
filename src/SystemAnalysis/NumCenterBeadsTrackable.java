/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SystemAnalysis;

import Engine.PolymerSimulator;
import Engine.PolymerState.SystemGeometry.Interfaces.SystemGeometry;
import FocusedSimulation.simulationrunner.StatisticsTracker.TrackableVariable;
import SystemAnalysis.AreaPerimeter.rectangleareaperimeter.BeadRectangle;

/**
 *
 * @author bmoths
 */
public class NumCenterBeadsTrackable implements TrackableVariable {

    static private class Limits {

        final double[] lowerLimts;
        final double[] upperLimits;

        public Limits(double[] lowerLimts, double[] upperLimts) {
            this.lowerLimts = lowerLimts;
            this.upperLimits = upperLimts;
        }

    }

    final double lowerFraction;
    final double upperFraction;

    public NumCenterBeadsTrackable(double sizeFraction) {
        lowerFraction = .5 - sizeFraction / 2;
        upperFraction = .5 + sizeFraction / 2;
    }

    @Override
    public double getValue(PolymerSimulator polymerSimulator) {
        Limits limits = getLimits(polymerSimulator.getGeometry());
        double numBeads = getNumBeadsInLimits(polymerSimulator, limits);

        return numBeads;
    }

    private Limits getLimits(SystemGeometry systemGeometry) {
        final int numDimensions = systemGeometry.getNumDimensions();
        final double[] lowerLimits = new double[numDimensions];
        final double[] upperLimits = new double[numDimensions];
        for (int dimension = 0; dimension < numDimensions; dimension++) {
            final double sizeOfDimension = systemGeometry.getSizeOfDimension(dimension);
            lowerLimits[dimension] = lowerFraction * sizeOfDimension;
            upperLimits[dimension] = upperFraction * sizeOfDimension;
        }
        final Limits limits = new Limits(lowerLimits, upperLimits);
        return limits;
    }

    private double getNumBeadsInLimits(PolymerSimulator polymerSimulator, Limits limits) {
        double numBeads = 0;
        for (int bead = 0; bead < polymerSimulator.getNumBeads(); bead++) {
            if (isPositionInBounds(polymerSimulator.getSystemAnalyzer().getBeadPosition(bead), limits.lowerLimts, limits.upperLimits)) {
                numBeads++;
            }
        }
        return numBeads;
    }

    private boolean isPositionInBounds(double[] beadPosition, double[] lowerLimits, double[] upperLimits) {
        final int numDimensions = lowerLimits.length;
        for (int dimension = 0; dimension < numDimensions; dimension++) {
            if (beadPosition[dimension] < lowerLimits[dimension] || beadPosition[dimension] > upperLimits[dimension]) {
                return false;
            }
        }
        return true;
    }

}
