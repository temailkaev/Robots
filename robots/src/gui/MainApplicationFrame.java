package gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import log.Logger;

public class MainApplicationFrame extends JFrame {

    private final JDesktopPane desktopPane = new JDesktopPane();
    private final WindowStateStore windowStateStore = new WindowStateStore();
    private final RobotModel robotModel = new RobotModel();

    private LogWindow logWindow;
    private GameWindow gameWindow;
    private RobotCoordinatesWindow coordinatesWindow;

    // Таймер для обновления модели
    private javax.swing.Timer modelUpdateTimer;

    public MainApplicationFrame() {
        windowStateStore.loadAllStates();

        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
                screenSize.width - inset * 2,
                screenSize.height - inset * 2);

        setContentPane(desktopPane);

        logWindow = createLogWindow();
        gameWindow = new GameWindow(robotModel);
        coordinatesWindow = new RobotCoordinatesWindow();

        // Подписываем окно координат на обновления модели
        robotModel.addListener(coordinatesWindow);

        // Применяем сохранённые состояния
        windowStateStore.applyWindowState(logWindow, "logWindow");
        windowStateStore.applyWindowState(gameWindow, "gameWindow");
        windowStateStore.applyWindowState(coordinatesWindow, "coordinatesWindow");

        addWindow(logWindow);
        addWindow(gameWindow);
        addWindow(coordinatesWindow);

        // Располагаем окно координат в удобном месте
        coordinatesWindow.setLocation(10, 300);

        setJMenuBar(createMenuBar());

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitApplication();
            }
        });

        // Запускаем таймер для обновления модели
        startModelUpdater();
    }

    private void startModelUpdater() {
        modelUpdateTimer = new javax.swing.Timer(10, (e) -> {
            robotModel.updateModel();
        });
        modelUpdateTimer.start();
    }

    protected LogWindow createLogWindow() {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        logWindow.setLocation(10, 10);
        logWindow.setSize(300, 250);
        logWindow.pack();
        Logger.debug("Протокол работает");
        Logger.debug("Робот готов к работе");
        return logWindow;
    }

    protected void addWindow(JInternalFrame frame) {
        desktopPane.add(frame);
        frame.setVisible(true);
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(createFileMenu());
        menuBar.add(createLookAndFeelMenu());
        menuBar.add(createTestMenu());
        menuBar.add(createWindowsMenu());
        return menuBar;
    }

    private JMenu createWindowsMenu() {
        JMenu windowsMenu = new JMenu("Окна");
        windowsMenu.setMnemonic(KeyEvent.VK_W);

        JMenuItem showCoordinatesItem = new JMenuItem("Показать координаты");
        showCoordinatesItem.addActionListener(e -> {
            coordinatesWindow.setVisible(true);
            try {
                coordinatesWindow.setIcon(false);
            } catch (Exception ex) {}
        });
        windowsMenu.add(showCoordinatesItem);

        JMenuItem showGameItem = new JMenuItem("Показать игровое поле");
        showGameItem.addActionListener(e -> {
            gameWindow.setVisible(true);
            try {
                gameWindow.setIcon(false);
            } catch (Exception ex) {}
        });
        windowsMenu.add(showGameItem);

        JMenuItem showLogItem = new JMenuItem("Показать протокол");
        showLogItem.addActionListener(e -> {
            logWindow.setVisible(true);
            try {
                logWindow.setIcon(false);
            } catch (Exception ex) {}
        });
        windowsMenu.add(showLogItem);

        return windowsMenu;
    }

    private JMenu createFileMenu() {
        JMenu fileMenu = new JMenu("Файл");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        fileMenu.add(createExitMenuItem());
        return fileMenu;
    }

    private JMenuItem createExitMenuItem() {
        JMenuItem exitMenuItem = new JMenuItem("Выход", KeyEvent.VK_X);
        exitMenuItem.addActionListener((event) -> exitApplication());
        return exitMenuItem;
    }

    private JMenu createLookAndFeelMenu() {
        JMenu lookAndFeelMenu = new JMenu("Режим отображения");
        lookAndFeelMenu.setMnemonic(KeyEvent.VK_V);
        lookAndFeelMenu.getAccessibleContext().setAccessibleDescription(
                "Управление режимом отображения приложения");

        lookAndFeelMenu.add(createSystemLookAndFeelMenuItem());
        lookAndFeelMenu.add(createCrossplatformLookAndFeelMenuItem());

        return lookAndFeelMenu;
    }

    private JMenuItem createSystemLookAndFeelMenuItem() {
        JMenuItem systemLookAndFeel = new JMenuItem("Системная схема", KeyEvent.VK_S);
        systemLookAndFeel.addActionListener((event) -> {
            setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            this.invalidate();
        });
        return systemLookAndFeel;
    }

    private JMenuItem createCrossplatformLookAndFeelMenuItem() {
        JMenuItem crossplatformLookAndFeel = new JMenuItem("Универсальная схема", KeyEvent.VK_S);
        crossplatformLookAndFeel.addActionListener((event) -> {
            setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            this.invalidate();
        });
        return crossplatformLookAndFeel;
    }

    private JMenu createTestMenu() {
        JMenu testMenu = new JMenu("Тесты");
        testMenu.setMnemonic(KeyEvent.VK_T);
        testMenu.getAccessibleContext().setAccessibleDescription(
                "Тестовые команды");

        testMenu.add(createAddLogMessageMenuItem());

        return testMenu;
    }

    private JMenuItem createAddLogMessageMenuItem() {
        JMenuItem addLogMessageItem = new JMenuItem("Сообщение в лог", KeyEvent.VK_S);
        addLogMessageItem.addActionListener((event) -> {
            Logger.debug("Новая строка");
        });
        return addLogMessageItem;
    }

    private void setLookAndFeel(String className) {
        try {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        } catch (ClassNotFoundException | InstantiationException
                 | IllegalAccessException | UnsupportedLookAndFeelException e) {
            // just ignore
        }
    }

    private void exitApplication() {
        Object[] options = {"Да", "Нет", "Отмена"};

        int result = JOptionPane.showOptionDialog(
                this,
                "Вы действительно хотите выйти из приложения?",
                "Подтверждение выхода",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[1]);

        if (result == JOptionPane.YES_OPTION) {
            if (modelUpdateTimer != null) {
                modelUpdateTimer.stop();
            }
            saveWindowStates();
            Logger.debug("Приложение завершает работу");
            System.exit(0);
        }
    }

    private void saveWindowStates() {
        if (logWindow != null) {
            windowStateStore.saveWindowState(logWindow, "logWindow");
        }
        if (gameWindow != null) {
            windowStateStore.saveWindowState(gameWindow, "gameWindow");
        }
        if (coordinatesWindow != null) {
            windowStateStore.saveWindowState(coordinatesWindow, "coordinatesWindow");
        }
        windowStateStore.saveAllStates();
    }
}