/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SystemAnalysis.AreaPerimeter.circleareaperimeter;

import SystemAnalysis.AreaPerimeter.rectangleareaperimeter.Interval;
import SystemAnalysis.AreaPerimeter.rectangleareaperimeter.OverlappingIntervalLengthFinder;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author bmoths
 */
public class BoundaryPerimeterFinder {

    public static double findClippedPerimeter(Iterable<Circle> circleIterable, Rectangle2D boundaryRectangle) {
        double perimeter = 0;
        perimeter += leftClippedPerimeter(circleIterable, boundaryRectangle);
        perimeter += rightClippedPerimeter(circleIterable, boundaryRectangle);
        perimeter += topClippedPerimeter(circleIterable, boundaryRectangle);
        perimeter += bottomClippedPerimeter(circleIterable, boundaryRectangle);
        return perimeter;
    }

    private static double bottomClippedPerimeter(Iterable<Circle> circleIterable, Rectangle2D boundaryRectangle) {
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

    private static double rightClippedPerimeter(Iterable<Circle> circleIterable, Rectangle2D boundaryRectangle) {
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

    private static double leftClippedPerimeter(Iterable<Circle> circleIterable, Rectangle2D boundaryRectangle) {
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

    private static double topClippedPerimeter(Iterable<Circle> circleIterable, Rectangle2D boundaryRectangle) {
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

}
