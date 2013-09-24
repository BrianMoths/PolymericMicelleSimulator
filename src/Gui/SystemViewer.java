/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Gui;

import Engine.PolymerSimulator;
import Engine.SystemAnalyzer;
import SystemAnalysis.GeometryAnalyzer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bmoths
 */
public class SystemViewer extends javax.swing.JFrame {

    //<editor-fold defaultstate="collapsed" desc="updater thread classes">
    private class UpdaterRunnable implements Runnable {
        
        int lastIteration = -1;
        
        @Override
        public void run() {
            while (true) {
                if (isUpdateNecessary()) {
                    updateLastIteration();
                    updateDisplay();
                }
                sleepUntilNextFrame();
            }
        }
        
        private boolean isUpdateNecessary() {
            return polymerSimulator.getIterationNumber() != lastIteration;
        }
        
        private void updateLastIteration() {
            lastIteration = polymerSimulator.getIterationNumber();
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
//</editor-fold>

    private final Thread updaterThread;
    private final PolymerSimulator polymerSimulator;
    private AnalysisWindow analysisWindow;

    /**
     * Creates new form SystemViewer
     */
    public SystemViewer(PolymerSimulator polymerSimulator) {
        initComponents();
        this.polymerSimulator = polymerSimulator;
        updaterThread = new UpdaterThread();
        initialize();
    }
    
    private void initialize() {
        registerGuiWithSystem();
        updateConstantLabels();
        updaterThread.start();
    }
    
    private void registerGuiWithSystem() {
        displayPanel.setPolymerSimulator(polymerSimulator);
        if (analysisWindow != null) {
            analysisWindow.setPolymerSimulator(polymerSimulator);
        }
    }
    
    private void updateConstantLabels() {
        AACoefficientLbl.setText(String.format("%.4f", polymerSimulator.getPhysicalConstants().getAAOverlapCoefficient()));
        BBCoefficientLbl.setText(String.format("%.4f", polymerSimulator.getPhysicalConstants().getBBOverlapCoefficient()));
        ABCoefficientLbl.setText(String.format("%.4f", polymerSimulator.getPhysicalConstants().getABOverlapCoefficient()));
        temperatureLbl.setText(String.format("%.4f", polymerSimulator.getPhysicalConstants().getTemperature()));
        springConstantLbl.setText(String.format("%.4f", polymerSimulator.getPhysicalConstants().getSpringCoefficient()));
        beadSizeLbl.setText(String.format("%.4f", polymerSimulator.getSimulationParameters().getInteractionLength()));
        systemSizeLbl.setText(String.format("%.4f", polymerSimulator.getGeometry().getRMax()[0]));
        hardCoresChk.setSelected(polymerSimulator.getSimulationParameters().getCoreLength() != 0);
    }
    
    private void updateDisplay() {
        SystemAnalyzer systemAnalyzer = polymerSimulator.getSystemAnalyzer();
        
        final double energy = polymerSimulator.getEnergy();
        
        GeometryAnalyzer.AreaPerimeter areaPerimeter = systemAnalyzer.findAreaAndPerimeter();
        systemAnalyzer.addPerimeterAreaEnergySnapshot(areaPerimeter.perimeter, areaPerimeter.area, energy);
        
        energyLbl.setText(String.format("%.4f", energy));
        numIterationsLbl.setText(String.valueOf(polymerSimulator.getIterationNumber()));
        externalEnergyLbl.setText(String.format("%.4f", systemAnalyzer.externalEnergy()));
        if (analysisWindow != null) {
            analysisWindow.updateDisplay();
        }
        repaint();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        displayPanel = new Gui.DisplayPanel();
        energyCaptionLbl = new javax.swing.JLabel();
        energyLbl = new javax.swing.JLabel();
        numIterationsLbl = new javax.swing.JLabel();
        numIterationsCaptionLbl = new javax.swing.JLabel();
        analysisWindowBtn = new javax.swing.JButton();
        physicalConstantsPanel = new javax.swing.JPanel();
        temperatureCaptionLbl = new javax.swing.JLabel();
        AACoefficientCaptionLbl = new javax.swing.JLabel();
        ABCoefficientCaptionLbl = new javax.swing.JLabel();
        springConstantCaptionLbl = new javax.swing.JLabel();
        BBCoeffCaptionLbl = new javax.swing.JLabel();
        hardCoresChk = new javax.swing.JCheckBox();
        interactionLengthCaptionLbl = new javax.swing.JLabel();
        temperatureLbl = new javax.swing.JLabel();
        AACoefficientLbl = new javax.swing.JLabel();
        BBCoefficientLbl = new javax.swing.JLabel();
        ABCoefficientLbl = new javax.swing.JLabel();
        springConstantLbl = new javax.swing.JLabel();
        beadSizeLbl = new javax.swing.JLabel();
        hardCoresCaptionLbl = new javax.swing.JLabel();
        systemSizeCaptionLbl = new javax.swing.JLabel();
        systemSizeLbl = new javax.swing.JLabel();
        externalEnergyCaptionLbl = new javax.swing.JLabel();
        externalEnergyLbl = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout displayPanelLayout = new javax.swing.GroupLayout(displayPanel);
        displayPanel.setLayout(displayPanelLayout);
        displayPanelLayout.setHorizontalGroup(
            displayPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 600, Short.MAX_VALUE)
        );
        displayPanelLayout.setVerticalGroup(
            displayPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 600, Short.MAX_VALUE)
        );

        energyCaptionLbl.setText("Energy:");

        energyLbl.setText("1");
        energyLbl.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        numIterationsLbl.setText("0");
        numIterationsLbl.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        numIterationsCaptionLbl.setText("Number of Iterations:");

        analysisWindowBtn.setText("Show Analysis Window");
        analysisWindowBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                analysisWindowBtnActionPerformed(evt);
            }
        });

        physicalConstantsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Physical Constants"));

        temperatureCaptionLbl.setText("Temperature:");

        AACoefficientCaptionLbl.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        AACoefficientCaptionLbl.setText("AACoeff.:");

        ABCoefficientCaptionLbl.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        ABCoefficientCaptionLbl.setText("AB Coeff.:");

        springConstantCaptionLbl.setText("Spring Const:");

        BBCoeffCaptionLbl.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        BBCoeffCaptionLbl.setText("BB Coeff.:");

        hardCoresChk.setEnabled(false);

        interactionLengthCaptionLbl.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        interactionLengthCaptionLbl.setText("Bead Size:");

        temperatureLbl.setText("jLabel1");
        temperatureLbl.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        AACoefficientLbl.setText("jLabel2");
        AACoefficientLbl.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        BBCoefficientLbl.setText("jLabel3");
        BBCoefficientLbl.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        ABCoefficientLbl.setText("jLabel4");
        ABCoefficientLbl.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        springConstantLbl.setText("jLabel5");
        springConstantLbl.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        beadSizeLbl.setText("jLabel6");
        beadSizeLbl.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        hardCoresCaptionLbl.setText("Give Beads Hard Cores");

        systemSizeCaptionLbl.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        systemSizeCaptionLbl.setText("System Size:");

        systemSizeLbl.setText("jLabel1");
        systemSizeLbl.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        javax.swing.GroupLayout physicalConstantsPanelLayout = new javax.swing.GroupLayout(physicalConstantsPanel);
        physicalConstantsPanel.setLayout(physicalConstantsPanelLayout);
        physicalConstantsPanelLayout.setHorizontalGroup(
            physicalConstantsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(physicalConstantsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(physicalConstantsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(physicalConstantsPanelLayout.createSequentialGroup()
                        .addComponent(systemSizeCaptionLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(systemSizeLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(physicalConstantsPanelLayout.createSequentialGroup()
                        .addComponent(hardCoresChk)
                        .addGap(2, 2, 2)
                        .addComponent(hardCoresCaptionLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(physicalConstantsPanelLayout.createSequentialGroup()
                        .addGroup(physicalConstantsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(interactionLengthCaptionLbl, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(BBCoeffCaptionLbl, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(springConstantCaptionLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(ABCoefficientCaptionLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(AACoefficientCaptionLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(temperatureCaptionLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(physicalConstantsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(temperatureLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(AACoefficientLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(BBCoefficientLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(ABCoefficientLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(springConstantLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(beadSizeLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        physicalConstantsPanelLayout.setVerticalGroup(
            physicalConstantsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(physicalConstantsPanelLayout.createSequentialGroup()
                .addGroup(physicalConstantsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(temperatureCaptionLbl)
                    .addComponent(temperatureLbl))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(physicalConstantsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(AACoefficientCaptionLbl)
                    .addComponent(AACoefficientLbl))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(physicalConstantsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BBCoeffCaptionLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BBCoefficientLbl))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(physicalConstantsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ABCoefficientCaptionLbl)
                    .addComponent(ABCoefficientLbl))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(physicalConstantsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(springConstantCaptionLbl)
                    .addComponent(springConstantLbl))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(physicalConstantsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(interactionLengthCaptionLbl)
                    .addComponent(beadSizeLbl))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(physicalConstantsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(systemSizeCaptionLbl)
                    .addComponent(systemSizeLbl))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 33, Short.MAX_VALUE)
                .addGroup(physicalConstantsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(hardCoresChk)
                    .addComponent(hardCoresCaptionLbl))
                .addContainerGap())
        );

        externalEnergyCaptionLbl.setText("External Energy:");

        externalEnergyLbl.setText("0");
        externalEnergyLbl.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(displayPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(physicalConstantsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(numIterationsCaptionLbl, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(energyCaptionLbl))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(energyLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(numIterationsLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(analysisWindowBtn)
                        .addGap(53, 53, 53)
                        .addComponent(externalEnergyCaptionLbl)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(externalEnergyLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(displayPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(196, 196, 196)
                        .addComponent(physicalConstantsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(energyCaptionLbl)
                    .addComponent(energyLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(analysisWindowBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(externalEnergyCaptionLbl)
                    .addComponent(externalEnergyLbl))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(numIterationsCaptionLbl, javax.swing.GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE)
                    .addComponent(numIterationsLbl)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void analysisWindowBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_analysisWindowBtnActionPerformed
        analysisWindow = new AnalysisWindow(polymerSimulator);
        analysisWindow.setVisible(true);
    }//GEN-LAST:event_analysisWindowBtnActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel AACoefficientCaptionLbl;
    private javax.swing.JLabel AACoefficientLbl;
    private javax.swing.JLabel ABCoefficientCaptionLbl;
    private javax.swing.JLabel ABCoefficientLbl;
    private javax.swing.JLabel BBCoeffCaptionLbl;
    private javax.swing.JLabel BBCoefficientLbl;
    private javax.swing.JButton analysisWindowBtn;
    private javax.swing.JLabel beadSizeLbl;
    private Gui.DisplayPanel displayPanel;
    private javax.swing.JLabel energyCaptionLbl;
    private javax.swing.JLabel energyLbl;
    private javax.swing.JLabel externalEnergyCaptionLbl;
    private javax.swing.JLabel externalEnergyLbl;
    private javax.swing.JLabel hardCoresCaptionLbl;
    private javax.swing.JCheckBox hardCoresChk;
    private javax.swing.JLabel interactionLengthCaptionLbl;
    private javax.swing.JLabel numIterationsCaptionLbl;
    private javax.swing.JLabel numIterationsLbl;
    private javax.swing.JPanel physicalConstantsPanel;
    private javax.swing.JLabel springConstantCaptionLbl;
    private javax.swing.JLabel springConstantLbl;
    private javax.swing.JLabel systemSizeCaptionLbl;
    private javax.swing.JLabel systemSizeLbl;
    private javax.swing.JLabel temperatureCaptionLbl;
    private javax.swing.JLabel temperatureLbl;
    // End of variables declaration//GEN-END:variables
}
