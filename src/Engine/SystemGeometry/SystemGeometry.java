/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine.SystemGeometry;

import Engine.SimulationParameters;

/**
 *
 * @author bmoths
 */
public interface SystemGeometry {

    public boolean isPositionValid(double[] position);

    public boolean isSumInBounds(double[] position, double[] translation);

    public void setDimension(int dimension);

    public int getDimension();

    public void setDimensionSize(int dimensionToBeSized, double size);

    public double[] getRMax();

    public double getVolume();

    public SimulationParameters getParameters();

    public void setParameters(SimulationParameters parameters);

    public double sqDist(double[] position1, double[] position2);

    public double areaOverlap(double[] position1, double[] position2);

    public double[] randomPosition();

    public double[][] randomPositions(int numPositions);

    public double[] randomGaussian();

    public void incrementFirstVector(double[] toStep, double[] stepVector);

    public void decrementFirstVector(double[] toStep, double[] stepVector);
}
