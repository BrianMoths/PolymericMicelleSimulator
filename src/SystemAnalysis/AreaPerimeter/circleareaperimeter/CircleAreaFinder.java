/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SystemAnalysis.AreaPerimeter.circleareaperimeter;

import java.awt.geom.Rectangle2D;
import kn.uni.voronoitreemap.j2d.Site;

/**
 *
 * @author bmoths
 */
public class CircleAreaFinder extends PowerDiagramEvaluator<Double> {

    public static double findAreaOfCircles(final Iterable<Circle> circles, Rectangle2D boundaryRectangle) {
        CircleAreaFinder circleAreaFinder = new CircleAreaFinder();
        return circleAreaFinder.computeValue(circles, boundaryRectangle);
    }

    static public double findAreaOfCirclesPeriodic(final Iterable<Circle> circles, Rectangle2D boundaryRectangle) {
        final Iterable<Circle> wrappedCircles = new WrappedCircleIterable(circles, boundaryRectangle);
        return CircleAreaFinder.findAreaOfCircles(wrappedCircles, boundaryRectangle);
    }

    double area;
    double squareRadius;

    @Override
    protected void initialize() {
        area = 0;
    }

    @Override
    protected void initializeForSite(Site site) {
        squareRadius = site.getWeight();
    }

    @Override
    protected void processNonIntersectingRegion(double deltaTheta) {
        area += squareRadius * deltaTheta / 2;
    }

    @Override
    protected void processIntersectingRegion(double length, double y) {
        area -= length * y / 2;
    }

    @Override
    protected Double getValue() {
        return area;
    }

}
