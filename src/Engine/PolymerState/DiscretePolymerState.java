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
public class DiscretePolymerState implements ImmutableDiscretePolymerState, Serializable {

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

    public void reptateChainOfBead(int bead, boolean isGoingRight) {
        final int leftBead = getLeftBeadOfChain(bead);
        final int rightBead = getRightBeadOfChain(bead);
        if (leftBead != rightBead) {
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

    @Override
    public int getNeighborToLeftOfBead(int bead) {
        return neighbors[bead][0];
    }

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
    public boolean isTypeA(int bead) {
        return bead < numABeads;
    }

}
