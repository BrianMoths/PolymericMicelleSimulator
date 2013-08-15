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
    
    static public class Interval {
        
        public double start, end;
        
        public Interval(double start, double end) {
            this.start = start;
            this.end = end;
        }
    }
    
    static public class IntervalEndpoints {
        
        List<Double> endpoints;
        
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
        
        static public int getLinearIndexFromCompoundIndex(int intervalIndex, boolean isStart) {
            return 2 * intervalIndex + (isStart ? 0 : 1);
        }
        
        static public int getIntervalFromLinearIndex(int index) {
            return index / 2;
        }
        
        static public boolean getIsStartFromLinearIndex(int index) {
            return index % 2 == 0;
        }
        
        public int size() {
            return endpoints.size();
        }
    }
    
    static private class NodeInterval {
        
        int start, end;
        boolean isBeingAdded;
        
        public NodeInterval(int start, int end, boolean isBeingAdded) {
            this.start = start;
            this.end = end;
            this.isBeingAdded = isBeingAdded;
        }
    }
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
    
    private OverlappingIntervalLengthFinder() {
        nextEdgeIndex = 0;
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
}
