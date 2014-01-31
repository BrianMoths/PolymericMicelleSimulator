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

    static private final double defaultFractionalSize = .1;

    static public double[][] calculateStress(PolymerSimulator polymerSimulator) {
//        final int numDimensions = polymerSimulator.getSystemAnalyzer().getSystemGeometry().getNumDimensions();
//        double[][] stress = new double[numDimensions][numDimensions];
//        final int numBeads = polymerSimulator.getNumBeads();
//        final BeadRectangle boundaryRectangle = makeBoundaryRectangle(polymerSimulator);
//        for (int bead = 0; bead < numBeads; bead++) {
//            if (!isBeadInRectangle(bead, boundaryRectangle, polymerSimulator)) {
//                continue;
//            } else {
//                incrementStress(stress, calculateStressFromBead(bead, polymerSimulator));
//            }
//        }
//        return stress;
        StressFinder stressFinder = new StressFinder(polymerSimulator, defaultFractionalSize);
        return stressFinder.calculateStress();
    }

    static private void incrementStress(double[][] stress, double[][] summandStress) {
        for (int i = 0; i < summandStress.length; i++) {
            final double[] stressRow = stress[i];
            final double[] summandStressRow = summandStress[i];
            for (int j = 0; j < stressRow.length; j++) {
                stressRow[j] += summandStressRow[j];
            }
        }
    }

    static private double[][] calculateStressFromBead(int bead, PolymerSimulator polymerSimulator) {
        final double[] beadPosition = polymerSimulator.getSystemAnalyzer().getBeadPosition(bead);
        final int numDimensions = polymerSimulator.getSystemAnalyzer().getSystemGeometry().getNumDimensions();
        double[][] stress = new double[numDimensions][numDimensions];
        final int leftNeighbor = polymerSimulator.getSystemAnalyzer().getNeighbor(bead, 0); //check -1 case
        if (leftNeighbor > 0) {
            incrementStress(stress, calculateStressFromNeighbor(polymerSimulator, leftNeighbor, beadPosition));
        }
        final int rightNeighbor = polymerSimulator.getSystemAnalyzer().getNeighbor(bead, 0);
        if (rightNeighbor > 0) {
            incrementStress(stress, calculateStressFromNeighbor(polymerSimulator, leftNeighbor, beadPosition));
        }
        return stress;
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

    static private double[] calculateForce(double[] displacement, PolymerSimulator polymerSimulator) {
        for (int dimension = 0; dimension < displacement.length; dimension++) {
            displacement[dimension] *= polymerSimulator.getEnergeticsConstants().getSpringCoefficient();
        }
        return displacement;
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

    private static double[][] calculateStressFromNeighbor(PolymerSimulator polymerSimulator, final int leftNeighbor, final double[] beadPosition) {
        final double[] neighborPosition = polymerSimulator.getSystemAnalyzer().getBeadPosition(leftNeighbor);
        final double[] displacement = polymerSimulator.getSystemAnalyzer().getSystemGeometry().getDisplacement(neighborPosition, beadPosition);
        final double[] force = calculateForce(displacement, polymerSimulator);
        double[] displacementDirection = calculateDisplacementDirection(displacement);
        double[][] stress = makeStressFromDirection(displacementDirection, force);
        return stress;
    }

    private static boolean isBeadInRectangle(int bead, BeadRectangle boundaryRectangle, PolymerSimulator polymerSimulator) {
        final double[] beadPosition = polymerSimulator.getSystemAnalyzer().getBeadPosition(bead);
        return boundaryRectangle.isPointContained(beadPosition);
    }

    private static BeadRectangle makeBoundaryRectangle(PolymerSimulator polymerSimulator) {
        SystemGeometry systemGeometry = polymerSimulator.getGeometry();
        final double fractionalSize = .1;
        final double lowerFraction = .5 - fractionalSize / 2;
        final double upperFraction = .5 + fractionalSize / 2;
        final double left = systemGeometry.getSizeOfDimension(0) * lowerFraction;
        final double right = systemGeometry.getSizeOfDimension(0) * upperFraction;
        final double top = systemGeometry.getSizeOfDimension(1) * upperFraction;
        final double bottom = systemGeometry.getSizeOfDimension(1) * lowerFraction;
        return new BeadRectangle(left, right, top, bottom);
    }

    private final PolymerSimulator polymerSimulator;
    private final double[][] accumulatedStress;
    private final int numDimensions;
    private final double fractionalSize;
    private final BeadRectangle boundaryRectangle;

    private StressFinder(PolymerSimulator polymerSimulator, double rectangleFraction) {
        this.polymerSimulator = polymerSimulator;
        this.fractionalSize = rectangleFraction;
        numDimensions = polymerSimulator.getGeometry().getNumDimensions();
        accumulatedStress = new double[numDimensions][numDimensions];
        boundaryRectangle = makeBoundaryRectangle();
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
        if (neighbor > 0) {
            incrementByStressFromNeighbor(neighbor, beadPosition);
        }
    }

    private void incrementByStressFromNeighbor(final int neighbor, final double[] beadPosition) {
        final double[] neighborPosition = polymerSimulator.getSystemAnalyzer().getBeadPosition(neighbor);
        final double[] displacement = polymerSimulator.getSystemAnalyzer().getSystemGeometry().getDisplacement(neighborPosition, beadPosition);
        final double[] force = calculateForce(displacement);
        double[] displacementDirection = calculateDisplacementDirection(displacement);
        incrementStressBy(makeStressFromDirection(displacementDirection, force));
    }

    private double[] calculateForce(double[] displacement) {
        final double springCoefficient = polymerSimulator.getEnergeticsConstants().getSpringCoefficient();
        for (int dimension = 0; dimension < displacement.length; dimension++) {
            displacement[dimension] *= springCoefficient;
        }
        return displacement;
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
