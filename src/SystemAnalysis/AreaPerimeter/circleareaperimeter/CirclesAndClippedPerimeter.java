/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SystemAnalysis.AreaPerimeter.circleareaperimeter;

import java.util.List;

/**
 *
 * @author bmoths
 */
public class CirclesAndClippedPerimeter {

    private final Iterable<Circle> circles;
    private final double clippedPerimeter;

    public CirclesAndClippedPerimeter(Iterable<Circle> circles, double clippedPerimeter) {
        this.circles = circles;
        this.clippedPerimeter = clippedPerimeter;
    }

    public Iterable<Circle> getCircles() {
        return circles;
    }

    public double getClippedPerimeter() {
        return clippedPerimeter;
    }

}
