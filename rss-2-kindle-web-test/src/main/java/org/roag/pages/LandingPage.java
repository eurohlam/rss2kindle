package org.roag.pages;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Selenide.$;

public class LandingPage extends AbstractPage {

    private SelenideElement name = $("input#name");
    private SelenideElement email = $("input#email");
    private SelenideElement phone = $("input#phone");
    private SelenideElement message = $("textarea#message");
    private SelenideElement sendBtn = $("button#sendMessageButton");

    @Override
    public String getPath() {
        return "";
    }

    @Step("Send message from name: {name} email: {email}")
    public LandingPage sendMessage(String name, String email, String phone, String message) {
        this.name.setValue(name);
        this.email.setValue(email);
        this.phone.setValue(phone);
        this.message.setValue(message);
        this.sendBtn.click();
        return this;
    }
}
