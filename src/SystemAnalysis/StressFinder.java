/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SystemAnalysis;

import Engine.PolymerSimulator;
import Engine.PolymerState.SystemGeometry.Interfaces.SystemGeometry;
import SystemAnalysis.AreaPerimeter.rectangleareaperimeter.BeadRectangle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author bmoths
 */
public class StressFinder {

    static private interface ForceCalculator {

        public double[] calculateForce(double[] displacment, PolymerSimulator polymerSimulator);

    }

    static private interface NeighborIteratorFactory {

        public Iterator<Integer> getNeighborIterator(int bead, PolymerSimulator polymerSimulator);

    }

    //<editor-fold defaultstate="collapsed" desc="neighbor iterators">
    static private NeighborIteratorFactory connectedNeighbors = new NeighborIteratorFactory() {
        @Override
        public Iterator<Integer> getNeighborIterator(int bead, PolymerSimulator polymerSimulator) {
            return makeNeighborList(bead, polymerSimulator).iterator();
        }

        private List<Integer> makeNeighborList(int bead, PolymerSimulator polymerSimulator) {
            List<Integer> neighborList = new ArrayList<>();
            final int leftNeighbor = polymerSimulator.getSystemAnalyzer().getNeighbor(bead, 0);
            addNeighbor(leftNeighbor, neighborList);
            final int rightNeighbor = polymerSimulator.getSystemAnalyzer().getNeighbor(bead, 1);
            addNeighbor(rightNeighbor, neighborList);
            return neighborList;
        }

        private void addNeighbor(final int neighbor, List<Integer> neighborList) {
            if (neighbor != -1) {
                neighborList.add(neighbor);
            }
        }

    };
    static private NeighborIteratorFactory nearbyNeighbors = new NeighborIteratorFactory() {
        @Override
        public Iterator<Integer> getNeighborIterator(int bead, PolymerSimulator polymerSimulator) {
            return polymerSimulator.getSystemAnalyzer().getNearbyBeadIterator(bead);
        }

    };
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="force calculators">
    static private final ForceCalculator springForceFinder = new ForceCalculator() {
        @Override
        public double[] calculateForce(double[] displacement, PolymerSimulator polymerSimulator) {
            final double[] force = new double[displacement.length];
            for (int dimension = 0; dimension < displacement.length; dimension++) {
                force[dimension] = polymerSimulator.getEnergeticsConstants().calculateSpringForce(displacement[dimension]);
            }
            return force;
        }

    };
    static private final ForceCalculator overlapForceFinder = new ForceCalculator() {
        @Override
        public double[] calculateForce(double[] displacement, PolymerSimulator polymerSimulator) {
            double[] outerForce = calculateOuterForce(displacement, polymerSimulator);
            double[] innerForce = calculateInnerForce(displacement, polymerSimulator);

            for (int i = 0; i < innerForce.length; i++) {
                outerForce[i] += innerForce[i];
            }
            return outerForce;
        }

        private double[] calculateOuterForce(double[] displacement, PolymerSimulator polymerSimulator) {
            final double interactionLength = polymerSimulator.getGeometry().getGeometricalParameters().getInteractionLength();
            final double outerOverlapCoefficient = polymerSimulator.getEnergeticsConstants().getBBOverlapCoefficient();
            final double[] outerForce = calculateOverlapForce(displacement, interactionLength, outerOverlapCoefficient);
            return outerForce;
        }

        private double[] calculateInnerForce(double[] displacement, PolymerSimulator polymerSimulator) {
            final double coreLength = polymerSimulator.getGeometry().getGeometricalParameters().getCoreLength();
            final double innerOverlapCoefficient = polymerSimulator.getEnergeticsConstants().getHardOverlapCoefficient();
            final double[] innerForce = calculateOverlapForce(displacement, coreLength, innerOverlapCoefficient);
            return innerForce;
        }

        private double[] calculateOverlapForce(double[] displacement, double interactionLength, double overlapCoefficient) {
            if (interactionLength > 0) {
                final double squareDistance = calculateSquareDisplacement(displacement);
                final double squareDisplacementRatio = squareDistance / (interactionLength * interactionLength);
                final double forceMagnitude = overlapCoefficient * calculateOverlapGradientMagnitude(squareDisplacementRatio, interactionLength);
                return calculateScaledDisplacement(displacement, forceMagnitude / Math.sqrt(squareDistance));
            } else {
                return calculateScaledDisplacement(displacement, 0);
            }
        }

        private double calculateSquareDisplacement(double[] displacement) {
            double sum = 0;
            for (int i = 0; i < displacement.length; i++) {
                sum += displacement[i] * displacement[i];
            }
            return sum;
        }

        private double calculateOverlapGradientMagnitude(final double squareDisplacementRatio, double interactionLength) {
            final double forceMagnitude;
            if (squareDisplacementRatio >= 1.) {
                forceMagnitude = 0;
            } else {
                forceMagnitude = interactionLength * Math.sqrt(1. - squareDisplacementRatio);
            }
            return forceMagnitude;
        }

        private double[] calculateScaledDisplacement(double[] displacement, final double forceMagnitude) {
            double[] force = new double[displacement.length];
            for (int i = 0; i < displacement.length; i++) {
                force[i] = displacement[i] * forceMagnitude;
            }
            return force;
        }

    };
//</editor-fold>
    static private final double defaultFractionalSize = 1. / 3.;

    static public double[][] calculateSpringStress(PolymerSimulator polymerSimulator) {
        StressFinder stressFinder = new StressFinder(polymerSimulator, connectedNeighbors, springForceFinder, defaultFractionalSize);
        return stressFinder.calculateStress();
    }

    static public double[][] calculateOverlapStress(PolymerSimulator polymerSimulator) {
        StressFinder stressFinder = new StressFinder(polymerSimulator, nearbyNeighbors, overlapForceFinder, defaultFractionalSize);
        return stressFinder.calculateStress();
    }

    static public double[][] calculateTotalStress(PolymerSimulator polymerSimulator) {
        final double[][] springStress = calculateSpringStress(polymerSimulator);
        final double[][] overlapStress = calculateOverlapStress(polymerSimulator);
        final int numDimensions = overlapStress.length;
        for (int i = 0; i < numDimensions; i++) {
            for (int j = 0; j < numDimensions; j++) {
                overlapStress[i][j] += springStress[i][j];
            }
        }
        return overlapStress;
    }

    static private double[][] makeStressFromDisplacement(double[] displacement, double[] force) {
        final int numDimensions = displacement.length;
        final double[][] stress = new double[numDimensions][numDimensions];
        for (int i = 0; i < numDimensions; i++) {
            final double[] stressRow = stress[i];
            for (int j = 0; j < numDimensions; j++) {
                stressRow[j] = displacement[i] * force[j];
            }
        }
        return stress;
    }

    private final PolymerSimulator polymerSimulator;
    private final double[][] accumulatedStress;
    private final int numDimensions;
    private final double fractionalSize;
    private final BeadRectangle boundaryRectangle;
    private final ForceCalculator forceCalculator;
    private final NeighborIteratorFactory neighborIteratorFactory;

    private StressFinder(PolymerSimulator polymerSimulator, NeighborIteratorFactory neighborIteratorFactory, ForceCalculator forceCalculator, double rectangleFraction) {
        this.polymerSimulator = polymerSimulator;
        this.neighborIteratorFactory = neighborIteratorFactory;
        this.forceCalculator = forceCalculator;
        this.fractionalSize = rectangleFraction;
        numDimensions = polymerSimulator.getGeometry().getNumDimensions();
        accumulatedStress = new double[numDimensions][numDimensions];
        boundaryRectangle = makeBoundaryRectangle();
    }

    private double[][] calculateStress() {
        final int numBeads = polymerSimulator.getNumBeads();
        for (int bead = 0; bead < numBeads; bead++) {
            if (isBeadInRectangle(bead)) {
                incrementByStressFromBead(bead);
            }
        }
        divideStressByVolume();
        return accumulatedStress;
    }

    private void incrementByStressFromBead(int bead) {
        final double[] beadPosition = polymerSimulator.getSystemAnalyzer().getBeadPosition(bead);
        Iterator<Integer> neighborIterator = neighborIteratorFactory.getNeighborIterator(bead, polymerSimulator);
        while (neighborIterator.hasNext()) {
            final Integer neighbor = neighborIterator.next();
            if (neighbor < bead) { //only counts each bond once
                incrementByStressFromNeighbor(neighbor, beadPosition);
            }
        }
    }

    private void incrementByStressFromNeighbor(final int neighbor, final double[] beadPosition) {
        final double[] neighborPosition = polymerSimulator.getSystemAnalyzer().getBeadPosition(neighbor);
        final double[] displacement = polymerSimulator.getSystemAnalyzer().getSystemGeometry().getDisplacement(neighborPosition, beadPosition);
        final double[] force = forceCalculator.calculateForce(displacement, polymerSimulator);
        incrementStressBy(makeStressFromDisplacement(displacement, force));
    }

    private BeadRectangle makeBoundaryRectangle() {
        final SystemGeometry systemGeometry = polymerSimulator.getGeometry();
        final double lowerFraction = .5 - fractionalSize / 2;
        final double upperFraction = .5 + fractionalSize / 2;
        final double left = systemGeometry.getSizeOfDimension(0) * lowerFraction;
        final double right = systemGeometry.getSizeOfDimension(0) * upperFraction;
        final double top = systemGeometry.getSizeOfDimension(1) * upperFraction;
        final double bottom = systemGeometry.getSizeOfDimension(1) * lowerFraction;
        return new BeadRectangle(left, right, top, bottom);
    }

    private boolean isBeadInRectangle(int bead) {
        final double[] beadPosition = polymerSimulator.getSystemAnalyzer().getBeadPosition(bead);
        return boundaryRectangle.isPointContained(beadPosition);
    }

    private void incrementStressBy(double[][] stressIncrement) {
        for (int i = 0; i < numDimensions; i++) {
            for (int j = 0; j < numDimensions; j++) {
                accumulatedStress[i][j] += stressIncrement[i][j];
            }
        }
    }

    private void divideStressByVolume() {
        final double volume = boundaryRectangle.getVolume();
        for (int i = 0; i < numDimensions; i++) {
            for (int j = 0; j < numDimensions; j++) {
                accumulatedStress[i][j] /= volume;
            }
        }
    }

}
