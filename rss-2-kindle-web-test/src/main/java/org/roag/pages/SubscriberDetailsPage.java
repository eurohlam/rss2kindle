package org.roag.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.roag.pages.modules.*;

import java.util.Arrays;
import java.util.function.Supplier;

import static com.codeborne.selenide.Selenide.*;

public class SubscriberDetailsPage extends AbstractPage {

    private SelenideElement addBtn = $("button#add_btn");
    private SelenideElement activateBtn = $("button#activate_btn");
    private SelenideElement deactivateBtn = $("button#deactivate_btn");
    private SelenideElement removeBtn = $("button#remove_btn");

    private ModalForm activateModalForm = new ModalForm($("div#activateModal"));
    private ModalForm deactivateModalForm = new ModalForm($("div#deactivateModal"));
    private ModalForm removeModalForm = new ModalForm($("div#removeModal"));
    private NewSubscriptionsForm newSubscriptionsForm = new NewSubscriptionsForm($("div#addModal"));


    private PageModuleCollection<SubscriptionRecord> subscriptionList = new PageModuleCollection<>(
            $$x("//div[@id='details']/table/tbody/tr"), SubscriptionRecord::new);

    @Override
    public String getPath() {
        return "/view/subscriberDetails";
    }

    @Override
    public boolean isDisplayed() {
        return $x("//h5[contains(text(), 'Details of subscriptions for subscriber:')]").isDisplayed();
    }

    @Step("Add new subscription {url}")
    public SubscriberDetailsPage addSubscriptions(String... url) {
        addBtn.click();
        newSubscriptionsForm.shouldBe(Condition.visible);
        Arrays.stream(url).forEach(newSubscriptionsForm::addRss);
        newSubscriptionsForm.clickSubmit();
        return this;
    }

    @Step("Activate subscription {subscriptionRecord}")
    public SubscriberDetailsPage activateSubscription(SubscriptionRecord subscriptionRecord) {
        subscriptionRecord.check();
        activateBtn.click();
        activateModalForm.shouldBe(Condition.visible);
        activateModalForm.submit();
        return this;
    }

    @Step("Activate subscription {url}")
    public SubscriberDetailsPage activateSubscriptionByUrl(String url) {
        return activateSubscription(subscriptionList.findBy(Condition.text(url)));
    }

    @Step("Activate first subscription")
    public SubscriberDetailsPage activateFirst() {
        return activateSubscription(subscriptionList.first());
    }

    @Step("Activate last subscription")
    public SubscriberDetailsPage activateLast() {
        return activateSubscription(subscriptionList.last());
    }

    @Step("Deactivate subscription {subscriptionRecord}")
    public SubscriberDetailsPage deactivateSubscription(SubscriptionRecord subscriptionRecord) {
        subscriptionRecord.check();
        deactivateBtn.click();
        deactivateModalForm.shouldBe(Condition.visible);
        deactivateModalForm.submit();
        return this;
    }

    @Step("Deactivate subscription with URL: {url}")
    public SubscriberDetailsPage deactivateSubscriptionByUrl(String url) {
        return deactivateSubscription(subscriptionList.findBy(Condition.text(url)));
    }

    @Step("Deactivate first subscription")
    public SubscriberDetailsPage deactivateFirst() {
        return deactivateSubscription(subscriptionList.first());
    }

    @Step("Deactivate last subscription")
    public SubscriberDetailsPage deactivateLast() {
        return deactivateSubscription(subscriptionList.last());
    }

    @Step("Remove subscription {url}")
    public SubscriberDetailsPage removeSubscription(SubscriptionRecord subscriptionRecord) {
        subscriptionRecord.check();
        removeBtn.click();
        removeModalForm.shouldBe(Condition.visible);
        removeModalForm.submit();
        return this;
    }

    @Step("Remove subscription with URL: {url}")
    public SubscriberDetailsPage removeSubscriptionByUrl(String url) {
        return removeSubscription(subscriptionList.findBy(Condition.text(url)));
    }

    @Step("Remove first subscription")
    public SubscriberDetailsPage removeFirst() {
        return removeSubscription(subscriptionList.first());
    }

    @Step("Remove last subscription")
    public SubscriberDetailsPage removeLast() {
        return  removeSubscription(subscriptionList.last());
    }


    private static class NewSubscriptionsForm extends AbstractPageModule {

        private SelenideElement rssList = selenideElement().$("select#rss_list");
        private SelenideElement newRss = selenideElement().$("input#new_rss");
        private SelenideElement addRssBtn = selenideElement().$("button#btn_addrss");
        private SelenideElement deleteRssBtn = selenideElement().$("button#btn_deleterss");
        private SelenideElement submitBtn = selenideElement().$("button[type='submit']");

        public NewSubscriptionsForm(SelenideElement selector) {
            super(selector);
        }

        public NewSubscriptionsForm(Supplier<SelenideElement> selector) {
            super(selector);
        }

        @Override
        public boolean isDisplayed() {
            return selenideElement().isDisplayed();
        }

        @Step("Add subscription {rss}")
        public NewSubscriptionsForm addRss(String rss) {
            rssList.shouldBe(Condition.visible);
            newRss.setValue(rss);
            addRssBtn.click();
            return this;
        }

        @Step("Delete subscription {rss}")
        public NewSubscriptionsForm deleteRss(String rss) {
            //TODO: implement to delete RSS
            deleteRssBtn.click();
            return this;
        }

        @Step("Click submit")
        public NewSubscriptionsForm clickSubmit() {
            submitBtn.shouldBe(Condition.visible);
            submitBtn.shouldBe(Condition.enabled);
            submitBtn.click();
            return this;
        }

    }
}
