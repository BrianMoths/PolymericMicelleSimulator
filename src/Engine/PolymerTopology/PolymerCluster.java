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
public class PolymerCluster implements Serializable {

    private static final long serialVersionUID = 0L;

    static public PolymerCluster makeEmptyPolymerCluster() {
        return new PolymerCluster();
    }

    static public PolymerCluster makeDefaultPolymerCluster() {
        PolymerChain polymerChain = PolymerChain.makeChainStartingWithA(6, 6);
        PolymerCluster polymerCluster = PolymerCluster.makeRepeatedChainCluster(polymerChain, 100);
        return polymerCluster;
    }

    static public PolymerCluster makeClusterFromChain(PolymerChain polymerChain) {
        PolymerCluster polymerCluster = new PolymerCluster(polymerChain);
        return polymerCluster;
    }

    static public PolymerCluster makeRepeatedChainCluster(PolymerChain polymerChain, int numTimes) {
        PolymerCluster polymerCluster = new PolymerCluster();
        polymerCluster.addChainMultipleTimes(polymerChain, numTimes);
        return polymerCluster;
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

    private final List<PolymerChain> polymerChainList;
    private int numBeads, numABeads;
    private double concentrationInWater = 1;

    private PolymerCluster() {
        polymerChainList = new ArrayList<>();
        numBeads = 0;
        numABeads = 0;
    }

    public PolymerCluster(PolymerChain polymerChain) {
        polymerChainList = new ArrayList<>();
        numBeads = 0;
        numABeads = 0;

        addChainPrivate(polymerChain);
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
        PolymerChain polymerChain = PolymerChain.makeSingletChainOfType(type);
        addChain(polymerChain);
    }

    public void addSingletOfTypeMultipleTimes(boolean type, int numTimes) {
        PolymerChain polymerChain = PolymerChain.makeSingletChainOfType(type);
        addChainMultipleTimes(polymerChain, numTimes);
    }

    public int[][] makeNeighbors() {
        int[][] neighbors = new int[numBeads][2];
        int nextFreeABeadIndex = 0;
        int nextFreeBBeadIndex = numABeads;
        int currentIndex, lastIndex;

        for (PolymerChain polymerChain : polymerChainList) {
            currentIndex = -1;
            for (int chainIndex = 0, N = polymerChain.getNumBeads(); chainIndex < N; chainIndex++) {
                lastIndex = currentIndex;
                if (polymerChain.isTypeA(chainIndex)) {
                    currentIndex = nextFreeABeadIndex;
                    nextFreeABeadIndex++;
                } else {
                    currentIndex = nextFreeBBeadIndex;
                    nextFreeBBeadIndex++;
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

    public double getConcentrationInWater() {
        return concentrationInWater;
    }

    public void setConcentrationInWater(double concentrationInWater) {
        this.concentrationInWater = concentrationInWater;
    }

    public double getNumBeadsIncludingWater() {
        return numBeads / concentrationInWater;
    }

    public int getNumChains() {
        return polymerChainList.size();
    }

    public int getNumBeads() {
        return numBeads;
    }

    public int getNumABeads() {
        return numABeads;
    }

    public int getNumBBeads() {
        return getNumBeads() - getNumABeads();
    }

    public double getNumBeadsPerChain() {
        return ((double) getNumBeads()) / getNumChains();
    }

    public double getNumABeadsPerChain() {
        return ((double) getNumABeads()) / getNumChains();
    }

    public double getNumBBeadsPerChain() {
        return ((double) getNumBBeads()) / getNumChains();
    }

}
