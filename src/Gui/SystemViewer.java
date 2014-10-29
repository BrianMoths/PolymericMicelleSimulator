/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Gui;

import Engine.Energetics.EnergeticsConstants;
import Engine.PolymerSimulator;
import Engine.PolymerState.SystemGeometry.GeometricalParameters;
import Engine.SystemAnalyzer;
import Gui.analysiswindow.AnalysisWindow;
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
    private PolymerSimulator polymerSimulator;
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
        final EnergeticsConstants energeticsConstants = polymerSimulator.getEnergeticsConstants();
        AACoefficientLbl.setText(stringFormatDouble(energeticsConstants.getAAOverlapCoefficient()));
        BBCoefficientLbl.setText(stringFormatDouble(energeticsConstants.getBBOverlapCoefficient()));
        ABCoefficientLbl.setText(stringFormatDouble(energeticsConstants.getABOverlapCoefficient()));
        temperatureLbl.setText(stringFormatDouble(energeticsConstants.getTemperature()));
        springConstantLbl.setText(stringFormatDouble(energeticsConstants.getSpringConstant()));
        coreCoefficientLbl.setText(stringFormatDouble(energeticsConstants.getHardOverlapCoefficient()));
        numBeadsLbl.setText(Integer.toString(polymerSimulator.getNumBeads()));

        final GeometricalParameters geometricalParameters = polymerSimulator.getGeometricalParameters();
        beadSizeLbl.setText(stringFormatDouble(geometricalParameters.getInteractionLength()));
        coreSizeLbl.setText(stringFormatDouble(geometricalParameters.getCoreLength()));

        hardCoresChk.setSelected(geometricalParameters.getCoreLength() != 0);
    }

    private void updateDisplay() {
        synchronized (polymerSimulator) {
            SystemAnalyzer systemAnalyzer = polymerSimulator.getSystemAnalyzer();

            final double energy = polymerSimulator.getEnergy();


            energyLbl.setText(stringFormatDouble(energy));
            numIterationsLbl.setText(String.valueOf(polymerSimulator.getIterationNumber()));
            externalEnergyLbl.setText(stringFormatDouble(systemAnalyzer.externalEnergy()));
            systemSizeLbl.setText(stringFormatDouble(polymerSimulator.getGeometry().getSizeOfDimension(0)));
            systemHeightLabel.setText(stringFormatDouble(polymerSimulator.getGeometry().getSizeOfDimension(1)));
        }
        if (analysisWindow != null) {
            analysisWindow.updateDisplay();
        }
        repaint();
    }

    private String stringFormatDouble(double doubleForFormatting) {
        return String.format("%.4f", doubleForFormatting);
    }

    public void setPolymerSimulator(PolymerSimulator polymerSimulator) {
        this.polymerSimulator = polymerSimulator;
        registerGuiWithSystem();
        updateDisplay();
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
        temperatureLbl = new javax.swing.JLabel();
        AACoefficientLbl = new javax.swing.JLabel();
        BBCoefficientLbl = new javax.swing.JLabel();
        ABCoefficientLbl = new javax.swing.JLabel();
        springConstantLbl = new javax.swing.JLabel();
        coreCoeffCaptoinLbl = new javax.swing.JLabel();
        coreCoefficientLbl = new javax.swing.JLabel();
        externalEnergyCaptionLbl = new javax.swing.JLabel();
        externalEnergyLbl = new javax.swing.JLabel();
        geometricParametersPnl = new javax.swing.JPanel();
        systemSizeCaptionLbl = new javax.swing.JLabel();
        systemSizeLbl = new javax.swing.JLabel();
        interactionLengthCaptionLbl = new javax.swing.JLabel();
        beadSizeLbl = new javax.swing.JLabel();
        coreSizeCaptionLbl = new javax.swing.JLabel();
        coreSizeLbl = new javax.swing.JLabel();
        systemHeightCaption = new javax.swing.JLabel();
        systemHeightLabel = new javax.swing.JLabel();
        hardCoresChk = new javax.swing.JCheckBox();
        hardCoresCaptionLbl = new javax.swing.JLabel();
        numBeadsCaptionLbl = new javax.swing.JLabel();
        numBeadsLbl = new javax.swing.JLabel();

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

        coreCoeffCaptoinLbl.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        coreCoeffCaptoinLbl.setText("Core Coeff.:");

        coreCoefficientLbl.setText("jLabel2");
        coreCoefficientLbl.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        javax.swing.GroupLayout physicalConstantsPanelLayout = new javax.swing.GroupLayout(physicalConstantsPanel);
        physicalConstantsPanel.setLayout(physicalConstantsPanelLayout);
        physicalConstantsPanelLayout.setHorizontalGroup(
            physicalConstantsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(physicalConstantsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(physicalConstantsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(physicalConstantsPanelLayout.createSequentialGroup()
                        .addComponent(coreCoeffCaptoinLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(coreCoefficientLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(physicalConstantsPanelLayout.createSequentialGroup()
                        .addGroup(physicalConstantsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(BBCoeffCaptionLbl, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(springConstantCaptionLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(ABCoefficientCaptionLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(AACoefficientCaptionLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(temperatureCaptionLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(physicalConstantsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(temperatureLbl, javax.swing.GroupLayout.DEFAULT_SIZE, 78, Short.MAX_VALUE)
                            .addComponent(AACoefficientLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(BBCoefficientLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(ABCoefficientLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(springConstantLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
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
                    .addComponent(coreCoeffCaptoinLbl)
                    .addComponent(coreCoefficientLbl))
                .addContainerGap(107, Short.MAX_VALUE))
        );

        externalEnergyCaptionLbl.setText("External Energy:");

        externalEnergyLbl.setText("0");
        externalEnergyLbl.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        geometricParametersPnl.setBorder(javax.swing.BorderFactory.createTitledBorder("Geometric Parameters"));

        systemSizeCaptionLbl.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        systemSizeCaptionLbl.setText("System Width:");

        systemSizeLbl.setText("jLabel1");
        systemSizeLbl.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        interactionLengthCaptionLbl.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        interactionLengthCaptionLbl.setText("Bead Size:");

        beadSizeLbl.setText("jLabel6");
        beadSizeLbl.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        coreSizeCaptionLbl.setText("Core Size:");

        coreSizeLbl.setText("jLabel4");
        coreSizeLbl.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        systemHeightCaption.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        systemHeightCaption.setText("System Height:");

        systemHeightLabel.setText("jLabel1");
        systemHeightLabel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        javax.swing.GroupLayout geometricParametersPnlLayout = new javax.swing.GroupLayout(geometricParametersPnl);
        geometricParametersPnl.setLayout(geometricParametersPnlLayout);
        geometricParametersPnlLayout.setHorizontalGroup(
            geometricParametersPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(geometricParametersPnlLayout.createSequentialGroup()
                .addGroup(geometricParametersPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, geometricParametersPnlLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(geometricParametersPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(geometricParametersPnlLayout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(coreSizeCaptionLbl))
                            .addComponent(interactionLengthCaptionLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(systemSizeCaptionLbl, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 115, Short.MAX_VALUE)
                    .addComponent(systemHeightCaption, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(geometricParametersPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(systemSizeLbl, javax.swing.GroupLayout.DEFAULT_SIZE, 73, Short.MAX_VALUE)
                    .addComponent(beadSizeLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(coreSizeLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(systemHeightLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        geometricParametersPnlLayout.setVerticalGroup(
            geometricParametersPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(geometricParametersPnlLayout.createSequentialGroup()
                .addGroup(geometricParametersPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(coreSizeCaptionLbl)
                    .addComponent(coreSizeLbl))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(geometricParametersPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(interactionLengthCaptionLbl)
                    .addComponent(beadSizeLbl))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(geometricParametersPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(systemSizeCaptionLbl)
                    .addComponent(systemSizeLbl))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(geometricParametersPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(systemHeightCaption)
                    .addComponent(systemHeightLabel))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        hardCoresChk.setEnabled(false);

        hardCoresCaptionLbl.setText("Give Beads Hard Cores");

        numBeadsCaptionLbl.setText("Number of Beads:");

        numBeadsLbl.setText("jLabel2");
        numBeadsLbl.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
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
                        .addComponent(externalEnergyLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(displayPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addComponent(hardCoresChk)
                                        .addGap(2, 2, 2)
                                        .addComponent(hardCoresCaptionLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(physicalConstantsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addGap(0, 0, Short.MAX_VALUE)
                                        .addComponent(numBeadsCaptionLbl)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(numBeadsLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(20, 20, 20))))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(geometricParametersPnl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(12, 12, 12))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(displayPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(physicalConstantsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(geometricParametersPnl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(numBeadsCaptionLbl)
                            .addComponent(numBeadsLbl))
                        .addGap(108, 108, 108)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(hardCoresChk)
                            .addComponent(hardCoresCaptionLbl))))
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
    private javax.swing.JLabel coreCoeffCaptoinLbl;
    private javax.swing.JLabel coreCoefficientLbl;
    private javax.swing.JLabel coreSizeCaptionLbl;
    private javax.swing.JLabel coreSizeLbl;
    private Gui.DisplayPanel displayPanel;
    private javax.swing.JLabel energyCaptionLbl;
    private javax.swing.JLabel energyLbl;
    private javax.swing.JLabel externalEnergyCaptionLbl;
    private javax.swing.JLabel externalEnergyLbl;
    private javax.swing.JPanel geometricParametersPnl;
    private javax.swing.JLabel hardCoresCaptionLbl;
    private javax.swing.JCheckBox hardCoresChk;
    private javax.swing.JLabel interactionLengthCaptionLbl;
    private javax.swing.JLabel numBeadsCaptionLbl;
    private javax.swing.JLabel numBeadsLbl;
    private javax.swing.JLabel numIterationsCaptionLbl;
    private javax.swing.JLabel numIterationsLbl;
    private javax.swing.JPanel physicalConstantsPanel;
    private javax.swing.JLabel springConstantCaptionLbl;
    private javax.swing.JLabel springConstantLbl;
    private javax.swing.JLabel systemHeightCaption;
    private javax.swing.JLabel systemHeightLabel;
    private javax.swing.JLabel systemSizeCaptionLbl;
    private javax.swing.JLabel systemSizeLbl;
    private javax.swing.JLabel temperatureCaptionLbl;
    private javax.swing.JLabel temperatureLbl;
    // End of variables declaration//GEN-END:variables
}
