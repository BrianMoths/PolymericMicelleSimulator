/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SystemAnalysis;

import SystemAnalysis.OverlappingIntervalLengthFinder.IntervalEndpoints;
import java.util.List;

/**
 *
 * @author bmoths
 */
public class VolumeFinder {

    static public double findVolume(List<BeadRectangle> beadRectangles) {

        final IntervalEndpoints xs = IntervalEndpoints.makeFromBeadRectanglesHorizontal(beadRectangles);

        List<Integer> xPermutation = OverlappingIntervalLengthFinder.getPermutation(xs);

        OverlappingIntervalLengthFinder overlappingIntervalLengthFinder = OverlappingIntervalLengthFinder.makeFromBeadRectangles(beadRectangles, xPermutation);

        double volume = 0;
        double oldx = 0;

        for (int permutedXIndex = 0; permutedXIndex < xPermutation.size(); permutedXIndex++) {
            final int linearIndex = xPermutation.get(permutedXIndex);
            final int beadIndex = IntervalEndpoints.getIntervalFromLinearIndex(linearIndex);
            final BeadRectangle beadRectangle = beadRectangles.get(beadIndex);
            final double newX = IntervalEndpoints.getIsStartFromLinearIndex(linearIndex) ? beadRectangle.left : beadRectangle.right;

            final double deltaX = newX - oldx;
            final double coveredVerticalLength = overlappingIntervalLengthFinder.getLength();
            volume += deltaX * coveredVerticalLength;

            overlappingIntervalLengthFinder.doNextStep();

            oldx = newX;
        }
        return volume;
    }
}
