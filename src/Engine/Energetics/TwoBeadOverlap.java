/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine.Energetics;

/**
 *
 * @author bmoths
 */
public class TwoBeadOverlap {

    public double softOverlap, hardOverlap;

    public TwoBeadOverlap() {
        softOverlap = 0;
        hardOverlap = 0;
    }

    public TwoBeadOverlap(double softOverlap, double hardOverlap) {
        this.softOverlap = softOverlap;
        this.hardOverlap = hardOverlap;
    }

    public TwoBeadOverlap(TwoBeadOverlap twoBeadOverlap) {
        this.softOverlap = twoBeadOverlap.softOverlap;
        this.hardOverlap = twoBeadOverlap.hardOverlap;
    }

    public void increment(TwoBeadOverlap twoBeadOverlap) {
        this.softOverlap += twoBeadOverlap.softOverlap;
        this.hardOverlap += twoBeadOverlap.hardOverlap;
    }
}
