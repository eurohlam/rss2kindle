package org.roag.pages.modules;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import java.util.function.Supplier;

public class UpdateSubscriberModalForm extends ModalForm {

    private SelenideElement subscriberName = selenideElement().$("input#update_subscriber_name");
    private SelenideElement subscriberEmail = selenideElement().$("input#update_subscriber_email");
    private SelenideElement subscriberStatus = selenideElement().$("input#update_subscriber_status");
    private SelenideElement rssList = selenideElement().$("select#update_subscriber_rsslist");
    private SelenideElement addRss = selenideElement().$("input#update_subscriber_addrss");
    private SelenideElement addRssBtn = selenideElement().$("button#btn_update_subscriber_addrss");
    private SelenideElement deleteRssBtn = selenideElement().$("button#btn_update_subscriber_deleterss");

    public UpdateSubscriberModalForm(SelenideElement selector) {
        super(selector);
    }

    public UpdateSubscriberModalForm(Supplier<SelenideElement> selector) {
        super(selector);
    }

    @Step("Set subscriber name {name}")
    public UpdateSubscriberModalForm setName(String name) {
        subscriberName.setValue(name);
        return this;
    }

    @Step("Add RSS {url}")
    public UpdateSubscriberModalForm addRss(String url) {
        addRss.setValue(url);
        addRssBtn.click();
        return this;
    }

    public UpdateSubscriberModalForm deleteRss(String rss) {
        rssList.selectOptionByValue(rss);
        deleteRssBtn.click();
        return this;
    }

    @Step("Get subscriber email")
    public String getSubscriberEmail() {
        return subscriberEmail.getValue();
    }

    @Step("Get subscriber status")
    public String setSubscriberStatus() {
        return subscriberStatus.getValue();
    }
}
