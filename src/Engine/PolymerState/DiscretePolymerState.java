/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine.PolymerState;

import Engine.PolymerTopology.PolymerCluster;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author bmoths
 */
public class DiscretePolymerState implements ImmutableDiscretePolymerState {

    private static final long serialVersionUID = -7787663309633497712L;
    private final int[][] neighbors;
    private final int numBeads;
    private final int numABeads;
    private final int numBBeads;

    public DiscretePolymerState(PolymerCluster polymerCluster) {
        neighbors = polymerCluster.makeNeighbors();
        numBeads = polymerCluster.getNumBeads();
        numABeads = polymerCluster.getNumABeads();
        numBBeads = numBeads - numABeads;
    }

    public DiscretePolymerState(DiscretePolymerState discretePolymerState) {
        numBeads = discretePolymerState.numBeads;
        numABeads = discretePolymerState.numABeads;
        numBBeads = discretePolymerState.numBBeads;
        neighbors = new int[numBeads][2];
        copyNeighbors(discretePolymerState.neighbors);
    }

    private void copyNeighbors(int[][] otherNeighbors) {
        for (int bead = 0; bead < otherNeighbors.length; bead++) {
            int[] otherBeadNeighbor = otherNeighbors[bead];
            int[] beadNeighbor = neighbors[bead];
            System.arraycopy(otherBeadNeighbor, 0, beadNeighbor, 0, 2);
        }
    }

    /**
     * reptates a chain.
     *
     * @param bead a bead on the chain to be reptated
     * @param isGoingRight true if the bead on the right of the chain is to move
     * to the left of the chain, false for going the other direction
     */
    public void reptateChainOfBead(int bead, boolean isGoingRight) {
        final int leftBead = getLeftBeadOfChain(bead);
        final int rightBead = getRightBeadOfChain(bead);
        final boolean isMoveNontrivial = leftBead != rightBead;
        if (isMoveNontrivial) {
            reptateBeads(leftBead, rightBead, isGoingRight);
        }
    }
    //<editor-fold defaultstate="collapsed" desc="reptateChainOfBead helpers">

    private void reptateBeads(int leftBead, int rightBead, boolean isGoingRight) {
        if (isGoingRight) {
            breakBondToRight(leftBead);
            setOrderedBond(rightBead, leftBead);
        } else {
            breakBondToLeft(rightBead);
            setOrderedBond(rightBead, leftBead);
        }
    }

    private void breakBondToRight(int leftBead) {
        int neighborOnRight = neighbors[leftBead][1];
        neighbors[leftBead][1] = -1;
        neighbors[neighborOnRight][0] = -1;
    }

    private void breakBondToLeft(int rightBead) {
        int neighborOnLeft = neighbors[rightBead][0];
        neighbors[rightBead][0] = -1;
        neighbors[neighborOnLeft][1] = -1;
    }

    private void setOrderedBond(int leftBead, int rightBead) {
        neighbors[leftBead][1] = rightBead;
        neighbors[rightBead][0] = leftBead;
    }
    //</editor-fold>

    @Override
    public List<Integer> getChainOfBead(int bead) {
        List<Integer> chain = new ArrayList<>();
        chain.add(bead);
        addBeadsLeftToChain(bead, chain);
        addBeadsRightToChain(bead, chain);

        return chain;
    }

    //<editor-fold defaultstate="collapsed" desc="getChainOfBead helpers">
    private void addBeadsLeftToChain(int bead, List<Integer> chain) {
        int nextBead = getNeighborToLeftOfBead(bead);
        while (nextBead != -1) {
            chain.add(nextBead);
            nextBead = getNeighborToLeftOfBead(nextBead);
        }
    }

    private void addBeadsRightToChain(int bead, List<Integer> chain) {
        int nextBead = getNeighborToRightOfBead(bead);
        while (nextBead != -1) {
            chain.add(nextBead);
            nextBead = getNeighborToRightOfBead(nextBead);
        }
    }
//</editor-fold>

    public List<Integer> getLeftmostBeads() {
        List<Integer> leftmostBeads = new ArrayList<>();

        for (int bead = 0; bead < numBeads; bead++) {
            if (isLeftmostBead(bead)) {
                leftmostBeads.add(bead);
            }
        }

        return leftmostBeads;

    }

    private boolean isLeftmostBead(int bead) {
        return getNeighborToLeftOfBead(bead) == -1;
    }

    public int getReptatingBead(int beadInChain, boolean isGoingRight) {
        int movingBead;

        if (isGoingRight) {
            movingBead = getLeftBeadOfChain(beadInChain);
        } else {
            movingBead = getRightBeadOfChain(beadInChain);
        }

        return movingBead;
    }

    public int getReptatingBeadDestination(int beadInChain, boolean isGoingRight) {
        return getReptatingBead(beadInChain, !isGoingRight);
    }

    public int getLeftBeadOfChain(int bead) {
        int nextBead = bead;
        int currentBead = bead;
        int count = 0;
        while (nextBead != -1) {
            currentBead = nextBead;
            nextBead = getNeighborToLeftOfBead(nextBead);
        }
        return currentBead;
    }

    public int getRightBeadOfChain(int bead) {
        int nextBead = bead;
        int currentBead = bead;
        int count = 0;
        while (nextBead != -1) {
            currentBead = nextBead;
            nextBead = getNeighborToRightOfBead(nextBead);
        }
        return currentBead;
    }

    /**
     * Returns the neighbor to the left of the given bead. The bead is
     * represented by its index, which is an integer. If the bead does not have
     * a neighbor to its left, then -1 is output.
     *
     * @param bead the bead whose left neighbor is to be found
     * @return the index of the left neighbor if it exists, or -1 if it does
     * not.
     */
    @Override
    public int getNeighborToLeftOfBead(int bead) {
        return neighbors[bead][0];
    }

    /**
     * Returns the neighbor to the right of the given bead. The bead is
     * represented by its index, which is an integer. If the bead does not have
     * a neighbor to its right, then -1 is output.
     *
     * @param bead the bead whose right neighbor is to be found
     * @return the index of the right neighbor if it exists, or -1 if it does
     * not.
     */
    @Override
    public int getNeighborToRightOfBead(int bead) {
        return neighbors[bead][1];
    }

    @Override
    public int getNumBeads() {
        return numBeads;
    }

    @Override
    public int getNumABeads() {
        return numABeads;
    }

    @Override
    public int getNumBBeads() {
        return numBBeads;
    }

    @Override
    public List<List<Integer>> getChains() {
        List<Boolean> isRandomized = new ArrayList<>(numBeads);
        for (int bead = 0; bead < numBeads; bead++) {
            isRandomized.add(false);
        }
        List<List<Integer>> chains = new ArrayList<>();
        for (int bead = 0; bead < numBeads; bead++) {
            if (!isRandomized.get(bead)) {
                List<Integer> chainOfBead = getChainOfBead(bead);
                chains.add(chainOfBead);
                for (Integer randomizedBead : chainOfBead) {
                    isRandomized.set(randomizedBead, true);
                }
            }
        }
        return chains;
    }

    public List<Integer> getLeftBeadsOfChains() {
        List<List<Integer>> chains = getChains();
        List<Integer> leftBeads = new ArrayList<>();
        for (List<Integer> chain : chains) {
            leftBeads.add(chain.get(0));
        }
        return leftBeads;
    }

    public int getNumChains() {
        return getLeftBeadsOfChains().size();
    }

    @Override
    public boolean isTypeA(int bead) {
        return bead < numABeads;
    }

}
