package gui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JPanel;

public class GameVisualizer extends JPanel implements RobotModel.RobotStateListener {

    private final Timer m_timer = initTimer();
    private final RobotModel model;

    private volatile double robotPositionX = 100;
    private volatile double robotPositionY = 100;
    private volatile double robotDirection = 0;
    private volatile int targetPositionX = 150;
    private volatile int targetPositionY = 100;

    private static Timer initTimer() {
        return new Timer("events generator", true);
    }

    public GameVisualizer(RobotModel model) {
        this.model = model;

        model.addListener(this);

        updateFromModel();

        m_timer.schedule(new TimerTask() {
            @Override
            public void run() {
                onRedrawEvent();
            }
        }, 0, 50);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                model.setTargetPosition(e.getPoint().x, e.getPoint().y);
            }
        });
        setDoubleBuffered(true);
    }

    private void updateFromModel() {
        robotPositionX = model.getRobotPositionX();
        robotPositionY = model.getRobotPositionY();
        robotDirection = model.getRobotDirection();
        targetPositionX = model.getTargetPositionX();
        targetPositionY = model.getTargetPositionY();
    }

    @Override
    public void onRobotStateChanged(double x, double y, double direction, int targetX, int targetY) {
        robotPositionX = x;
        robotPositionY = y;
        robotDirection = direction;
        targetPositionX = targetX;
        targetPositionY = targetY;
        repaint();
    }

    protected void setTargetPosition(Point p) {
        model.setTargetPosition(p.x, p.y);
    }

    protected void onRedrawEvent() {
        EventQueue.invokeLater(this::repaint);
    }

    private static int round(double value) {
        return (int)(value + 0.5);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D)g;
        drawRobot(g2d, round(robotPositionX), round(robotPositionY), robotDirection);
        drawTarget(g2d, targetPositionX, targetPositionY);
    }

    private static void fillOval(Graphics g, int centerX, int centerY, int diam1, int diam2) {
        g.fillOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    private static void drawOval(Graphics g, int centerX, int centerY, int diam1, int diam2) {
        g.drawOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    private void drawRobot(Graphics2D g, int x, int y, double direction) {
        int robotCenterX = x;
        int robotCenterY = y;
        AffineTransform t = AffineTransform.getRotateInstance(direction, robotCenterX, robotCenterY);
        g.setTransform(t);
        g.setColor(Color.MAGENTA);
        fillOval(g, robotCenterX, robotCenterY, 30, 10);
        g.setColor(Color.BLACK);
        drawOval(g, robotCenterX, robotCenterY, 30, 10);
        g.setColor(Color.WHITE);
        fillOval(g, robotCenterX + 10, robotCenterY, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, robotCenterX + 10, robotCenterY, 5, 5);
        g.setTransform(new AffineTransform());
    }

    private void drawTarget(Graphics2D g, int x, int y) {
        g.setTransform(new AffineTransform());
        g.setColor(Color.GREEN);
        fillOval(g, x, y, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, x, y, 5, 5);
    }
}