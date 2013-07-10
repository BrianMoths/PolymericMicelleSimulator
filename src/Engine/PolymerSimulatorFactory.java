/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine;

import Engine.PhysicalConstants.PhysicalConstantsBuilder;
import Engine.SystemGeometry.HardWallGeometry;

/**
 *
 * @author bmoths
 */
public class PolymerSimulatorFactory {

    private HardWallGeometry systemGeometry;
    private PolymerCluster polymerCluster;
    private PhysicalConstantsBuilder physicalConstantsBuilder;
    private SimulationParameters simulationParameters;

    public PolymerSimulatorFactory() {
        systemGeometry = HardWallGeometry.getDefaultGeometry();
        polymerCluster = PolymerCluster.defaultCluster();
        physicalConstantsBuilder = new PhysicalConstantsBuilder();
    }

    public PolymerSimulator makePolymerSystem() {
        return new PolymerSimulator(
                systemGeometry,
                polymerCluster,
                physicalConstantsBuilder.buildPhysicalConstants());
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
//        simulationParameters.setInteractionLength(interactionLength);
//    }
//
//    public void setStepLength(double stepLength) {
//        simulationParameters.setStepLength(stepLength);
//    }
    public void setPolymerCluster(PolymerCluster polymerCluster) {
        this.polymerCluster = PolymerCluster.copy(polymerCluster);
    }
}
