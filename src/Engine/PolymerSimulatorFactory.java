/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine;

import Engine.SystemGeometry.HardWallSystemGeometry;

/**
 *
 * @author bmoths
 */
public class PolymerSimulatorFactory {

    private HardWallSystemGeometry systemGeometry;
    private PolymerCluster polymerCluster;
    private PhysicalConstants physicalConstants;
    private SimulationParameters simulationParameters;

    public PolymerSimulatorFactory() {
        systemGeometry = new HardWallSystemGeometry();
        polymerCluster = PolymerCluster.defaultCluster();
        physicalConstants = new PhysicalConstants();
        simulationParameters = new SimulationParameters();
    }

    public PolymerSimulator makePolymerSystem() {
        return new PolymerSimulator(
                systemGeometry,
                polymerCluster,
                physicalConstants,
                simulationParameters);
    }

    public void setTemperature(double temperature) {
        physicalConstants.setTemperature(temperature);
    }

    public void setSimilarOverlapCoefficient(double similarOverlapCoefficient) {
        physicalConstants.setSimilarOverlapCoefficient(similarOverlapCoefficient);
    }

    public void setDifferentOverlapCoefficient(double differentOverlapCoefficient) {
        physicalConstants.setDifferentOverlapCoefficient(differentOverlapCoefficient);
    }

    public void setSpringCoefficient(double springCoefficient) {
        physicalConstants.setSpringCoefficient(springCoefficient);
    }

    public void setInteractionLength(double interactionLength) {
        simulationParameters.setInteractionLength(interactionLength);
    }

    public void setStepLength(double stepLength) {
        simulationParameters.setStepLength(stepLength);
    }

    public void setPolymerCluster(PolymerCluster polymerCluster) {
        this.polymerCluster = PolymerCluster.copy(polymerCluster);
    }
}
