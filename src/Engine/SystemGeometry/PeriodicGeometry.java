/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine.SystemGeometry;

import Engine.SimulationParameters;
import Engine.TwoBeadOverlap;
import SystemAnalysis.BeadRectangle;
import SystemAnalysis.Interval;
import SystemAnalysis.OverlappingIntervalLengthFinder;
import SystemAnalysis.RectanglesAndIntervals;
import SystemAnalysis.RectanglesAndPerimeter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author bmoths
 */
public final class PeriodicGeometry extends AbstractGeometry {

    @Override
    public GeometryBuilder toBuilder() {
        return new PeriodicGeometryBuilder(this);
    }

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

    static public PeriodicGeometry defaultGeometry() {
        PeriodicGeometryBuilder builder = new PeriodicGeometryBuilder();
        return builder.buildGeometry();
    }

    public PeriodicGeometry(int dimension, double[] fullRMax, SimulationParameters parameters) {
        super(dimension, fullRMax, parameters);
    }

    @Override
    public RectanglesAndPerimeter getRectanglesAndPerimeterFromPositions(double[][] beadPositions) {
        List<BeadRectangle> beadRectangles = new ArrayList<>(beadPositions.length);
        for (double[] beadPosition : beadPositions) {
            beadRectangles.add(getRectangleFromPosition(beadPosition));
        }

        List<Interval> verticalIntervals = new ArrayList<>();
        List<Interval> horizontalIntervals = new ArrayList<>();
        List<List<Interval>> intervals = new ArrayList<>();
        intervals.add(verticalIntervals);
        intervals.add(horizontalIntervals);

        RectanglesAndIntervals rectanglesAndIntervals = new RectanglesAndIntervals(beadRectangles, intervals);
        rectanglesAndIntervals.splitOverVerticalPeriodicBoundaries(0, fullRMax[0]);
        rectanglesAndIntervals.splitOverHorizontalPeriodicBoundaries(0, fullRMax[1]);
        double gluedPerimeter = 0;
        gluedPerimeter += OverlappingIntervalLengthFinder.getCoveredLengthOfIntervals(rectanglesAndIntervals.intervals.get(1));
        gluedPerimeter += OverlappingIntervalLengthFinder.getCoveredLengthOfIntervals(rectanglesAndIntervals.intervals.get(0));
        RectanglesAndPerimeter rectanglesAndPerimeter;
        rectanglesAndPerimeter = new RectanglesAndPerimeter(rectanglesAndIntervals.rectangles, gluedPerimeter);
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
    public double areaOverlap(double[] position1, double[] position2) {
        double overlap = 1;

        for (int i = 0; i < dimension; i++) {
            overlap *= Math.max(parameters.getInteractionLength() - componentDistance(position1[i], position2[i], i), 0.0);
        }

        return overlap;
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
