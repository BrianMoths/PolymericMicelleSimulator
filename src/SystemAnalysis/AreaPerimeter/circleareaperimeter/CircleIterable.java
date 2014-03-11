/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SystemAnalysis.AreaPerimeter.circleareaperimeter;

import SystemAnalysis.AreaPerimeter.circleareaperimeter.Circle;
import SystemAnalysis.AreaPerimeter.circleareaperimeter.CircleIterator;
import java.awt.geom.Point2D;
import java.util.Iterator;

/**
 *
 * @author bmoths
 */
public class CircleIterable implements Iterable<Circle> {

    private final Iterable<Point2D> centerIterable;
    private final Iterable<Double> radiusIterable;

    public CircleIterable(Iterable<Point2D> centerIterable, Iterable<Double> radiusIterable) {
        this.centerIterable = centerIterable;
        this.radiusIterable = radiusIterable;
    }

    @Override
    public Iterator<Circle> iterator() {
        return new CircleIterator(centerIterable.iterator(), radiusIterable.iterator());
    }

}
