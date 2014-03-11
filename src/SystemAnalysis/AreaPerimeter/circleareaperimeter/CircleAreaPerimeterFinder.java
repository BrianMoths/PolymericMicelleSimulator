/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SystemAnalysis.AreaPerimeter.circleareaperimeter;

import SystemAnalysis.AreaPerimeter.AreaPerimeter;
import SystemAnalysis.AreaPerimeter.rectangleareaperimeter.Interval;
import SystemAnalysis.AreaPerimeter.rectangleareaperimeter.OverlappingIntervalLengthFinder;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import kn.uni.voronoitreemap.j2d.Site;

/**
 *
 * @author bmoths
 */
public class CircleAreaPerimeterFinder extends PowerDiagramEvaluator<AreaPerimeter> {

    public static AreaPerimeter findAreaPerimeterOfCircles(final Iterable<Circle> circles, Rectangle2D boundaryRectangle) {
        AreaPerimeter areaPerimeter = CircleAreaPerimeterFinder.findAreaPerimeterOfCirclesNoClipPerimeter(circles, boundaryRectangle);
        areaPerimeter.perimeter += findClippedPerimeter(circles, boundaryRectangle);
        return areaPerimeter;
    }

    public static AreaPerimeter findAreaPerimeterOfCirclesPeriodic(final Iterable<Circle> circles, Rectangle2D boundaryRectangle) {
        final Iterable<Circle> wrappedCircles = new WrappedCircleIterable(circles, boundaryRectangle);
        return findAreaPerimeterOfCirclesNoClipPerimeter(wrappedCircles, boundaryRectangle);
    }

    public static AreaPerimeter findAreaPerimeterOfCirclesNoClipPerimeter(final Iterable<Circle> circles, Rectangle2D boundaryRectangle) {
        CircleAreaPerimeterFinder circleAreaPerimeterFinder = new CircleAreaPerimeterFinder();
        return circleAreaPerimeterFinder.computeValue(circles, boundaryRectangle);
    }

    static private double findClippedPerimeter(Iterable<Circle> circleIterable, Rectangle2D boundaryRectangle) {
        double perimeter = 0;
        perimeter += leftClippedPerimeter(circleIterable, boundaryRectangle);
        perimeter += rightClippedPerimeter(circleIterable, boundaryRectangle);
        perimeter += topClippedPerimeter(circleIterable, boundaryRectangle);
        perimeter += bottomClippedPerimeter(circleIterable, boundaryRectangle);
        return perimeter;
    }

    static private double leftClippedPerimeter(Iterable<Circle> circleIterable, Rectangle2D boundaryRectangle) {
        List<Interval> clippedIntervals = new ArrayList<>();
        for (Circle circle : circleIterable) {
            final double deltaX = Math.abs(boundaryRectangle.getMinX() - circle.getCenterX());
            if (deltaX < circle.getRadius()) {
                final double intervalHalfLength = Math.sqrt(circle.getRadius() * circle.getRadius() - deltaX * deltaX);
                final double intervalStart = Math.max(boundaryRectangle.getMinY(), circle.getCenterY() - intervalHalfLength);
                final double intervalEnd = Math.min(boundaryRectangle.getMaxY(), circle.getCenterY() + intervalHalfLength);
                clippedIntervals.add(new Interval(intervalStart, intervalEnd));
            }
        }
        return OverlappingIntervalLengthFinder.getCoveredLengthOfIntervals(clippedIntervals);
    }

    static private double rightClippedPerimeter(Iterable<Circle> circleIterable, Rectangle2D boundaryRectangle) {
        List<Interval> clippedIntervals = new ArrayList<>();
        for (Circle circle : circleIterable) {
            final double deltaX = Math.abs(boundaryRectangle.getMaxX() - circle.getCenterX());
            if (deltaX < circle.getRadius()) {
                final double intervalHalfLength = Math.sqrt(circle.getRadius() * circle.getRadius() - deltaX * deltaX);
                final double intervalStart = Math.max(boundaryRectangle.getMinY(), circle.getCenterY() - intervalHalfLength);
                final double intervalEnd = Math.min(boundaryRectangle.getMaxY(), circle.getCenterY() + intervalHalfLength);
                clippedIntervals.add(new Interval(intervalStart, intervalEnd));
            }
        }
        return OverlappingIntervalLengthFinder.getCoveredLengthOfIntervals(clippedIntervals);
    }

    static private double topClippedPerimeter(Iterable<Circle> circleIterable, Rectangle2D boundaryRectangle) {
        List<Interval> clippedIntervals = new ArrayList<>();
        for (Circle circle : circleIterable) {
            final double deltaY = Math.abs(boundaryRectangle.getMinY() - circle.getCenterY());
            if (deltaY < circle.getRadius()) {
                final double intervalHalfLength = Math.sqrt(circle.getRadius() * circle.getRadius() - deltaY * deltaY);
                final double intervalStart = Math.max(boundaryRectangle.getMinX(), circle.getCenterX() - intervalHalfLength);
                final double intervalEnd = Math.min(boundaryRectangle.getMaxX(), circle.getCenterX() + intervalHalfLength);
                clippedIntervals.add(new Interval(intervalStart, intervalEnd));
            }
        }
        return OverlappingIntervalLengthFinder.getCoveredLengthOfIntervals(clippedIntervals);
    }

    static private double bottomClippedPerimeter(Iterable<Circle> circleIterable, Rectangle2D boundaryRectangle) {
        List<Interval> clippedIntervals = new ArrayList<>();
        for (Circle circle : circleIterable) {
            final double deltaY = Math.abs(boundaryRectangle.getMaxY() - circle.getCenterY());
            if (deltaY < circle.getRadius()) {
                final double intervalHalfLength = Math.sqrt(circle.getRadius() * circle.getRadius() - deltaY * deltaY);
                final double intervalStart = Math.max(boundaryRectangle.getMinX(), circle.getCenterX() - intervalHalfLength);
                final double intervalEnd = Math.min(boundaryRectangle.getMaxX(), circle.getCenterX() + intervalHalfLength);
                clippedIntervals.add(new Interval(intervalStart, intervalEnd));
            }
        }
        return OverlappingIntervalLengthFinder.getCoveredLengthOfIntervals(clippedIntervals);
    }

    double area;
    double perimeter;
    double squareRadius;
    double radius;

    @Override
    protected void initialize() {
        area = 0;
        perimeter = 0;
    }

    @Override
    protected void initializeForSite(Site site) {
        squareRadius = site.getWeight();
        radius = Math.sqrt(squareRadius);
    }

    @Override
    protected void processNonIntersectingRegion(double deltaTheta) {
        area += squareRadius * deltaTheta / 2;
        perimeter += deltaTheta * radius;
    }

    @Override
    protected void processIntersectingRegion(double length, double y) {
        area -= length * y / 2;
    }

    @Override
    protected AreaPerimeter getValue() {
        return new AreaPerimeter(area, perimeter);
    }

}
