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
public interface RectangleSplitter {

    public List<BeadRectangle> splitRectanglesOverBoundary(List<BeadRectangle> beadRectangles, BeadRectangle boundaries);

}
