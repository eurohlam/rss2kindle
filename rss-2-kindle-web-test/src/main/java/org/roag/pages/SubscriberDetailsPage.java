package org.roag.pages;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class SubscriberDetailsPage extends AbstractPage {

    private SelenideElement addBtn = $("button#add_btn");
    private SelenideElement activateBtn = $("button#activate_btn");
    private SelenideElement deactivateBtn = $("button#deactivate_btn");
    private SelenideElement removeBtn = $("button#remove_btn");

    @Override
    public String getPath() {
        return "/view/subscriberDetails";
    }

    public SubscriberDetailsPage addSubscriptions(String url) {
        //TODO: add subscription
        addBtn.click();
        return this;
    }

    public SubscriberDetailsPage activateSubscriptions(String url) {
        //TODO: add subscription
        activateBtn.click();
        return this;
    }

    public SubscriberDetailsPage deactivateSubscriptions(String url) {
        //TODO: add subscription
        deactivateBtn.click();
        return this;
    }

    public SubscriberDetailsPage removeSubscriptions(String url) {
        //TODO: add subscription
        removeBtn.click();
        return this;
    }

}
