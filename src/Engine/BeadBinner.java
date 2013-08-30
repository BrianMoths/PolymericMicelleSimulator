/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine;

import Engine.SystemGeometry.SystemGeometry;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author bmoths
 */
public class BeadBinner implements Serializable{ //keep track of where beads are so bead moves require only bead number and new position

    private final int dimension;
    private final double[] binSize;
    private final int[] numBins;
    private List<List<Set<Integer>>> beadBins;
//    private SimulationStep simulationStep;
    private boolean isStepDone = false;
    private List<BinIndex> binIndices;

    static private class BinIndex implements Serializable{

        public int x, y;

        public BinIndex() {
        }

        public BinIndex(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public BinIndex(BinIndex binIndex) {
            this.x = binIndex.x;
            this.y = binIndex.y;
        }
    }

    private class NearbyBeadIterator implements Iterator<Integer> {

        private final BinIndex binIndex;
        private Iterator<Integer> beadIterator;
        int i, j;

        public NearbyBeadIterator(BinIndex binIndex) {
            i = -1;
            j = -1;
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
            j++;
            if (j > 1) {
                j = -1;
                i++;
                if (isOutOfBins()) {
                    return;
                }
            }

            updateBeadIterator();
        }

        private void updateBeadIterator() {
            BinIndex neighboringBinIndex = new BinIndex(binIndex.x + i, binIndex.y + j);
            projectBinIndex(neighboringBinIndex);
            beadIterator = getBin(neighboringBinIndex).iterator();
        }

        private boolean isOutOfBins() {
            return i > 1;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

    public BeadBinner(double[][] beadPositions, SystemGeometry systemGeometry) {
        final double[] rMax = systemGeometry.getRMax();
        final double interactionLength = systemGeometry.getParameters().getInteractionLength();
        dimension = systemGeometry.getDimension();
        numBins = new int[dimension];
        binSize = new double[dimension];
        for (int i = 0; i < dimension; i++) {
            numBins[i] = (int) Math.floor(rMax[i] / interactionLength);
            binSize[i] = rMax[i] / numBins[i];
        }

        binBeadsPrivate(beadPositions);
    }

    public BeadBinner(BeadBinner beadBinner) {
        binIndices = new ArrayList<>();
        for (BinIndex binIndex : beadBinner.binIndices) {
            binIndices.add(new BinIndex(binIndex));
        }
        dimension = beadBinner.dimension;
        binSize = beadBinner.binSize;
        numBins = beadBinner.numBins;
        beadBins = copyBeadBins(beadBinner.beadBins);
//        simulationStep = new SimulationStep(beadBinner.simulationStep);
        isStepDone = beadBinner.isStepDone;
    }

    private List<List<Set<Integer>>> copyBeadBins(List<List<Set<Integer>>> beadBins) {
        List<List<Set<Integer>>> beadBinsCopy = new ArrayList<>(numBins[0]);
        for (int i = 0; i < numBins[0]; i++) {
            beadBinsCopy.add(copy1DBinList(beadBins.get(i)));
        }
        return beadBinsCopy;
    }

    private List<Set<Integer>> copy1DBinList(List<Set<Integer>> binList1D) {
        List<Set<Integer>> binList1DCopy = new ArrayList<>();
        for (int i = 0; i < numBins[1]; i++) {
            binList1DCopy.add(new HashSet<>(binList1D.get(i))); //linkedHashSet is a better implementation?
        }
        return binList1DCopy;
    }

    public void binBeads(double[][] beadPositions) {
        binBeadsPrivate(beadPositions);
    }

    private void binBeadsPrivate(double[][] beadPositions) {
        allocateBeadBins();
        binIndices = new ArrayList<>(beadPositions.length);
        for (int i = 0; i < beadPositions.length; i++) {
            binIndices.add(new BinIndex());
        }
        for (int bead = 0; bead < beadPositions.length; bead++) {
            addBeadAt(bead, beadPositions[bead]);
        }
    }

    private void allocateBeadBins() {
        beadBins = new ArrayList<>(numBins[0]);
        for (int i = 0; i < numBins[0]; i++) {
            beadBins.add(allocate1DBinList());
        }
    }

    private List<Set<Integer>> allocate1DBinList() {
        List<Set<Integer>> binList = new ArrayList<>(numBins[1]);
        for (int i = 0; i < numBins[1]; i++) {
            binList.add(new HashSet<Integer>()); //linkedHashSet is a better implementation?
        }
        return binList;
    }

    public void updateBeadPosition(int stepBead, double[] newBeadPosition) {
        removeBead(stepBead);
        addBeadAt(stepBead, newBeadPosition);
    }

    private void addBeadAt(int bead, double[] position) {
        final BinIndex index = getBinIndex(position);
        addBeadToBin(bead, index);
    }

    private void removeBead(int bead) {
        BinIndex binIndex = binIndices.get(bead);
        removeBeadFromBin(bead, binIndex);
    }

    private void addBeadToBin(int bead, BinIndex binIndex) {
        getBin(binIndex).add(bead);
        binIndices.set(bead, binIndex);
    }

    private void removeBeadFromBin(int bead, BinIndex binIndex) {
        getBin(binIndex).remove(bead);
    }

    private Set<Integer> getBin(BinIndex binIndex) {
        return beadBins.get(binIndex.x).get(binIndex.y);
    }

    private BinIndex getBinIndex(double[] position) {
        BinIndex binIndex = new BinIndex();
        binIndex.x = (int) Math.floor(position[0] / binSize[0]);
        binIndex.y = (int) Math.floor(position[1] / binSize[1]); //make this nicer like a loop or something
        return binIndex;
    }

//    public void setStep(SimulationStep simulationStep) {
//        this.simulationStep = simulationStep;
//    }
//
//    public void doStep() {
//        removeBeadAt(simulationStep.getStepBead(), simulationStep.getInitialPosition());
//        addBeadAt(simulationStep.getStepBead(), simulationStep.getFinalPosition());
//        isStepDone = true;
//    }
//
//    public void undoStep() {
//        removeBeadAt(simulationStep.getStepBead(), simulationStep.getFinalPosition());
//        addBeadAt(simulationStep.getStepBead(), simulationStep.getInitialPosition());
//        isStepDone = false;
//    }
    private void projectBinIndex(BinIndex binIndex) {
        if (binIndex.x < 0) {
            binIndex.x += numBins[0];
        } else if (binIndex.x >= numBins[0]) {
            binIndex.x -= numBins[0];
        }

        if (binIndex.y < 0) {
            binIndex.y += numBins[1];
        } else if (binIndex.y >= numBins[1]) {
            binIndex.y -= numBins[1];
        }
    }

//    public Iterator<Integer> getStepBeadNearbyBeadIterator() {
//        final double[] position = isStepDone ? simulationStep.getFinalPosition() : simulationStep.getInitialPosition();
//        return getNearbyBeadIterator(position);
//    }
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
