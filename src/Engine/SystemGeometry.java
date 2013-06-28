/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine;

import java.util.Random;

/**
 *
 * @author bmoths
 */
public class SystemGeometry {

    int dimension;
    double[] fullRMax;
    public static Random randomNumberGenerator = new Random();

    public SystemGeometry() {
        dimension = 2;
        fullRMax = new double[]{20, 20, 20};
    }

    public boolean isPositionValid(double[] position) {
        for (int i = 0; i < dimension; i++) {
            if (position[i] < 0 || position[i] > fullRMax[i]) {
                return false;
            }
        }
        return true;
    }

    public void setDimension(int dimension) {
        if (dimension > 2 || dimension <= 3) {
            this.dimension = dimension;
        }
    }

    public int getDimension() {
        return dimension;
    }

    public void setDimensionSize(int dimensionToBeSized, double size) {
        if (dimensionToBeSized >= 0 && dimensionToBeSized < 3 && size > 0) {
            fullRMax[dimensionToBeSized] = size;
        }
    }

    public double[] getRMax() {
        double[] rMax = new double[dimension];
        System.arraycopy(fullRMax, 0, rMax, 0, dimension);
        return rMax;
    }

    public double sqDist(double[] position1, double[] position2) {
        double sqDist = 0;
        for (int i = 0; i < dimension; i++) {
            sqDist += (position1[i] - position2[i]) * (position1[i] - position2[i]);
        }
        return sqDist;
    }

    public double areaOverlap(double[] position1, double[] position2, double interactionLength) {
        double overlap = 1;

        for (int i = 0; i < dimension; i++) {
            overlap *= Math.max(interactionLength - Math.abs(position1[i] - position2[i]), 0.0);
        }

        return overlap;
    }

    public double[] randomPosition() {
        double[] position = new double[dimension];
        for (int i = 0; i < dimension; i++) {
            position[i] = randomNumberGenerator.nextDouble() * fullRMax[i];
        }
        return position;
    }

    public double[][] randomPositions(int numPositions) {
        double[][] randomPositions = new double[numPositions][];
        for (int i = 0; i < numPositions; i++) {
            randomPositions[i] = randomPosition();
        }
        return randomPositions;
    }

    public double[] randomGaussian(double stepLength) {
        double[] randomVector = new double[dimension];
        for (int i = 0; i < dimension; i++) {
            randomVector[i] = randomNumberGenerator.nextGaussian() * stepLength;
        }
        return randomVector;
    }

    public void doStep(double[] toStep, double[] stepVector) {
        for (int i = 0; i < dimension; i++) {
            toStep[i] += stepVector[i];
        }
    }

    public void undoStep(double[] toStep, double[] stepVector) {
        for (int i = 0; i < dimension; i++) {
            toStep[i] -= stepVector[i];
        }
    }
}
