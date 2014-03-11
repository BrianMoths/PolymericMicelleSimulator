/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SystemAnalysis.AreaPerimeter.rectangleareaperimeter.RectangleSplitting;

import SystemAnalysis.AreaPerimeter.rectangleareaperimeter.BeadRectangle;
import SystemAnalysis.AreaPerimeter.rectangleareaperimeter.Interval;
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

        intervals = new ArrayList<>();
        List<Interval> verticalIntervals = new ArrayList<>();
        List<Interval> horizontalIntervals = new ArrayList<>();
        intervals.add(verticalIntervals);
        intervals.add(horizontalIntervals);
    }

    public RectanglesAndBoundaryIntervals(List<BeadRectangle> rectangles, List<List<Interval>> intervals) {
        this.rectangles = rectangles;
        this.intervals = intervals;
    }

    public void splitOverPeriodicBoundary(BeadRectangle boundary) {
        int dimension = getDimension();
        for (int currentDimension = 0; currentDimension < dimension; currentDimension++) {
            splitOverPeriodicBoundaryDimension(boundary, currentDimension);
        }
    }

    private void splitOverPeriodicBoundaryDimension(BeadRectangle boundary, int dimension) {
        Interval boundaryInterval = boundary.getIntervalOfDimension(dimension);
        List<Interval> gluedIntervalsOfOtherDimension = intervals.get(1 - dimension); //this is a hack, I should be dealing with faces not intervals
        List<Interval> gluedIntervalsOfDimension = intervals.get(dimension);
        List<BeadRectangle> newRectangles = new ArrayList<>();

        for (BeadRectangle beadRectangle : rectangles) {
            List<BeadRectangle> splitBeadRectangles = beadRectangle.getSplitOverPeriodicIntervalOfDimension(boundaryInterval, dimension);
            newRectangles.addAll(splitBeadRectangles);
            if (splitBeadRectangles.size() > 1) {
                gluedIntervalsOfOtherDimension.add(beadRectangle.getIntervalOfDimension(1 - dimension));
            }
        }
        rectangles = newRectangles;

        List<Interval> newIntervalsOfDimension = new ArrayList<>();
        for (Interval interval : gluedIntervalsOfDimension) {
            newIntervalsOfDimension.addAll(interval.splitOverPeriodicBoundary(boundaryInterval));
        }
        intervals.set(dimension, newIntervalsOfDimension);
    }

    private int getDimension() {
        return intervals.size();
    }

}
