/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SystemAnalysis.AreaPerimeter.RectangleSplitting;

import SystemAnalysis.AreaPerimeter.BeadRectangle;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author bmoths
 */
public class HardWallRectangleSplitter implements RectangleSplitter {

    @Override
    public List<BeadRectangle> splitRectanglesOverBoundary(List<BeadRectangle> beadRectangles, BeadRectangle boundaries) {
        List<BeadRectangle> splitBeadRectangles = new ArrayList<>();
        for (BeadRectangle beadRectangle : beadRectangles) {
            splitBeadRectangles.add(beadRectangle.getIntersectionWith(boundaries));
        }
        return splitBeadRectangles;
    }

}
