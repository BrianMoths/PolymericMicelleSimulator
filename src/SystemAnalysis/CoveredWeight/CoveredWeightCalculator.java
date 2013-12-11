/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SystemAnalysis.CoveredWeight;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author bmoths
 */
public class CoveredWeightCalculator {

    private class WeightedNode {

        private int leftEnd, rightEnd, middle, timesCovered;
        private double totalWeight, coveredWeight;
        private WeightedNode leftChild, rightChild;

        WeightedNode(final int leftEnd, final int rightEnd) {
            this.leftEnd = leftEnd;
            this.rightEnd = rightEnd;
            middle = (leftEnd + rightEnd) / 2;
            timesCovered = 0;
            coveredWeight = 0;

            if (leftEnd == rightEnd) {
                totalWeight = weights.get(leftEnd);
            } else {
                leftChild = new WeightedNode(leftEnd, middle);
                rightChild = new WeightedNode(middle + 1, rightEnd);
                totalWeight = leftChild.totalWeight + rightChild.totalWeight;
            }
        }

        public void addCover(final int start, final int end) {
            if (isNodeCoveredByInterval(start, end)) {
                addCoverToMe();
                if (timesCovered == 1) {
                    coveredWeight = totalWeight;
                }
            } else {
                addCoverToChildren(start, end);
                registerChildrensCover();
            }
        }

        private void addCoverToMe() {
            timesCovered++;
        }

        private void addCoverToChildren(final int start, final int end) {
            if (start <= middle) {
                leftChild.addCover(start, end);
            }
            if (end >= middle + 1) {
                rightChild.addCover(start, end);
            }
        }

        public void removeCover(final int start, final int end) {
            if (isNodeCoveredByInterval(start, end)) {
                removeCoverFromMe();
                if (timesCovered == 0) {
                    if (isLeafNode()) {
                        coveredWeight = 0;
                    } else {
                        coveredWeight = leftChild.coveredWeight + rightChild.coveredWeight;
                    }
                }
            } else {
                removeCoverFromChildren(start, end);
                registerChildrensCover();
            }
        }

        private void removeCoverFromMe() {
            timesCovered--;
        }

        private void removeCoverFromChildren(final int start, final int end) {
            if (start <= middle) {
                leftChild.removeCover(start, end);
            }
            if (end >= middle + 1) {
                rightChild.removeCover(start, end);
            }
        }

        private void registerChildrensCover() {
            if (timesCovered <= 0) {
                coveredWeight = leftChild.coveredWeight + rightChild.coveredWeight;
            }
        }

        private boolean isNodeCoveredByInterval(final int start, final int end) {
            return start <= leftEnd && end >= rightEnd;
        }

        private boolean isLeafNode() {
            return leftChild == null;
        }

        public double getCoveredWeight() {
            return coveredWeight;
        }

    }

    private List<Double> weights; //nodes are private inner class so have acces to this variable
    WeightedNode rootNode;

    public CoveredWeightCalculator(List<Double> weights) {
        if (weights.isEmpty()) {
            this.weights = new ArrayList<>();
            this.weights.add(0.);
        } else {
            this.weights = weights;
        }
        final int numLeaves = this.weights.size();
        rootNode = new WeightedNode(0, numLeaves - 1);
    }

    public void addCover(int start, int end) {
        rootNode.addCover(start, end);
    }

    public void removeCover(int start, int end) {
        rootNode.removeCover(start, end);
    }

    public double getWeight() {
        return rootNode.getCoveredWeight();
    }

}
