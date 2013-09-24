/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine.SystemGeometry;

import Engine.PolymerCluster;
import Engine.SimulationParameters;
import SystemAnalysis.AreaPerimeter.BeadRectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Dimension: ").append(Double.toString(dimension)).append("\n");
        stringBuilder.append("R Max: ").append(Arrays.toString(fullRMax)).append("\n");
        stringBuilder.append("Simulation Parameters: \n").append(parameters.toString()).append("\n");
        return stringBuilder.toString();
    }

    @Override
    public double[] randomPosition() {
        double[] position = new double[dimension];
        for (int i = 0; i < dimension; i++) {
            position[i] = (randomNumberGenerator.nextDouble() / 3 + 1. / 3) * fullRMax[i];
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
    public double[][] randomColumnPositions(int numPositions) {
        double[][] randomColumnPositions = new double[numPositions][];
        for (int i = 0; i < numPositions; i++) {
            randomColumnPositions[i] = randomColumnPosition();
        }
        return randomColumnPositions;
    }

    @Override
    public double[] randomColumnPosition() {
        double[] position = new double[dimension];
        position[0] = randomNumberGenerator.nextDouble() * fullRMax[0];
        position[1] = (randomNumberGenerator.nextDouble() / 3 + 1. / 3) * fullRMax[1];
        return position;
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
    public double[] randomGaussian(double scaleFactor) {
        double[] randomVector = new double[dimension];
        for (int i = 0; i < dimension; i++) {
            randomVector[i] = randomNumberGenerator.nextGaussian() * parameters.getStepLength() * scaleFactor;
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

    @Override
    public BeadRectangle getRectangleFromPosition(double[] beadPosition) {
        double left, right, top, bottom, halfWidth;
        halfWidth = parameters.getInteractionLength() / 2;
        left = beadPosition[0] - halfWidth;
        right = beadPosition[0] + halfWidth;
        top = beadPosition[1] + halfWidth;
        bottom = beadPosition[1] - halfWidth;
        BeadRectangle rectangle = new BeadRectangle(left, right, top, bottom);

        return rectangle;
    }

    @Override
    public List<BeadRectangle> getRectanglesFromPositions(double[][] beadPositions) {
        List<BeadRectangle> beadRectangles = new ArrayList<>(beadPositions.length);
        for (int bead = 0; bead < beadPositions.length; bead++) {
            beadRectangles.add(getRectangleFromPosition(beadPositions[bead]));
        }
        return beadRectangles;
    }

    @Override
    public void checkedCopyPositions(double[][] src, double[][] dest) {
        if (src.length != dest.length) {
            System.err.println("SystemGeometry::checkCopyPosition: position arrays not of same length");
            return;
        }
        for (int bead = 0; bead < src.length; bead++) {
            checkedCopyPosition(src[bead], dest[bead]);
        }
    }

    @Override
    public void setRMax(int index, double rMax) {
        this.fullRMax[index] = rMax;
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
