package gui;

public class ApplicationDependencies {
    private final WindowStateStore windowStateStore;
    private final RobotModel robotModel;

    public ApplicationDependencies() {
        this.windowStateStore = new WindowStateStore();
        this.robotModel = new RobotModel();
    }

    public WindowStateStore getWindowStateStore() {
        return windowStateStore;
    }

    public RobotModel getRobotModel() {
        return robotModel;
    }
}