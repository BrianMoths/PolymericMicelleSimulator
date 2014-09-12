/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine.PolymerState.SystemGeometry;

import Engine.Energetics.EnergeticsConstants.EnergeticsConstantsBuilder;
import java.io.Serializable;

/**
 *
 * @author bmoths
 */
public final class GeometricalParameters implements Serializable {

    static public enum Shape {

        SQUARE,
        CIRCLE;
    }

    private static final long serialVersionUID = 0L;
    private final double interactionLength, coreLength, stepLength;
    private Shape shape = Shape.CIRCLE;

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + (int) (Double.doubleToLongBits(this.interactionLength) ^ (Double.doubleToLongBits(this.interactionLength) >>> 32));
        hash = 89 * hash + (int) (Double.doubleToLongBits(this.coreLength) ^ (Double.doubleToLongBits(this.coreLength) >>> 32));
        hash = 89 * hash + (int) (Double.doubleToLongBits(this.stepLength) ^ (Double.doubleToLongBits(this.stepLength) >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final GeometricalParameters other = (GeometricalParameters) obj;
        if (Double.doubleToLongBits(this.interactionLength) != Double.doubleToLongBits(other.interactionLength)) {
            return false;
        }
        if (Double.doubleToLongBits(this.coreLength) != Double.doubleToLongBits(other.coreLength)) {
            return false;
        }
        if (Double.doubleToLongBits(this.stepLength) != Double.doubleToLongBits(other.stepLength)) {
            return false;
        }
        return true;
    }

    public GeometricalParameters() {
        interactionLength = 5;
        coreLength = 1;
        stepLength = interactionLength / 2; //step length should be set by spring constant
    }

    public GeometricalParameters(double stepLength, double interactionLength) {
        this(stepLength, interactionLength, 0);
    }

    public GeometricalParameters(double stepLength, double interactionLength, double coreLength) {
        this.interactionLength = interactionLength;
        this.stepLength = stepLength;
        this.coreLength = coreLength;
    }

    public GeometricalParameters(GeometricalParameters geometricalParameters) {
        stepLength = geometricalParameters.stepLength;
        interactionLength = geometricalParameters.interactionLength;
        coreLength = geometricalParameters.coreLength;
    }

    public GeometricalParameters(double interactionLength, EnergeticsConstantsBuilder energeticsConstantsBuilder, double coreLength) {
        this.interactionLength = interactionLength;
        stepLength = energeticsConstantsBuilder.idealStepLength();
        this.coreLength = coreLength;
    }

    public GeometricalParameters(double interactionLength, EnergeticsConstantsBuilder energeticsConstantsBuilder) {
        this.interactionLength = interactionLength;
        stepLength = energeticsConstantsBuilder.idealStepLength();
        coreLength = coreLengthFromPhysicalConstants(energeticsConstantsBuilder, interactionLength);
    }

    static public double coreLengthFromPhysicalConstants(EnergeticsConstantsBuilder energeticsConstantsBuilder, double interactionLength) {
        final double attractionInT = .5; //.5
        double thermalForce = attractionInT * energeticsConstantsBuilder.getTemperature() / interactionLength;
        double minCoefficientForBonding = -thermalForce / interactionLength;
        double minAttraction = Math.min(Math.min(energeticsConstantsBuilder.getBBOverlapCoefficient() / 6, energeticsConstantsBuilder.getAAOverlapCoefficient() / 6), minCoefficientForBonding);
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

    public Shape getShape() {
        return shape;
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
