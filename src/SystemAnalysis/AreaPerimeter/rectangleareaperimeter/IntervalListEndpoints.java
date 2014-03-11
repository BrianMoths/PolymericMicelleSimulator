/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SystemAnalysis.AreaPerimeter.rectangleareaperimeter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author bmoths
 */
public class IntervalListEndpoints {

    //<editor-fold defaultstate="expanded" desc="factories">
    static public IntervalListEndpoints endpointsOfIntervals(List<Interval> intervals) {
        IntervalListEndpoints intervalListEndpoints = new IntervalListEndpoints();

        intervalListEndpoints.endpoints = new ArrayList<>(2 * intervals.size());
        for (Interval interval : intervals) {
            intervalListEndpoints.endpoints.addAll(interval.toCollection());
        }

        return intervalListEndpoints;
    }

    static public IntervalListEndpoints endpointsOfVerticalRectangleEdges(List<BeadRectangle> beadRectangles) {
        return endpointsOfDimensionRectangleEdges(1, beadRectangles);
    }

    static public IntervalListEndpoints endpointsOfHorizontalRectangleEdges(List<BeadRectangle> beadRectangles) {
        return endpointsOfDimensionRectangleEdges(0, beadRectangles);
    }

    static public IntervalListEndpoints endpointsOfDimensionRectangleEdges(int dimension, List<BeadRectangle> beadRectangles) {
        IntervalListEndpoints intervalListEndpoints = new IntervalListEndpoints();

        intervalListEndpoints.endpoints = new ArrayList<>(2 * beadRectangles.size());
        for (BeadRectangle beadRectangle : beadRectangles) {
            for (BeadRectangle.Extreme extreme : BeadRectangle.Extreme.values()) {
                intervalListEndpoints.endpoints.add(beadRectangle.getExtremeOfDimension(extreme, dimension));
            }
        }

        return intervalListEndpoints;
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="static getters">
    static public int getLinearIndexFromCompoundIndex(int intervalIndex, boolean isStart) {
        return 2 * intervalIndex + (isStart ? 0 : 1);
    }

    static public int getIntervalIndexFromLinearIndex(int index) {
        return index / 2;
    }

    static public boolean getIsStartFromLinearIndex(int index) {
        return index % 2 == 0;
    }
    //</editor-fold>

    private List<Double> endpoints;

    private IntervalListEndpoints() {
    }

    public List<Integer> getPermutation() {
        List<Integer> permutation = new ArrayList<>(size());
        for (int i = 0; i < size(); i++) {
            permutation.add(i);
        }

        Comparator<Integer> endpointComparator = new Comparator<Integer>() {
            @Override
            public int compare(Integer integer1, Integer integer2) {
                return Double.compare(getFromLinearIndex(integer1), getFromLinearIndex(integer2));
            }

        };

        Collections.sort(permutation, endpointComparator);

        return permutation;
    }

    public void setFromCompoundIndex(int intervalIndex, boolean isStart, double endpoint) {
        endpoints.set(getLinearIndexFromCompoundIndex(intervalIndex, isStart), endpoint);
    }

    public void setFromLinearIndex(int linearIndex, double endpoint) {
        endpoints.set(linearIndex, endpoint);
    }

    public double getFromCompoundIndex(int intervalIndex, boolean isStart) {
        return endpoints.get(getLinearIndexFromCompoundIndex(intervalIndex, isStart));
    }

    public double getFromLinearIndex(int index) {
        return endpoints.get(index);
    }

    public int size() {
        return endpoints.size();
    }

}
