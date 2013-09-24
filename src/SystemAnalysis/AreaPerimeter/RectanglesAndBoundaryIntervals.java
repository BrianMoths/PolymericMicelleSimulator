/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SystemAnalysis.AreaPerimeter;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author bmoths
 */
public class RectanglesAndBoundaryIntervals {

    public List<BeadRectangle> rectangles;
    public List<List<Interval>> intervals;

    public RectanglesAndBoundaryIntervals(List<BeadRectangle> beadRectangles) {
        this.rectangles = beadRectangles;

        List<Interval> verticalIntervals = new ArrayList<>();
        List<Interval> horizontalIntervals = new ArrayList<>();
        intervals.add(verticalIntervals);
        intervals.add(horizontalIntervals);
    }

    public RectanglesAndBoundaryIntervals(List<BeadRectangle> rectangles, List<List<Interval>> intervals) {
        this.rectangles = rectangles;
        this.intervals = intervals;
    }

    public void splitOverVerticalPeriodicBoundaries(double leftBoundary, double rightBoundary) {
        List<BeadRectangle> newRectangles = new ArrayList<>();
        List<Interval> horizontalIntervals = intervals.get(1);
        List<Interval> verticalIntervals = intervals.get(0);
        List<Interval> newHorizontalIntervals = new ArrayList<>();
        for (BeadRectangle beadRectangle : rectangles) {
            if (beadRectangle.left < leftBoundary) {
                newRectangles.add(new BeadRectangle(leftBoundary, beadRectangle.right, beadRectangle.top, beadRectangle.bottom));
                newRectangles.add(new BeadRectangle(beadRectangle.left + rightBoundary - leftBoundary, rightBoundary, beadRectangle.top, beadRectangle.bottom));
                verticalIntervals.add(new Interval(beadRectangle.bottom, beadRectangle.top));
            } else if (beadRectangle.right > rightBoundary) {
                newRectangles.add(new BeadRectangle(beadRectangle.left, rightBoundary, beadRectangle.top, beadRectangle.bottom));
                newRectangles.add(new BeadRectangle(leftBoundary, beadRectangle.right - (rightBoundary - leftBoundary), beadRectangle.top, beadRectangle.bottom));
                verticalIntervals.add(new Interval(beadRectangle.bottom, beadRectangle.top));
            } else {
                newRectangles.add(beadRectangle);
            }
        }
        rectangles = newRectangles;

        for (Interval horizontalInterval : horizontalIntervals) {
            if (horizontalInterval.start < leftBoundary) {
                newHorizontalIntervals.add(new Interval(leftBoundary, horizontalInterval.end));
                newHorizontalIntervals.add(new Interval(horizontalInterval.start + rightBoundary - leftBoundary, rightBoundary));
            } else if (horizontalInterval.end > rightBoundary) {
                newHorizontalIntervals.add(new Interval(horizontalInterval.start, rightBoundary));
                newHorizontalIntervals.add(new Interval(leftBoundary, horizontalInterval.end - rightBoundary + leftBoundary));
            } else {
                newHorizontalIntervals.add(horizontalInterval);
            }
        }
        intervals.set(1, newHorizontalIntervals);
    }

    public void splitOverHorizontalPeriodicBoundaries(double lowerBoundary, double upperBoundary) {
        List<BeadRectangle> newRectangles = new ArrayList<>();
        List<Interval> horizontalIntervals = intervals.get(1);
        List<Interval> verticalIntervals = intervals.get(0);
        List<Interval> newVerticalIntervals = new ArrayList<>();
        for (BeadRectangle beadRectangle : rectangles) {
            if (beadRectangle.bottom < lowerBoundary) {
                newRectangles.add(new BeadRectangle(beadRectangle.left, beadRectangle.right, beadRectangle.top, lowerBoundary));
                newRectangles.add(new BeadRectangle(beadRectangle.left, beadRectangle.right, upperBoundary, beadRectangle.bottom + upperBoundary - lowerBoundary));
                horizontalIntervals.add(new Interval(beadRectangle.left, beadRectangle.right));
            } else if (beadRectangle.top > upperBoundary) {
                newRectangles.add(new BeadRectangle(beadRectangle.left, beadRectangle.right, upperBoundary, beadRectangle.bottom));
                newRectangles.add(new BeadRectangle(beadRectangle.left, beadRectangle.right, beadRectangle.top - (upperBoundary - lowerBoundary), lowerBoundary));
                horizontalIntervals.add(new Interval(beadRectangle.left, beadRectangle.right));
            } else {
                newRectangles.add(beadRectangle);
            }
        }
        rectangles = newRectangles;

        for (Interval verticalInterval : verticalIntervals) {
            if (verticalInterval.start < lowerBoundary) {
                newVerticalIntervals.add(new Interval(lowerBoundary, verticalInterval.end));
                newVerticalIntervals.add(new Interval(verticalInterval.start + upperBoundary - lowerBoundary, upperBoundary));
            } else if (verticalInterval.end > upperBoundary) {
                newVerticalIntervals.add(new Interval(verticalInterval.start, upperBoundary));
                newVerticalIntervals.add(new Interval(lowerBoundary, verticalInterval.end - upperBoundary + lowerBoundary));
            } else {
                newVerticalIntervals.add(verticalInterval);
            }
        }
        intervals.set(0, newVerticalIntervals);
    }

}
