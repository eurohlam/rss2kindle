package org.roag.pages;

import com.codeborne.selenide.Condition;
import io.qameta.allure.Step;
import org.roag.pages.modules.ContactForm;

import java.util.function.Consumer;

import static com.codeborne.selenide.Selenide.$;

public class LandingPage extends AbstractPage {

    private ContactForm contactForm = new ContactForm($("form#contactForm"));

    @Override
    public String getPath() {
        return "";
    }

    @Step("Send a message with name: {name} and email: {email}")
    public LandingPage sendMessage(String name, String email, String phone, String message) {
        return sendMessage(
                form -> form
                        .setName(name)
                        .setEmail(email)
                        .setPhone(phone)
                        .setMessage(message)
                        .clickSend()
        );
    }

    @Step("Send a message via Contact form")
    public LandingPage sendMessage(Consumer<ContactForm> consumer) {
        this.contactForm.selenideElement().shouldBe(Condition.visible);
        consumer.accept(contactForm);
        return this;
    }
}
