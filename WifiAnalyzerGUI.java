import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class WifiAnalyzerGUI extends JFrame {
    private final JTextField ssid = new JTextField("Home_WiFi", 10);
    private final JComboBox<Integer> channel = new JComboBox<>();
    private final JSlider signal = new JSlider(-90, -30, -60);

    private final DefaultTableModel model = new DefaultTableModel(
        new String[]{"SSID", "Channel", "Signal (dBm)"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable table = new JTable(model);
    private final JLabel recommended = new JLabel("Not analyzed");
    private final JLabel interference = new JLabel("Interference: —");
    private final JLabel networkCount = new JLabel("Networks: 0");
    private final JTextArea report = new JTextArea(4, 40);

    public WifiAnalyzerGUI() {
        setTitle("Wi-Fi Channel Analyzer");
        setSize(780, 570);
        setMinimumSize(new Dimension(660, 500));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        setContentPane(root);

        JLabel title = new JLabel("Wi-Fi Channel Analyzer");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        root.add(title, BorderLayout.NORTH);

        JPanel main = new JPanel(new BorderLayout(8, 8));
        JPanel input = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 9));
        input.setBorder(BorderFactory.createTitledBorder("Network Details"));

        for (int i = 1; i <= 14; i++) {
            channel.addItem(i);
        }

        signal.setPreferredSize(new Dimension(115, 35));
        signal.setToolTipText("Signal strength in dBm");

        input.add(new JLabel("SSID:"));
        input.add(ssid);
        input.add(new JLabel("Channel:"));
        input.add(channel);
        input.add(new JLabel("Signal:"));
        input.add(signal);
        main.add(input, BorderLayout.NORTH);

        table.setRowHeight(26);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);
        table.getTableHeader().setReorderingAllowed(false);
        table.setFillsViewportHeight(true);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createTitledBorder("Network List"));
        main.add(scroll, BorderLayout.CENTER);
        root.add(main, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout(8, 8));
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));

        JButton add = makeButton("Add Network");
        JButton remove = makeButton("Remove Selected");
        JButton analyze = makeButton("Analyze Networks");
        JButton clear = makeButton("Clear All");

        buttons.add(add);
        buttons.add(remove);
        buttons.add(analyze);
        buttons.add(clear);
        bottom.add(buttons, BorderLayout.NORTH);

        JPanel results = new JPanel(new BorderLayout(8, 8));
        results.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        JPanel summary = new JPanel(new GridLayout(2, 2, 8, 6));
        recommended.setFont(new Font("SansSerif", Font.BOLD, 14));
        interference.setFont(new Font("SansSerif", Font.BOLD, 12));

        summary.add(new JLabel("ANALYSIS RESULT"));
        summary.add(recommended);
        summary.add(interference);
        summary.add(networkCount);

        report.setEditable(false);
        report.setLineWrap(true);
        report.setWrapStyleWord(true);
        report.setFont(new Font("SansSerif", Font.PLAIN, 12));
        report.setText("Add networks and click Analyze Networks.");

        results.add(summary, BorderLayout.NORTH);
        results.add(new JScrollPane(report), BorderLayout.CENTER);
        bottom.add(results, BorderLayout.CENTER);
        root.add(bottom, BorderLayout.SOUTH);

        add.addActionListener(e -> addNetwork());
        remove.addActionListener(e -> removeNetwork());
        analyze.addActionListener(e -> analyzeNetworks());
        clear.addActionListener(e -> clearNetworks());
    }

    private JButton makeButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 11));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void addNetwork() {
        String name = ssid.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter a network name.");
            ssid.requestFocusInWindow();
            return;
        }

        model.addRow(new Object[]{
            name, channel.getSelectedItem(), signal.getValue()
        });

        ssid.setText("Router_" + (model.getRowCount() + 1));
        updateCount();
        resetResult("Network added. Analyze again for updated results.");
    }

    private void removeNetwork() {
        int selected = table.getSelectedRow();

        if (selected == -1) {
            JOptionPane.showMessageDialog(this, "Select a network to remove.");
            return;
        }

        model.removeRow(table.convertRowIndexToModel(selected));
        updateCount();
        resetResult("Network removed. Analyze the remaining networks.");
    }

    private void clearNetworks() {
        if (model.getRowCount() == 0) {
            resetResult("The network list is already empty.");
            return;
        }

        int choice = JOptionPane.showConfirmDialog(
            this,
            "Remove all networks?",
            "Confirm Clear",
            JOptionPane.YES_NO_OPTION
        );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        model.setRowCount(0);
        updateCount();
        resetResult("All networks cleared.");
    }

    private void updateCount() {
        networkCount.setText("Networks: " + model.getRowCount());
    }

    private void resetResult(String message) {
        recommended.setText("Not analyzed");
        interference.setText("Interference: —");
        report.setText(message);
    }

    private int calculateScore(int candidate) {
        int score = 0;

        for (int i = 0; i < model.getRowCount(); i++) {
            int existingChannel =
                ((Number) model.getValueAt(i, 1)).intValue();
            int dbm =
                ((Number) model.getValueAt(i, 2)).intValue();
            int distance = Math.abs(candidate - existingChannel);
            int weight = dbm >= -55 ? 3 : dbm >= -70 ? 2 : 1;

            if (distance == 0) {
                score += 3 * weight;
            } else if (distance == 1) {
                score += 2 * weight;
            } else if (distance == 2) {
                score += weight;
            }
        }

        return score;
    }

    private void analyzeNetworks() {
        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(
                this, "Add a network before analyzing.");
            return;
        }

        int[] candidates = {1, 6, 11};
        int bestChannel = candidates[0];
        int lowestScore = Integer.MAX_VALUE;
        StringBuilder comparison = new StringBuilder();

        for (int candidate : candidates) {
            int score = calculateScore(candidate);

            comparison.append("Channel ")
                .append(candidate)
                .append(": score ")
                .append(score)
                .append("\n");

            if (score < lowestScore) {
                lowestScore = score;
                bestChannel = candidate;
            }
        }

        String level = lowestScore <= 3 ? "Low"
            : lowestScore <= 12 ? "Moderate" : "High";

        recommended.setText("Recommended Channel: " + bestChannel);
        interference.setText("Estimated Interference: " + level);

        report.setText(
            "ANALYSIS REPORT\n\n"
            + "Networks entered: " + model.getRowCount() + "\n"
            + "Recommended channel: " + bestChannel + "\n\n"
            + "Channel comparison:\n" + comparison
            + "Best score: " + lowestScore + " (lower is better).\n\n"
            + "Based on entered networks and signal strengths. "
            + "This is not a live Wi-Fi scan."
        );

        report.setCaretPosition(0);
        updateCount();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }

            new WifiAnalyzerGUI().setVisible(true);
        });
    }
}