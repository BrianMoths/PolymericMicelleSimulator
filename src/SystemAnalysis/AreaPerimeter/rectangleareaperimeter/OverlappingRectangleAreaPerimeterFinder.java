/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SystemAnalysis.AreaPerimeter.rectangleareaperimeter;

import SystemAnalysis.AreaPerimeter.AreaPerimeter;

/**
 *
 * @author bmoths
 */
public class OverlappingRectangleAreaPerimeterFinder extends SweepLineAlgorithm<AreaPerimeter> {

    private LengthAndEdgeFinder lengthAndEdgeFinder;
    private double oldX;
    private double area;
    private double perimeter;
    private double oldCoveredLength;

    @Override
    protected void initialize() {
        lengthAndEdgeFinder = LengthAndEdgeFinder.makeForBeadRectangles(getBeadRectangles(), getXPermutation());
        oldX = 0;
        oldCoveredLength = 0;
        area = 0;
        perimeter = 0;
    }

    @Override
    protected void doIteration() {
        final double newX = getCurrentX();
        final double deltaX = newX - oldX;

        final double coveredVerticalLength = lengthAndEdgeFinder.getLength();
        area += deltaX * coveredVerticalLength;

        final int numEdges = lengthAndEdgeFinder.getNumEdges();
        perimeter += numEdges * deltaX;
        perimeter += Math.abs(coveredVerticalLength - oldCoveredLength);

        lengthAndEdgeFinder.doNextStep();
        oldCoveredLength = coveredVerticalLength;
        oldX = newX;
    }

    @Override
    protected AreaPerimeter getResult() {
        return new AreaPerimeter(area, perimeter);
    }

}
