/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SystemAnalysis;

import Engine.PolymerSimulator;

/**
 *
 * @author bmoths
 */
public class FullStressTrackable extends FractionalVolumeStressTrackable {

    static public final FractionalVolumeStressTrackable FULL_REGION_STRESS_TRACKABLE = new FullStressTrackable();
    double scaleFactor;

    private FullStressTrackable() {
        super(1.);
    }

    @Override
    protected double getStress(int i, int j, PolymerSimulator polymerSimulator) {
        if (isCalculationNeeded()) {
            final double coveredArea = polymerSimulator.getSystemAnalyzer().findArea();
            final double totalArea = polymerSimulator.getSystemAnalyzer().getSystemGeometry().getVolume();
            scaleFactor = totalArea / coveredArea;
        }
        final double unscaledStress = super.getStress(i, j, polymerSimulator); //To change body of generated methods, choose Tools | Templates.
        return unscaledStress * scaleFactor;
    }

}
