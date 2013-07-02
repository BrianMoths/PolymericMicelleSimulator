/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author bmoths
 */
public class PolymerChain {

    private final List<Boolean> types;
    private int numABeads;

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

    private PolymerChain() {
        types = new ArrayList<>();
        numABeads = 0;
    }

    static public PolymerChain copy(PolymerChain polymerChain) {
        PolymerChain chainCopy = new PolymerChain();
        chainCopy.numABeads = polymerChain.numABeads;
        chainCopy.types.addAll(polymerChain.types); //ok does perform deep copy
        return chainCopy;
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
