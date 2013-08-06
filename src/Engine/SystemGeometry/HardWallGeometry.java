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
public final class HardWallGeometry extends AbstractGeometry {

    @Override
    public GeometryBuilder toBuilder() {
        return new HardWallGeometryBuilder(this);
    }

    public static class HardWallGeometryBuilder extends AbstractGeometryBuilder {

        private HardWallGeometryBuilder(HardWallGeometry geometry) {
            super(geometry);
        }

        public HardWallGeometryBuilder() {
            super();
        }

        @Override
        public HardWallGeometry buildGeometry() {
            return new HardWallGeometry(dimension, fullRMax, parameters);
        }
    }

    static public HardWallGeometry getDefaultGeometry() {
        HardWallGeometryBuilder builder = new HardWallGeometryBuilder();
        return builder.buildGeometry();
    }

    public HardWallGeometry(int dimension, double[] fullRMax, SimulationParameters parameters) {
        super(dimension, fullRMax, parameters);
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
    public TwoBeadOverlap twoBeadOverlap(double[] position1, double[] position2) {
        TwoBeadOverlap twoBeadOverlap = new TwoBeadOverlap(1, 1);

        for (int i = 0; i < dimension; i++) {
            double componentDistance = Math.abs(position1[i] - position2[i]);
            twoBeadOverlap.softOverlap *= Math.max(parameters.getInteractionLength() - componentDistance, 0.0);
            twoBeadOverlap.hardOverlap *= Math.max(parameters.getCoreLength() - componentDistance, 0.0);
        }

        return twoBeadOverlap;
    }

    @Override
    public void incrementFirstVector(double[] toStep, double[] stepVector) {
        for (int i = 0; i < dimension; i++) {
            toStep[i] += stepVector[i];
        }
    }

    @Override
    public void decrementFirstVector(double[] toStep, double[] stepVector) {
        for (int i = 0; i < dimension; i++) {
            toStep[i] -= stepVector[i];
        }
    }

    @Override
    public void checkedCopyPosition(double[] src, double[] dest) {
        if (!isPositionValid(src)) {
            return;
        }
        System.arraycopy(src, 0, dest, 0, dimension);
    }
}
