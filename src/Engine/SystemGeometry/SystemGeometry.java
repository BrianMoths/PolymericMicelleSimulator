/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine.SystemGeometry;

import Engine.SimulationParameters;
import Engine.TwoBeadOverlap;

/**
 *
 * @author bmoths
 */
public interface SystemGeometry {

    public GeometryBuilder toBuilder();

    public boolean isPositionValid(double[] position);

    public int getDimension();

    public double[] getRMax();

    public double getVolume();

    public SimulationParameters getParameters();

    public double sqDist(double[] position1, double[] position2);

    public double areaOverlap(double[] position1, double[] position2);

    public TwoBeadOverlap twoBeadOverlap(double[] position1, double[] position2);

    public double[] randomPosition();

    public double[][] randomPositions(int numPositions);

    public double[] randomGaussian();

    public boolean incrementFirstVector(double[] toStep, double[] stepVector);

    public void decrementFirstVector(double[] toStep, double[] stepVector);

    public void checkedCopyPosition(double[] src, double[] dest);

    public void checkedCopyPositions(double[][] src, double[][] dest);
}
