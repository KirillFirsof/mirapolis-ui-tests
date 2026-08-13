package tests;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;

import config.TestConfig;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
public class LoginTest {

    public static String url = "https://qa.copy.mirapolis.ru/mira";

    // Источник данных — все комбинации браузеров из TestConfig
    static String[][] browserResolutionProvider() {
        return TestConfig.getAllCombinations().toArray(new String[0][0]);
    }

    // Комбинации всех негативных сценариев.
    static Stream<Arguments> negativeScenarios() {
        return Stream.of(
            Arguments.of("пустой и логин и пароль", TestConfig.getTestLogin(), ""),
            Arguments.of("пустой пароль", TestConfig.getTestLogin(), ""),
            Arguments.of("пустой логин", "", TestConfig.getTestPassword()),
            Arguments.of("неправильный логин", "wrongLogin", TestConfig.getTestPassword()),
            Arguments.of("неправильный пароль", TestConfig.getTestLogin(), "wrongPassword"),
            Arguments.of("оба поля неправильные", "wrongLogin", "wrongPassword")
        );
    }

    //  Комбинации (браузеры и негативные сценарии)
    static Stream<Arguments> allNegativeCombinations() {
        List<Arguments> combinations = new ArrayList<>();
        String[][] browsers = TestConfig.getAllCombinations().toArray(new String[0][0]);
        List<Arguments> scenarios = negativeScenarios().collect(Collectors.toList());

        for (String[] browserConfig : browsers) {
            for (Arguments scenario : scenarios) {
                Object[] args = scenario.get();
                combinations.add(Arguments.of(
                    browserConfig[0], // browser
                    browserConfig[1], // resolution
                    args[0],          // description
                    args[1],          // login
                    args[2]           // password
                ));
            }
        }
        return combinations.stream();
    }


    // Закрываем браузер ПОСЛЕ КАЖДОГО ТЕСТА
    @AfterEach
    public void tearDown() {
        BrowserManager.closeBrowser();
    }

    // Тест 1: проверяем, что страница загрузилась
    @ParameterizedTest
    @MethodSource("browserResolutionProvider")
    @DisplayName("{0} {1} — проверка загрузки")
    public void loginPageShouldBeVisible(String browser, String resolution) {
        BrowserManager.openBrowser(browser, resolution, url);

        $("#button_submit_login_form")
                .shouldBe(visible);
    }

    // Тест 2: проверяем успешный вход
    @ParameterizedTest
    @MethodSource("browserResolutionProvider")
    @DisplayName("{0} {1} — успешный вход")
    public void successfulLoginTest(String browser, String resolution) {
        BrowserManager.openBrowser(browser, resolution, url);

        $("[name='user']").setValue(TestConfig.getTestLogin());
        $("[name='password']").setValue(TestConfig.getTestPassword());

        $("#button_submit_login_form").click();

        $(".user_info_widget.is-logged").shouldBe(visible);
    }

    // Тест 3: проверяем переход на страницу восстановления
    @ParameterizedTest
    @MethodSource("browserResolutionProvider")
    @DisplayName("{0} {1} — переход на страницу восстановления пароля")
    public void forgotPasswordLinkTest(String browser, String resolution) {
        BrowserManager.openBrowser(browser, resolution, url);

        // Нажимаем на ссылку "Забыли пароль?" (если есть)
        $(".mira-default-login-page-link").shouldBe(visible);

        // Нажимаем на ссылку
        $(".mira-default-login-page-link").click();

        // Проверяем, что появилась форма восстановления пароля
        $("input[name='loginOrEmail']").shouldBe(visible);
    }

    // Тест 3: проверяем возврат на страницу входа
    @ParameterizedTest
    @MethodSource("browserResolutionProvider")
    @DisplayName("{0} {1} — возврат на страницу логина")
    public void backToLoginLinkTest(String browser, String resolution) {
        BrowserManager.openBrowser(browser, resolution, url);

        // 1. Переходим на страницу восстановления пароля
        $("a.mira-default-login-page-link").click();
        $("input[name='loginOrEmail']").shouldBe(visible);

        // 2. Нажимаем на ссылку "Назад к входу в систему"
        $("a.mira-page-forgot-password-link").click();

        // 3. Проверяем, что вернулись на страницу логина
        $("#button_submit_login_form").shouldBe(visible);
    }

    // Проверяем, все вариации напрваильных данных
    @ParameterizedTest
    @MethodSource("allNegativeCombinations")
    @DisplayName("{0} {1} — {2}")
    public void negativeLoginTest(String browser, String resolution, String description,
                                String login, String password) {
        BrowserManager.openBrowser(browser, resolution, url);

        $("[name='user']").setValue(login);
        $("[name='password']").setValue(password);
        $("#button_submit_login_form").click();

        Selenide.Wait().until(webDriver -> {
            try {
                return webDriver != null && webDriver.getCurrentUrl().contains("iserrorauth");
            } catch (Exception e) {
                return false; // Если произошла ошибка, просто возвращаем false и пробуем ещё раз
            }
        });
    }


    @ParameterizedTest
    @MethodSource("browserResolutionProvider")
    @DisplayName("{0} {1} — восстановление после ошибки")
    public void recoveryAfterErrorTest(String browser, String resolution) {
        BrowserManager.openBrowser(browser, resolution, url);

        // Вводим неверные данные
        $("[name='user']").setValue("wrongLogin");
        $("[name='password']").setValue("wrongPassword");
        $("#button_submit_login_form").click();

        // закрываем alert
        Selenide.Wait().until(webDriver -> {
            try {
                if (webDriver == null) return false;
                webDriver.switchTo().alert().accept();
                return true;
            } catch (Exception e) {
                return false;
            }
        });

        // Проверяем, что вернулись на страницу логина
        $("#button_submit_login_form").shouldBe(visible);
    }
}
