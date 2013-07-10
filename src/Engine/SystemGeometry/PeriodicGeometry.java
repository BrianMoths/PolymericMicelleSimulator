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
public class PeriodicGeometry extends AbstractGeometry {
    
    public static class PeriodicGeometryBuilder extends GeometryBuilder {
        
        @Override
        public PeriodicGeometry buildGeometry() {
            return new PeriodicGeometry(dimension, fullRMax, parameters);
        }
    }
    
    static public PeriodicGeometry defaultGeometry() {
        PeriodicGeometryBuilder builder = new PeriodicGeometryBuilder();
        return builder.buildGeometry();
    }
    
    public PeriodicGeometry(int dimension, double[] fullRMax, SimulationParameters parameters) {
        super(dimension, fullRMax, parameters);
    }
    
    @Override
    public boolean isPositionValid(double[] position) {
        return true;
    }
    
    @Override
    public boolean isSumInBounds(double[] position, double[] translation) {
        return true;
    }
    
    @Override
    public double sqDist(double[] position1, double[] position2) {
        double sqDist = 0;
        double distance;
        for (int i = 0; i < dimension; i++) {
            distance = componentDistance(position1[i], position2[i], i);
            sqDist += distance * distance;
        }
        return sqDist;
    }
    
    @Override
    public double areaOverlap(double[] position1, double[] position2) {
        double overlap = 1;
        
        for (int i = 0; i < dimension; i++) {
            overlap *= Math.max(parameters.getInteractionLength() - componentDistance(position1[i], position2[i], i), 0.0);
        }
        
        return overlap;
    }
    
    @Override
    public void incrementFirstVector(double[] toStep, double[] stepVector) {
        for (int i = 0; i < dimension; i++) {
            toStep[i] += stepVector[i];
            toStep[i] = projectComponent(toStep[i], i);
        }
    }
    
    @Override
    public void decrementFirstVector(double[] toStep, double[] stepVector) {
        for (int i = 0; i < dimension; i++) {
            toStep[i] -= stepVector[i];
            toStep[i] = projectComponent(toStep[i], i);
        }
    }
    
    private double projectComponent(double component, int dimension) {
        if (component < 0) {
            component += fullRMax[dimension];
        } else if (component > fullRMax[dimension]) {
            component -= fullRMax[dimension];
        }
        return component;
    }
    
    private double componentDistance(double component1, double component2, int dimension) {
        double distance;
        distance = Math.abs(component1 - component2);
        distance = Math.min(distance, fullRMax[dimension] - distance);
        return distance;
    }
}
