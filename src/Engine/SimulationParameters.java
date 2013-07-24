/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine;

/**
 *
 * @author bmoths
 */
public class SimulationParameters {

    private final double interactionLength, coreLength, stepLength;

    public SimulationParameters() {
        double tempCoreLength;

        interactionLength = 5;
        tempCoreLength = calculateCoreLength(interactionLength);
        coreLength = tempCoreLength;
        stepLength = interactionLength / 2;
    }

    public SimulationParameters(double stepLength, double interactionLength) {
        double tempCoreLength;

        this.interactionLength = interactionLength;
        this.stepLength = stepLength;

        tempCoreLength = calculateCoreLength(interactionLength);
        coreLength = tempCoreLength;
    }

    public SimulationParameters(SimulationParameters simulationParameters) {
        double tempCoreLength;

        this.stepLength = simulationParameters.stepLength;
        this.interactionLength = simulationParameters.interactionLength;

        tempCoreLength = calculateCoreLength(interactionLength);
        coreLength = tempCoreLength;
    }

    public SimulationParameters makeParametersFromPhysicalConstants(PhysicalConstants physicalConstants) {
        return new SimulationParameters(this, physicalConstants);
    }

    private SimulationParameters(SimulationParameters simulationParameters, PhysicalConstants physicalConstants) {
        this.stepLength = simulationParameters.stepLength;
        this.interactionLength = simulationParameters.interactionLength;

        coreLength = coreLengthFromPhysicalConstants(physicalConstants);
    }

    private double coreLengthFromPhysicalConstants(PhysicalConstants physicalConstants) {
        double thermalForce = .5 * physicalConstants.getTemperature() / interactionLength;
        double minCoefficientForBonding = -thermalForce / interactionLength;
        System.out.println(minCoefficientForBonding);
        double minAttraction = Math.min(Math.min(physicalConstants.getBBOverlapCoefficient(), physicalConstants.getAAOverlapCoefficient()), minCoefficientForBonding);
        return interactionLength + thermalForce / minAttraction;
    }

    private double calculateCoreLength(double interactionLength) {
        return interactionLength / 2;
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
}
