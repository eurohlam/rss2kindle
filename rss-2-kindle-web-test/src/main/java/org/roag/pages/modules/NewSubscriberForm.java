package org.roag.pages.modules;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import java.util.function.Supplier;

public class NewSubscriberForm extends AbstractPageModule {

    private SelenideElement subscriberEmail = selenideElement().$("input#new_subscriber_email");
    private SelenideElement subscriberName = selenideElement().$("input#new_subscriber_name");
    private SelenideElement subscriberRssList = selenideElement().$("select#new_subscriber_rsslist");
    private SelenideElement newRss = selenideElement().$("input#new_subscriber_addrss");
    private SelenideElement addRssBtn = selenideElement().$("button#btn_new_subscriber_addrss");
    private SelenideElement deleteRssBtn = selenideElement().$("button#btn_new_subscriber_deleterss");
    private SelenideElement submitBtn = selenideElement().$("input[type='submit']"); //TODO: why it is input instead of button?

    public NewSubscriberForm(SelenideElement selector) {
        super(selector);
    }

    public NewSubscriberForm(Supplier<SelenideElement> selector) {
        super(selector);
    }

    @Step("Set subscriber's email {email}")
    public NewSubscriberForm setEmail(String email) {
        subscriberEmail.shouldBe(Condition.visible);
        subscriberEmail.setValue(email);
        return this;
    }

    @Step("Set subscriber's name {name}")
    public NewSubscriberForm setName(String name) {
        subscriberName.shouldBe(Condition.visible);
        subscriberName.setValue(name);
        return this;
    }

    @Step("Add subscriber's RSS {rss}")
    public NewSubscriberForm addRss(String rss) {
        subscriberRssList.shouldBe(Condition.visible);
        newRss.setValue(rss);
        addRssBtn.click();
        return this;
    }

    public NewSubscriberForm clickSubmit() {
        submitBtn.shouldBe(Condition.visible);
        submitBtn.shouldBe(Condition.enabled);
        submitBtn.click();
        return this;
    }

}
