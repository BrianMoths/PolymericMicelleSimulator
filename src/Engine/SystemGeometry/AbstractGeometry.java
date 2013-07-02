/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine.SystemGeometry;

import Engine.SimulationParameters;
import java.util.Random;

/**
 *
 * @author bmoths
 */
abstract class AbstractGeometry implements SystemGeometry {

    protected int dimension;
    protected double[] fullRMax;
    public static Random randomNumberGenerator = new Random();
    protected SimulationParameters parameters;

    @Override
    public void setDimension(int dimension) {
        if (dimension > 2 || dimension <= 3) {
            this.dimension = dimension;
        }
    }

    @Override
    public int getDimension() {
        return dimension;
    }

    @Override
    public void setDimensionSize(int dimensionToBeSized, double size) {
        if (dimensionToBeSized >= 0 && dimensionToBeSized < 3 && size > 0) {
            fullRMax[dimensionToBeSized] = size;
        }
    }

    @Override
    public double[] getRMax() {
        double[] rMax = new double[dimension];
        System.arraycopy(fullRMax, 0, rMax, 0, dimension);
        return rMax;
    }

    public double getVolume() {
        double volume = 1;
        for (int i = 0; i < dimension; i++) {
            volume *= fullRMax[i];
        }
        return volume;
    }

    @Override
    public SimulationParameters getParameters() {
        return parameters;
    }

    @Override
    public void setParameters(SimulationParameters parameters) {
        this.parameters = parameters;
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

    public double[] randomGaussian() {
        double[] randomVector = new double[dimension];
        for (int i = 0; i < dimension; i++) {
            randomVector[i] = randomNumberGenerator.nextGaussian() * parameters.getStepLength();
        }
        return randomVector;
    }
}
