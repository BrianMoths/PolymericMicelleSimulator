/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine;

import Engine.PolymerState.SystemGeometry.GeometricalParameters;
import Engine.EnergeticsConstants.EnergeticsConstantsBuilder;
import Engine.PolymerState.SystemGeometry.Implementations.HardWallGeometry;

/**
 *
 * @author bmoths
 */
public class PolymerSimulatorFactory {

    private HardWallGeometry systemGeometry;
    private PolymerCluster polymerCluster;
    private EnergeticsConstantsBuilder physicalConstantsBuilder;
    private GeometricalParameters geometricalParameters;

    public PolymerSimulatorFactory() {
        systemGeometry = HardWallGeometry.getDefaultGeometry();
        polymerCluster = PolymerCluster.makeDefaultPolymerCluster();
        physicalConstantsBuilder = new EnergeticsConstantsBuilder();
    }

    public PolymerSimulator makePolymerSystem() {
        return new PolymerSimulator(
                systemGeometry,
                polymerCluster,
                physicalConstantsBuilder.buildEnergeticsConstants());
    }

    public void setTemperature(double temperature) {
        physicalConstantsBuilder.setTemperature(temperature);
    }

    public void setSimilarOverlapCoefficient(double similarOverlapCoefficient) {
        physicalConstantsBuilder.setAAOverlapCoefficient(similarOverlapCoefficient);
        physicalConstantsBuilder.setBBOverlapCoefficient(similarOverlapCoefficient);
    }

    public void setDifferentOverlapCoefficient(double differentOverlapCoefficient) {
        physicalConstantsBuilder.setABOverlapCoefficient(differentOverlapCoefficient);
    }

    public void setSpringCoefficient(double springCoefficient) {
        physicalConstantsBuilder.setSpringCoefficient(springCoefficient);
    }

//    public void setInteractionLength(double interactionLength) {
//        geometricalParameters.setInteractionLength(interactionLength);
//    }
//
//    public void setStepLength(double stepLength) {
//        geometricalParameters.setStepLength(stepLength);
//    }
    public void setPolymerCluster(PolymerCluster polymerCluster) {
        this.polymerCluster = PolymerCluster.copy(polymerCluster);
    }

}
