/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine.SystemGeometry;

import Engine.PolymerCluster;

/**
 *
 * @author bmoths
 */
public interface GeometryBuilder {

    public int getDimension();

    public GeometryBuilder setDimension(int dimension);

    public double[] getFullRMax();

    public GeometryBuilder setDimensionSize(int dimension, double size);

    public GeometricalParameters getParameters();

    public GeometryBuilder setParameters(GeometricalParameters parameters);

    public void makeConsistentWith(PolymerCluster polymerCluster, GeometricalParameters simulationParameters);

    public SystemGeometry buildGeometry();
}
