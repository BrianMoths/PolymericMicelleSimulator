/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SystemAnalysis.AreaPerimeter.rectangleareaperimeter;

import java.util.List;

/**
 *
 * @author bmoths
 */
public abstract class SweepLineAlgorithm<T> {

    private List<Integer> xPermutation;
    private double currentX;
    private List<BeadRectangle> beadRectangles;

    public T compute(List<BeadRectangle> beadRectangles) {
        this.beadRectangles = beadRectangles;
        final IntervalListEndpoints horizontalEndpoints = IntervalListEndpoints.endpointsOfHorizontalRectangleEdges(beadRectangles);
        xPermutation = horizontalEndpoints.getPermutation();
        initialize();

        for (int linearIndex : xPermutation) {
            currentX = getNewX(beadRectangles, linearIndex);
            doIteration();
        }


        return getResult();
    }

    protected abstract void initialize();

    protected abstract void doIteration();

    protected abstract T getResult();

    private static double getNewX(List<BeadRectangle> beadRectangles, int linearIndex) {
        final int beadIndex = IntervalListEndpoints.getIntervalIndexFromLinearIndex(linearIndex);
        final BeadRectangle beadRectangle = beadRectangles.get(beadIndex);
        return IntervalListEndpoints.getIsStartFromLinearIndex(linearIndex) ? beadRectangle.getLeft() : beadRectangle.getRight();
    }

    protected final List<Integer> getXPermutation() {
        return xPermutation;
    }

    protected final List<BeadRectangle> getBeadRectangles() {
        return beadRectangles;
    }

    protected final double getCurrentX() {
        return currentX;
    }

}
