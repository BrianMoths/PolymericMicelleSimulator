/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine.PolymerState.SystemGeometry.Interfaces;

import Engine.Energetics.TwoBeadOverlap;
import Engine.PolymerState.SystemGeometry.GeometricalParameters;
import SystemAnalysis.AreaPerimeter.circleareaperimeter.Circle;
import SystemAnalysis.AreaPerimeter.circleareaperimeter.CirclesAndClippedPerimeter;
import SystemAnalysis.AreaPerimeter.rectangleareaperimeter.BeadRectangle;
import SystemAnalysis.AreaPerimeter.rectangleareaperimeter.RectangleSplitting.RectanglesAndGluedPerimeter;
import java.awt.geom.Rectangle2D;
import java.util.List;

/**
 *
 * @author bmoths
 */
public interface ImmutableSystemGeometry {

    public GeometryBuilder toBuilder();

    public boolean isPositionValid(double[] position);

    public int getNumDimensions();

    public double[] getRMax();

    public double getSizeOfDimension(int dimension);

    public double getVolume();

    public GeometricalParameters getParameters();

    /**
     * Finds the difference {@code position1} - {@code position2}. The method
     * should satisfy
     * {@code incrementBy(position2,getDisplacement(position1,position2))}
     * copies {@code position1} into {@code position2}. If there are many return
     * values which satisfy this relationship, then the one whose components
     * have smallest magnitude among those is returned.
     *
     * @param position1 the position to which the displacement is being
     * calculated
     * @param position2 the position form which the displacement is being
     * calculated
     * @return the displacement to position1 from position2.
     */
    public double[] getDisplacement(double[] position1, double[] position2);

    public double sqDist(double[] position1, double[] position2);

    public TwoBeadOverlap twoBeadRectangularOverlap(double[] position1, double[] position2);

    public TwoBeadOverlap twoBeadCircularOverlap(double[] position1, double[] position2);

    public double[] randomMiddlePosition();

    public double[][] randomMiddlePositions(int numPositions);

    public double[] randomColumnPosition();

    public double[] randomColumnPosition(double frac);

    public double[][] randomColumnPositions(int numPositions);

    public double[][] randomColumnPositions(int numPositions, double frac);

    public double[] randomPosition();

    public double[][] randomPositions(int numPositions);

    /**
     *
     * @return a random vector whose components are normally distributed with
     * standard deviation of step length
     */
    public double[] randomGaussian();

    /**
     *
     * @param scaleFactor factor by which to scale the Gaussian
     * @return a random vector whose components are normally distributed with
     * standard deviation of step length times the scale factor.
     */
    public double[] randomGaussian(double scaleFactor);

    /**
     * attempts to increment <tt>toStep</tt> by <tt>stepVector</tt>. If the
     * resulting position would not be valid according to the
     * {@link isPositionValid()} method, then neither double array is modified,
     * otherwise the <tt>toStep</tt> argument is modified to be equal to the
     * geometry's representation of a displacement of <tt>toStep</tt> by
     * <tt>stepVector</tt>, and <tt>stepVector</tt>
     * is unmodified.
     *
     * @param toStep the vector to be incremented. Must have length equal to
     * getNumDimensions()
     * @param stepVector the amount toStep ought to be incremented by. Must have
     * length equal to getNumDimensions()
     * @return a boolean indicating whether or not the move was legal
     */
    public boolean incrementFirstVector(double[] toStep, double[] stepVector);

    public void decrementFirstVector(double[] toStep, double[] stepVector);

    public void checkedCopyPosition(double[] src, double[] dest);

    public void checkedCopyPositions(double[][] src, double[][] dest);

    public RectanglesAndGluedPerimeter getRectanglesAndPerimeterFromPositions(double[][] beadPostions);

    public List<BeadRectangle> getRectanglesFromPositions(double[][] beadPositions);

    public Iterable<Circle> getCirclesFromPositions(double[][] beadPositions);

    public CirclesAndClippedPerimeter getCirclesAndBoundaryPerimeterFromPosition(double[][] beadPositions);

    public Rectangle2D getBoundaryRectangle();

}
