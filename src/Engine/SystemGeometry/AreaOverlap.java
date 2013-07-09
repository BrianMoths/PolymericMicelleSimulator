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

    public double similarOverlap;
    public double differentOverlap;

    public static AreaOverlap overlapWithSimDiff(double similarOverlap, double differentOverlap) {
        AreaOverlap areaOverlap = new AreaOverlap();
        areaOverlap.similarOverlap = similarOverlap;
        areaOverlap.differentOverlap = differentOverlap;
        return areaOverlap;
    }

    private AreaOverlap() {
        similarOverlap = 0;
        differentOverlap = 0;
    }

    public static AreaOverlap subtract(AreaOverlap overlap1, AreaOverlap overlap2) {
        return AreaOverlap.overlapWithSimDiff(overlap1.similarOverlap - overlap2.similarOverlap,
                overlap1.differentOverlap - overlap2.differentOverlap);
    }
}
