/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine;

import Engine.SystemGeometry.SystemGeometry;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 *
 * @author bmoths
 */
public class BeadBinner {

    private List<List<Set<Integer>>> beadBins;
    private SimulationStep simulationStep;
    private double[] binSize;
    private int[] numBins;
    private int dimension;
    private boolean isStepDone = false;

    static private class BinIndex {

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

        private BinIndex binIndex;
        private Iterator<Integer> beadIterator;
        int i = -1, j = -1;

        public NearbyBeadIterator(BinIndex binIndex) {
            this.binIndex = new BinIndex(binIndex);
            updateBeadIterator();
        }

        @Override
        public boolean hasNext() { //this still doesn't work
            if (beadIterator.hasNext()) {
                return true;
            } else {
                goToNextNonEmptyBin();
                return i <= 1; //we have iterated too far already
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
            while (!beadIterator.hasNext()) {
                iterateBin();
            }
        }

        private void iterateBin() {
            j++;
            if (j > 1) {
                j = -1;
                i++;
                if (i > 1) {
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

    private void initializeBeadBins() {
        beadBins = new ArrayList<>(numBins[0]);
        for (int i = 0; i < numBins[0]; i++) {
            beadBins.add(make1DBinList());
        }
    }

    private List<Set<Integer>> make1DBinList() {
        List<Set<Integer>> binList = new ArrayList<>(numBins[1]);
        for (int i = 0; i < numBins[1]; i++) {
            binList.add(new HashSet<Integer>());
        }
        return binList;
    }

    public void binBeads(double[][] beadPositions) {
        binBeadsPrivate(beadPositions);
    }

    private void binBeadsPrivate(double[][] beadPositions) {
        initializeBeadBins();
        for (int bead = 0; bead < beadPositions.length; bead++) {
            addBeadAt(bead, beadPositions[bead]);
        }
    }

    private void addBeadAt(int bead, double[] position) {
        final BinIndex index = getBinIndex(position);
        addBeadToBin(bead, index);
    }

    private void removeBeadAt(int bead, double[] position) {
        final BinIndex index = getBinIndex(position);
        removeBeadFromBin(bead, index);
    }

    private void addBeadToBin(int bead, BinIndex binIndex) {
        getBin(binIndex).add(bead);
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
        binIndex.y = (int) Math.floor(position[1] / binSize[1]); //make this nicer
        return binIndex;
    }

    public void setStep(SimulationStep simulationStep) {
        this.simulationStep = simulationStep;
    }

    public void doStep() {
        removeBeadAt(simulationStep.getStepBead(), simulationStep.getInitialPosition());
        addBeadAt(simulationStep.getStepBead(), simulationStep.getFinalPosition());
        isStepDone = true;
    }

    public void undoStep() {
        removeBeadAt(simulationStep.getStepBead(), simulationStep.getFinalPosition());
        addBeadAt(simulationStep.getStepBead(), simulationStep.getInitialPosition());
        isStepDone = false;
    }

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

    public Iterator<Integer> getNearbyBeadIterator() {
        BinIndex binIndex;
        if (isStepDone) {
            binIndex = getBinIndex(simulationStep.getFinalPosition());
        } else {
            binIndex = getBinIndex(simulationStep.getInitialPosition());
        }

        NearbyBeadIterator iterator = new NearbyBeadIterator(binIndex);

        return iterator;
    }
}
