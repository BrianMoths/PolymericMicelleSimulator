/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine.SystemGeometry;

import Engine.PolymerCluster;
import Engine.SimulationParameters;
import java.util.Random;

/**
 *
 * @author bmoths
 */
public abstract class AbstractGeometry implements SystemGeometry {

    //<editor-fold defaultstate="collapsed" desc="BuilderClass">
    public abstract static class AbstractGeometryBuilder implements GeometryBuilder {

        protected int dimension;
        protected double[] fullRMax;
        protected SimulationParameters parameters;

        public AbstractGeometryBuilder() {
            this.dimension = 2;
            this.fullRMax = new double[]{20, 20, 20};
            this.parameters = new SimulationParameters();
        }

        public AbstractGeometryBuilder(SystemGeometry geometry) {
            dimension = geometry.getDimension();
            fullRMax = new double[dimension];
            System.arraycopy(fullRMax, 0, geometry.getRMax(), 0, dimension);
            parameters = geometry.getParameters();
        }

        @Override
        public int getDimension() {
            return dimension;
        }

        @Override
        public AbstractGeometryBuilder setDimension(int dimension) {
            this.dimension = dimension;
            return this;
        }

        @Override
        public double[] getFullRMax() {
            double[] rMax = new double[dimension];
            System.arraycopy(fullRMax, 0, rMax, 0, dimension);
            return rMax;
        }

        @Override
        public AbstractGeometryBuilder setDimensionSize(int dimension, double size) {
            this.fullRMax[dimension] = size;
            return this;
        }

        @Override
        public SimulationParameters getParameters() {
            return parameters;
        }

        @Override
        public AbstractGeometryBuilder setParameters(SimulationParameters parameters) {
            this.parameters = parameters;
            return this;
        }

        @Override
        public void makeConsistentWith(PolymerCluster polymerCluster, SimulationParameters simulationParameters) {
            this.parameters = simulationParameters;
            double boxLength;
            boxLength = findBoxLength(polymerCluster, simulationParameters);
            for (int i = 0; i < dimension; i++) {
                setDimensionSize(i, boxLength);
            }
        }

        private double findBoxLength(PolymerCluster polymerCluster, SimulationParameters simulationParameters) {
            double fractionInteracting = 14 / polymerCluster.getNumBeadsIncludingWater();
            return simulationParameters.getInteractionLength() * Math.pow(1 / fractionInteracting, 1. / dimension);
        }
    }
    //</editor-fold>
    public static final Random randomNumberGenerator = new Random();
    protected final int dimension;
    protected final double[] fullRMax; //try to make this constant
    protected final SimulationParameters parameters;

    protected AbstractGeometry(int dimension, double[] fullRMax, SimulationParameters parameters) {
        this.dimension = dimension;
        this.fullRMax = new double[dimension];
        System.arraycopy(fullRMax, 0, this.fullRMax, 0, dimension);
        this.parameters = parameters;
    }

    @Override
    public double[] randomPosition() {
        double[] position = new double[dimension];
        for (int i = 0; i < dimension; i++) {
            position[i] = randomNumberGenerator.nextDouble() * fullRMax[i] / 3;
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
