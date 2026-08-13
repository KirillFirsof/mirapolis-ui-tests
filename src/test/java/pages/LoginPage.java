package pages;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class LoginPage {

    public static final String URL = "https://qa.copy.mirapolis.ru/mira";

    // Локаторы
    private final SelenideElement loginInput = $("[name='user']");
    private final SelenideElement passwordInput = $("[name='password']");
    private final SelenideElement loginButton = $("#button_submit_login_form");
    private final SelenideElement userWidget = $(".user_info_widget.is-logged");
    private final SelenideElement forgotPasswordLink = $("a.mira-default-login-page-link");
    private final SelenideElement backToLoginLink = $("a.mira-page-forgot-password-link");
    private final SelenideElement forgotPasswordField = $("input[name='loginOrEmail']");

    // ДЕЙСТВИЯ
    public void enterLogin(String login) {
        loginInput.setValue(login);
    }

    public void enterPassword(String password) {
        passwordInput.setValue(password);
    }

    public void clickLoginButton() {
        loginButton.click();
    }

    public void login(String login, String password) {
        enterLogin(login);
        enterPassword(password);
        clickLoginButton();
    }

    public void clickForgotPasswordLink() {
        forgotPasswordLink.click();
    }

    public void clickBackToLoginLink() {
        backToLoginLink.click();
    }

    //  ПРОВЕРКИ
    public void shouldDisplayLoginButton() {
        loginButton.shouldBe(visible);
    }

    public void shouldBeLoggedIn() {
        userWidget.shouldBe(visible);
    }

    public void shouldDisplayForgotPasswordForm() {
        forgotPasswordField.shouldBe(visible);
    }
}