package tests;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;

public class BrowserManager {

    public static void openBrowser(String browser, String resolution, String url) {
        Configuration.browser = browser;
        Configuration.browserSize = resolution;
        Selenide.open(url);
    }

    public static void closeBrowser() {
        Selenide.closeWebDriver();
    }
}