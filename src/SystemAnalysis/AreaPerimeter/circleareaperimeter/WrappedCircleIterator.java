/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SystemAnalysis.AreaPerimeter.circleareaperimeter;

import SystemAnalysis.AreaPerimeter.circleareaperimeter.Circle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

/**
 *
 * @author bmoths
 */
class WrappedCircleIterator implements Iterator<Circle> {

    private final Iterator<Circle> circleIterator;
    private final Rectangle2D boundaryRectangle;
    private Circle lastCircle;
    private boolean needsReflectionOverVertical;
    private boolean needsReflectionOverHorizontal;
    private boolean needsReflectionOverBoth;

    public WrappedCircleIterator(Iterator<Circle> circleIterator, Rectangle2D boundaryRectangle) {
        this.circleIterator = circleIterator;
        this.boundaryRectangle = boundaryRectangle;
        needsReflectionOverHorizontal = false;
        needsReflectionOverVertical = false;
        needsReflectionOverBoth = false;
    }

    @Override
    public boolean hasNext() {
        return needsAnyReflection() || circleIterator.hasNext();
    }

    @Override
    public Circle next() {
        if (!needsAnyReflection()) {
            lastCircle = circleIterator.next();
            computeNeedsReflection();
            return lastCircle;
        } else if (needsReflectionOverVertical) {
            needsReflectionOverVertical = false;
            return reflectOverVertical(lastCircle);
        } else if (needsReflectionOverHorizontal) {
            needsReflectionOverHorizontal = false;
            return reflectOverHorizontal(lastCircle);
        } else if (needsReflectionOverBoth) {
            needsReflectionOverBoth = false;
            return reflectOverHorizontal(reflectOverVertical(lastCircle));
        }
        throw new AssertionError();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void computeNeedsReflection() {
        needsReflectionOverHorizontal = (lastCircle.getCenterY() - lastCircle.getRadius() < boundaryRectangle.getMinY()) || (lastCircle.getCenterY() + lastCircle.getRadius() > boundaryRectangle.getMaxY());
        needsReflectionOverVertical = (lastCircle.getCenterX() - lastCircle.getRadius() < boundaryRectangle.getMinX()) || (lastCircle.getCenterX() + lastCircle.getRadius() > boundaryRectangle.getMaxX());
        needsReflectionOverBoth = needsReflectionOverHorizontal && needsReflectionOverVertical;
    }

    private Circle reflectOverVertical(Circle lastCircle) {
        if (lastCircle.getCenterX() - lastCircle.getRadius() < boundaryRectangle.getMinX()) {
            return new Circle(new Point2D.Double(lastCircle.getCenterX() + boundaryRectangle.getWidth(), lastCircle.getCenterY()), lastCircle.getRadius());
        } else if (lastCircle.getCenterX() + lastCircle.getRadius() > boundaryRectangle.getMaxX()) {
            return new Circle(new Point2D.Double(lastCircle.getCenterX() - boundaryRectangle.getWidth(), lastCircle.getCenterY()), lastCircle.getRadius());
        }
        throw new AssertionError("reflect over verticle true, but reflection over vertical not needed");
    }

    private Circle reflectOverHorizontal(Circle lastCircle) {
        if (lastCircle.getCenterY() - lastCircle.getRadius() < boundaryRectangle.getMinY()) {
            return new Circle(new Point2D.Double(lastCircle.getCenterX(), lastCircle.getCenterY() + boundaryRectangle.getHeight()), lastCircle.getRadius());
        } else if (lastCircle.getCenterY() + lastCircle.getRadius() > boundaryRectangle.getMaxY()) {
            return new Circle(new Point2D.Double(lastCircle.getCenterX(), lastCircle.getCenterY() - boundaryRectangle.getHeight()), lastCircle.getRadius());
        }
        throw new AssertionError("reflect over horizontal true, but reflection over horizontal not needed");
    }

    private boolean needsAnyReflection() {
        return needsReflectionOverHorizontal || needsReflectionOverBoth || needsReflectionOverVertical;
    }

}
