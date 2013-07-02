/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine;

import java.util.Random;

/**
 *
 * @author bmoths
 */
public class PhysicalConstants {

    static private Random randomNumberGenerator = new Random();
    private double temperature, similarOverlapCoefficient,
            differentOverlapCoefficient, springCoefficient;

    public PhysicalConstants() {
        temperature = 300;
        similarOverlapCoefficient = 1;
        differentOverlapCoefficient = 4;
        springCoefficient = 40;
    }

    public double springEnergy(double squareLength) {
        return springCoefficient * squareLength;
    }

    public double densityEnergy(double similarOverlap, double differentOverlap) {
        return similarOverlapCoefficient * similarOverlap + differentOverlapCoefficient * differentOverlap;
    }

    public boolean isEnergeticallyAllowed(double energyChange) {
        return energyChange < 0
                || randomNumberGenerator.nextDouble() < Math.exp(-energyChange / temperature);
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        if (temperature > 0) {
            this.temperature = temperature;
        }
    }

    public double getSimilarOverlapCoefficient() {
        return similarOverlapCoefficient;
    }

    public void setSimilarOverlapCoefficient(double similarOverlapCoefficient) {
        this.similarOverlapCoefficient = similarOverlapCoefficient;
    }

    public double getDifferentOverlapCoefficient() {
        return differentOverlapCoefficient;
    }

    public void setDifferentOverlapCoefficient(double differentOverlapCoefficient) {
        this.differentOverlapCoefficient = differentOverlapCoefficient;
    }

    public double getSpringCoefficient() {
        return springCoefficient;
    }

    public void setSpringCoefficient(double springCoefficient) {
        this.springCoefficient = springCoefficient;
    }
}
