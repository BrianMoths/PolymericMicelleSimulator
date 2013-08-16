/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SystemAnalysis;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author bmoths
 */
public class IntervalEndpoints {

    static public int getLinearIndexFromCompoundIndex(int intervalIndex, boolean isStart) {
        return 2 * intervalIndex + (isStart ? 0 : 1);
    }

    static public int getIntervalFromLinearIndex(int index) {
        return index / 2;
    }

    static public boolean getIsStartFromLinearIndex(int index) {
        return index % 2 == 0;
    }
    private List<Double> endpoints;

    private IntervalEndpoints() {
    }

    static public IntervalEndpoints makeFromIntervals(List<Interval> intervals) {
        IntervalEndpoints intervalEndpoints = new IntervalEndpoints();

        intervalEndpoints.endpoints = new ArrayList<>(2 * intervals.size());
        for (Interval interval : intervals) {
            intervalEndpoints.endpoints.add(interval.start);
            intervalEndpoints.endpoints.add(interval.end);
        }

        return intervalEndpoints;
    }

    static public IntervalEndpoints makeFromBeadRectangles(List<BeadRectangle> beadRectangles) {
        IntervalEndpoints intervalEndpoints = new IntervalEndpoints();

        intervalEndpoints.endpoints = new ArrayList<>(2 * beadRectangles.size());
        for (BeadRectangle beadRectangle : beadRectangles) {
            intervalEndpoints.endpoints.add(beadRectangle.bottom);
            intervalEndpoints.endpoints.add(beadRectangle.top);
        }

        return intervalEndpoints;
    }

    static public IntervalEndpoints makeFromBeadRectanglesHorizontal(List<BeadRectangle> beadRectangles) {
        IntervalEndpoints intervalEndpoints = new IntervalEndpoints();

        intervalEndpoints.endpoints = new ArrayList<>(2 * beadRectangles.size());
        for (BeadRectangle beadRectangle : beadRectangles) {
            intervalEndpoints.endpoints.add(beadRectangle.left);
            intervalEndpoints.endpoints.add(beadRectangle.right);
        }

        return intervalEndpoints;
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
