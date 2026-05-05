package gui;

import java.io.*;
import java.util.Properties;

public class ConfigurationStorage {

    private static final String CONFIG_FILE = System.getProperty("user.home") +
            File.separator +
            ".robot_game_config.properties";

    private Properties properties;

    public ConfigurationStorage() {
        this.properties = new Properties();
    }

    public boolean load() {
        File configFile = new File(CONFIG_FILE);
        if (!configFile.exists()) {
            return false;
        }

        try (FileInputStream in = new FileInputStream(configFile)) {
            properties.load(in);
            return true;
        } catch (IOException e) {
            System.err.println("Ошибка загрузки конфигурации: " + e.getMessage());
            return false;
        }
    }

    /**
     * Сохраняет текущую конфигурацию в файл
     */
    public void save() {
        try (FileOutputStream out = new FileOutputStream(CONFIG_FILE)) {
            properties.store(out, "Robot Game Configuration");
        } catch (IOException e) {
            System.err.println("Ошибка сохранения конфигурации: " + e.getMessage());
        }
    }

    /**
     * Получает значение свойства
     */
    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    /**
     * Получает значение свойства с значением по умолчанию
     */
    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    /**
     * Устанавливает значение свойства
     */
    public void setProperty(String key, String value) {
        if (value != null) {
            properties.setProperty(key, value);
        } else {
            properties.remove(key);
        }
    }

    /**
     * Проверяет, загружена ли конфигурация (есть ли свойства)
     */
    public boolean hasProperties() {
        return !properties.isEmpty();
    }
}