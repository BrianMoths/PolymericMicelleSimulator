/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SystemAnalysis.AreaPerimeter.circleareaperimeter;

import java.awt.geom.Point2D;

/**
 *
 * @author bmoths
 */
public class Circle {

    private final Point2D center;
    final Double radius;

    public Circle(Point2D center, Double radius) {
        this.center = center;
        this.radius = radius;
    }

    public Point2D getCenter() {
        return center;
    }

    public Double getRadius() {
        return radius;
    }

    public double getCenterX() {
        return center.getX();
    }

    public double getCenterY() {
        return center.getY();
    }

}
