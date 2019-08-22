package org.roag.pages;


import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.roag.config.Credentials;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by eurohlam on 17/08/19.
 */
public class SignUpPage extends AbstractPage {


    private SelenideElement username = $("input#username");
    private SelenideElement email = $("input#email");
    private SelenideElement password = $("input#password");
    private SelenideElement confirmPassword = $("input#confirmPassword");
    private SelenideElement signUpBtn = $("button.btn");

    @Override
    public String getPath() {
        return "/view/register";
    }

    @Override
    public boolean isDisplayed() {
        return $("form#newUserForm").isDisplayed();
    }

    @Step("Sign Up with: username={username} and email={email}")
    public SignUpPage signUpWith(String username, String password, String email) {
        setUsername(username);
        setEmail(email);
        setPassword(password);
        setConfirmPassword(password);
        clickSignUp();
        return this;
    }

    @Step("Sign Up with credentials {credentials}")
    public SignUpPage signUpWith(Credentials credentials) {
        return signUpWith(credentials.username(), credentials.password(), credentials.email());
    }

    @Step("Set username for Singing Up {username}")
    public SignUpPage setUsername(String username) {
        this.username.shouldBe(Condition.visible);
        this.username.setValue(username);
        return this;
    }

    @Step("Set email for Singing Up {email}")
    public SignUpPage setEmail(String email) {
        this.email.shouldBe(Condition.visible);
        this.email.setValue(email);
        return this;
    }

    @Step("Set password for Singing Up {password}")
    public SignUpPage setPassword(String password) {
        this.password.shouldBe(Condition.visible);
        this.password.setValue(password);
        return this;
    }

    @Step("Confirm password for Singing Up {confirmPassword}")
    public SignUpPage setConfirmPassword(String confirmPassword) {
        this.confirmPassword.shouldBe(Condition.visible);
        this.confirmPassword.setValue(confirmPassword);
        return this;
    }

    @Step("Click Sign Up button")
    public SignUpPage clickSignUp() {
        this.signUpBtn.shouldBe(Condition.visible);
        this.signUpBtn.click();
        return this;
    }
}
