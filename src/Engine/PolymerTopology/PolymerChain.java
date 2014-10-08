/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine.PolymerTopology;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author bmoths
 */
public class PolymerChain implements Serializable {

    private static final long serialVersionUID = 0L;

    static public PolymerChain makeSingletChainOfType(boolean isTypeA) {
        PolymerChain polymerChain = new PolymerChain();
        polymerChain.addBead(isTypeA);
        return polymerChain;
    }

    static public PolymerChain makeChainOfType(boolean isTypeA, int numBeads) {
        PolymerChain polymerChain = new PolymerChain();
        polymerChain.addBeads(isTypeA, numBeads);
        return polymerChain;
    }

    static public PolymerChain makeChainStartingWithA(int... numberOfBeads) {
        PolymerChain polymerChain = new PolymerChain();
        polymerChain.addBeadsStartingWithA(numberOfBeads);
        return polymerChain;
    }

    static public PolymerChain makeMultiblockPolymerChain(int numBeads, int numBlocks, double hydrophobicFraction) {
        final double numHydrophobicBeadsPerBlock = hydrophobicFraction * numBeads / numBlocks;
        final double numHydrophilicBeadsPerBlock = (1 - hydrophobicFraction) * numBeads / numBlocks;

        int numHydrophilicBeadsAddedSoFar = 0;
        int numHydrophobicBeadsAddedSoFar = 0;
        PolymerChain polymerChain = new PolymerChain();
        for (int currentBlock = 1; currentBlock <= numBlocks; currentBlock++) {
            final int numHydrophilicBeadsToBeAdded = (int) Math.round(numHydrophilicBeadsPerBlock * currentBlock - .000001) - numHydrophilicBeadsAddedSoFar; //avoid case when both numbers of blocks are rounded up
            final int numHydrophobicBeadsToBeAdded = (int) Math.round(numHydrophobicBeadsPerBlock * currentBlock) - numHydrophobicBeadsAddedSoFar;

            polymerChain.addBeads(true, numHydrophilicBeadsToBeAdded);
            numHydrophilicBeadsAddedSoFar += numHydrophilicBeadsToBeAdded;
            polymerChain.addBeads(false, numHydrophobicBeadsToBeAdded);
            numHydrophobicBeadsAddedSoFar += numHydrophobicBeadsToBeAdded;
        }
        if (polymerChain.getNumBeads() != numBeads) {
            throw new AssertionError("polymer chain has the wrong length, length is supposed to be " + numBeads + ", but actual length was " + polymerChain.getNumBeads());
        }
        return polymerChain;
    }

    static public PolymerChain copy(PolymerChain polymerChain) {
        PolymerChain chainCopy = new PolymerChain();
        chainCopy.numABeads = polymerChain.numABeads;
        chainCopy.types.addAll(polymerChain.types); //ok does perform deep copy
        return chainCopy;
    }

    private final List<Boolean> types;
    private int numABeads;

    public PolymerChain() {
        types = new ArrayList<>();
        numABeads = 0;
    }

    public void addBead(boolean isTypeA) {
        types.add(isTypeA);
        if (isTypeA) {
            numABeads++;
        }
    }

    public void addBeads(boolean isTypeA, int numberOfBeads) {
        for (int i = 0; i < numberOfBeads; i++) {
            addBead(isTypeA);
        }
    }

    public void addBeadsStartingWithA(int... numberOfBeads) {
        boolean isTypeA = true;
        for (int i = 0, N = numberOfBeads.length; i < N; i++) {
            addBeads(isTypeA, numberOfBeads[i]);
            isTypeA = !isTypeA;
        }
    }

    public void appendChain(PolymerChain polymerChain) {
        if (this == polymerChain) {
            polymerChain = PolymerChain.copy(polymerChain);
        }
        for (Boolean isTypeA : polymerChain.types) {
            addBead(isTypeA);
        }
    }

    public void appendChains(PolymerChain... polymerChains) {
        for (PolymerChain polymerChain : polymerChains) {
            appendChain(polymerChain);
        }
    }

    public void appendChainRepeatedly(PolymerChain polymerChain, int numTimes) {
        for (int i = 0; i < numTimes; i++) {
            appendChain(polymerChain);
        }
    }

    public int getNumBeads() {
        return types.size();
    }

    public int getNumABeads() {
        return numABeads;
    }

    public boolean isTypeA(int index) {
        return types.get(index);
    }

}
