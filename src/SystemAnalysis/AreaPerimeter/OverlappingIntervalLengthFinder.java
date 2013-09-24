/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SystemAnalysis.AreaPerimeter;

import SystemAnalysis.CoveredWeight.NodeInterval;
import SystemAnalysis.CoveredWeight.CoveredWeightCalculator;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author bmoths
 */
public class OverlappingIntervalLengthFinder {

    static public OverlappingIntervalLengthFinder makeFromIntervals(List<Interval> intervals) { //only can add, no removing
        OverlappingIntervalLengthFinder overlappingIntervalLengthFinder = new OverlappingIntervalLengthFinder();

        final IntervalListEndpoints intervalListEndpoints = IntervalListEndpoints.endpointsOfIntervals(intervals);
        overlappingIntervalLengthFinder.initializeForIntervals(intervalListEndpoints);

        return overlappingIntervalLengthFinder;
    }

    static public OverlappingIntervalLengthFinder makeFromBeadRectangles(List<BeadRectangle> beadRectangles, List<Integer> xPermutation) {
        OverlappingIntervalLengthFinder overlappingIntervalLengthFinder = new OverlappingIntervalLengthFinder();

        final IntervalListEndpoints intervalListEndpoints = IntervalListEndpoints.endpointsOfVerticalRectangleEdges(beadRectangles);
        overlappingIntervalLengthFinder.initializeForBeadRectangles(intervalListEndpoints, xPermutation);

        return overlappingIntervalLengthFinder;
    }

    static public double getCoveredLengthOfIntervals(List<Interval> intervals) {
        OverlappingIntervalLengthFinder overlappingIntervalLengthFinder;
        overlappingIntervalLengthFinder = OverlappingIntervalLengthFinder.makeFromIntervals(intervals);
        overlappingIntervalLengthFinder.doAllSteps();
        return overlappingIntervalLengthFinder.getLength();
    }

    CoveredWeightCalculator overlappingIntervalLengthCalculator;
    List<NodeInterval> nodeIntervals;
    int nextEdgeIndex;

    private OverlappingIntervalLengthFinder() {
        nextEdgeIndex = 0;
    }

    private void initializeForIntervals(IntervalListEndpoints intervalListEndpoints) {
        List<Integer> permutation = intervalListEndpoints.getPermutation();
        makeOverlappingIntervalLengthCalculator(intervalListEndpoints, permutation);
        makeNodeIntervals(permutation);
    }

    private void initializeForBeadRectangles(IntervalListEndpoints intervalEndpoints, List<Integer> xPermutation) {
        List<Integer> permutation = intervalEndpoints.getPermutation();
        makeOverlappingIntervalLengthCalculator(intervalEndpoints, permutation);
        makeNodeIntervalsWithBeadRectangles(permutation);

        permuteNodeIntervals(xPermutation);
    }

    private void makeOverlappingIntervalLengthCalculator(IntervalListEndpoints intervalEndpoints, List<Integer> permutation) {
        List<Double> endPointDifferences = calculateEndpointDifferences(intervalEndpoints, permutation);

        overlappingIntervalLengthCalculator = new CoveredWeightCalculator(endPointDifferences);
    }

    private void makeNodeIntervals(List<Integer> permutation) {
        nodeIntervals = new ArrayList<>(permutation.size() / 2);
        for (int i = 0; i < permutation.size() / 2; i++) {
            nodeIntervals.add(new NodeInterval(0, 0, true));
        }
        for (int permutedIndex = 0; permutedIndex < permutation.size(); permutedIndex++) {
            final int linearEndpointIndex = permutation.get(permutedIndex);
            final int intervalIndex = IntervalListEndpoints.getIntervalFromLinearIndex(linearEndpointIndex);

            final NodeInterval nodeInterval = nodeIntervals.get(intervalIndex);

            if (IntervalListEndpoints.getIsStartFromLinearIndex(linearEndpointIndex)) {
                nodeInterval.start = permutedIndex;
            } else {
                nodeInterval.end = permutedIndex - 1; //indexForSortedYTop/Bottom specify endpoints, but addCover accepts intervals; endpoint 0 to endpoint 2 covers intervals 0 and 1, hence -1
            }
        }
    }

    private void makeNodeIntervalsWithBeadRectangles(List<Integer> permutation) {
        nodeIntervals = new ArrayList<>(permutation.size());
        for (int i = 0; i < permutation.size(); i++) {
            nodeIntervals.add(new NodeInterval(0, 0, IntervalListEndpoints.getIsStartFromLinearIndex(i)));
        }
        for (int permutedIndex = 0; permutedIndex < permutation.size(); permutedIndex++) {
            final int linearEndpointIndex = permutation.get(permutedIndex);
            final int intervalIndex = IntervalListEndpoints.getIntervalFromLinearIndex(linearEndpointIndex);

            final NodeInterval beginInterval = nodeIntervals.get(2 * intervalIndex);
            final NodeInterval endInterval = nodeIntervals.get(2 * intervalIndex + 1);

            if (IntervalListEndpoints.getIsStartFromLinearIndex(linearEndpointIndex)) {
                beginInterval.start = permutedIndex;
                endInterval.start = permutedIndex;
            } else {
                beginInterval.end = permutedIndex - 1; //indexForSortedYTop/Bottom specify endpoints, but addCover accepts intervals; endpoint 0 to endpoint 2 covers intervals 0 and 1, hence -1
                endInterval.end = permutedIndex - 1;
            }
        }
    }

    private void permuteNodeIntervals(List<Integer> xPermutation) {
        List<NodeInterval> permutedNodeIntervals = new ArrayList<>(nodeIntervals);
        for (int permutedIndex = 0; permutedIndex < xPermutation.size(); permutedIndex++) {
            permutedNodeIntervals.set(permutedIndex, nodeIntervals.get(xPermutation.get(permutedIndex)));
        }
        nodeIntervals = permutedNodeIntervals;
    }

    private List<Double> calculateEndpointDifferences(IntervalListEndpoints intervalEndpoints, List<Integer> permutation) {
        if (intervalEndpoints.size() == 0) {
            return new ArrayList<>();
        }
        List<Double> endpointDifferences = new ArrayList<>(intervalEndpoints.size() - 1);
        int largeEndpointIndex = permutation.get(0);
        int smallEndpointIndex;
        for (int i = 1; i < intervalEndpoints.size(); i++) {
            smallEndpointIndex = largeEndpointIndex;
            largeEndpointIndex = permutation.get(i);
            endpointDifferences.add(intervalEndpoints.getFromLinearIndex(largeEndpointIndex) - intervalEndpoints.getFromLinearIndex(smallEndpointIndex));
        }
        return endpointDifferences;
    }

    public double getLength() {
        return overlappingIntervalLengthCalculator.getWeight();
    }

    public void doNextStep() {
        NodeInterval nodeInterval = nodeIntervals.get(nextEdgeIndex);
        if (nodeInterval.isBeingAdded) {
            overlappingIntervalLengthCalculator.addCover(nodeInterval.start, nodeInterval.end);
        } else {
            overlappingIntervalLengthCalculator.removeCover(nodeInterval.start, nodeInterval.end);
        }
        nextEdgeIndex++;
    }

    public void doAllSteps() {
        while (nextEdgeIndex < nodeIntervals.size()) {
            doNextStep();
        }
    }

}
