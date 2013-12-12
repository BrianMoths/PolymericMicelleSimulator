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
 *
 * @author bmoths
 */
public class BeadBinner implements Serializable {

    static private class BinIndex implements Serializable {

        static BinIndex addIndices(BinIndex firstSummand, List<Integer> secondSummand) {
            final int numDimensions = firstSummand.indices.size();

            BinIndex sum = new BinIndex(numDimensions);

            for (int dimension = 0; dimension < numDimensions; dimension++) {
                final int newIndexOfDimension = firstSummand.indices.get(dimension) + secondSummand.get(dimension);
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

    private final int numDimensions;

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
            if (beadIterator.hasNext()) {
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
            beadIterator = getBin(neighboringBinIndex).iterator();
        }

        private boolean isOutOfBins() {
            return isOutOfBins;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }

    private final double[] binSize;
    private final int[] numBins;
    private MultidimensionalArray<Set<Integer>> beadBins;
    private boolean isStepDone = false;
    private List<BinIndex> binIndices;
    private final List<Integer> ranges;
    private final List<Integer> initialBinOffset;

    public BeadBinner(double[][] beadPositions, ImmutableSystemGeometry systemGeometry) {
        final double[] rMax = systemGeometry.getRMax();
        final double interactionLength = systemGeometry.getParameters().getInteractionLength();
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
        binBeadsPrivate(beadPositions);
    }

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
    }

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

    public Iterator<Integer> getNearbyBeadIterator(int bead) {
        BinIndex binIndex = binIndices.get(bead);

        NearbyBeadIterator iterator = new NearbyBeadIterator(binIndex);

        return iterator;
    }

    public Iterator<Integer> getNearbyBeadIterator(double[] position) {
        BinIndex binIndex = getBinIndex(position);

        NearbyBeadIterator iterator = new NearbyBeadIterator(binIndex);

        return iterator;
    }

}
