package org.roag.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.roag.config.Credentials;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.page;

/**
 * Created by eurohlam on 17/08/19.
 */
public class LoginPage extends AbstractPage {

    private SelenideElement username = $("input#username");
    private SelenideElement password = $("input#password");
    private SelenideElement signInBtn = $("button.btn");


    @Override
    public String getPath() {
        return "/view/login.jsp";
    }


    @Step("Login as user: {username}")
    public ProfilePage loginWith(String username, String password) {
        this.username.shouldBe(Condition.visible);
        this.password.shouldBe(Condition.visible);
        this.signInBtn.shouldBe(Condition.visible);
        this.username.setValue(username);
        this.password.setValue(password);
        this.signInBtn.click();
        return page(ProfilePage.class);
    }

    public ProfilePage loginWith(Credentials credentials) {
        return loginWith(credentials.username(), credentials.password());
    }
}
