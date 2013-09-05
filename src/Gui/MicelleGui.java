/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Gui;

import Engine.PolymerSimulator;
import Engine.SystemAnalyzer;
import SystemAnalysis.GeometryAnalyzer.AreaPerimeter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;

/**
 *
 * @author brian
 */
public class MicelleGui extends javax.swing.JFrame {

    private class SimulationTask extends FutureTask<Void> {

        public SimulationTask(int numIterations) {
            super(new SimulationRunnable(numIterations), null);
        }

    }

    private class SimulationRunnable implements Runnable {

        private int numIterations;

        public SimulationRunnable(int numIterations) {
            this.numIterations = numIterations;
        }

        @Override
        public void run() {
            system.doIterations(numIterations);
        }

    }

    private PolymerSimulator system;
    private int frameNumber;
    private final Thread updaterThread;
    private final ThreadPoolExecutor simulationExecutor;
    private final Set<SimulationTask> simulationTasks;

    /**
     * Creates new form MicelleGui
     */
    public MicelleGui() {
        initComponents();
        this.getRootPane().setDefaultButton(doIterationsBtn);
        setSystem(new PolymerSimulator());
        updateConstantLabels();
        updaterThread = new Thread(
                new Runnable() {
            @Override
            public void run() {
                while (true) {
                    updateDisplay();
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(MicelleGui.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

        });

        frameNumber = 0;
        final int numThreadsAlwaysPresent = 1;
        final int maxThreads = 1;
        final long keepAliveTime = 1;
        simulationExecutor = new ThreadPoolExecutor(numThreadsAlwaysPresent, maxThreads, keepAliveTime, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(5));
        simulationTasks = new HashSet<>();
        initializePrivate();
    }

    public void initialize() {
        registerGuiWithSystem();
        system.randomizePositions();
        updaterThread.start();
    }

    private void initializePrivate() {
        registerGuiWithSystem();
        system.randomizePositions();
        updaterThread.start();
    }

    private void registerGuiWithSystem() {
        displayPanel.setPolymerSimulator(system);
    }

    private void updateDisplay() {
        frameNumber++;
        SystemAnalyzer systemAnalyzer = system.getSystemAnalyzer();

        energyLbl.setText(String.format("%.4f", system.getEnergy()));
        numIterationsLbl.setText(String.valueOf(system.getIterationNumber()));
        numAcceptedIterationsLbl.setText(String.valueOf(system.getAcceptedIterations()));
        AreaPerimeter areaPerimeter = systemAnalyzer.findAreaAndPerimeter();
        volumeLbl.setText(String.format("%.4f", areaPerimeter.area));
        perimeterLbl.setText(String.format("%.4f", areaPerimeter.perimeter));
        chainMovesLbl.setText(Integer.toString(system.getNumChainMoves()));
        acceptChainMovesLbl.setText(Integer.toString(system.getAcceptedChainMoves()));

        systemAnalyzer.addPerimeterAreaEnergySnapshot(
                areaPerimeter.perimeter,
                areaPerimeter.area,
                system.getEnergy());
        if (frameNumber > 300) {
            systemAnalyzer.recordSurface();
        }
        if (frameNumber > 300 && frameNumber % 100 == 50) {
            System.out.println(systemAnalyzer.estimateSurfaceTension());
        }

        //System.out.println(String.valueOf(system.springEnergy() / system.getNumBeads()));
        repaint();
    }

    private void updateConstantLabels() {
        AACoefficientLbl.setText(String.format("%.4f", system.getPhysicalConstants().getAAOverlapCoefficient()));
        BBCoefficientLbl.setText(String.format("%.4f", system.getPhysicalConstants().getBBOverlapCoefficient()));
        ABCoefficientLbl.setText(String.format("%.4f", system.getPhysicalConstants().getABOverlapCoefficient()));
        temperatureLbl.setText(String.format("%.4f", system.getPhysicalConstants().getTemperature()));
        springConstantLbl.setText(String.format("%.4f", system.getPhysicalConstants().getSpringCoefficient()));
        beadSizeLbl.setText(String.format("%.4f", system.getSimulationParameters().getInteractionLength()));
        systemSizeLbl.setText(String.format("%.4f", system.getGeometry().getRMax()[0]));
        hardCoresChk.setSelected(system.getSimulationParameters().getCoreLength() != 0);
    }

    public void setSystem(PolymerSimulator system) {
        this.system = system;
        registerGuiWithSystem();
        updateConstantLabels();
        updateDisplay();
    }

    public PolymerSimulator getPolymerSimulator() {
        return new PolymerSimulator(system);
    }

    public void cancelComputation() {
        cancelBtnActionPerformed(null);
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

        iterateBtn = new javax.swing.JButton();
        doIterationsBtn = new javax.swing.JButton();
        numIterationsFld = new javax.swing.JTextField();
        randomizeBtn = new javax.swing.JButton();
        energyCaptionLbl = new javax.swing.JLabel();
        energyLbl = new javax.swing.JLabel();
        numIterationsCaptionLbl = new javax.swing.JLabel();
        numIterationsLbl = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        numAcceptedIterationsCaptionLbl = new javax.swing.JLabel();
        numAcceptedIterationsLbl = new javax.swing.JLabel();
        displayPanel = new Gui.DisplayPanel();
        cancelBtn = new javax.swing.JButton();
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
        volumeCaptionLbl = new javax.swing.JLabel();
        volumeLbl = new javax.swing.JLabel();
        perimeterCaptionLbl = new javax.swing.JLabel();
        perimeterLbl = new javax.swing.JLabel();
        randomColumnBtn = new javax.swing.JButton();
        saveBtn = new javax.swing.JButton();
        loadBtn = new javax.swing.JButton();
        chainMovesCaptionLbl = new javax.swing.JLabel();
        acceptedChainMovesCaptionLbl = new javax.swing.JLabel();
        chainMovesLbl = new javax.swing.JLabel();
        acceptChainMovesLbl = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Polymer Simulator");

        iterateBtn.setText("Iterate");
        iterateBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                iterateBtnActionPerformed(evt);
            }
        });

        doIterationsBtn.setText("Do Iterations");
        doIterationsBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doIterationsBtnActionPerformed(evt);
            }
        });

        numIterationsFld.setText("1000000000");

        randomizeBtn.setText("Randomize");
        randomizeBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                randomizeBtnActionPerformed(evt);
            }
        });

        energyCaptionLbl.setText("Energy:");

        energyLbl.setText("1");
        energyLbl.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        numIterationsCaptionLbl.setText("Number of Iterations:");

        numIterationsLbl.setText("0");
        numIterationsLbl.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        jButton1.setText("Set Parameters");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        numAcceptedIterationsCaptionLbl.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        numAcceptedIterationsCaptionLbl.setText("Accepted Iterations:");

        numAcceptedIterationsLbl.setText("0");
        numAcceptedIterationsLbl.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        displayPanel.setPreferredSize(new java.awt.Dimension(600, 600));

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

        cancelBtn.setText("Cancel");
        cancelBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelBtnActionPerformed(evt);
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 35, Short.MAX_VALUE)
                .addGroup(physicalConstantsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(hardCoresChk)
                    .addComponent(hardCoresCaptionLbl))
                .addContainerGap())
        );

        volumeCaptionLbl.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        volumeCaptionLbl.setText("Area:");

        volumeLbl.setText("0");
        volumeLbl.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        perimeterCaptionLbl.setText("Perimeter:");

        perimeterLbl.setText("0");
        perimeterLbl.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        randomColumnBtn.setText("Random Column");
        randomColumnBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                randomColumnBtnActionPerformed(evt);
            }
        });

        saveBtn.setText("Save");
        saveBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveBtnActionPerformed(evt);
            }
        });

        loadBtn.setText("Load");
        loadBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadBtnActionPerformed(evt);
            }
        });

        chainMovesCaptionLbl.setText("cm:");

        acceptedChainMovesCaptionLbl.setText("acm:");

        chainMovesLbl.setText("0");
        chainMovesLbl.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        acceptChainMovesLbl.setText("0");
        acceptChainMovesLbl.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(displayPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(numAcceptedIterationsCaptionLbl, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(numIterationsCaptionLbl, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(energyCaptionLbl))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(energyLbl, javax.swing.GroupLayout.DEFAULT_SIZE, 134, Short.MAX_VALUE)
                            .addComponent(numIterationsLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(numAcceptedIterationsLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(volumeCaptionLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(perimeterCaptionLbl))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(4, 4, 4)
                                .addComponent(volumeLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(perimeterLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chainMovesCaptionLbl)
                            .addComponent(acceptedChainMovesCaptionLbl))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(acceptChainMovesLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(chainMovesLbl, javax.swing.GroupLayout.DEFAULT_SIZE, 44, Short.MAX_VALUE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(numIterationsFld, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(doIterationsBtn, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cancelBtn, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(randomizeBtn, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(physicalConstantsPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(iterateBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(randomColumnBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(saveBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(loadBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(iterateBtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(numIterationsFld, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(doIterationsBtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(randomizeBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(randomColumnBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(physicalConstantsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(displayPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 7, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(energyCaptionLbl)
                            .addComponent(energyLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(volumeCaptionLbl)
                            .addComponent(volumeLbl)
                            .addComponent(chainMovesCaptionLbl)
                            .addComponent(chainMovesLbl))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(numIterationsCaptionLbl, javax.swing.GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(numIterationsLbl)
                                .addComponent(perimeterCaptionLbl)
                                .addComponent(perimeterLbl)
                                .addComponent(acceptedChainMovesCaptionLbl)
                                .addComponent(acceptChainMovesLbl)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(numAcceptedIterationsCaptionLbl)
                            .addComponent(numAcceptedIterationsLbl))
                        .addGap(20, 20, 20))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(saveBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(loadBtn)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void iterateBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_iterateBtnActionPerformed
        system.doIteration();
        updateDisplay();
    }//GEN-LAST:event_iterateBtnActionPerformed

    private void doIterationsBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doIterationsBtnActionPerformed
        final int numIterations;
        try {
            final String iterationString = numIterationsFld.getText();
            numIterations = Integer.parseInt(iterationString);
        } catch (NumberFormatException e) {
            return;
        }
        SimulationTask simulationTask = new SimulationTask(numIterations);
        simulationTasks.add(simulationTask);
        simulationExecutor.execute(simulationTask);
    }//GEN-LAST:event_doIterationsBtnActionPerformed
    private void randomizeBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_randomizeBtnActionPerformed
        cancelComputation();
        system.randomizePositions();
        updateDisplay();
    }//GEN-LAST:event_randomizeBtnActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        SystemConfiguration configurator = new SystemConfiguration(this);
        configurator.setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void cancelBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelBtnActionPerformed
        for (SimulationTask simulationTask : simulationTasks) {
            simulationTask.cancel(true);
        }
        simulationExecutor.purge();
        simulationTasks.removeAll(simulationTasks);
    }//GEN-LAST:event_cancelBtnActionPerformed

    private void randomColumnBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_randomColumnBtnActionPerformed
        cancelComputation();
        system.columnRandomizePositions();
        updateDisplay();
    }//GEN-LAST:event_randomColumnBtnActionPerformed

    private void saveBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveBtnActionPerformed
        String toBeSaved = "hello";
        String path = System.getProperty("user.dir");
        try {
            JFileChooser fileChooser = new JFileChooser(path, null);
            int returnVal = fileChooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(
                        new FileOutputStream(file));
                objectOutputStream.writeObject(system);//also save frameNumber
                objectOutputStream.flush();
                objectOutputStream.close();
                System.out.println("Save successful");
            }
        } catch (IOException ex) {
            Logger.getLogger(MicelleGui.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_saveBtnActionPerformed

    private void loadBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadBtnActionPerformed
        String path = System.getProperty("user.dir");
        try {
            JFileChooser fileChooser = new JFileChooser(path, null);
            int returnVal = fileChooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try (ObjectInputStream objectInputStream = new ObjectInputStream(
                        new FileInputStream(file))) {
                    setSystem((PolymerSimulator)objectInputStream.readObject());
                    System.out.println("Load successful");
                }
            }
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(MicelleGui.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_loadBtnActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;










                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MicelleGui.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MicelleGui.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MicelleGui.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MicelleGui.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

//        List<BeadRectangle> beadRectangles = new ArrayList<>();
//        beadRectangles.add(new BeadRectangle(0, 1, 1, 0));
//        beadRectangles.add(new BeadRectangle(.5, 1.5, 1.5, 0.5));
//        beadRectangles.add(new BeadRectangle(.5, .75, .75, 0.5));
//        beadRectangles.add(new BeadRectangle(5, 5, 5, 5));
////        beadRectangles.add(new BeadRectangle(0, 2, 2, 0));
//        beadRectangles.add(new BeadRectangle(0, 10, .5, 0)); //6.25 and 23
//
//        AreaPerimeter areaPerimeter = GeometryAnalyzer.findAreaAndPerimeter(beadRectangles);
//        System.out.println("area: " + areaPerimeter.area + ", perimeter: " + areaPerimeter.perimeter);

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                MicelleGui gui = new MicelleGui();
                gui.setVisible(true);
//                gui.initialize();
            }

        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel AACoefficientCaptionLbl;
    private javax.swing.JLabel AACoefficientLbl;
    private javax.swing.JLabel ABCoefficientCaptionLbl;
    private javax.swing.JLabel ABCoefficientLbl;
    private javax.swing.JLabel BBCoeffCaptionLbl;
    private javax.swing.JLabel BBCoefficientLbl;
    private javax.swing.JLabel acceptChainMovesLbl;
    private javax.swing.JLabel acceptedChainMovesCaptionLbl;
    private javax.swing.JLabel beadSizeLbl;
    private javax.swing.JButton cancelBtn;
    private javax.swing.JLabel chainMovesCaptionLbl;
    private javax.swing.JLabel chainMovesLbl;
    private Gui.DisplayPanel displayPanel;
    private javax.swing.JButton doIterationsBtn;
    private javax.swing.JLabel energyCaptionLbl;
    private javax.swing.JLabel energyLbl;
    private javax.swing.JLabel hardCoresCaptionLbl;
    private javax.swing.JCheckBox hardCoresChk;
    private javax.swing.JLabel interactionLengthCaptionLbl;
    private javax.swing.JButton iterateBtn;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton loadBtn;
    private javax.swing.JLabel numAcceptedIterationsCaptionLbl;
    private javax.swing.JLabel numAcceptedIterationsLbl;
    private javax.swing.JLabel numIterationsCaptionLbl;
    private javax.swing.JTextField numIterationsFld;
    private javax.swing.JLabel numIterationsLbl;
    private javax.swing.JLabel perimeterCaptionLbl;
    private javax.swing.JLabel perimeterLbl;
    private javax.swing.JPanel physicalConstantsPanel;
    private javax.swing.JButton randomColumnBtn;
    private javax.swing.JButton randomizeBtn;
    private javax.swing.JButton saveBtn;
    private javax.swing.JLabel springConstantCaptionLbl;
    private javax.swing.JLabel springConstantLbl;
    private javax.swing.JLabel systemSizeCaptionLbl;
    private javax.swing.JLabel systemSizeLbl;
    private javax.swing.JLabel temperatureCaptionLbl;
    private javax.swing.JLabel temperatureLbl;
    private javax.swing.JLabel volumeCaptionLbl;
    private javax.swing.JLabel volumeLbl;
    // End of variables declaration//GEN-END:variables
}
