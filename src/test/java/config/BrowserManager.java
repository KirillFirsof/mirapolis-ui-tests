package config;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;

public final class BrowserManager {

    private BrowserManager() {
    }

    public static void open(String url, String browser, String resolution) {
        Configuration.browser = browser;
        Configuration.browserSize = resolution;
        Selenide.open(url);
    }

    public static void close() {
        Selenide.closeWebDriver();
    }
}
