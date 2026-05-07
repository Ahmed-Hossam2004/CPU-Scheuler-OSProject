package osproject;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class SchedulerGUI extends JFrame {

    private final SchedulerController controller = new SchedulerController();
    private final JComboBox<String> cbAlgo = new JComboBox<>(new String[]{"FCFS", "RR", "PRIORITY"});
    private final JTextField tfPID = new JTextField("P1", 7);
    private final JTextField tfArrival = new JTextField("0", 5);
    private final JTextField tfBurst = new JTextField("6", 5);
    private final JTextField tfPriority = new JTextField("1", 5);
    private final JSpinner spQuantum = new JSpinner(new SpinnerNumberModel(2, 1, 10, 1));

    private final DefaultTableModel mdlInput = new DefaultTableModel(
            new String[]{"PID", "Arrival (Cycle)", "Burst (Cycles)", "Priority"}, 0);
    private final JTable tblInput = new JTable(mdlInput);

    private final DefaultTableModel mdlResults = new DefaultTableModel(
            new String[]{"ID", "Type", "TAT (Cycles)", "WT (Cycles)"}, 0);
    private final JTable tblResults = new JTable(mdlResults);

    private final GanttChartPanel ganttPanel = new GanttChartPanel();
    private final JButton btnAdd = new JButton("Add to List");
    private final JButton btnStart = new JButton("▶ Start Scheduler");
    private final JButton btnReset = new JButton("↺ Reset All");

    private int pidCounter = 1;

    public SchedulerGUI() {
        super("MSA OS Project: CPU Scheduler Simulator");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1300, 800);
        setLayout(new BorderLayout(10, 10));

        buildUI();
        wireEvents();
        setLocationRelativeTo(null);
    }

    private void buildUI() {
        JLabel title = new JLabel("  CPU Scheduler Simulator (Ubuntu Linux Controller)", SwingConstants.LEFT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setOpaque(true);
        title.setBackground(new Color(40, 40, 60));
        title.setForeground(Color.WHITE);
        title.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(title, BorderLayout.NORTH);

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setPreferredSize(new Dimension(350, 0));
        left.setBorder(new EmptyBorder(10, 10, 10, 5));

        JPanel pInput = titledPanel("Add Process Configuration");
        pInput.setLayout(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 4, 4, 4);
        g.fill = GridBagConstraints.HORIZONTAL;

        addRow(pInput, g, 0, "PID:", tfPID);
        addRow(pInput, g, 1, "Arrival (Cycle):", tfArrival);
        addRow(pInput, g, 2, "Burst (Cycles):", tfBurst);
        addRow(pInput, g, 3, "Priority:", tfPriority);

        g.gridx = 0; g.gridy = 4; g.gridwidth = 2;
        pInput.add(btnAdd, g);
        left.add(pInput);

        JPanel pSettings = titledPanel("Algorithm Settings");
        pSettings.setLayout(new GridLayout(2, 2, 5, 5));
        pSettings.add(new JLabel("Algorithm:")); pSettings.add(cbAlgo);
        pSettings.add(new JLabel("Quantum (Cycles):")); pSettings.add(spQuantum);
        left.add(pSettings);

        left.add(new JScrollPane(tblInput));

        JPanel pBtns = new JPanel(new GridLayout(1, 2, 5, 5));
        styleBtn(btnStart, new Color(46, 139, 87), Color.GREEN);
        styleBtn(btnReset, new Color(220, 20, 60), Color.RED);
        pBtns.add(btnStart); pBtns.add(btnReset);
        left.add(pBtns);

        add(left, BorderLayout.WEST);

        JPanel right = new JPanel(new BorderLayout(0, 10));
        right.setBorder(new EmptyBorder(10, 5, 10, 10));

        JScrollPane ganttScroll = new JScrollPane(ganttPanel);
        ganttScroll.setBorder(BorderFactory.createTitledBorder("Execution Timeline (Gantt Chart)"));
        ganttScroll.setPreferredSize(new Dimension(0, 300));
        right.add(ganttScroll, BorderLayout.NORTH);
        right.add(new JScrollPane(tblResults), BorderLayout.CENTER);

        add(right, BorderLayout.CENTER);
    }

    private void wireEvents() {
        btnAdd.addActionListener(e -> {
            try {
                String pid = tfPID.getText().trim();
                
                int arr = tfArrival.getText().isEmpty() ? 0 : Integer.parseInt(tfArrival.getText());
                int bst = Integer.parseInt(tfBurst.getText());
                int pri = tfPriority.getText().isEmpty() ? 0 : Integer.parseInt(tfPriority.getText());

                mdlInput.addRow(new Object[]{pid, arr, bst, pri});
                pidCounter++;
                tfPID.setText("P" + pidCounter);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Check numeric inputs!");
            }
        });

        btnStart.addActionListener(e -> runSimulation());

        btnReset.addActionListener(e -> {
            mdlInput.setRowCount(0);
            mdlResults.setRowCount(0);
            ganttPanel.setGanttData(new ArrayList<>());
            pidCounter = 1;
            tfPID.setText("P1");
        });
    }

    private void runSimulation() {
        if (mdlInput.getRowCount() == 0) return;

        btnStart.setEnabled(false);
        mdlResults.setRowCount(0);

        ProcessLauncher launcher = new ProcessLauncher();
        controller.getReadyQueue().clear();

        
        for (int i = 0; i < mdlInput.getRowCount(); i++) {
            String customPID = (String) mdlInput.getValueAt(i, 0);
            int arrival = (Integer) mdlInput.getValueAt(i, 1);
            int burst = (Integer) mdlInput.getValueAt(i, 2);
            int pri = (Integer) mdlInput.getValueAt(i, 3);

            String category = (i % 2 == 0) ? "CPU" : "IO";

           
            PCB pcb = launcher.createWorker(customPID, category, pri, burst, arrival);
            if (pcb != null) controller.getReadyQueue().add(pcb);
        }

        
        controller.getReadyQueue().sort(Comparator.comparingInt(PCB::getArrivalCycle));

        
        new Thread(() -> {
            String mode = (String) cbAlgo.getSelectedItem();

            
            int quantumValue = (Integer) spQuantum.getValue(); 

            
            controller.setQuantum(quantumValue); 
            controller.runScheduler(mode, System.currentTimeMillis());

            SwingUtilities.invokeLater(() -> {
                updateOutputUI();
                btnStart.setEnabled(true);
            });
        }).start();
    }

    private void updateOutputUI() {
        mdlResults.setRowCount(0);
        for (PCB p : controller.getReadyQueue()) {
            
            int arrival = p.getArrivalCycle();
            int finish = p.getEndCycle();
            int burst = p.getBurstTime();

            int tat = finish - arrival;
            int wt = tat - burst;

            mdlResults.addRow(new Object[]{
                    p.getPid(),
                    p.getTaskType(),
                    tat,
                    Math.max(0, wt)
            });
        }

        List<GanttChart> finalGantt = controller.getGanttRecord();
        ganttPanel.setGanttData(finalGantt);
    }

    private JPanel titledPanel(String title) {
        JPanel p = new JPanel();
        p.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(180,180,200)), title,
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 12), new Color(50, 50, 120)));
        return p;
    }

    private void addRow(Container c, GridBagConstraints g, int row, String lbl, Component field) {
        g.gridx = 0; g.gridy = row; g.gridwidth = 1; c.add(new JLabel(lbl), g);
        g.gridx = 1; c.add(field, g);
    }

    private void styleBtn(JButton b, Color bg, Color fg) {
        b.setBackground(bg); b.setForeground(fg);
        b.setFocusPainted(false); b.setFont(new Font("Segoe UI", Font.BOLD, 12));
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception e) {}
        SwingUtilities.invokeLater(() -> new SchedulerGUI().setVisible(true));
    }
}