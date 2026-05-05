package gui;

import java.awt.Point;
import javax.swing.JInternalFrame;

public class WindowStateStore {

    private final ConfigurationStorage storage;

    public WindowStateStore() {
        this.storage = new ConfigurationStorage();
    }



    public boolean loadAllStates() {
        return storage.load();
    }

    /**
     * Сохраняет все состояния окон в файл
     */
    public void saveAllStates() {
        storage.save();
    }

    /**
     * Сохраняет состояние одного окна в памяти
     */
    public void saveWindowState(JInternalFrame frame, String windowKey) {
        try {
            Point location = frame.getLocation();
            storage.setProperty(windowKey + ".x", String.valueOf(location.x));
            storage.setProperty(windowKey + ".y", String.valueOf(location.y));

            storage.setProperty(windowKey + ".width", String.valueOf(frame.getWidth()));
            storage.setProperty(windowKey + ".height", String.valueOf(frame.getHeight()));

            if (frame.isIcon()) {
                storage.setProperty(windowKey + ".state", "ICONIFIED");
            } else if (frame.isMaximum()) {
                storage.setProperty(windowKey + ".state", "MAXIMIZED");
            } else {
                storage.setProperty(windowKey + ".state", "NORMAL");
            }

        } catch (Exception e) {
            System.err.println("Ошибка сохранения состояния окна " + windowKey + ": " + e.getMessage());
        }
    }

    /**
     * Применяет сохранённое состояние к окну
     */
    public void applyWindowState(JInternalFrame frame, String windowKey) {
        try {
            String xStr = storage.getProperty(windowKey + ".x");
            String yStr = storage.getProperty(windowKey + ".y");
            if (xStr != null && yStr != null) {
                frame.setLocation(Integer.parseInt(xStr), Integer.parseInt(yStr));
            }

            String widthStr = storage.getProperty(windowKey + ".width");
            String heightStr = storage.getProperty(windowKey + ".height");
            if (widthStr != null && heightStr != null) {
                frame.setSize(Integer.parseInt(widthStr), Integer.parseInt(heightStr));
            }

            String state = storage.getProperty(windowKey + ".state");
            if (state != null) {
                switch (state) {
                    case "ICONIFIED":
                        frame.setIcon(true);
                        break;
                    case "MAXIMIZED":
                        try {
                            frame.setMaximum(true);
                        } catch (Exception e) {
                            // игнорируем, если окно не поддерживает максимизацию
                        }
                        break;
                    default:
                        // NORMAL — ничего не делаем
                        break;
                }
            }

        } catch (Exception e) {
            System.err.println("Ошибка восстановления окна " + windowKey + ": " + e.getMessage());
        }
    }

    /**
     * Проверяет, есть ли сохранённые состояния
     */
    public boolean hasSavedStates() {
        return storage.hasProperties();
    }
}