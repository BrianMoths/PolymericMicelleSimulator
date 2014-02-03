/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SystemAnalysis;

import Engine.PolymerSimulator;
import Engine.PolymerState.SystemGeometry.Interfaces.SystemGeometry;
import SystemAnalysis.AreaPerimeter.BeadRectangle;

/**
 *
 * @author bmoths
 */
public class StressFinder {

    static private interface ForceCalculator {

        public double[] calculateForce(double[] displacment, PolymerSimulator polymerSimulator);

    }

    static private final double defaultFractionalSize = .1;
    static private final ForceCalculator springForceFinder = new ForceCalculator() {
        @Override
        public double[] calculateForce(double[] displacement, PolymerSimulator polymerSimulator) {
            final double springCoefficient = polymerSimulator.getEnergeticsConstants().getSpringCoefficient();
            for (int dimension = 0; dimension < displacement.length; dimension++) {
                displacement[dimension] *= -springCoefficient;
            }
            return displacement;
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

        private double[] calculateOverlapForce(double[] displacement, double interactionLength, double overlapCoefficient) {
            final double squareDisplacementRatio = calculateSquareDisplacement(displacement) / (interactionLength * interactionLength);
            final double forceMagnitude = calculateForceMagnitude(squareDisplacementRatio, interactionLength);
            return calculateScaledDisplacement(displacement, forceMagnitude);
        }

        private double calculateSquareDisplacement(double[] displacement) {
            double sum = 0;
            for (int i = 0; i < displacement.length; i++) {
                sum += displacement[i] * displacement[i];
            }
            return sum;
        }

        private double calculateForceMagnitude(final double squareDisplacementRatio, double interactionLength) {
            final double forceMagnitude;
            if (squareDisplacementRatio >= 1) {
                forceMagnitude = 0;
            } else {
                forceMagnitude = interactionLength * Math.sqrt(1 - squareDisplacementRatio);
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

        private double[] calculateOuterForce(double[] displacement, PolymerSimulator polymerSimulator) {
            final double interactionLength = polymerSimulator.getGeometry().getParameters().getInteractionLength();
            final double outerOverlapCoefficient = polymerSimulator.getEnergeticsConstants().getBBOverlapCoefficient();
            final double[] outerForce = calculateOverlapForce(displacement, interactionLength, outerOverlapCoefficient);
            return outerForce;
        }

        private double[] calculateInnerForce(double[] displacement, PolymerSimulator polymerSimulator) {
            final double coreLength = polymerSimulator.getGeometry().getParameters().getCoreLength();
            final double innerOverlapCoefficient = polymerSimulator.getEnergeticsConstants().getHardOverlapCoefficient();
            final double[] innerForce = calculateOverlapForce(displacement, coreLength, innerOverlapCoefficient);
            return innerForce;
        }

    };
    static private final ForceCalculator totalForceCalculator = new ForceCalculator() {
        @Override
        public double[] calculateForce(double[] displacment, PolymerSimulator polymerSimulator) {
            double[] springForce = springForceFinder.calculateForce(displacment, polymerSimulator);
            double[] overlapForce = overlapForceFinder.calculateForce(displacment, polymerSimulator);
            for (int i = 0; i < overlapForce.length; i++) {
                overlapForce[i] += springForce[i];
            }
            return overlapForce;
        }

    };

    static public double[][] calculateSpringStress(PolymerSimulator polymerSimulator) {
        StressFinder stressFinder = new StressFinder(polymerSimulator, springForceFinder, defaultFractionalSize);
        return stressFinder.calculateStress();
    }

    static public double[][] calculateTotalStress(PolymerSimulator polymerSimulator) {
        StressFinder stressFinder = new StressFinder(polymerSimulator, totalForceCalculator, defaultFractionalSize);
        return stressFinder.calculateStress();
    }

    static private double calculateDistance(final double[] displacement) {
        double distance = 0;
        for (int component = 0; component < displacement.length; component++) {
            distance += component * component;
        }
        return Math.sqrt(distance);
    }

    static private double[] divideDisplacement(final double[] displacement, final double distance) {
        final double[] normalizedDisplacement = new double[displacement.length];
        for (int dimension = 0; dimension < normalizedDisplacement.length; dimension++) {
            normalizedDisplacement[dimension] = displacement[dimension] / distance;
        }
        return normalizedDisplacement;
    }

    static private double[][] makeStressFromDirection(double[] displacementDirection, double[] force) {
        final int numDimensions = displacementDirection.length;
        final double[][] stress = new double[numDimensions][numDimensions];
        for (int i = 0; i < numDimensions; i++) {
            final double[] stressRow = stress[i];
            for (int j = 0; j < numDimensions; j++) {
                stressRow[j] = displacementDirection[i] * force[j];
            }
        }
        return stress;
    }

    private static double[] calculateDisplacementDirection(final double[] displacement) {
        final double distance = calculateDistance(displacement);
        final double[] displacementDirection = divideDisplacement(displacement, distance);
        return displacementDirection;
    }

    private final PolymerSimulator polymerSimulator;
    private final double[][] accumulatedStress;
    private final int numDimensions;
    private final double fractionalSize;
    private final BeadRectangle boundaryRectangle;
    private final ForceCalculator forceCalculator;

    private StressFinder(PolymerSimulator polymerSimulator, ForceCalculator forceCalculator, double rectangleFraction) {
        this.polymerSimulator = polymerSimulator;
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
        return accumulatedStress;
    }

    private void incrementByStressFromBead(int bead) {
        final double[] beadPosition = polymerSimulator.getSystemAnalyzer().getBeadPosition(bead);
        final int leftDirection = 0;
        final int rightDirection = 1;
        incrementByBeadNeighborStress(bead, leftDirection, beadPosition);
        incrementByBeadNeighborStress(bead, rightDirection, beadPosition);
    }

    private void incrementByBeadNeighborStress(final int bead, final int neighborDirection, final double[] beadPosition) {
        final int neighbor = polymerSimulator.getSystemAnalyzer().getNeighbor(bead, neighborDirection);
        if (neighbor >= 0) {
            incrementByStressFromNeighbor(neighbor, beadPosition);
        }
    }

    private void incrementByStressFromNeighbor(final int neighbor, final double[] beadPosition) {
        final double[] neighborPosition = polymerSimulator.getSystemAnalyzer().getBeadPosition(neighbor);
        final double[] displacement = polymerSimulator.getSystemAnalyzer().getSystemGeometry().getDisplacement(neighborPosition, beadPosition);
        final double[] force = forceCalculator.calculateForce(displacement, polymerSimulator);
        double[] displacementDirection = calculateDisplacementDirection(displacement);
        incrementStressBy(makeStressFromDirection(displacementDirection, force));
    }

    private BeadRectangle makeBoundaryRectangle() {
        final SystemGeometry systemGeometry = polymerSimulator.getGeometry();
//        final double fractionalSize = .1;
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

}
