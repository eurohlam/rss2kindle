package org.roag.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.roag.pages.modules.ContactForm;

import java.util.function.Consumer;

import static com.codeborne.selenide.Selenide.$;
import static org.roag.pages.PageUtils.at;

public class LandingPage extends AbstractPage {

    private ContactForm contactForm = new ContactForm($("form#contactForm"));
    private SelenideElement whyBtn = $("a.portfolio-item[href='#howto-modal-1']");
    private SelenideElement howBtn = $("a.portfolio-item[href='#howto-modal-2']");
    private SelenideElement psBtn = $("a.portfolio-item[href='#howto-modal-3']");
    private SelenideElement signUpBtn = $("a.btn[href='view/register']");
    private SelenideElement whyModalForm = $("div#howto-modal-1");
    private SelenideElement howModalForm = $("div#howto-modal-2");
    private SelenideElement psModalForm = $("div#howto-modal-3");


    @Override
    public String getPath() {
        return "";
    }

    @Override
    public boolean isDisplayed() {
        return contactForm.isDisplayed();
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
        this.contactForm.shouldBe(Condition.visible);
        consumer.accept(contactForm);
        return this;
    }

    @Step("Click Why button in HowTo section")
    public LandingPage clickWhy() {
        whyBtn.shouldBe(Condition.visible);
        whyBtn.click();
        if (!whyModalForm.isDisplayed()) {
            whyBtn.pressEnter();
        }
        whyModalForm.shouldBe(Condition.visible);
        return this;
    }

    @Step("Click P&S button in HowTo section")
    public LandingPage clickPrivacyAndSecurity() {
        psBtn.shouldBe(Condition.visible);
        psBtn.click();
        if (!psModalForm.isDisplayed()) {
            psBtn.pressEnter();
        }
        psModalForm.shouldBe(Condition.visible);
        return this;
    }

    @Step("Click How button in HowTo section")
    public LandingPage clickHow() {
        howBtn.shouldBe(Condition.visible, Condition.enabled);
        howBtn.click();
        if (!howModalForm.isDisplayed()) {
            howBtn.pressEnter();
        }
        howModalForm.shouldBe(Condition.visible);
        return this;
    }

    @Step("Close modal form")
    public LandingPage closeModalForm() {
        if (whyModalForm.isDisplayed()) {
            whyModalForm.$("a.portfolio-modal-dismiss.btn").click();
        }
        if (howModalForm.isDisplayed()) {
            howModalForm.$("a.portfolio-modal-dismiss.btn").click();
        }
        if (psModalForm.isDisplayed()) {
            psModalForm.$("a.portfolio-modal-dismiss.btn").click();
        }
        return this;
    }

    @Step("Click Sign Up button")
    public SignUpPage clickSignUp() {
        signUpBtn.shouldBe(Condition.visible, Condition.enabled);
        signUpBtn.click();
        return at(SignUpPage.class);
    }

}
