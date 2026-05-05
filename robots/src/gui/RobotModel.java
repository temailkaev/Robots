package gui;

import java.util.ArrayList;
import java.util.List;

public class RobotModel {
/*
For git
 */
    private double robotPositionX = 100;
    private double robotPositionY = 100;
    private double robotDirection = 0;
    private int targetPositionX = 150;
    private int targetPositionY = 100;

    private static final double MAX_VELOCITY = 0.1;
    private static final double MAX_ANGULAR_VELOCITY = 0.001;
    private static final double DISTANCE_THRESHOLD = 0.5;

    private final List<RobotStateListener> listeners = new ArrayList<>();

    public interface RobotStateListener {
        void onRobotStateChanged(double x, double y, double direction, int targetX, int targetY);
    }

    public void addListener(RobotStateListener listener) {
        listeners.add(listener);
    }

    public void removeListener(RobotStateListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners() {
        for (RobotStateListener listener : listeners) {
            listener.onRobotStateChanged(robotPositionX, robotPositionY, robotDirection,
                    targetPositionX, targetPositionY);
        }
    }

    public void updateModel() {
        double distance = distance(targetPositionX, targetPositionY,
                robotPositionX, robotPositionY);

        if (distance < DISTANCE_THRESHOLD) {
            notifyListeners();
            return;
        }

        double velocity = MAX_VELOCITY;
        double angleToTarget = angleTo(robotPositionX, robotPositionY, targetPositionX, targetPositionY);
        double angularVelocity = calculateAngularVelocity(angleToTarget);

        moveRobot(velocity, angularVelocity, 10);
        notifyListeners();
    }

    private double calculateAngularVelocity(double angleToTarget) {
        double angleDifference = angleToTarget - robotDirection;

        // Нормализуем разницу углов в диапазон [-PI, PI]
        while (angleDifference > Math.PI) angleDifference -= 2 * Math.PI;
        while (angleDifference < -Math.PI) angleDifference += 2 * Math.PI;

        // Выбираем кратчайший путь поворота
        if (Math.abs(angleDifference) < 0.01) {
            return 0;
        }

        if (angleDifference > 0) {
            return MAX_ANGULAR_VELOCITY;
        } else {
            return -MAX_ANGULAR_VELOCITY;
        }
    }

    public void setTargetPosition(int x, int y) {
        this.targetPositionX = x;
        this.targetPositionY = y;
        notifyListeners();
    }

    public double getRobotPositionX() { return robotPositionX; }
    public double getRobotPositionY() { return robotPositionY; }
    public double getRobotDirection() { return robotDirection; }
    public int getTargetPositionX() { return targetPositionX; }
    public int getTargetPositionY() { return targetPositionY; }

    private double distance(double x1, double y1, double x2, double y2) {
        double diffX = x1 - x2;
        double diffY = y1 - y2;
        return Math.sqrt(diffX * diffX + diffY * diffY);
    }

    private double angleTo(double fromX, double fromY, double toX, double toY) {
        double diffX = toX - fromX;
        double diffY = toY - fromY;
        return asNormalizedRadians(Math.atan2(diffY, diffX));
    }

    private void moveRobot(double velocity, double angularVelocity, double duration) {
        velocity = applyLimits(velocity, 0, MAX_VELOCITY);
        angularVelocity = applyLimits(angularVelocity, -MAX_ANGULAR_VELOCITY, MAX_ANGULAR_VELOCITY);

        double newX = robotPositionX;
        double newY = robotPositionY;
        double newDirection = robotDirection;

        if (Math.abs(angularVelocity) < 1e-10) {
            // Прямолинейное движение
            newX = robotPositionX + velocity * duration * Math.cos(robotDirection);
            newY = robotPositionY + velocity * duration * Math.sin(robotDirection);
            newDirection = robotDirection;
        } else {
            // Движение по дуге
            newX = robotPositionX + velocity / angularVelocity *
                    (Math.sin(robotDirection + angularVelocity * duration) - Math.sin(robotDirection));
            newY = robotPositionY - velocity / angularVelocity *
                    (Math.cos(robotDirection + angularVelocity * duration) - Math.cos(robotDirection));
            newDirection = asNormalizedRadians(robotDirection + angularVelocity * duration);
        }

        robotPositionX = newX;
        robotPositionY = newY;
        robotDirection = newDirection;
    }

    private double applyLimits(double value, double min, double max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    private double asNormalizedRadians(double angle) {
        while (angle < 0) angle += 2 * Math.PI;
        while (angle >= 2 * Math.PI) angle -= 2 * Math.PI;
        return angle;
    }
}