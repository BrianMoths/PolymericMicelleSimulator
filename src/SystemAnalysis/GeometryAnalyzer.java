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
public class GeometryAnalyzer {

    static public class AreaPerimeter {

        public double area;
        public double perimeter;

        public AreaPerimeter(double area, double perimeter) {
            this.area = area;
            this.perimeter = perimeter;
        }
    }

    static public double findArea(List<BeadRectangle> beadRectangles) {

        final IntervalEndpoints xs = IntervalEndpoints.makeFromBeadRectanglesHorizontal(beadRectangles);
        List<Integer> xPermutation = OverlappingIntervalLengthFinder.getPermutation(xs);
        OverlappingIntervalLengthFinder overlappingIntervalLengthFinder = OverlappingIntervalLengthFinder.makeFromBeadRectangles(beadRectangles, xPermutation);

        double area = 0;
        double oldx = 0;

        for (int linearIndex : xPermutation) {
            final double newX = getNewX(beadRectangles, linearIndex);
            final double deltaX = newX - oldx;

            final double coveredVerticalLength = overlappingIntervalLengthFinder.getLength();
            area += deltaX * coveredVerticalLength;

            overlappingIntervalLengthFinder.doNextStep();
            oldx = newX;
        }
        return area;
    }

    static public AreaPerimeter findAreaAndPerimeter(List<BeadRectangle> beadRectangles) {
        final IntervalEndpoints xs = IntervalEndpoints.makeFromBeadRectanglesHorizontal(beadRectangles);
        List<Integer> xPermutation = OverlappingIntervalLengthFinder.getPermutation(xs);
        LengthAndEdgeFinder lengthAndEdgeFinder = LengthAndEdgeFinder.makeFromBeadRectangles(beadRectangles, xPermutation);

        double area = 0;
        double oldCoveredLength = 0;
        double perimeter = 0;
        double oldx = 0;

        for (int linearIndex : xPermutation) {
            final double newX = getNewX(beadRectangles, linearIndex);
            final double deltaX = newX - oldx;

            final double coveredVerticalLength = lengthAndEdgeFinder.getLength();
            area += deltaX * coveredVerticalLength;

            final int numEdges = lengthAndEdgeFinder.getNumEdges();
            perimeter += numEdges * deltaX;
            perimeter += Math.abs(coveredVerticalLength - oldCoveredLength);

            lengthAndEdgeFinder.doNextStep();
            oldCoveredLength = coveredVerticalLength;
            oldx = newX;
        }
        perimeter += oldCoveredLength; //add on perimeter from rightmost vertical edge
        return new AreaPerimeter(area, perimeter);
    }

    private static double getNewX(List<BeadRectangle> beadRectangles, int linearIndex) {
        final int beadIndex = IntervalEndpoints.getIntervalFromLinearIndex(linearIndex);
        final BeadRectangle beadRectangle = beadRectangles.get(beadIndex);
        return IntervalEndpoints.getIsStartFromLinearIndex(linearIndex) ? beadRectangle.left : beadRectangle.right;
    }
}
