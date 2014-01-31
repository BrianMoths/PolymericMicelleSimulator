/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SystemAnalysis.AreaPerimeter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;

/**
 *
 * @author bmoths
 */
public class Interval {

    private EnumMap<BeadRectangle.Extreme, Double> endpoints;

    private Interval() {
        endpoints = new EnumMap<>(BeadRectangle.Extreme.class);
    }

    public Interval(double beginning, double end) {
        this();
        endpoints.put(BeadRectangle.Extreme.BEGINNING, beginning);
        endpoints.put(BeadRectangle.Extreme.END, end);
    }

    public Interval(Interval interval) {
        this.endpoints = new EnumMap<>(interval.endpoints);
    }

    public void setBeginning(double beginning) {
        setExtreme(beginning, BeadRectangle.Extreme.BEGINNING);
    }

    public void setEnd(double end) {
        setExtreme(end, BeadRectangle.Extreme.END);
    }

    public void setExtreme(double value, BeadRectangle.Extreme extreme) {
        endpoints.put(extreme, value);
    }

    public void setInterval(Interval interval) {
        for (BeadRectangle.Extreme extreme : BeadRectangle.Extreme.values()) {
            copyExtremeOfInterval(extreme, interval);
        }
    }

    public boolean isInBounds(Interval boundary) {
        for (BeadRectangle.Extreme extreme : BeadRectangle.Extreme.values()) {
            if (isExtremeOutOfBounds(extreme, boundary)) {
                return false;
            }
        }
        return true;
    }

    public boolean isPointContained(double point) {
        for (BeadRectangle.Extreme extreme : BeadRectangle.Extreme.values()) {
            if (!isExtremePastBoundary(extreme, point)) {
                return false;
            }
        }
        return true;
    }

    public Interval getIntersectionWIth(Interval boundary) {
        Interval interval = new Interval(this);
        interval.intersectWith(boundary);
        return interval;
    }

    public void intersectWith(Interval boundary) {
        for (BeadRectangle.Extreme extreme : BeadRectangle.Extreme.values()) {
            final boolean isExtremeOutOfBounds = isExtremeOutOfBounds(extreme, boundary);
            if (isExtremeOutOfBounds) {
                copyExtremeOfInterval(extreme, boundary);
            }
        }
    }

    public List<Interval> splitOverPeriodicBoundary(final Interval boundary) {

        class IntervalSplitter {

            private final BeadRectangle.Extreme extreme;
            private final BeadRectangle.Extreme otherExtreme;

            public IntervalSplitter(BeadRectangle.Extreme extreme, Interval boundary) {
                this.extreme = extreme;
                otherExtreme = extreme.other();
            }

            public boolean isSplitNeeded() {
                return isExtremeOutOfBounds(extreme, boundary);
            }

            public List<Interval> getSplitOverBoundary() {
                List<Interval> splitIntervals = new ArrayList<>();

                splitIntervals.add(makeInBoundsPiece());
                splitIntervals.add(makeWrappedPiece());

                return splitIntervals;
            }

            private Interval makeInBoundsPiece() {
                Interval inBoundsPiece = new Interval();

                inBoundsPiece.copyExtremeOfInterval(otherExtreme, Interval.this);
                inBoundsPiece.copyExtremeOfInterval(extreme, boundary);

                return inBoundsPiece;
            }

            private Interval makeWrappedPiece() {
                Interval wrappedPiece = new Interval();

                wrappedPiece.copyExtremeOfInterval(otherExtreme, boundary);
                wrappedPiece.setExtreme(getWrappedEndpoint(), extreme);

                return wrappedPiece;
            }

            private double getWrappedEndpoint() {
                return getExtreme(extreme) + getWrapTranslation();
            }

            private double getWrapTranslation() {
                return boundary.getExtreme(otherExtreme) - boundary.getExtreme(extreme);
            }

        }

        List<Interval> splitIntervals = new ArrayList<>();

        for (BeadRectangle.Extreme extreme : BeadRectangle.Extreme.values()) {
            IntervalSplitter intervalSplitter = new IntervalSplitter(extreme, boundary);
            if (intervalSplitter.isSplitNeeded()) {
                splitIntervals = intervalSplitter.getSplitOverBoundary();
            }
        }

        if (splitIntervals.isEmpty()) {
            splitIntervals.add(this);
        }

        return splitIntervals;
    }

    private void copyExtremeOfInterval(BeadRectangle.Extreme extreme, Interval boundary) {
        setExtreme(boundary.getExtreme(extreme), extreme);
    }

    public boolean isExtremeOutOfBounds(BeadRectangle.Extreme extreme, Interval boundary) {
        return isExtremePastBoundary(extreme, boundary.getExtreme(extreme));
    }

    public boolean isExtremePastBoundary(BeadRectangle.Extreme extreme, double boundary) {
        return extreme.isPositionOutOfBounds(getExtreme(extreme), boundary);
    }

    public Collection<Double> toCollection() {
        return endpoints.values();
    }

    public double getRange() {
        return getEnd() - getBeginning();
    }

    public double getExtreme(BeadRectangle.Extreme extreme) {
        return endpoints.get(extreme);
    }

    public double getBeginning() {
        return getExtreme(BeadRectangle.Extreme.BEGINNING);
    }

    public double getEnd() {
        return getExtreme(BeadRectangle.Extreme.END);
    }

}
