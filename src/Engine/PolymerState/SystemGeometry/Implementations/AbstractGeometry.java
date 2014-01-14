/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine.PolymerState.SystemGeometry.Implementations;

import Engine.PolymerState.SystemGeometry.GeometricalParameters;
import Engine.PolymerState.SystemGeometry.Interfaces.GeometryBuilder;
import Engine.PolymerState.SystemGeometry.Interfaces.SystemGeometry;
import SystemAnalysis.AreaPerimeter.BeadRectangle;
import SystemAnalysis.AreaPerimeter.Interval;
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
        protected GeometricalParameters parameters;

        public AbstractGeometryBuilder() {
            this.dimension = 2;
            this.fullRMax = new double[]{20, 20};
            this.parameters = new GeometricalParameters();
        }

        public AbstractGeometryBuilder(SystemGeometry geometry) {
            dimension = geometry.getNumDimensions();
            fullRMax = new double[dimension];
            System.arraycopy(geometry.getRMax(), 0, fullRMax, 0, dimension);
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
        public double[] getFullRMaxCopy() {
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
        public double getDimensionSize(int dimension) {
            return fullRMax[dimension];
        }

        public void resizeAccordingToAspectRatio(double aspectRatio) {
            final double area = getDimensionSize(0) * getDimensionSize(1);
            final double xBoxLength = Math.sqrt(area * aspectRatio);
            setDimensionSize(0, xBoxLength);
            final double yBoxLength = Math.sqrt(area / aspectRatio);
            setDimensionSize(1, yBoxLength);
        }

        @Override
        public GeometricalParameters getParameters() {
            return parameters;
        }

        @Override
        public AbstractGeometryBuilder setParameters(GeometricalParameters parameters) {
            this.parameters = parameters;
            return this;
        }

        /**
         * sets the GeometricalParameters field of the builder object. Also sets
         * the size of the system so that on average, each bead interacts with
         * 14 other beads. The number 14 was chosen because it produces
         * reasonable results
         *
         * @param numBeadsIncludingWater the total number of beads to be put in
         * the region specified by the geometry object to be built. The implicit
         * water beads do count towards this number
         * @param geometricalParameters the geometrical parameters to be
         * contained by the the geometry object to be built
         */
        @Override
        public void makeConsistentWith(double numBeadsIncludingWater, GeometricalParameters geometricalParameters) {
            this.parameters = geometricalParameters;
            double boxLength;
            boxLength = findBoxLength(numBeadsIncludingWater, geometricalParameters);
            for (int i = 0; i < dimension; i++) {
                setDimensionSize(i, boxLength);
            }
        }

        @Override
        public void makeConsistentWith(double numBeadsIncludingWater, GeometricalParameters geometricalParameters, double aspectRatio) {
            makeConsistentWith(numBeadsIncludingWater, geometricalParameters);
            resizeAccordingToAspectRatio(aspectRatio);
        }

        private double findBoxLength(double numBeadsIncludingWater, GeometricalParameters geometricalParameters) {
            double fractionInteracting = 14 / numBeadsIncludingWater;
            return geometricalParameters.getInteractionLength() * Math.pow(1 / fractionInteracting, 1. / dimension);
        }

    }
    //</editor-fold>

    public static final Random randomNumberGenerator = new Random();
    protected final int dimension;
    protected final double[] fullRMax;
    protected final GeometricalParameters parameters;

    protected AbstractGeometry(int dimension, double[] fullRMax, GeometricalParameters parameters) {
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

    //<editor-fold defaultstate="collapsed" desc="deal with rectangles">
    final protected List<BeadRectangle> getUnsplitRectanglesFromPositions(double[][] beadPositions) {
        List<BeadRectangle> beadRectangles = new ArrayList<>();

        for (int i = 0; i < beadPositions.length; i++) {
            final double[] beadPosition = beadPositions[i];
            beadRectangles.add(getUnsplitRectangleFromPosition(beadPosition));
        }

        return beadRectangles;
    }

    final protected BeadRectangle getUnsplitRectangleFromPosition(double[] beadPosition) {
        double left, right, top, bottom, halfWidth;
        halfWidth = parameters.getInteractionLength() / 2;
        double initialX = beadPosition[0];
        double initialY = beadPosition[1];
        left = beadPosition[0] - halfWidth;
        right = beadPosition[0] + halfWidth;
        top = beadPosition[1] + halfWidth;
        bottom = beadPosition[1] - halfWidth;
        BeadRectangle rectangle = new BeadRectangle(left, right, top, bottom);
        return rectangle;
    }

    final protected BeadRectangle makeLimits() {
        BeadRectangle limits = new BeadRectangle(0, 0, 0, 0);

        for (int currentDimension = 0; currentDimension < dimension; currentDimension++) {
            limits.setIntervalOfDimension(makeLimit(currentDimension), currentDimension);
        }

        return limits;
    }

    private Interval makeLimit(int dimension) {
        final double lowerLimit = 0;
        final double upperLimit = fullRMax[dimension];

        final Interval limits = new Interval(lowerLimit, upperLimit);

        return limits;
    }
    //</editor-fold>

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
    public int getNumDimensions() {
        return dimension;
    }

    @Override
    public double[] getRMax() {
        double[] rMax = new double[dimension];
        System.arraycopy(fullRMax, 0, rMax, 0, dimension);
        return rMax;
    }

    @Override
    public double getSizeOfDimension(int dimension) {
        return fullRMax[dimension];
    }

    @Override
    public GeometricalParameters getParameters() {
        return parameters;
    }
    //</editor-fold>

}
