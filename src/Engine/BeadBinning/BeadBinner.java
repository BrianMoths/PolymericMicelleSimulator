/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine.BeadBinning;

import Engine.PolymerState.SystemGeometry.Interfaces.ImmutableSystemGeometry;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * A class which keeps track of the positions of a collection of beads within a
 * specified geometry and can return an iterator guaranteed to iterate over a
 * superset of beads within an interaction length of a given bead or position.
 * Positions may be updated by passing an array containing all positions or by
 * specifying a new position of a single bead.
 *
 * @author bmoths
 * @see ImmutableSystemGeometry
 */
public class BeadBinner implements Serializable {

    static private class BinIndex implements Serializable {

        private static final long serialVersionUID = 0L;

        static BinIndex addIndices(BinIndex firstSummand, List<Integer> secondSummand) { //TODO make projected sum
            final int numDimensions = firstSummand.indices.size();

            BinIndex sum = new BinIndex(numDimensions);

            for (int dimension = 0; dimension < numDimensions; dimension++) {
                final int newIndexOfDimension = firstSummand.indices.get(dimension) + secondSummand.get(dimension);
                sum.setIndexOfDimension(dimension, newIndexOfDimension);
            }

            return sum;
        }

        static BinIndex subtractIndices(BinIndex firstSummand, List<Integer> secondSummand) { //TODO make projected sum
            final int numDimensions = firstSummand.indices.size();

            BinIndex sum = new BinIndex(numDimensions);

            for (int dimension = 0; dimension < numDimensions; dimension++) {
                final int newIndexOfDimension = firstSummand.indices.get(dimension) - secondSummand.get(dimension);
                sum.setIndexOfDimension(dimension, newIndexOfDimension);
            }

            return sum;
        }

//        public int x, y;
        private final List<Integer> indices;

        public BinIndex(int numDimensions) {
            indices = new ArrayList<>(numDimensions);
            for (int dimension = 0; dimension < numDimensions; dimension++) {
                indices.add(0);
            }
        }

        public BinIndex(BinIndex binIndex) {
            this.indices = new ArrayList<>(binIndex.indices);
        }

        public void setIndexOfDimension(int dimension, int value) {
            indices.set(dimension, value);
        }

        public int getIndexOfDimension(int dimension) {
            return indices.get(dimension);
        }

        public List<Integer> getIndices() {
            return indices;
        }

    }

    private class NearbyBeadIterator implements Iterator<Integer> {

        private final BinIndex binIndex;
        private Iterator<Integer> beadIterator;
        final List<Integer> currentBinOffset;
        private boolean isOutOfBins;

        public NearbyBeadIterator(BinIndex binIndex) {
            currentBinOffset = new ArrayList<>(initialBinOffset);
            isOutOfBins = false;
            this.binIndex = new BinIndex(binIndex);
            updateBeadIterator();
        }

        @Override
        public boolean hasNext() {
            if (beadIterator.hasNext()) {//TODO keep track of how many beads are left in each bin.
                return true;
            } else {
                goToNextNonEmptyBin();
                return !isOutOfBins();
            }
        }

        @Override
        public Integer next() {
            if (beadIterator.hasNext()) {
                return beadIterator.next();
            } else {
                goToNextNonEmptyBin();
                return beadIterator.next();
            }
        }

        private void goToNextNonEmptyBin() {
            while (!beadIterator.hasNext() && !isOutOfBins()) {
                iterateBin();
            }
        }

        private void iterateBin() {
            iterateBinWithDimension(numDimensions - 1);

            updateBeadIterator();
        }

        private void iterateBinWithDimension(int dimension) { //highest dimension is least significant
            int offsetOfDimension = currentBinOffset.get(dimension);
            offsetOfDimension++;
            if (isCurrentBinOffsetTooHigh(offsetOfDimension, dimension)) {
                if (dimension > 0) {
                    resetOffsetOfDimension(dimension);
                    iterateBinWithDimension(dimension - 1);
                } else {
                    isOutOfBins = true;
                }
            } else {
                currentBinOffset.set(dimension, offsetOfDimension);
            }
        }

        private boolean isCurrentBinOffsetTooHigh(int offsetOfDimension, int dimension) {
            return offsetOfDimension > ranges.get(dimension);
        }

        private void resetOffsetOfDimension(int dimension) {
            currentBinOffset.set(dimension, -ranges.get(dimension));
        }

        private void updateBeadIterator() {
            BinIndex neighboringBinIndex = BinIndex.addIndices(binIndex, currentBinOffset);
            projectBinIndex(neighboringBinIndex);
            final Set<Integer> neighboringBin = getBin(neighboringBinIndex);
            beadIterator = neighboringBin.iterator();
        }

        private boolean isOutOfBins() {
            return isOutOfBins;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }

    private static final long serialVersionUID = 0L;
    private final int numDimensions;
    private final double[] binSize;
    private final int[] numBins;
    private MultidimensionalArray<Set<Integer>> beadBins;
    private boolean isStepDone = false;
    private List<BinIndex> binIndices;
    private final List<Integer> ranges;
    private final List<Integer> initialBinOffset; //TODO make a list of bin offsets.
//    private final List<List<Integer>> binOffsets;

    /**
     * Constructs a <tt>BeadBinner</tt> which is designed to track bead
     * movements in a space with the specified geometry
     *
     * @param beadPositions the current positions of the beads to be tracked
     * @param systemGeometry the geometry of the space the beads will be moved
     * in
     */
    public BeadBinner(double[][] beadPositions, ImmutableSystemGeometry systemGeometry) {
        final double[] rMax = systemGeometry.getRMax();
        final double interactionLength = systemGeometry.getGeometricalParameters().getInteractionLength();
        numDimensions = systemGeometry.getNumDimensions();
        numBins = new int[numDimensions];
        binSize = new double[numDimensions];
        for (int dimension = 0; dimension < numDimensions; dimension++) {
            numBins[dimension] = (int) Math.floor(rMax[dimension] / interactionLength);
            if (numBins[dimension] < 4) {
                numBins[dimension] = 1;
            }
            binSize[dimension] = rMax[dimension] / numBins[dimension];
        }
        ranges = makeRanges();
        initialBinOffset = makeInitialBinOffset();
//        binOffsets = makeBinOffsets();
        binBeadsPrivate(beadPositions);
    }

    /**
     * Constructs a copy of the specified <tt>BeadBinner</tt>.
     *
     * @param beadBinner the <tt>BeadBinner</tt> to be copied
     */
    public BeadBinner(BeadBinner beadBinner) {
        binIndices = new ArrayList<>();
        for (BinIndex binIndex : beadBinner.binIndices) {
            binIndices.add(new BinIndex(binIndex));
        }
        numDimensions = beadBinner.numDimensions;
        binSize = beadBinner.binSize;
        numBins = beadBinner.numBins;
        beadBins = new MultidimensionalArray<>(beadBinner.beadBins);
        isStepDone = beadBinner.isStepDone;
        ranges = new ArrayList<>(beadBinner.ranges);
        initialBinOffset = new ArrayList<>(beadBinner.initialBinOffset);
//        binOffsets = makeBinOffsets();
    }

    //<editor-fold defaultstate="collapsed" desc="constructor helpers">
    private List<Integer> makeRanges() {
        List<Integer> rangesLocal;
        rangesLocal = new ArrayList<>(numDimensions);

        for (int dimension = 0; dimension < numDimensions; dimension++) {
            rangesLocal.add(getRangeForDimension(dimension));
        }

        return rangesLocal;
    }

    private int getRangeForDimension(int dimension) {
        final int numBinsOfDimension = numBins[dimension];
        if (numBinsOfDimension > 3) {
            return 1;
        } else {
            return 0;
        }
    }

    private List<Integer> makeInitialBinOffset() {
        List<Integer> currentBinOffsetLocal;
        currentBinOffsetLocal = new ArrayList<>(numDimensions);
        for (Integer range : ranges) {
            currentBinOffsetLocal.add(-range);
        }
        return currentBinOffsetLocal;
    }
    //</editor-fold>

    /**
     * resets the state of the <tt>BeadBinner</tt> to be consistent with the
     * specified bead positions. This would be necessary if the bead positions
     * are reset to a random configuration or otherwise all changed at once.
     *
     * @param beadPositions the positions of the beads
     */
    public void binBeads(double[][] beadPositions) {
        binBeadsPrivate(beadPositions);
    }

    private void binBeadsPrivate(double[][] beadPositions) {
        allocateBeadBins();
        binIndices = new ArrayList<>(beadPositions.length);
        for (int i = 0; i < beadPositions.length; i++) {
            binIndices.add(new BinIndex(numDimensions));
        }
        for (int bead = 0; bead < beadPositions.length; bead++) {
            addBeadAt(bead, beadPositions[bead]);
        }
    }

    private void allocateBeadBins() {
        beadBins = new MultidimensionalArray<>(numBins);
        Iterator<List<Integer>> indexIterator = beadBins.getIndexIterator();
        while (indexIterator.hasNext()) {
            final List<Integer> index = indexIterator.next();
            beadBins.set(index, new HashSet<Integer>());
        }
    }

    /**
     * updates the bead specified beads position to be the specified position
     *
     * @param stepBead the bead whose position is to be updated
     * @param newBeadPosition the new position of the bead
     */
    public void updateBeadPosition(int stepBead, double[] newBeadPosition) {
        removeBead(stepBead);
        addBeadAt(stepBead, newBeadPosition);
    }

    private void removeBead(int bead) {
        BinIndex binIndex = binIndices.get(bead);
        removeBeadFromBin(bead, binIndex);
    }

    private void removeBeadFromBin(int bead, BinIndex binIndex) {
        getBin(binIndex).remove(bead);
    }

    private void addBeadAt(int bead, double[] position) {
        final BinIndex index = getBinIndex(position);
        addBeadToBin(bead, index);
    }

    private void addBeadToBin(int bead, BinIndex binIndex) {
        getBin(binIndex).add(bead);
        binIndices.set(bead, binIndex);
    }

    private Set<Integer> getBin(BinIndex binIndex) {
        return beadBins.get(binIndex.getIndices());
    }

    private BinIndex getBinIndex(double[] position) {
        BinIndex binIndex = new BinIndex(numDimensions);
        for (int dimension = 0; dimension < numDimensions; dimension++) {
            final int indexOfDimension = calculateBinIndexForPositionOfDimension(position, dimension);
            binIndex.setIndexOfDimension(dimension, indexOfDimension);
        }
        return binIndex;
    }

    private int calculateBinIndexForPositionOfDimension(double[] position, int dimension) {
        return (int) Math.floor(position[dimension] / binSize[dimension]);
    }

    private void projectBinIndex(BinIndex binIndex) {
        for (int dimension = 0; dimension < numDimensions; dimension++) {
            final int projectedIndexOfDimension = calculateProjectedIndexOfDimension(dimension, binIndex.getIndexOfDimension(dimension));
            binIndex.setIndexOfDimension(dimension, projectedIndexOfDimension);
        }
    }

    private int calculateProjectedIndexOfDimension(int dimension, int indexOfDimension) {
        if (indexOfDimension < 0) {
            indexOfDimension += numBins[dimension];
        } else if (indexOfDimension >= numBins[dimension]) {
            indexOfDimension -= numBins[dimension];
        }

        return indexOfDimension;
    }

    /**
     * returns an iterator which is guaranteed to contain the indices of all
     * beads "close" to the given bead. By "close" we mean within the
     * interaction length specified by the geometry specified in the
     * constructor.
     *
     * @param bead the bead whose neighbors are to be found
     * @return an iterator giving the neighbors of the given bead
     */
    public Iterator<Integer> getNearbyBeadIterator(int bead) {
        BinIndex binIndex = binIndices.get(bead);

        NearbyBeadIterator iterator = new NearbyBeadIterator(binIndex);

        return iterator;
    }

    /**
     * returns an iterator which is guaranteed to contain the indices of all
     * beads "close" to the given position. By "close" we mean within the
     * interaction length specified by the geometry specified in the
     * constructor.
     *
     * @param position the position around which nearby beads are to be found
     * @return an iterator giving the beads near the given position
     */
    public Iterator<Integer> getNearbyBeadIterator(double[] position) {
        BinIndex binIndex = getBinIndex(position);

        NearbyBeadIterator iterator = new NearbyBeadIterator(binIndex);

        return iterator;
    }

}
