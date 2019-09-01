package org.roag.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.roag.pages.modules.PageModuleCollection;
import org.roag.pages.modules.SubscriptionRecord;

import static com.codeborne.selenide.Selenide.*;

public class SubscriberDetailsPage extends AbstractPage {

    private SelenideElement addBtn = $("button#add_btn");
    private SelenideElement activateBtn = $("button#activate_btn");
    private SelenideElement deactivateBtn = $("button#deactivate_btn");
    private SelenideElement removeBtn = $("button#remove_btn");


    private PageModuleCollection<SubscriptionRecord> subscriptionList = new PageModuleCollection<>(
            $$x("//div[id='details']/table/tbody/tr"), SubscriptionRecord::new);

    @Override
    public String getPath() {
        return "/view/subscriberDetails";
    }

    @Override
    public boolean isDisplayed() {
        return $x("//h5[contains(text(), 'Details of subscriptions for subscriber:')]").isDisplayed();
    }

    @Step("Add new subscription {url}")
    public SubscriberDetailsPage addSubscriptions(String url) {
        //TODO: add subscription
        addBtn.click();
        return this;
    }

    @Step("Activate subscription {url}")
    public SubscriberDetailsPage activateSubscription(String url) {
        subscriptionList.findBy(Condition.text(url)).check();
        activateBtn.click();
        //TODO: modal form
        return this;
    }

    @Step("Activate first subscription")
    public SubscriberDetailsPage activateFirst() {
        subscriptionList.first().check();
        activateBtn.click();
        //TODO: modal form
        return this;
    }

    @Step("Activate last subscription")
    public SubscriberDetailsPage activateLast() {
        subscriptionList.last().check();
        activateBtn.click();
        //TODO: modal form
        return this;
    }

    @Step("Deactivate subscription {url}")
    public SubscriberDetailsPage deactivateSubscription(String url) {
        subscriptionList.findBy(Condition.text(url)).check();
        deactivateBtn.click();
        //TODO: modal form
        return this;
    }

    @Step("Deactivate first subscription")
    public SubscriberDetailsPage deactivateFirst() {
        subscriptionList.first().check();
        deactivateBtn.click();
        //TODO: modal form
        return this;
    }

    @Step("Deactivate last subscription")
    public SubscriberDetailsPage deactivateLast() {
        subscriptionList.last().check();
        deactivateBtn.click();
        //TODO: modal form
        return this;
    }

    @Step("Remove subscription {url}")
    public SubscriberDetailsPage removeSubscription(String url) {
        subscriptionList.findBy(Condition.text(url)).check();
        removeBtn.click();
        //TODO: modal form
        return this;
    }

    @Step("Remove first subscription")
    public SubscriberDetailsPage removeFirst() {
        subscriptionList.first().check();
        removeBtn.click();
        //TODO: modal form
        return this;
    }

    @Step("Remove last subscription")
    public SubscriberDetailsPage removeLast() {
        subscriptionList.last().check();
        removeBtn.click();
        //TODO: modal form
        return this;
    }

}
