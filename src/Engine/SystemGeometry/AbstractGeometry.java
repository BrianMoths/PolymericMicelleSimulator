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
public abstract class AbstractGeometry implements SystemGeometry {

    //<editor-fold defaultstate="collapsed" desc="BuilderClass">
    public abstract static class GeometryBuilder {

        protected int dimension;
        protected double[] fullRMax;
        protected SimulationParameters parameters;

        public GeometryBuilder() {
            this.dimension = 2;
            this.fullRMax = new double[]{20, 20, 20};
            this.parameters = new SimulationParameters();
        }

        public GeometryBuilder(AbstractGeometry geometry) {
            dimension = geometry.dimension;
            fullRMax = new double[dimension];
            System.arraycopy(fullRMax, 0, geometry.getRMax(), 0, dimension);
            parameters = geometry.getParameters();
        }

        public int getDimension() {
            return dimension;
        }

        public GeometryBuilder setDimension(int dimension) {
            this.dimension = dimension;
            return this;
        }

        public double[] getFullRMax() {
            double[] rMax = new double[dimension];
            System.arraycopy(fullRMax, 0, rMax, 0, dimension);
            return rMax;
        }

        public GeometryBuilder setDimensionSize(int dimension, double size) {
            this.fullRMax[dimension] = size;
            return this;
        }

        public SimulationParameters getParameters() {
            return parameters;
        }

        public GeometryBuilder setParameters(SimulationParameters parameters) {
            this.parameters = parameters;
            return this;
        }

        abstract public SystemGeometry buildGeometry();
    }
    //</editor-fold>
    public static final Random randomNumberGenerator = new Random();
    protected final int dimension;
    protected final double[] fullRMax; //try to make this constant
    protected final SimulationParameters parameters;
    protected final InteractionCalculator interactionCalculator;

    protected AbstractGeometry(int dimension, double[] fullRMax, SimulationParameters parameters) {
        this.dimension = dimension;
        this.fullRMax = new double[dimension];
        System.arraycopy(fullRMax, 0, this.fullRMax, 0, dimension);
        this.parameters = parameters;
        interactionCalculator = new InteractionCalculator(parameters);
    }

    @Override
    public double[] randomPosition() {
        double[] position = new double[dimension];
        for (int i = 0; i < dimension; i++) {
            position[i] = randomNumberGenerator.nextDouble() * fullRMax[i];
        }
        return position;
    }

    @Override
    public double[][] randomPositions(int numPositions) {
        double[][] randomPositions = new double[numPositions][];
        for (int i = 0; i < numPositions; i++) {
            randomPositions[i] = randomPosition();
        }
        return randomPositions;
    }

    @Override
    public double[] randomGaussian() {
        double[] randomVector = new double[dimension];
        for (int i = 0; i < dimension; i++) {
            randomVector[i] = randomNumberGenerator.nextGaussian() * parameters.getStepLength();
        }
        return randomVector;
    }

    @Override
    public double getVolume() {
        double volume = 1;
        for (int i = 0; i < dimension; i++) {
            volume *= fullRMax[i];
        }
        return volume;
    }

    /**
     *
     * @param position1
     * @param position2
     * @return
     */
    public final double calculateInteraction(double[] position1, double[] position2) {
        double overlap = areaOverlap(position1, position2);
        return interactionCalculator.calculateInteraction(overlap);
    }

    //<editor-fold defaultstate="collapsed" desc="getters">
    @Override
    public int getDimension() {
        return dimension;
    }

    @Override
    public double[] getRMax() {
        double[] rMax = new double[dimension];
        System.arraycopy(fullRMax, 0, rMax, 0, dimension);
        return rMax;
    }

    @Override
    public SimulationParameters getParameters() {
        return parameters;
    }
    //</editor-fold>
}
