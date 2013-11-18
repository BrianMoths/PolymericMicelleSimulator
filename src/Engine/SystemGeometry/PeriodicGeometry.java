/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine.SystemGeometry;

import Engine.TwoBeadOverlap;
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
    public double sqDist(double[] position1, double[] position2) {
        double sqDist = 0;
        double distance;
        for (int i = 0; i < dimension; i++) {
            distance = componentDistance(position1[i], position2[i], i);
            sqDist += distance * distance;
        }
        return sqDist;
    }

    @Override
    public TwoBeadOverlap twoBeadOverlap(double[] position1, double[] position2) {
        TwoBeadOverlap twoBeadOverlap = new TwoBeadOverlap(1, 1);

        for (int i = 0; i < dimension; i++) {
            double componentDistance = componentDistance(position1[i], position2[i], i);
            twoBeadOverlap.softOverlap *= Math.max(parameters.getInteractionLength() - componentDistance, 0.0);
            twoBeadOverlap.hardOverlap *= Math.max(parameters.getCoreLength() - componentDistance, 0.0);
        }

        return twoBeadOverlap;
    }

    @Override
    public boolean incrementFirstVector(double[] toStep, double[] stepVector) {
        for (int i = 0; i < dimension; i++) {
            toStep[i] += stepVector[i];
            toStep[i] = projectComponent(toStep[i], i);
        }
        return true;
    }

    @Override
    public void decrementFirstVector(double[] toStep, double[] stepVector) {
        for (int i = 0; i < dimension; i++) {
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

    private double componentDistance(double component1, double component2, int dimension) {
        double distance;
        distance = Math.abs(component1 - component2);
        distance = Math.min(distance, fullRMax[dimension] - distance);
        return distance;
    }

    @Override
    public void checkedCopyPosition(double[] src, double[] dest) {
        if (!isPositionValid(src)) {
            return;
        }
        System.arraycopy(src, 0, dest, 0, dimension);
        projectVector(dest);
    }

    @Override
    public boolean isPositionValid(double[] position) {
        return true;
    }

    private void projectVector(double[] position) {
        for (int i = 0; i < dimension; i++) {
            position[i] = projectComponent(position[i], i);
        }
    }

}
