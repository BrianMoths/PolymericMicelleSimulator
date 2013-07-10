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
}
