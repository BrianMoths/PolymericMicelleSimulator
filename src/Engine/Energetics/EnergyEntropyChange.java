/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine.Energetics;

import java.io.Serializable;

/**
 * This class represents a change in energy and entropy. It provides a method to
 * find a free energy changed given a temperature. Objects of this class are
 * immutable.
 *
 * @author brian
 */
public class EnergyEntropyChange implements Serializable {

    private static final long serialVersionUID = 0L;

    /**
     *
     * Returns an energy entropy change representing the sum of the two given
     * energy entropy changes.
     *
     * @param firstSummand the first summand of the sum
     * @param secondSummand the second summand of the sum
     * @return the sum
     */
    static public EnergyEntropyChange sum(EnergyEntropyChange firstSummand, EnergyEntropyChange secondSummand) {
        final double energySum = firstSummand.getEnergy() + secondSummand.getEnergy();
        final double entropySum = firstSummand.getEntropy() + secondSummand.getEntropy();

        EnergyEntropyChange sum;
        sum = new EnergyEntropyChange(energySum, entropySum);
        return sum;
    }

    private final double energy, entropy;

    /**
     * constructs an energy entropy change with the given changes in energy and
     * entropy
     *
     * @param energy the change in energy
     * @param entropy the change in entropy
     */
    public EnergyEntropyChange(double energy, double entropy) {
        this.energy = energy;
        this.entropy = entropy;
    }

    /**
     * a copy constructor for energy entropy
     *
     * @param energyEntropyChange the energy entropy to be copied
     */
    public EnergyEntropyChange(EnergyEntropyChange energyEntropyChange) {
        this(energyEntropyChange.energy, energyEntropyChange.entropy);
    }

    /**
     * returns the free energy change implied by the energy entropy change at
     * the given temperature.
     *
     * @param temperature the temperature at which the changes in energy and
     * entropy took place.
     * @return the resulting change in free energy
     */
    public double calculateFreeEnergy(double temperature) {
        return energy - temperature * entropy;
    }

    /**
     * returns the energy entropy change obtained by incrementing this energy
     * entropy change by the given increment. This method has no side effects.
     * In particular this energy entropy change is not modified.
     *
     * @param increment the energy entropy changed to be added to this energy
     * entropy change
     * @return
     */
    public EnergyEntropyChange incrementedBy(EnergyEntropyChange increment) {
        return new EnergyEntropyChange(energy + increment.energy, entropy + increment.entropy);
    }

    //<editor-fold defaultstate="collapsed" desc="equals and hashcode">
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + (int) (Double.doubleToLongBits(this.energy) ^ (Double.doubleToLongBits(this.energy) >>> 32));
        hash = 67 * hash + (int) (Double.doubleToLongBits(this.entropy) ^ (Double.doubleToLongBits(this.entropy) >>> 32));
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
        final EnergyEntropyChange other = (EnergyEntropyChange) obj;
        if (Double.doubleToLongBits(this.energy) != Double.doubleToLongBits(other.energy)) {
            return false;
        }
        if (Double.doubleToLongBits(this.entropy) != Double.doubleToLongBits(other.entropy)) {
            return false;
        }
        return true;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="getters">
    /**
     * returns the change in energy of this energy entropy change.
     *
     * @return the change in energy
     */
    public double getEnergy() {
        return energy;
    }

    /**
     * returns the change in entropy of this energy entropy change.
     *
     * @returnthe change in entropy
     */
    public double getEntropy() {
        return entropy;
    }
    //</editor-fold>

}
