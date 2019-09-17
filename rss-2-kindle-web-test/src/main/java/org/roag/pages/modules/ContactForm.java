package org.roag.pages.modules;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import java.util.function.Supplier;

/**
 * Created eurohlam on 19/08/2019.
 */
public class ContactForm extends AbstractPageModule {

    private SelenideElement name = selenideElement().$("input#name");
    private SelenideElement email = selenideElement().$("input#email");
    private SelenideElement phone = selenideElement().$("input#phone");
    private SelenideElement message = selenideElement().$("textarea#message");
    private SelenideElement sendBtn = selenideElement().$("button#sendMessageButton");

    public ContactForm(SelenideElement selector) {
        super(selector);
    }

    public ContactForm(Supplier<SelenideElement> selector) {
        super(selector);
    }

    @Override
    public boolean isDisplayed() {
        return selenideElement().isDisplayed();
    }

    @Step("Set contact name {name}")
    public ContactForm setName(String name) {
        this.name.shouldBe(Condition.visible);
        this.name.setValue(name);
        return this;
    }

    @Step("Set contact email {email}")
    public ContactForm setEmail(String email) {
        this.email.shouldBe(Condition.visible);
        this.email.setValue(email);
        return this;
    }

    @Step("Set contact phone {phone}")
    public ContactForm setPhone(String phone) {
        this.phone.shouldBe(Condition.visible);
        this.phone.setValue(phone);
        return this;
    }

    @Step("Set message {message}")
    public ContactForm setMessage(String message) {
        this.message.shouldBe(Condition.visible);
        this.message.setValue(message);
        return this;
    }

    @Step("Click send button")
    public ContactForm clickSend() {
        this.sendBtn.shouldBe(Condition.visible);
        this.sendBtn.shouldBe(Condition.enabled);
        this.sendBtn.click();
        return this;
    }
}
