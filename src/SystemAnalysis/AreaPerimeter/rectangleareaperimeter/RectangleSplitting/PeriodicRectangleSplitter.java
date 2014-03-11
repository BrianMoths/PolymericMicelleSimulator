/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SystemAnalysis.AreaPerimeter.rectangleareaperimeter.RectangleSplitting;

import SystemAnalysis.AreaPerimeter.rectangleareaperimeter.BeadRectangle;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author bmoths
 */
public class PeriodicRectangleSplitter implements RectangleSplitter {

    @Override
    public List<BeadRectangle> splitRectanglesOverBoundary(List<BeadRectangle> beadRectangles, BeadRectangle boundaries) {
        List<BeadRectangle> splitBeadRectangles = new ArrayList<>();
        for (BeadRectangle beadRectangle : beadRectangles) {
            splitBeadRectangles.addAll(beadRectangle.splitOverPeriodicBoundary(boundaries));
        }
        return splitBeadRectangles;
    }

}
