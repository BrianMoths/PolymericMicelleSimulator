/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SystemAnalysis.CoveredWeight;

/**
 *
 * @author bmoths
 */
public class NodeInterval {

    public int start, end;
    public boolean isBeingAdded;

    public NodeInterval(int start, int end, boolean isBeingAdded) {
        this.start = start;
        this.end = end;
        this.isBeingAdded = isBeingAdded;
    }

}
