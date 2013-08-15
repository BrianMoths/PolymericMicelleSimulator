/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SystemAnalysis;

/**
 *
 * @author bmoths
 */
public class VerticalEdge {

    public final boolean isLeftEdge;
    public int indexForSortedYBottom, indexForSortedYTop;
    public final double x;

    public VerticalEdge(int indexForSortedYBottom, int indexForSortedYTop, double x, boolean isLeftEdge) {
        this.indexForSortedYBottom = indexForSortedYBottom;
        this.indexForSortedYTop = indexForSortedYTop;
        this.x = x;
        this.isLeftEdge = isLeftEdge;
    }
}
