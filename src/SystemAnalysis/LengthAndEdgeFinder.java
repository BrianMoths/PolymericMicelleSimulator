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
public class LengthAndEdgeFinder {

    CoveredWeightAndEdgeCalculator lengthAndEdgeCalculator;
    List<NodeInterval> nodeIntervals;
    int nextEdgeIndex;

    static public LengthAndEdgeFinder makeFromIntervals(List<Interval> intervals) { //only can add, no removing
        LengthAndEdgeFinder overlappingIntervalLengthFinder = new LengthAndEdgeFinder();
        final IntervalEndpoints intervalEndpoints = IntervalEndpoints.makeFromIntervals(intervals);
        List<Integer> permutation = getPermutation(intervalEndpoints);
        overlappingIntervalLengthFinder.makeOverlappingIntervalLengthCalculator(intervalEndpoints, permutation);
        overlappingIntervalLengthFinder.makeNodeIntervals(permutation);
        return overlappingIntervalLengthFinder;
    }

    static public LengthAndEdgeFinder makeFromBeadRectangles(List<BeadRectangle> beadRectangles, List<Integer> xPermutation) {
        LengthAndEdgeFinder overlappingIntervalLengthFinder = new LengthAndEdgeFinder();
        final IntervalEndpoints intervalEndpoints = IntervalEndpoints.makeFromBeadRectangles(beadRectangles);
        List<Integer> permutation = getPermutation(intervalEndpoints); //todo move this and above line into one method
        overlappingIntervalLengthFinder.makeOverlappingIntervalLengthCalculator(intervalEndpoints, permutation);
        overlappingIntervalLengthFinder.makeNodeIntervalsWithBeadRectangles(permutation);

        overlappingIntervalLengthFinder.permuteNodeIntervals(xPermutation);

        return overlappingIntervalLengthFinder;
    }

    private LengthAndEdgeFinder() {
        nextEdgeIndex = 0;
    }

    private void makeOverlappingIntervalLengthCalculator(IntervalEndpoints intervalEndpoints, List<Integer> permutation) {
        List<Double> endPointDifferences = calculateEndpointDifferences(intervalEndpoints, permutation);

        lengthAndEdgeCalculator = new CoveredWeightAndEdgeCalculator(endPointDifferences);
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
        return lengthAndEdgeCalculator.getWeight();
    }

    public int getNumEdges() {
        return lengthAndEdgeCalculator.getNumEdges();
    }

    public void doNextStep() {
        NodeInterval nodeInterval = nodeIntervals.get(nextEdgeIndex);
        if (nodeInterval.isBeingAdded) {
            lengthAndEdgeCalculator.addCover(nodeInterval.start, nodeInterval.end);
        } else {
            lengthAndEdgeCalculator.removeCover(nodeInterval.start, nodeInterval.end);
        }
        nextEdgeIndex++;
    }
}
