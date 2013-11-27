/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine.PolymerState;

/**
 *
 * @author bmoths
 */
public interface ImmutablePolymerPosition {

    public int getNumBeads();

    public double[] getBeadPosition(int bead);

    public double[][] getBeadPositions();

}
