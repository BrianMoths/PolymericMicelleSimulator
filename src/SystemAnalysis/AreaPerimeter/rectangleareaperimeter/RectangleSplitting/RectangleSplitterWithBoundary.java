/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SystemAnalysis.AreaPerimeter.rectangleareaperimeter.RectangleSplitting;

import SystemAnalysis.AreaPerimeter.rectangleareaperimeter.BeadRectangle;
import java.util.List;

/**
 *
 * @author bmoths
 */
public interface RectangleSplitterWithBoundary {

    public RectanglesAndBoundaryIntervals splitRectanglesOverBoundary(List<BeadRectangle> beadRectangles, BeadRectangle boundaries);

}
