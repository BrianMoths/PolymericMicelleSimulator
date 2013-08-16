/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SystemAnalysis;

import java.util.List;

/**
 *
 * @author bmoths
 */
public class CoveredWeightAndEdgeCalculator {

    private class WeightedNode {

        private int leftEnd, rightEnd, middle, timesCovered;
        private int numInternalEdges;
        private boolean isLeftEdgeCovered, isRightEdgeCovered;
        private double totalWeight, coveredWeight;
        private WeightedNode leftChild, rightChild;

        WeightedNode(final int leftEnd, final int rightEnd) {
            this.leftEnd = leftEnd;
            this.rightEnd = rightEnd;
            middle = (leftEnd + rightEnd) / 2;
            timesCovered = 0;
            coveredWeight = 0;
            numInternalEdges = 0;
            isLeftEdgeCovered = false;
            isRightEdgeCovered = false;

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
                    numInternalEdges = 0;
                    isLeftEdgeCovered = true;
                    isRightEdgeCovered = true;
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
                        isLeftEdgeCovered = false;
                        isRightEdgeCovered = false;
                    } else {
                        coveredWeight = leftChild.coveredWeight + rightChild.coveredWeight;
                        isLeftEdgeCovered = leftChild.isLeftEdgeCovered;
                        isRightEdgeCovered = rightChild.isRightEdgeCovered;
                        numInternalEdges = leftChild.numInternalEdges + rightChild.numInternalEdges + (leftChild.isRightEdgeCovered ^ rightChild.isLeftEdgeCovered ? 1 : 0);
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
                isLeftEdgeCovered = leftChild.isLeftEdgeCovered;
                isRightEdgeCovered = rightChild.isRightEdgeCovered;
                numInternalEdges = leftChild.numInternalEdges + rightChild.numInternalEdges + (leftChild.isRightEdgeCovered ^ rightChild.isLeftEdgeCovered ? 1 : 0);
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

        public int getNumInternalEdges() {
            return numInternalEdges;
        }

        public boolean isLeftEdgeCovered() {
            return isLeftEdgeCovered;
        }

        public boolean isRightEdgeCovered() {
            return isRightEdgeCovered;
        }
    }
    private List<Double> weights; //nodes are private inner class so have acces to this variable
    WeightedNode rootNode;

    public CoveredWeightAndEdgeCalculator(List<Double> weights) {
        this.weights = weights;
        final int numLeaves = weights.size();
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

    public int getNumEdges() {
        return rootNode.getNumInternalEdges() + (rootNode.isLeftEdgeCovered() ? 1 : 0) + (rootNode.isRightEdgeCovered() ? 1 : 0);
    }
}
