/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine.PolymerState.SystemGeometry.Implementations;

import Engine.PolymerState.SystemGeometry.GeometricalParameters;
import Engine.PolymerState.SystemGeometry.Interfaces.GeometryBuilder;
import SystemAnalysis.AreaPerimeter.BeadRectangle;
import SystemAnalysis.AreaPerimeter.OverlappingIntervalLengthFinder;
import SystemAnalysis.AreaPerimeter.RectangleSplitting.PeriodicRectangleSplitter;
import SystemAnalysis.AreaPerimeter.RectangleSplitting.RectangleSplitter;
import SystemAnalysis.AreaPerimeter.RectangleSplitting.RectanglesAndBoundaryIntervals;
import SystemAnalysis.AreaPerimeter.RectangleSplitting.RectanglesAndGluedPerimeter;
import java.util.List;

/**
 *
 * @author bmoths
 */
public final class PeriodicGeometry extends AbstractGeometry {

    public static class PeriodicGeometryBuilder extends AbstractGeometryBuilder {

        private PeriodicGeometryBuilder(PeriodicGeometry geometry) {
            super(geometry);
        }

        public PeriodicGeometryBuilder() {
            super();
        }

        @Override
        public PeriodicGeometry buildGeometry() {
            return new PeriodicGeometry(dimension, fullRMax, parameters);
        }

    }

    public static PeriodicGeometry defaultGeometry() {
        PeriodicGeometryBuilder builder = new PeriodicGeometryBuilder();
        return builder.buildGeometry();
    }

    public PeriodicGeometry(int dimension, double[] fullRMax, GeometricalParameters parameters) {
        super(dimension, fullRMax, parameters);
    }

    @Override
    public GeometryBuilder toBuilder() {
        return new PeriodicGeometryBuilder(this);
    }

    @Override
    public List<BeadRectangle> getRectanglesFromPositions(double[][] beadPositions) {
        List<BeadRectangle> beadRectangles;
        beadRectangles = getUnsplitRectanglesFromPositions(beadPositions);

        RectangleSplitter rectangleSplitter = new PeriodicRectangleSplitter();
        return rectangleSplitter.splitRectanglesOverBoundary(beadRectangles, makeLimits());
    }

    @Override
    public RectanglesAndGluedPerimeter getRectanglesAndPerimeterFromPositions(double[][] beadPositions) {
        List<BeadRectangle> beadRectangles;
        beadRectangles = getUnsplitRectanglesFromPositions(beadPositions);
        RectanglesAndBoundaryIntervals rectanglesAndBoundaryIntervals = new RectanglesAndBoundaryIntervals(beadRectangles);

        rectanglesAndBoundaryIntervals.splitOverPeriodicBoundary(makeLimits());

        double gluedPerimeter = 0;
        gluedPerimeter += OverlappingIntervalLengthFinder.getCoveredLengthOfIntervals(rectanglesAndBoundaryIntervals.intervals.get(1));
        gluedPerimeter += OverlappingIntervalLengthFinder.getCoveredLengthOfIntervals(rectanglesAndBoundaryIntervals.intervals.get(0));
        RectanglesAndGluedPerimeter rectanglesAndPerimeter;
        rectanglesAndPerimeter = new RectanglesAndGluedPerimeter(rectanglesAndBoundaryIntervals.rectangles, gluedPerimeter);
        return rectanglesAndPerimeter;
    }

    @Override
    public boolean incrementFirstVector(double[] toStep, double[] stepVector) {
        for (int i = 0; i < numDimensions; i++) {
            toStep[i] += stepVector[i];
            toStep[i] = projectComponent(toStep[i], i);
        }
        return true;
    }

    @Override
    public void decrementFirstVector(double[] toStep, double[] stepVector) {
        for (int i = 0; i < numDimensions; i++) {
            toStep[i] -= stepVector[i];
            toStep[i] = projectComponent(toStep[i], i);
        }
    }

    private double projectComponent(double component, int dimension) {
        if (component < 0) {
            component = (component % fullRMax[dimension]) + fullRMax[dimension];
        } else if (component > fullRMax[dimension]) {
            component = component % fullRMax[dimension];
        }
        return component;
    }

    @Override
    protected double calculateComponentDistance(double component1, double component2, int dimension) {
        double distance;
        distance = Math.abs(component1 - component2);
        distance = Math.min(distance, fullRMax[dimension] - distance);
        return distance;
    }

    @Override
    protected double calculateComponentDisplacement(double component1, double component2, int dimension) {
        double displacement;
        displacement = component1 - component2;
        final double displacementLimit = fullRMax[dimension] / 2;
        if (displacement > displacementLimit) {
            displacement -= fullRMax[dimension];
        } else if (displacement < -displacementLimit) {
            displacement += fullRMax[dimension];
        }
        return displacement;
    }

    @Override
    public void checkedCopyPosition(double[] src, double[] dest) {
        if (!isPositionValid(src)) {
            return;
        }
        System.arraycopy(src, 0, dest, 0, numDimensions);
        projectVector(dest);
    }

    @Override
    public boolean isPositionValid(double[] position) {
        return true;
    }

    private void projectVector(double[] position) {
        for (int i = 0; i < numDimensions; i++) {
            position[i] = projectComponent(position[i], i);
        }
    }

}
