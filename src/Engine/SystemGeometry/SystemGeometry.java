/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine.SystemGeometry;

import Engine.TwoBeadOverlap;
import SystemAnalysis.AreaPerimeter.BeadRectangle;
import SystemAnalysis.AreaPerimeter.RectanglesAndBoundaryPerimeter;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author bmoths
 */
public interface SystemGeometry extends Serializable {

    public GeometryBuilder toBuilder();

    public boolean isPositionValid(double[] position);

    public int getDimension();

    public double[] getRMax();

    public void setRMax(int index, double rMax);

    public double getVolume();

    public GeometricalParameters getParameters();

    public double sqDist(double[] position1, double[] position2);

    public TwoBeadOverlap twoBeadOverlap(double[] position1, double[] position2);

    public double[] randomPosition();

    public double[][] randomPositions(int numPositions);

    public double[] randomColumnPosition();

    public double[][] randomColumnPositions(int numPositions);

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

    public boolean incrementFirstVector(double[] toStep, double[] stepVector);

    public void decrementFirstVector(double[] toStep, double[] stepVector);

    public void checkedCopyPosition(double[] src, double[] dest);

    public void checkedCopyPositions(double[][] src, double[][] dest);

    public RectanglesAndBoundaryPerimeter getRectanglesAndPerimeterFromPositions(double[][] beadPostions);

    public BeadRectangle getRectangleFromPosition(double[] beadPosition);

    public List<BeadRectangle> getRectanglesFromPositions(double[][] beadPositions);

}
