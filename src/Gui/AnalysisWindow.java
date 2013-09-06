/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Gui;

import Engine.PolymerSimulator;
import Engine.SystemAnalyzer;
import SystemAnalysis.GeometryAnalyzer;
import SystemAnalysis.GeometryAnalyzer.AreaPerimeter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bmoths
 */
public class AnalysisWindow extends javax.swing.JFrame {

    private class UpdaterRunnable implements Runnable {

        @Override
        public void run() {
            while (AnalysisWindow.this.isShowing()) {
                updateDisplay();
                sleepUntilNextFrame();
            }
        }

        private void sleepUntilNextFrame() {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(MicelleGui.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    private class UpdaterThread extends Thread {

        public UpdaterThread() {
            super(new UpdaterRunnable());
        }

    }

    private PolymerSimulator polymerSimulator;
    private SystemAnalyzer systemAnalyzer;
    private Thread updaterThread;

    /**
     * Creates new form AnalysisWindow
     */
    public AnalysisWindow(PolymerSimulator polymerSimulator) {
        initComponents();
        this.polymerSimulator = polymerSimulator;
        systemAnalyzer = polymerSimulator.getSystemAnalyzer();

        updaterThread = new UpdaterThread();
        initialize();
    }

    private void initialize() {
        updaterThread.start();
    }

    private void updateDisplay() {
        updateIterationStatisticsDisplay();
        updatePhysicalPropertiesDisplay();
    }

    private void updateIterationStatisticsDisplay() {
        updateTotalIterationsDisplay();
        updateChainIterationsDisplay();
    }

    private void updateTotalIterationsDisplay() {
        final int numIterations = polymerSimulator.getIterationNumber();
        final int acceptedIterations = polymerSimulator.getAcceptedIterations();
        final double acceptanceRate = (double)acceptedIterations / (double)numIterations;

        numIterationsLbl.setText(Integer.toString(numIterations));
        numAcceptedIterationsLbl.setText(Integer.toString(acceptedIterations));
        iterationAcceptanceRateLbl.setText(doubleToString(acceptanceRate));
    }

    private void updateChainIterationsDisplay() {
        final int numIterations = polymerSimulator.getNumChainMoves();
        final int acceptedIterations = polymerSimulator.getAcceptedChainMoves();
        final double acceptanceRate = (double)acceptedIterations / (double)numIterations;

        chainMovesLbl.setText(Integer.toString(numIterations));
        acceptChainMovesLbl.setText(Integer.toString(acceptedIterations));
        chainMoveAcceptanceRateLbl.setText(doubleToString(acceptanceRate));
    }

    private void updatePhysicalPropertiesDisplay() {
        final AreaPerimeter areaPerimeter = systemAnalyzer.findAreaAndPerimeter();
        final double area = areaPerimeter.area;
        final double perimeter = areaPerimeter.perimeter;

        updateEnergyDisplay();
        updateAreaDisplay(area);
        updatePerimeterDisplay(perimeter);
    }

    private void updateEnergyDisplay() {
        final double energy = polymerSimulator.getEnergy();

        energyLbl.setText(doubleToString(energy));
        energyChart.add(energy);
    }

    private void updateAreaDisplay(final double area) {
        areaLbl.setText(doubleToString(area));
        areaChart.add(area);
    }

    private void updatePerimeterDisplay(final double perimeter) {
        perimeterLbl.setText(doubleToString(perimeter));
        perimeterChart.add(perimeter);
    }

    private String doubleToString(double number) {
        return String.format("%.4f", number);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        iterationStatisticsLbl = new javax.swing.JPanel();
        numAcceptedIterationsCaptionLbl = new javax.swing.JLabel();
        numIterationsCaptionLbl = new javax.swing.JLabel();
        chainMovesLbl = new javax.swing.JLabel();
        chainMovesCaptionLbl = new javax.swing.JLabel();
        acceptedChainMovesCaptionLbl = new javax.swing.JLabel();
        chainMoveAcceptanceRateLbl = new javax.swing.JLabel();
        numIterationsLbl = new javax.swing.JLabel();
        iterationAcceptanceRateLbl = new javax.swing.JLabel();
        chainMoveAcceptanceRateCaptionLbl = new javax.swing.JLabel();
        acceptChainMovesLbl = new javax.swing.JLabel();
        iterationAcceptanceRateCaptionLbl = new javax.swing.JLabel();
        numAcceptedIterationsLbl = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        volumeCaptionLbl = new javax.swing.JLabel();
        areaLbl = new javax.swing.JLabel();
        areaChart = new Gui.JStripChart();
        perimeterChart = new Gui.JStripChart();
        perimeterCaptionLbl = new javax.swing.JLabel();
        perimeterLbl = new javax.swing.JLabel();
        energyCaptionLbl = new javax.swing.JLabel();
        energyLbl = new javax.swing.JLabel();
        energyChart = new Gui.JStripChart();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        iterationStatisticsLbl.setBorder(javax.swing.BorderFactory.createTitledBorder("Iteration Statistics"));

        numAcceptedIterationsCaptionLbl.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        numAcceptedIterationsCaptionLbl.setText("Accepted Iterations:");

        numIterationsCaptionLbl.setText("Number of Iterations:");

        chainMovesLbl.setText("0");
        chainMovesLbl.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        chainMovesCaptionLbl.setText("Number of Chain Moves:");

        acceptedChainMovesCaptionLbl.setText("Accepted Chain Moves:");

        chainMoveAcceptanceRateLbl.setText("0");
        chainMoveAcceptanceRateLbl.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        numIterationsLbl.setText("0");
        numIterationsLbl.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        iterationAcceptanceRateLbl.setText("0");
        iterationAcceptanceRateLbl.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        chainMoveAcceptanceRateCaptionLbl.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        chainMoveAcceptanceRateCaptionLbl.setText("AcceptanceRate:");

        acceptChainMovesLbl.setText("0");
        acceptChainMovesLbl.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        iterationAcceptanceRateCaptionLbl.setText("Acceptance Rate:");

        numAcceptedIterationsLbl.setText("0");
        numAcceptedIterationsLbl.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        javax.swing.GroupLayout iterationStatisticsLblLayout = new javax.swing.GroupLayout(iterationStatisticsLbl);
        iterationStatisticsLbl.setLayout(iterationStatisticsLblLayout);
        iterationStatisticsLblLayout.setHorizontalGroup(
            iterationStatisticsLblLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(iterationStatisticsLblLayout.createSequentialGroup()
                .addGroup(iterationStatisticsLblLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(iterationStatisticsLblLayout.createSequentialGroup()
                        .addGroup(iterationStatisticsLblLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(chainMoveAcceptanceRateCaptionLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chainMovesCaptionLbl)
                            .addComponent(acceptedChainMovesCaptionLbl))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(iterationStatisticsLblLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(acceptChainMovesLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(chainMoveAcceptanceRateLbl, javax.swing.GroupLayout.DEFAULT_SIZE, 65, Short.MAX_VALUE)
                            .addComponent(chainMovesLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(iterationStatisticsLblLayout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addGroup(iterationStatisticsLblLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(iterationStatisticsLblLayout.createSequentialGroup()
                                .addComponent(numIterationsCaptionLbl)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(numIterationsLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(iterationStatisticsLblLayout.createSequentialGroup()
                                .addGroup(iterationStatisticsLblLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(numAcceptedIterationsCaptionLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(iterationAcceptanceRateCaptionLbl))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(iterationStatisticsLblLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(iterationAcceptanceRateLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(numAcceptedIterationsLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))))
                .addGap(0, 12, Short.MAX_VALUE))
        );
        iterationStatisticsLblLayout.setVerticalGroup(
            iterationStatisticsLblLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(iterationStatisticsLblLayout.createSequentialGroup()
                .addGroup(iterationStatisticsLblLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(numIterationsCaptionLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(numIterationsLbl))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(iterationStatisticsLblLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(numAcceptedIterationsCaptionLbl)
                    .addComponent(numAcceptedIterationsLbl))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(iterationStatisticsLblLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(iterationAcceptanceRateCaptionLbl)
                    .addComponent(iterationAcceptanceRateLbl))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(iterationStatisticsLblLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chainMovesCaptionLbl)
                    .addComponent(chainMovesLbl))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(iterationStatisticsLblLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(acceptedChainMovesCaptionLbl)
                    .addComponent(acceptChainMovesLbl))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(iterationStatisticsLblLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chainMoveAcceptanceRateCaptionLbl)
                    .addComponent(chainMoveAcceptanceRateLbl)))
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Physical Properties"));

        volumeCaptionLbl.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        volumeCaptionLbl.setText("Area:");

        areaLbl.setText("0");
        areaLbl.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        javax.swing.GroupLayout areaChartLayout = new javax.swing.GroupLayout(areaChart);
        areaChart.setLayout(areaChartLayout);
        areaChartLayout.setHorizontalGroup(
            areaChartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        areaChartLayout.setVerticalGroup(
            areaChartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout perimeterChartLayout = new javax.swing.GroupLayout(perimeterChart);
        perimeterChart.setLayout(perimeterChartLayout);
        perimeterChartLayout.setHorizontalGroup(
            perimeterChartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        perimeterChartLayout.setVerticalGroup(
            perimeterChartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        perimeterCaptionLbl.setText("Perimeter:");

        perimeterLbl.setText("0");
        perimeterLbl.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        energyCaptionLbl.setText("Energy:");

        energyLbl.setText("1");
        energyLbl.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        javax.swing.GroupLayout energyChartLayout = new javax.swing.GroupLayout(energyChart);
        energyChart.setLayout(energyChartLayout);
        energyChartLayout.setHorizontalGroup(
            energyChartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        energyChartLayout.setVerticalGroup(
            energyChartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(perimeterCaptionLbl)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(perimeterLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(126, 126, 126))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(volumeCaptionLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(areaLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(56, 56, 56))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(areaChart, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 281, Short.MAX_VALUE)
                            .addComponent(perimeterChart, javax.swing.GroupLayout.DEFAULT_SIZE, 281, Short.MAX_VALUE))
                        .addContainerGap())))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(49, 49, 49)
                .addComponent(energyCaptionLbl)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(energyLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(energyChart, javax.swing.GroupLayout.DEFAULT_SIZE, 281, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(energyChart, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(energyCaptionLbl)
                    .addComponent(energyLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(areaChart, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(volumeCaptionLbl)
                    .addComponent(areaLbl))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(perimeterChart, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(perimeterCaptionLbl)
                    .addComponent(perimeterLbl))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(iterationStatisticsLbl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(iterationStatisticsLbl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(145, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel acceptChainMovesLbl;
    private javax.swing.JLabel acceptedChainMovesCaptionLbl;
    private Gui.JStripChart areaChart;
    private javax.swing.JLabel areaLbl;
    private javax.swing.JLabel chainMoveAcceptanceRateCaptionLbl;
    private javax.swing.JLabel chainMoveAcceptanceRateLbl;
    private javax.swing.JLabel chainMovesCaptionLbl;
    private javax.swing.JLabel chainMovesLbl;
    private javax.swing.JLabel energyCaptionLbl;
    private Gui.JStripChart energyChart;
    private javax.swing.JLabel energyLbl;
    private javax.swing.JLabel iterationAcceptanceRateCaptionLbl;
    private javax.swing.JLabel iterationAcceptanceRateLbl;
    private javax.swing.JPanel iterationStatisticsLbl;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JLabel numAcceptedIterationsCaptionLbl;
    private javax.swing.JLabel numAcceptedIterationsLbl;
    private javax.swing.JLabel numIterationsCaptionLbl;
    private javax.swing.JLabel numIterationsLbl;
    private javax.swing.JLabel perimeterCaptionLbl;
    private Gui.JStripChart perimeterChart;
    private javax.swing.JLabel perimeterLbl;
    private javax.swing.JLabel volumeCaptionLbl;
    // End of variables declaration//GEN-END:variables
}