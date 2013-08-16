/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SystemAnalysis;

/**
 *
 * @author bmoths
 */
public class NodeInterval {

    int start, end;
    boolean isBeingAdded;

    public NodeInterval(int start, int end, boolean isBeingAdded) {
        this.start = start;
        this.end = end;
        this.isBeingAdded = isBeingAdded;
    }
}
