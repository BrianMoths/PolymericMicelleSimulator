/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Gui;

import Engine.SystemAnalyzer;
import Engine.SystemGeometry.Interfaces.SystemGeometry;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

/**
 *
 * @author bmoths
 */
public class SystemDrawer {

    private static final int displaySize = 600;
    private Graphics graphics;
    private SystemAnalyzer systemAnalyzer;
    private int radius, diameter;
    private double scaleFactor;
    private int maxX, maxY;

    public SystemDrawer() {
    }

    public SystemDrawer(Graphics graphics, SystemAnalyzer systemAnalyzer) {
        this.graphics = graphics;
        registerSystemAnalyzerPrivate(systemAnalyzer);
    }

    public SystemDrawer(SystemAnalyzer systemAnalyzer) {
        registerSystemAnalyzerPrivate(systemAnalyzer);
    }

    public void registerSystemAnalyzer(SystemAnalyzer systemAnalyzer) {
        registerSystemAnalyzerPrivate(systemAnalyzer);
    }

    private void registerSystemAnalyzerPrivate(SystemAnalyzer systemAnalyzer) {
        this.systemAnalyzer = systemAnalyzer;

        SystemGeometry systemGeometry = systemAnalyzer.getSystemGeometry();

        resetSystemGeometry(systemGeometry);
    }

    private void resetSystemGeometry(SystemGeometry systemGeometry) {
        scaleFactor = displaySize / Math.max(systemGeometry.getRMax()[0], systemGeometry.getRMax()[1]);
        diameter = (int)Math.round(systemGeometry.getParameters().getInteractionLength() * scaleFactor); //make diameter smaller
        radius = diameter / 2;
        maxX = (int)(systemGeometry.getRMax()[0] * scaleFactor);
        maxY = (int)(systemGeometry.getRMax()[1] * scaleFactor);
    }

    public void setGraphics(Graphics graphics) {
        this.graphics = graphics;
    }

    public void draw() {
        if (systemAnalyzer != null) {
            resetSystemGeometry(systemAnalyzer.getSystemGeometry());
            clear();
            drawBeads();
            drawBonds();
        }
    }

    public void draw(Graphics graphics) {
        this.graphics = graphics;
        clear();
        drawBeads();
        drawBonds();
    }

    private void clear() {
        graphics.clearRect(0, 0, displaySize, displaySize);//fix this later
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, maxX, maxY);
    }

    private void drawBeads() {
        drawABeads();
        drawBBeads();
    }

    private void drawABeads() {
        graphics.setColor(Color.RED);
        for (int i = 0; i < numABeads(); i++) {
            drawBead(i);
        }
    }

    private void drawBBeads() {
        graphics.setColor(Color.BLUE);
        for (int i = numABeads(); i < numBeads(); i++) {
            drawBead(i);
        }
    }

    private void drawBead(int i) {
        Point point = beadCenterPixel(i);
        graphics.fillRect(point.x - radius, point.y - radius, diameter, diameter);
    }

    private void drawBonds() {
        graphics.setColor(Color.BLACK);
        for (int bead1 = 0; bead1 < numBeads(); bead1++) {
            for (int bondDirection = 0; bondDirection < 2; bondDirection++) {
                int bead2 = systemAnalyzer.getNeighbor(bead1, bondDirection);
                if (bead2 > bead1) { //tests for bead2!=-1 (bead1 has no neighbor in that direction) *and* makes sure to draw each bond only once
                    drawBond(bead1, bead2);
                }
            }
        }
    }

    private void drawBond(int bead1, int bead2) {
        Point point1 = beadCenterPixel(bead1);
        Point point2 = beadCenterPixel(bead2);

        if (isCloseEnough(point1, point2)) {
            graphics.drawLine(point1.x, point1.y, point2.x, point2.y);
        }
    }

    private boolean isCloseEnough(Point point1, Point point2) {
        return Math.abs(point1.x - point2.x) < maxX / 2 && Math.abs(point1.y - point2.y) < maxY / 2;
    }

    private Point beadCenterPixel(int i) {
        return new Point((int)Math.round(beadPosition(i, 0) * scaleFactor),
                (int)Math.round(beadPosition(i, 1) * scaleFactor));
    }

    private int numABeads() {
        return systemAnalyzer.getNumABeads();
    }

    private int numBeads() {
        return systemAnalyzer.getNumBeads();
    }

    private double beadPosition(int bead, int component) {
        return systemAnalyzer.getBeadPositionComponent(bead, component);
    }

}
