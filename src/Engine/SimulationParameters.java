/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine;

import java.io.Serializable;

/**
 *
 * @author bmoths
 */
public final class SimulationParameters implements Serializable {

    private final double interactionLength, coreLength, stepLength;

    public SimulationParameters() {
        interactionLength = 5;
        coreLength = 0;
        stepLength = interactionLength / 2; //step length should be set by spring constant
    }

    public SimulationParameters(double stepLength, double interactionLength) {
        this.interactionLength = interactionLength;
        this.stepLength = stepLength;
        coreLength = 0;
    }

    public SimulationParameters(double stepLength, double interactionLength, double coreLength) {
        this.interactionLength = interactionLength;
        this.stepLength = stepLength;
        this.coreLength = coreLength;
    }

    public SimulationParameters(SimulationParameters simulationParameters) {
        stepLength = simulationParameters.stepLength;
        interactionLength = simulationParameters.interactionLength;
        coreLength = simulationParameters.getCoreLength();
    }

    public SimulationParameters makeParametersFromPhysicalConstants(PhysicalConstants physicalConstants) {
        return new SimulationParameters(this, physicalConstants);
    }

    private SimulationParameters(SimulationParameters simulationParameters, PhysicalConstants physicalConstants) {
//        this.stepLength = simulationParameters.stepLength;
        this.interactionLength = simulationParameters.interactionLength;
        stepLength = physicalConstants.idealStepLength();
        coreLength = coreLengthFromPhysicalConstants(physicalConstants);
    }

    private double coreLengthFromPhysicalConstants(PhysicalConstants physicalConstants) {
        final double attractionInT = .5; //.5
        double thermalForce = attractionInT * physicalConstants.getTemperature() / interactionLength;
        double minCoefficientForBonding = -thermalForce / interactionLength;
        System.out.println(minCoefficientForBonding);
        double minAttraction = Math.min(Math.min(physicalConstants.getBBOverlapCoefficient(), physicalConstants.getAAOverlapCoefficient()), minCoefficientForBonding);
        return interactionLength + thermalForce / minAttraction;
    }

    public double getStepLength() {
        return stepLength;
    }

    public double getInteractionLength() {
        return interactionLength;
    }

    public double getCoreLength() {
        return coreLength;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Interaction Length: ").append(Double.toString(interactionLength)).append("\n");
        stringBuilder.append("Core Length: ").append(Double.toString(coreLength)).append("\n");
        stringBuilder.append("Step Length: ").append(Double.toString(stepLength)).append("\n");
        return stringBuilder.toString();
    }
}
