/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine.Energetics;

/**
 *
 * @author brian
 */
public class EnergyEntropyChange {

    static public EnergyEntropyChange sum(EnergyEntropyChange firstSummand, EnergyEntropyChange secondSummand) {
        final double energySum = firstSummand.getEnergy() + secondSummand.getEnergy();
        final double entropySum = firstSummand.getEntropy() + secondSummand.getEntropy();

        EnergyEntropyChange sum;
        sum = new EnergyEntropyChange(energySum, entropySum);
        return sum;
    }

    private final double energy, entropy;

    public EnergyEntropyChange(double energy, double entropy) {
        this.energy = energy;
        this.entropy = entropy;
    }

    public EnergyEntropyChange(EnergyEntropyChange energyEntropyChange) {
        this(energyEntropyChange.energy, energyEntropyChange.entropy);
    }

    public double calculateFreeEnergyChange(double temperature) {
        return energy - temperature * entropy;
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
    public double getEnergy() {
        return energy;
    }

    public double getEntropy() {
        return entropy;
    }
    //</editor-fold>

}