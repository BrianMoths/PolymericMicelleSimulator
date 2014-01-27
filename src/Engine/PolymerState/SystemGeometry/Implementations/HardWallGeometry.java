/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine.PolymerState.SystemGeometry.Implementations;

import Engine.PolymerState.SystemGeometry.GeometricalParameters;
import Engine.PolymerState.SystemGeometry.Interfaces.GeometryBuilder;
import SystemAnalysis.AreaPerimeter.BeadRectangle;
import SystemAnalysis.AreaPerimeter.RectangleSplitting.HardWallRectangleSplitter;
import SystemAnalysis.AreaPerimeter.RectangleSplitting.RectangleSplitter;
import SystemAnalysis.AreaPerimeter.RectangleSplitting.RectanglesAndGluedPerimeter;
import java.util.List;

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

    public HardWallGeometry(int dimension, double[] fullRMax, GeometricalParameters parameters) {
        super(dimension, fullRMax, parameters);
    }

    @Override
    public List<BeadRectangle> getRectanglesFromPositions(double[][] beadPositions) {
        List<BeadRectangle> beadRectangles;
        beadRectangles = getUnsplitRectanglesFromPositions(beadPositions);

        RectangleSplitter rectangleSplitter = new HardWallRectangleSplitter();

        return rectangleSplitter.splitRectanglesOverBoundary(beadRectangles, makeLimits());
    }

    @Override
    public RectanglesAndGluedPerimeter getRectanglesAndPerimeterFromPositions(double[][] beadPositions) {
        List<BeadRectangle> beadRectangles = getRectanglesFromPositions(beadPositions);
        RectanglesAndGluedPerimeter rectanglesAndPerimeter = new RectanglesAndGluedPerimeter(beadRectangles, 0);
        return rectanglesAndPerimeter;
    }

    @Override
    public boolean isPositionValid(double[] position) {
        for (int i = 0; i < numDimensions; i++) {
            if (!isComponentValid(position[i], i)) {
                return false;
            }
        }
        return true;
    }

    private boolean isComponentValid(double component, int index) {
        return component < 0 || component > fullRMax[index];
    }

    @Override
    public boolean incrementFirstVector(double[] toStep, double[] stepVector) {
        for (int i = 0; i < numDimensions; i++) {
            toStep[i] += stepVector[i];
            if (!isComponentValid(toStep[i], i)) {
                for (int j = i; j >= 0; j--) {
                    toStep[j] -= stepVector[j];
                }
                return false;
            }
        }
        return true;
    }

    @Override
    public void decrementFirstVector(double[] toStep, double[] stepVector) {
        for (int i = 0; i < numDimensions; i++) {
            toStep[i] -= stepVector[i];
        }
    }

    @Override
    public void checkedCopyPosition(double[] src, double[] dest) {
        if (!isPositionValid(src)) {
            return;
        }
        System.arraycopy(src, 0, dest, 0, numDimensions);
    }

    @Override
    protected double calculateComponentDistance(double component1, double component2, int dimension) {
        return Math.abs(component1 - component2);
    }

}
