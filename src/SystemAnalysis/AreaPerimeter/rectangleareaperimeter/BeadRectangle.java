/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SystemAnalysis.AreaPerimeter.rectangleareaperimeter;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

/**
 *
 * @author bmoths
 */
public class BeadRectangle {

    public static enum Extreme {

        BEGINNING(new ExtremeTester() {
            @Override
            public boolean isPositionOutOfBounds(double position, double boundary) {
                return position < boundary;
            }

        }),
        END(new ExtremeTester() {
            @Override
            public boolean isPositionOutOfBounds(double position, double boundary) {
                return position > boundary;
            }

        });
        static final EnumMap<Extreme, Extreme> otherMap;

        static {
            otherMap = new EnumMap<>(Extreme.class);
            otherMap.put(BEGINNING, END);
            otherMap.put(END, BEGINNING);
        }

        private final ExtremeTester extremeTester;

        private Extreme(ExtremeTester extremeTester) {
            this.extremeTester = extremeTester;
        }

        public Extreme other() {
            return otherMap.get(this);
        }

        public boolean isPositionOutOfBounds(double position, double boundary) {
            return extremeTester.isPositionOutOfBounds(position, boundary);
        }

        private static interface ExtremeTester {

            public boolean isPositionOutOfBounds(double position, double boundary);

        }

    }

    private final List<Interval> intervals;

    private BeadRectangle() {
        intervals = new ArrayList<>();
    }

    public BeadRectangle(double left, double right, double top, double bottom) {
        this();
        intervals.add(new Interval(left, right));
        intervals.add(new Interval(bottom, top));
    }

    public BeadRectangle(BeadRectangle beadRectangle) {
        this();
        for (int currentDimension = 0; currentDimension < beadRectangle.getNumDimensions(); currentDimension++) {
            intervals.add(new Interval(beadRectangle.getIntervalOfDimension(currentDimension)));
        }
    }

    public BeadRectangle getIntersectionWith(BeadRectangle boundary) {
        BeadRectangle beadRectangle = new BeadRectangle(this);
        beadRectangle.intersectWith(boundary);
        return beadRectangle;
    }

    public void intersectWith(BeadRectangle boundary) {
        final int dimension = getNumDimensions();
        for (int currentDimension = 0; currentDimension < dimension; currentDimension++) {
            intersectAlongDimension(boundary, dimension);
        }
    }

    private void intersectAlongDimension(BeadRectangle boundary, int dimension) {
        Interval intervalOfDimension = getIntervalOfDimension(dimension);
        Interval boundaryIntervalOfDimension = boundary.getIntervalOfDimension(dimension);
        intervalOfDimension.intersectWith(boundaryIntervalOfDimension);
    }

    public List<BeadRectangle> splitOverPeriodicBoundary(final BeadRectangle boundary) {
        List<BeadRectangle> oldSplitBeadRectangles = new ArrayList<>();
        oldSplitBeadRectangles.add(this);
        List<BeadRectangle> newSplitBeadRectangles = new ArrayList<>();
        final int numDimensions = getNumDimensions();

        for (int dimension = 0; dimension < numDimensions; dimension++) {
            newSplitBeadRectangles = new ArrayList<>();
            for (BeadRectangle beadRectangle : oldSplitBeadRectangles) {
                newSplitBeadRectangles.addAll(beadRectangle.getSplitOverPeriodicBoundaryOfDimension(boundary, dimension));
            }
            oldSplitBeadRectangles = newSplitBeadRectangles;
        }
        return newSplitBeadRectangles;
    }

    public List<BeadRectangle> getSplitOverPeriodicBoundaryOfDimension(BeadRectangle boundary, int dimension) {
        final Interval boundaryInterval = boundary.getIntervalOfDimension(dimension);
        return getSplitOverPeriodicIntervalOfDimension(boundaryInterval, dimension);
    }

    public List<BeadRectangle> getSplitOverPeriodicIntervalOfDimension(Interval boundaryInterval, int dimension) {
        List<BeadRectangle> splitBeadRectangles = new ArrayList<>();

        Interval intervalOfDimension = getIntervalOfDimension(dimension);
        List<Interval> splitInterval = intervalOfDimension.splitOverPeriodicBoundary(boundaryInterval);

        for (Interval interval : splitInterval) {
            BeadRectangle beadRectangle = new BeadRectangle(this);
            beadRectangle.setIntervalOfDimension(interval, dimension);
            splitBeadRectangles.add(beadRectangle);
        }

        return splitBeadRectangles;
    }

    public boolean isPointContained(double[] point) {
        if (point.length != getNumDimensions()) {
            return false;
        }
        for (int dimension = 0; dimension < getNumDimensions(); dimension++) {
            final Interval intervalOfDimension = getIntervalOfDimensionReference(dimension);
            if (!intervalOfDimension.isPointContained(point[dimension])) {
                return false;
            }
        }
        return true;
    }

    public void setBeginningOfDimension(double start, int dimension) {
        setExtremeOfDimension(start, Extreme.BEGINNING, dimension);
    }

    public void setEndOfDimension(double end, int dimension) {
        setExtremeOfDimension(end, Extreme.END, dimension);
    }

    public void setExtremeOfDimension(double position, Extreme extreme, int dimension) {
        final Interval interval = getIntervalOfDimensionReference(dimension);
        interval.setExtreme(position, extreme);
    }

    public void setIntervalOfDimension(Interval interval, int dimension) {
        final Interval intervalOfDimension = getIntervalOfDimensionReference(dimension);
        intervalOfDimension.setInterval(interval);
    }

    public Interval getIntervalOfDimension(int dimension) {
        return new Interval(intervals.get(dimension));
    }

    private Interval getIntervalOfDimensionReference(int dimension) {
        return intervals.get(dimension);
    }

    public int getNumDimensions() {
        return intervals.size();
    }

    public double getLeft() {
        return getStartOfDimension(0);
    }

    public double getRight() {
        return getEndOfDimension(0);
    }

    public double getTop() {
        return getEndOfDimension(1);
    }

    public double getBottom() {
        return getStartOfDimension(1);
    }

    public double getStartOfDimension(int dimension) {
        return getExtremeOfDimension(Extreme.BEGINNING, dimension);
    }

    public double getEndOfDimension(int dimension) {
        return getExtremeOfDimension(Extreme.END, dimension);
    }

    public double getExtremeOfDimension(Extreme extreme, int dimension) {
        final Interval interval = getIntervalOfDimension(dimension);
        return interval.getExtreme(extreme);
    }

    public double getHeight() {
        return getRangeOfDimension(1);
    }

    public double getWidth() {
        return getRangeOfDimension(0);
    }

    public double getVolume() {
        double volume = 1.;
        for (int dimension = 0; dimension < getNumDimensions(); dimension++) {
            volume *= getRangeOfDimension(dimension);
        }
        return volume;
    }

    public double getRangeOfDimension(int dimension) {
        final Interval interval = getIntervalOfDimension(dimension);
        return interval.getRange();
    }

}
