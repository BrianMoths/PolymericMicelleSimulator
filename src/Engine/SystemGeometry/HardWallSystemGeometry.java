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
public class HardWallSystemGeometry extends AbstractGeometry {

    public HardWallSystemGeometry() {
        dimension = 2;
        fullRMax = new double[]{20, 20, 20};
        parameters = new SimulationParameters();
    }

    @Override
    public boolean isPositionValid(double[] position) {
        for (int i = 0; i < dimension; i++) {
            if (position[i] < 0 || position[i] > fullRMax[i]) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isSumInBounds(double[] position, double[] translation) {
        double sumCoordinate;
        for (int i = 0; i < dimension; i++) {
            sumCoordinate = position[i] + translation[i];
            if (sumCoordinate < 0 || sumCoordinate > fullRMax[i]) {
                return false;
            }
        }
        return true;
    }

    @Override
    public double sqDist(double[] position1, double[] position2) {
        double sqDist = 0;
        for (int i = 0; i < dimension; i++) {
            sqDist += (position1[i] - position2[i]) * (position1[i] - position2[i]);
        }
        return sqDist;
    }

    @Override
    public double areaOverlap(double[] position1, double[] position2) {
        double overlap = 1;

        for (int i = 0; i < dimension; i++) {
            overlap *= Math.max(parameters.getInteractionLength() - Math.abs(position1[i] - position2[i]), 0.0);
        }

        return overlap;
    }

    @Override
    public void doStep(double[] toStep, double[] stepVector) {
        for (int i = 0; i < dimension; i++) {
            toStep[i] += stepVector[i];
        }
    }

    @Override
    public void undoStep(double[] toStep, double[] stepVector) {
        for (int i = 0; i < dimension; i++) {
            toStep[i] -= stepVector[i];
        }
    }
}
