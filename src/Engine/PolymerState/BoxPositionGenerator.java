/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine.PolymerState;

import Engine.PolymerState.SystemGeometry.Interfaces.ImmutableSystemGeometry;

/**
 *
 * @author brian
 */
public class BoxPositionGenerator implements PositionGenerator {

    final double[] lowerFraction;
    final double[] upperFraction;
    final ImmutableSystemGeometry systemGeometry;

    public BoxPositionGenerator(double[] lowerFraction, double[] upperFraction, ImmutableSystemGeometry systemGeometry) {
        this.lowerFraction = lowerFraction;
        this.upperFraction = upperFraction;
        this.systemGeometry = systemGeometry;
    }

    @Override
    public double[] generatePosition() {
        return systemGeometry.randomBoxPosition(lowerFraction, upperFraction);
    }

}
