package config;

import java.io.InputStream;
import java.util.*;

public class TestConfig {
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = TestConfig.class.getClassLoader()
                .getResourceAsStream("selenide.properties")) {
            if (input == null) {
                throw new RuntimeException("Файл selenide.properties не найден в classpath");
            }
            properties.load(input);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка загрузки selenide.properties", e);
        }
    }

    public static List<String> getBrowsers() {
        String browsers = properties.getProperty("selenide.browsers");
        if (browsers == null || browsers.isEmpty()) {
            throw new RuntimeException("selenide.browsers не задан в selenide.properties");
        }
        return Arrays.asList(browsers.split(","));
    }

    public static List<String> getResolutions() {
        String resolutions = properties.getProperty("selenide.resolutions");
        if (resolutions == null || resolutions.isEmpty()) {
            throw new RuntimeException("selenide.resolutions не задан в selenide.properties");
        }
        return Arrays.asList(resolutions.split(","));
    }

    public static List<String[]> getAllCombinations() {
        List<String[]> combos = new ArrayList<>();
        for (String browser : getBrowsers()) {
            for (String resolution : getResolutions()) {
                combos.add(new String[]{browser.trim(), resolution.trim()});
            }
        }
        return combos;
    }

    public static String getTestLogin() {
        return properties.getProperty("test.user.login");
    }

    public static String getTestPassword() {
        return properties.getProperty("test.user.password");
    }
}