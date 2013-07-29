/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine.SystemGeometry;

import Engine.PolymerCluster;
import Engine.SimulationParameters;

/**
 *
 * @author bmoths
 */
public interface GeometryBuilder {

    public int getDimension();

    public GeometryBuilder setDimension(int dimension);

    public double[] getFullRMax();

    public GeometryBuilder setDimensionSize(int dimension, double size);

    public SimulationParameters getParameters();

    public GeometryBuilder setParameters(SimulationParameters parameters);

    public void makeConsistentWith(PolymerCluster polymerCluster, SimulationParameters simulationParameters);

    public SystemGeometry buildGeometry();
}
