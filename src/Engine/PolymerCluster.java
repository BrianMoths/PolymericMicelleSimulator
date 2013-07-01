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
public class PolymerCluster {

    private final List<PolymerChain> polymerChainList;
    private int numBeads, numABeads;

    public PolymerCluster() {
        polymerChainList = new ArrayList<>();
        numBeads = 0;
        numABeads = 0;

        PolymerChain polymerChain = new PolymerChain();
        polymerChain.addBeadsStartingWithA(5, 15);
        for (int i = 0; i < 8; i++) {
            this.addChainPrivate(polymerChain);
        }
    }

    public static PolymerCluster copy(PolymerCluster polymerCluster) {
        PolymerCluster copy = new PolymerCluster();

        copy.numBeads = polymerCluster.numBeads;
        copy.numABeads = polymerCluster.numABeads;

        for (PolymerChain polymerChain : polymerCluster.polymerChainList) {
            copy.addChain(polymerChain);
        }

        return copy;
    }

    public void addChain(PolymerChain polymerChain) {
        polymerChainList.add(PolymerChain.copy(polymerChain));
        numBeads += polymerChain.getNumBeads();
        numABeads += polymerChain.getNumABeads();
    }

    private void addChainPrivate(PolymerChain polymerChain) {
        polymerChainList.add(PolymerChain.copy(polymerChain));
        numBeads += polymerChain.getNumBeads();
        numABeads += polymerChain.getNumABeads();
    }

    public void addChainMultipleTimes(PolymerChain polymerChain, int numTimes) {
        for (int i = 0; i < numTimes; i++) {
            addChain(polymerChain);
        }
    }

    public void addSingletOfType(boolean type) {
        PolymerChain polymerChain = new PolymerChain();
        polymerChain.addBead(type);
        addChain(polymerChain);
    }

    public void addSingletOfTypeMultipleTimes(boolean type, int numTimes) {
        PolymerChain polymerChain = new PolymerChain();
        polymerChain.addBead(type);
        addChainMultipleTimes(polymerChain, numTimes);
    }

    public int getNumBeads() {
        return numBeads;
    }

    public int getNumABeads() {
        return numABeads;
    }

    public int[][] makeNeighbors() { //needs to be tested
        int[][] neighbors = new int[numBeads][2];
        int ABeadIndex = 0;
        int BBeadIndex = numABeads;
        int currentIndex, lastIndex;

        for (PolymerChain polymerChain : polymerChainList) {
            currentIndex = -1;
            for (int chainIndex = 0, N = polymerChain.getNumBeads(); chainIndex < N; chainIndex++) {
                lastIndex = currentIndex;
                if (polymerChain.isTypeA(chainIndex)) {
                    currentIndex = ABeadIndex;
                    ABeadIndex++;
                } else {
                    currentIndex = BBeadIndex;
                    BBeadIndex++;
                }

                neighbors[currentIndex][0] = lastIndex;
                if (lastIndex != -1) {
                    neighbors[lastIndex][1] = currentIndex;
                }
            }
            neighbors[currentIndex][1] = -1;
        }
        return neighbors;
    }
}
