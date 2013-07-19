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
class InteractionCalculator {

    private final double min, minValue, slope, maxArea;

    public InteractionCalculator(SimulationParameters simulationParameters) {
        double interactionLength = simulationParameters.getInteractionLength();
        maxArea = interactionLength * interactionLength;
        min = 0.5 - simulationParameters.getCoreLength() / (2 * interactionLength);
        slope = simulationParameters.getInteractionLength() / simulationParameters.getCoreLength();
        minValue = -slope * min;
    }

    double calculateInteraction(double overlap) {
        if (overlap < 0) {
            return 0;
        } else {
            overlap /= maxArea;
            return minValue + slope * Math.abs(overlap - min);
        }
    }
}
