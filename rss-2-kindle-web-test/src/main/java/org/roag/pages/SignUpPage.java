package org.roag.pages;


import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by eurohlam on 17/08/19.
 */
public class SignUpPage implements Page {


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
    public SignUpPage signUpWith(String username, String email, String password) {
        this.username.shouldBe(Condition.visible);
        this.email.shouldBe(Condition.visible);
        this.password.shouldBe(Condition.visible);
        this.confirmPassword.shouldBe(Condition.visible);

        this.username.setValue(username);
        this.email.setValue(email);
        this.password.setValue(password);
        this.confirmPassword.setValue(password);
        this.signUpBtn.click();
        return this;
    }
}
