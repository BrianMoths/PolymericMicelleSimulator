/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine.SystemGeometry;

import Engine.EnergeticsConstants.EnergeticsConstantsBuilder;
import java.io.Serializable;

/**
 *
 * @author bmoths
 */
public final class GeometricalParameters implements Serializable {

    private final double interactionLength, coreLength, stepLength;

    public GeometricalParameters() {
        interactionLength = 5;
        coreLength = 1;
        stepLength = interactionLength / 2; //step length should be set by spring constant
    }

    public GeometricalParameters(double stepLength, double interactionLength) {
        this.interactionLength = interactionLength;
        this.stepLength = stepLength;
        coreLength = 0;
    }

    public GeometricalParameters(GeometricalParameters geometricalParameters) {
        stepLength = geometricalParameters.stepLength;
        interactionLength = geometricalParameters.interactionLength;
        coreLength = geometricalParameters.coreLength;
    }

    public GeometricalParameters(double interactionLength, EnergeticsConstantsBuilder energeticsConstantsBuilder) {
        this.interactionLength = interactionLength;
        stepLength = energeticsConstantsBuilder.idealStepLength();
        coreLength = coreLengthFromPhysicalConstants(energeticsConstantsBuilder);
    }

    private double coreLengthFromPhysicalConstants(EnergeticsConstantsBuilder energeticsConstantsBuilder) {
        final double attractionInT = .5; //.5
        double thermalForce = attractionInT * energeticsConstantsBuilder.getTemperature() / interactionLength;
        double minCoefficientForBonding = -thermalForce / interactionLength;
        double minAttraction = Math.min(Math.min(energeticsConstantsBuilder.getBBOverlapCoefficient(), energeticsConstantsBuilder.getAAOverlapCoefficient()), minCoefficientForBonding);
        return interactionLength + thermalForce / minAttraction;
    }

    //<editor-fold defaultstate="collapsed" desc="getters">
    public double getStepLength() {
        return stepLength;
    }

    public double getInteractionLength() {
        return interactionLength;
    }

    public double getCoreLength() {
        return coreLength;
    }
    //</editor-fold>

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Interaction Length: ").append(Double.toString(interactionLength)).append("\n");
        stringBuilder.append("Core Length: ").append(Double.toString(coreLength)).append("\n");
        stringBuilder.append("Step Length: ").append(Double.toString(stepLength)).append("\n");
        return stringBuilder.toString();
    }

}
