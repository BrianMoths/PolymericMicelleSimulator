/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SystemAnalysis.AreaPerimeter.rectangleareaperimeter;

/**
 *
 * @author bmoths
 */
public class OverlappingRectangleAreaFinder extends SweepLineAlgorithm<Double> {

    private OverlappingIntervalLengthFinder overlappingIntervalLengthFinder;
    private double area;
    private double oldX;

    @Override
    protected void initialize() {
        overlappingIntervalLengthFinder = OverlappingIntervalLengthFinder.makeFromBeadRectangles(getBeadRectangles(), getXPermutation());
        area = 0;
        oldX = 0;
    }

    @Override
    protected void doIteration() {
        final double deltaX = getCurrentX() - oldX;
        final double coveredVerticalLength = overlappingIntervalLengthFinder.getLength();
        area += deltaX * coveredVerticalLength;

        overlappingIntervalLengthFinder.doNextStep();
        oldX = getCurrentX();
    }

    @Override
    protected Double getResult() {
        return area;
    }

}
