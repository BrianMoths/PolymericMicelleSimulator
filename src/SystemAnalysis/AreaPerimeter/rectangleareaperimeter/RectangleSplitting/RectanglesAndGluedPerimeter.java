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
public class RectanglesAndGluedPerimeter {

    public List<BeadRectangle> beadRectangles;
    public double gluedPerimeter;

    public RectanglesAndGluedPerimeter(List<BeadRectangle> beadRectangles, double gluedPerimeter) {
        this.beadRectangles = beadRectangles;
        this.gluedPerimeter = gluedPerimeter;
    }

}
