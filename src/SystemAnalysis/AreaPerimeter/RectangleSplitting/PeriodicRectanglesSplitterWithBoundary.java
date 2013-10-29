/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SystemAnalysis.AreaPerimeter.RectangleSplitting;

import SystemAnalysis.AreaPerimeter.BeadRectangle;
import java.util.List;

/**
 *
 * @author bmoths
 */
public class PeriodicRectanglesSplitterWithBoundary implements RectangleSplitterWithBoundary {

    @Override
    public RectanglesAndBoundaryIntervals splitRectanglesOverBoundary(List<BeadRectangle> beadRectangles, BeadRectangle boundaries) {
        RectanglesAndBoundaryIntervals rectanglesAndBoundaryIntervals = new RectanglesAndBoundaryIntervals(beadRectangles);
        rectanglesAndBoundaryIntervals.splitOverPeriodicBoundary(boundaries);
        return rectanglesAndBoundaryIntervals;
    }

}
