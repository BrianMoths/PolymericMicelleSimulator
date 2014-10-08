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

    static public PolymerCluster makePolymerCluster(double hydrophobicFraction, final int numChains, int numBeadsPerChain, int numSubblocks, double density) {
        if (numSubblocks < 0) {
            throw new IllegalArgumentException("numSubblocks must be non-negative");
        } else if (numSubblocks == 0) {
            final int numHydrophobicChains = (int) Math.ceil(hydrophobicFraction * numChains);
            final int numHydrophilicChains = numChains - numHydrophobicChains;

            PolymerChain hydrophobicPolymerChain = PolymerChain.makeChainStartingWithA(0, numBeadsPerChain);
            PolymerChain hydrophilicPolymerChain = PolymerChain.makeChainStartingWithA(numBeadsPerChain);

            PolymerCluster polymerCluster = PolymerCluster.makeRepeatedChainCluster(hydrophobicPolymerChain, numHydrophobicChains);
            polymerCluster.addChainMultipleTimes(hydrophilicPolymerChain, numHydrophilicChains);
            polymerCluster.setConcentrationInWater(density);
            return polymerCluster;
        } else {
            final PolymerChain polymerChain = PolymerChain.makeMultiblockPolymerChain(numBeadsPerChain, numSubblocks, hydrophobicFraction);
            final PolymerCluster polymerCluster = PolymerCluster.makeRepeatedChainCluster(polymerChain, numChains);
            polymerCluster.setConcentrationInWater(density);
            return polymerCluster;
        }
    }

    static public PolymerCluster makeRescaledHomogenousPolymerCluster(PolymerCluster polymerCluster, final double verticalScale, final double horizontalScale) {
        if (polymerCluster.getNumChains() == 0) {
            return new PolymerCluster();
        } else {
            final int numChains = (int) (polymerCluster.getNumChains() * verticalScale * horizontalScale);
            PolymerChain firstChain = polymerCluster.polymerChainList.get(0);
            final PolymerCluster newPolymerCluster = PolymerCluster.makeRepeatedChainCluster(firstChain, numChains);
            newPolymerCluster.setConcentrationInWater(polymerCluster.concentrationInWater);
            return newPolymerCluster;
        }
    }

    public static PolymerCluster copy(PolymerCluster polymerCluster) {
        return new PolymerCluster(polymerCluster);
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
        this();
        addChainPrivate(polymerChain);
    }

    public PolymerCluster(PolymerCluster polymerCluster) {
        this();
        for (PolymerChain polymerChain : polymerCluster.polymerChainList) {
            addChainPrivate(polymerChain);
        }
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
