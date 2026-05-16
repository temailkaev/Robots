package gui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

public class RobotCoordinatesWindow extends JInternalFrame
        implements RobotModel.RobotStateListener {

    private final JLabel xCoordinateLabel;
    private final JLabel yCoordinateLabel;
    private final JLabel directionLabel;
    private final JLabel targetXLabel;
    private final JLabel targetYLabel;

    public RobotCoordinatesWindow() {
        super("Координаты робота", true, true, true, true);
        setSize(300, 200);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel infoPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        infoPanel.setBorder(new TitledBorder("Текущее состояние"));

        infoPanel.add(new JLabel("Позиция X:", SwingConstants.RIGHT));
        xCoordinateLabel = new JLabel("0.00", SwingConstants.LEFT);
        xCoordinateLabel.setFont(new Font("Monospaced", Font.BOLD, 12));
        infoPanel.add(xCoordinateLabel);

        infoPanel.add(new JLabel("Позиция Y:", SwingConstants.RIGHT));
        yCoordinateLabel = new JLabel("0.00", SwingConstants.LEFT);
        yCoordinateLabel.setFont(new Font("Monospaced", Font.BOLD, 12));
        infoPanel.add(yCoordinateLabel);

        infoPanel.add(new JLabel("Направление (рад):", SwingConstants.RIGHT));
        directionLabel = new JLabel("0.00", SwingConstants.LEFT);
        directionLabel.setFont(new Font("Monospaced", Font.BOLD, 12));
        infoPanel.add(directionLabel);

        infoPanel.add(new JLabel("Цель X:", SwingConstants.RIGHT));
        targetXLabel = new JLabel("0", SwingConstants.LEFT);
        targetXLabel.setFont(new Font("Monospaced", Font.BOLD, 12));
        infoPanel.add(targetXLabel);

        infoPanel.add(new JLabel("Цель Y:", SwingConstants.RIGHT));
        targetYLabel = new JLabel("0", SwingConstants.LEFT);
        targetYLabel.setFont(new Font("Monospaced", Font.BOLD, 12));
        infoPanel.add(targetYLabel);

        mainPanel.add(infoPanel, BorderLayout.CENTER);

        JLabel hintLabel = new JLabel("Данные обновляются в реальном времени", SwingConstants.CENTER);
        hintLabel.setFont(new Font("Dialog", Font.ITALIC, 10));
        mainPanel.add(hintLabel, BorderLayout.SOUTH);

        getContentPane().add(mainPanel);
        pack();
    }

    @Override
    public void onRobotStateChanged(double x, double y, double direction, int targetX, int targetY) {
        // Обновляем значения в UI потоке
        javax.swing.SwingUtilities.invokeLater(() -> {
            xCoordinateLabel.setText(String.format("%.2f", x));
            yCoordinateLabel.setText(String.format("%.2f", y));
            directionLabel.setText(String.format("%.3f", direction));
            targetXLabel.setText(String.valueOf(targetX));
            targetYLabel.setText(String.valueOf(targetY));
        });
    }
}