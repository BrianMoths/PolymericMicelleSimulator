/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine.PolymerState.SystemGeometry.Interfaces;

import Engine.Energetics.TwoBeadOverlap;
import Engine.PolymerState.SystemGeometry.GeometricalParameters;
import SystemAnalysis.AreaPerimeter.BeadRectangle;
import SystemAnalysis.AreaPerimeter.RectangleSplitting.RectanglesAndGluedPerimeter;
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

    public double sqDist(double[] position1, double[] position2);

    public TwoBeadOverlap twoBeadRectangularOverlap(double[] position1, double[] position2);

    public TwoBeadOverlap twoBeadCircularOverlap(double[] position1, double[] position2);

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

    public RectanglesAndGluedPerimeter getRectanglesAndPerimeterFromPositions(double[][] beadPostions);

    public List<BeadRectangle> getRectanglesFromPositions(double[][] beadPositions);

}
