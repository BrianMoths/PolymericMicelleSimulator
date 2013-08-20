/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SystemAnalysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author bmoths
 */
public class OverlappingIntervalLengthFinder {

    CoveredWeightCalculator overlappingIntervalLengthCalculator;
    List<NodeInterval> nodeIntervals;
    int nextEdgeIndex;

    static public OverlappingIntervalLengthFinder makeFromIntervals(List<Interval> intervals) { //only can add, no removing
        OverlappingIntervalLengthFinder overlappingIntervalLengthFinder = new OverlappingIntervalLengthFinder();
        final IntervalEndpoints intervalEndpoints = IntervalEndpoints.makeFromIntervals(intervals);
        List<Integer> permutation = getPermutation(intervalEndpoints);
        overlappingIntervalLengthFinder.makeOverlappingIntervalLengthCalculator(intervalEndpoints, permutation);
        overlappingIntervalLengthFinder.makeNodeIntervals(permutation);
        return overlappingIntervalLengthFinder;
    }

    static public OverlappingIntervalLengthFinder makeFromBeadRectangles(List<BeadRectangle> beadRectangles, List<Integer> xPermutation) {
        OverlappingIntervalLengthFinder overlappingIntervalLengthFinder = new OverlappingIntervalLengthFinder();
        final IntervalEndpoints intervalEndpoints = IntervalEndpoints.makeFromBeadRectangles(beadRectangles);
        List<Integer> permutation = getPermutation(intervalEndpoints);
        overlappingIntervalLengthFinder.makeOverlappingIntervalLengthCalculator(intervalEndpoints, permutation);
        overlappingIntervalLengthFinder.makeNodeIntervalsWithBeadRectangles(permutation);

        overlappingIntervalLengthFinder.permuteNodeIntervals(xPermutation);

        return overlappingIntervalLengthFinder;
    }

    static public double getCoveredLengthOfIntervals(List<Interval> intervals) {
        OverlappingIntervalLengthFinder overlappingIntervalLengthFinder;
        overlappingIntervalLengthFinder = OverlappingIntervalLengthFinder.makeFromIntervals(intervals);
        overlappingIntervalLengthFinder.doAllSteps();
        return overlappingIntervalLengthFinder.getLength();
    }

    private OverlappingIntervalLengthFinder() {
        nextEdgeIndex = 0;
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

    private void makeOverlappingIntervalLengthCalculator(IntervalEndpoints intervalEndpoints, List<Integer> permutation) {
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
            final int intervalIndex = IntervalEndpoints.getIntervalFromLinearIndex(linearEndpointIndex);

            final NodeInterval nodeInterval = nodeIntervals.get(intervalIndex);

            if (IntervalEndpoints.getIsStartFromLinearIndex(linearEndpointIndex)) {
                nodeInterval.start = permutedIndex;
            } else {
                nodeInterval.end = permutedIndex - 1; //indexForSortedYTop/Bottom specify endpoints, but addCover accepts intervals; endpoint 0 to endpoint 2 covers intervals 0 and 1, hence -1
            }
        }
    }

    private void makeNodeIntervalsWithBeadRectangles(List<Integer> permutation) {
        nodeIntervals = new ArrayList<>(permutation.size());
        for (int i = 0; i < permutation.size(); i++) {
            nodeIntervals.add(new NodeInterval(0, 0, IntervalEndpoints.getIsStartFromLinearIndex(i)));
        }
        for (int permutedIndex = 0; permutedIndex < permutation.size(); permutedIndex++) {
            final int linearEndpointIndex = permutation.get(permutedIndex);
            final int intervalIndex = IntervalEndpoints.getIntervalFromLinearIndex(linearEndpointIndex);

            final NodeInterval beginInterval = nodeIntervals.get(2 * intervalIndex);
            final NodeInterval endInterval = nodeIntervals.get(2 * intervalIndex + 1);

            if (IntervalEndpoints.getIsStartFromLinearIndex(linearEndpointIndex)) {
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

    static public List<Integer> getPermutation(final IntervalEndpoints intervalEndpoints) {
        List<Integer> permutation = new ArrayList<>(intervalEndpoints.size());
        for (int i = 0; i < intervalEndpoints.size(); i++) {
            permutation.add(i);
        }

        Comparator<Integer> endpointComparator = new Comparator<Integer>() {
            @Override
            public int compare(Integer integer1, Integer integer2) {
                return Double.compare(intervalEndpoints.getFromLinearIndex(integer1), intervalEndpoints.getFromLinearIndex(integer2));
            }
        };

        Collections.sort(permutation, endpointComparator);

        return permutation;
    }

    private List<Double> calculateEndpointDifferences(IntervalEndpoints intervalEndpoints, List<Integer> permutation) {
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
}
