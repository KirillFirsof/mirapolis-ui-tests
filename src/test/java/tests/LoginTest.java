package tests;

import com.codeborne.selenide.Selenide;
import config.BrowserManager;
import config.TestConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import pages.LoginPage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LoginTest {

    // Источник данных — все комбинации браузеров из TestConfig
    static String[][] browserResolutionProvider() {
        return TestConfig.getAllCombinations().toArray(new String[0][0]);
    }

    // Негативные сценарии
    static Stream<Arguments> negativeScenarios() {
        return Stream.of(
            Arguments.of("пустой логин и пароль", "", ""),
            Arguments.of("пустой пароль", TestConfig.getTestLogin(), ""),
            Arguments.of("пустой логин", "", TestConfig.getTestPassword()),
            Arguments.of("неправильный логин", "wrongLogin", TestConfig.getTestPassword()),
            Arguments.of("неправильный пароль", TestConfig.getTestLogin(), "wrongPassword"),
            Arguments.of("оба поля неправильные", "wrongLogin", "wrongPassword")
        );
    }

    // Комбинации браузеров и негативных сценариев
    static Stream<Arguments> allNegativeCombinations() {
        List<Arguments> combinations = new ArrayList<>();
        String[][] browsers = TestConfig.getAllCombinations().toArray(new String[0][0]);
        List<Arguments> scenarios = negativeScenarios().collect(Collectors.toList());

        for (String[] browserConfig : browsers) {
            for (Arguments scenario : scenarios) {
                Object[] args = scenario.get();
                combinations.add(Arguments.of(
                    browserConfig[0],
                    browserConfig[1],
                    args[0],
                    args[1],
                    args[2]
                ));
            }
        }
        return combinations.stream();
    }

    @AfterEach
    public void tearDown() {
        BrowserManager.close();
    }

    //  ТЕСТЫ 

    @ParameterizedTest
    @MethodSource("browserResolutionProvider")
    @DisplayName("{0} {1} — проверка загрузки")
    public void loginPageShouldBeVisible(String browser, String resolution) {
        BrowserManager.open(LoginPage.URL, browser, resolution);
        LoginPage page = new LoginPage();
        page.shouldDisplayLoginButton();
    }

    @ParameterizedTest
    @MethodSource("browserResolutionProvider")
    @DisplayName("{0} {1} — успешный вход")
    public void successfulLoginTest(String browser, String resolution) {
        BrowserManager.open(LoginPage.URL, browser, resolution);
        LoginPage page = new LoginPage();
        page.login(TestConfig.getTestLogin(), TestConfig.getTestPassword());
        page.shouldBeLoggedIn();
    }

    @ParameterizedTest
    @MethodSource("browserResolutionProvider")
    @DisplayName("{0} {1} — переход на страницу восстановления")
    public void forgotPasswordLinkTest(String browser, String resolution) {
        BrowserManager.open(LoginPage.URL, browser, resolution);
        LoginPage page = new LoginPage();
        page.clickForgotPasswordLink();
        page.shouldDisplayForgotPasswordForm();
    }

    @ParameterizedTest
    @MethodSource("browserResolutionProvider")
    @DisplayName("{0} {1} — возврат на страницу логина")
    public void backToLoginLinkTest(String browser, String resolution) {
        BrowserManager.open(LoginPage.URL, browser, resolution);
        LoginPage page = new LoginPage();
        page.clickForgotPasswordLink();
        page.clickBackToLoginLink();
        page.shouldDisplayLoginButton();
    }

    @ParameterizedTest
    @MethodSource("allNegativeCombinations")
    @DisplayName("{0} {1} — {2}")
    public void negativeLoginTest(String browser, String resolution, String description,
                                  String login, String password) {
        BrowserManager.open(LoginPage.URL, browser, resolution);
        LoginPage page = new LoginPage();
        page.login(login, password);

        Selenide.Wait().until(webDriver -> {
            try {
                return webDriver != null && webDriver.getCurrentUrl().contains("iserrorauth");
            } catch (Exception e) {
                return false;
            }
        });
    }

    @ParameterizedTest
    @MethodSource("browserResolutionProvider")
    @DisplayName("{0} {1} — восстановление после ошибки")
    public void recoveryAfterErrorTest(String browser, String resolution) {
        BrowserManager.open(LoginPage.URL, browser, resolution);
        LoginPage page = new LoginPage();
        page.login("wrongLogin", "wrongPassword");

        Selenide.Wait().until(webDriver -> {
            try {
                if (webDriver == null) return false;
                webDriver.switchTo().alert().accept();
                return true;
            } catch (Exception e) {
                return false;
            }
        });

        page.shouldDisplayLoginButton();
    }
}