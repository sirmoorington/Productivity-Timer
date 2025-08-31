package packages;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.Duration;
import java.time.LocalTime;

public class ProductivityTracker extends JFrame {
    private JLabel productivityTimerLabel, freeTimeTimerLabel;
    private JButton startProductivityButton, stopProductivityButton, pauseProductivityButton;
    private JButton startFreeTimeButton, stopFreeTimeButton;
    private Timer productivityTimer, freeTimeTimer;
    private LocalTime productivityStartTime, freeTimeStartTime;
    private Duration totalDailyProductiveTime = Duration.ZERO;
    private Duration accumulatedFreeTime = Duration.ZERO;

    public ProductivityTracker() {
        setTitle("Produktivitäts-Tracker");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        productivityTimerLabel = new JLabel("Produktivitätszeit: 00:00:00");
        startProductivityButton = new JButton("Start Produktivität");
        stopProductivityButton = new JButton("Stopp Produktivität");
        pauseProductivityButton = new JButton("Pause Produktivität");

        freeTimeTimerLabel = new JLabel("Freizeit: 00:00:00");
        startFreeTimeButton = new JButton("Start Freizeit");
        stopFreeTimeButton = new JButton("Stopp Freizeit");

        add(productivityTimerLabel);
        add(startProductivityButton);
        add(stopProductivityButton);
        add(pauseProductivityButton);
        add(freeTimeTimerLabel);
        add(startFreeTimeButton);
        add(stopFreeTimeButton);

        productivityTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Duration elapsed = Duration.between(productivityStartTime, LocalTime.now());
                productivityTimerLabel.setText("Produktivitätszeit: " + formatDuration(elapsed));
            }
        });

        freeTimeTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                accumulatedFreeTime = accumulatedFreeTime.minusSeconds(1);
                freeTimeTimerLabel.setText("Freizeit: " + formatDuration(accumulatedFreeTime));

                // wenn freizeittimer leer = stopp
                if (accumulatedFreeTime.isZero() || accumulatedFreeTime.isNegative()) {
                    freeTimeTimer.stop();
                    accumulatedFreeTime = Duration.ZERO;
                    freeTimeTimerLabel.setText("Freizeit: 00:00:00");
                    JOptionPane.showMessageDialog(null, "Freizeit ist aufgebraucht!");
                }
            }
        });

        // produktivität starten
        startProductivityButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                productivityStartTime = LocalTime.now();
                productivityTimer.start();
            }
        });

        // produktivität stopp
        stopProductivityButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                productivityTimer.stop();
                Duration elapsed = Duration.between(productivityStartTime, LocalTime.now());
                totalDailyProductiveTime = totalDailyProductiveTime.plus(elapsed);

                // freizeit berechnen und zum timer hinzufügen
                Duration freeTime = calculateFreeTime(elapsed);
                accumulatedFreeTime = accumulatedFreeTime.plus(freeTime);

                JOptionPane.showMessageDialog(null,
                        "Produktive Zeit: " + formatDuration(elapsed) +
                        "\nFreizeit hinzugefügt: " + formatDuration(freeTime),
                        "Produktivitätsdurchgang abgeschlossen", JOptionPane.INFORMATION_MESSAGE);

                productivityTimerLabel.setText("Produktivitätszeit: 00:00:00"); // Reset
                freeTimeTimerLabel.setText("Freizeit: " + formatDuration(accumulatedFreeTime));
            }
        });

        // pause freizeit
        pauseProductivityButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                productivityTimer.stop();
            }
        });

        // start freizeit
        startFreeTimeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!accumulatedFreeTime.isZero()) {
                    freeTimeTimer.start();
                } else {
                    JOptionPane.showMessageDialog(null, "Keine Freizeit verfügbar!", "Fehler", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // stopp button freizeit
        stopFreeTimeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                freeTimeTimer.stop();
            }
        });
    }

    private Duration calculateFreeTime(Duration productiveTime) {
        // Umrechnung: 1 Stunde produktiv = 15 Minuten Freizeit
        long productiveSeconds = productiveTime.getSeconds();
        long freeSeconds = productiveSeconds / 4; // 15 Minuten pro Stunde = 1/4 der Zeit
        return Duration.ofSeconds(freeSeconds);
    }

    private String formatDuration(Duration duration) {
        long hours = duration.toHours();
        long minutes = (duration.toMinutes() % 60);
        long seconds = (duration.getSeconds() % 60);
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ProductivityTracker tracker = new ProductivityTracker();
            tracker.setVisible(true);
        });
    }
}
