/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SystemAnalysis.AreaPerimeter;

import java.util.List;

/**
 *
 * @author bmoths
 */
public class RectanglesAndBoundaryPerimeter {

    public List<BeadRectangle> beadRectangles;
    public double gluedPerimeter;

    public RectanglesAndBoundaryPerimeter(List<BeadRectangle> beadRectangles, double gluedPerimeter) {
        this.beadRectangles = beadRectangles;
        this.gluedPerimeter = gluedPerimeter;
    }

}
