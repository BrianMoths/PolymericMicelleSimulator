/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine.SystemGeometry;

/**
 *
 * @author bmoths
 */
public class AreaOverlap {

    public double AAOverlap, BBOverlap, ABOverlap;

    public static AreaOverlap overlapOfBead(boolean isTypeA, double AOverlap, double BOverlap) {
        AreaOverlap areaOverlap = new AreaOverlap();
        if (isTypeA) {
            areaOverlap.AAOverlap = AOverlap;
            areaOverlap.ABOverlap = BOverlap;
        } else {
            areaOverlap.ABOverlap = AOverlap;
            areaOverlap.BBOverlap = BOverlap;
        }

        return areaOverlap;
    }

    public AreaOverlap() {
        AAOverlap = 0;
        BBOverlap = 0;
        ABOverlap = 0;
    }

    public static AreaOverlap subtract(AreaOverlap overlap1, AreaOverlap overlap2) {
        AreaOverlap overlapDifference = new AreaOverlap();

        overlapDifference.AAOverlap = overlap1.AAOverlap - overlap2.AAOverlap;
        overlapDifference.ABOverlap = overlap1.ABOverlap - overlap2.ABOverlap;
        overlapDifference.BBOverlap = overlap1.BBOverlap - overlap2.BBOverlap;

        return overlapDifference;
    }

    public void incrementBy(AreaOverlap areaOverlap) {
        AAOverlap += areaOverlap.AAOverlap;
        BBOverlap += areaOverlap.BBOverlap;
        ABOverlap += areaOverlap.ABOverlap;
    }

    public void halve() {
        AAOverlap /= 2;
        ABOverlap /= 2;
        BBOverlap /= 2;
    }
}
