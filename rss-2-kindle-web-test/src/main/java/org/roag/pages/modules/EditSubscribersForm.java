package org.roag.pages.modules;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import java.util.function.Supplier;

import static com.codeborne.selenide.Selenide.$;


public class EditSubscribersForm extends AbstractPageModule {

    private PageModuleCollection<SubscriberRecord> subscriberList = new PageModuleCollection<>(
            selenideElement().$$x("//table/tbody/tr"), SubscriberRecord::new);

    private ModalForm suspendModalForm = new ModalForm($("form#suspend_subscriber_form"));
    private ModalForm resumeModalForm = new ModalForm($("form#resume_subscriber_form"));
    private ModalForm removeModalForm = new ModalForm($("form#remove_subscriber_form"));

    public EditSubscribersForm(final SelenideElement selector) {
        super(selector);
    }

    public EditSubscribersForm(final Supplier<SelenideElement> selector) {
        super(selector);
    }

    @Override
    public boolean isDisplayed() {
        return selenideElement().isDisplayed();
    }

    public PageModuleCollection<SubscriberRecord> getSubscriberList() {
        return this.subscriberList;
    }

    @Step("Suspend subscriber {subscriber}")
    public EditSubscribersForm suspendSubscriber(String subscriber) {
        subscriberList.findBy(Condition.text(subscriber)).clickSuspend();
        suspendModalForm.shouldBe(Condition.visible);
        suspendModalForm.shouldHave(Condition.text(subscriber));
        suspendModalForm.submit();
        return this;
    }

    @Step("Resume subscriber {subscriber}")
    public EditSubscribersForm resumeSubscriber(String subscriber) {
        subscriberList.findBy(Condition.text(subscriber)).clickResume();
        resumeModalForm.shouldBe(Condition.visible);
        resumeModalForm.shouldHave(Condition.text(subscriber));
        resumeModalForm.submit();
        return this;
    }

    @Step("Remove subscriber {subscriber}")
    public EditSubscribersForm removeSubscriber(String subscriber) {
        subscriberList.findBy(Condition.text(subscriber)).clickRemove();
        removeModalForm.shouldBe(Condition.visible);
        removeModalForm.shouldHave(Condition.text(subscriber));
        removeModalForm.submit();
        return this;
    }

    public static class SubscriberRecord extends AbstractPageModule {

        private SelenideElement updateBtn = selenideElement().$("button#btn_update");
        private SelenideElement suspendBtn = selenideElement().$("button#btn_suspend");
        private SelenideElement resumeBtn = selenideElement().$("button#btn_resume");
        private SelenideElement removeBtn = selenideElement().$("button#btn_remove");

        SubscriberRecord(Supplier selector) {
            super(selector);
        }

        SubscriberRecord(SelenideElement selector) {
            super(selector);
        }

        @Override
        public boolean isDisplayed() {
            return selenideElement().isDisplayed();
        }

        @Step("Click update subscriber button")
        public void clickUpdate() {
            updateBtn.click();
        }

        @Step("Click suspend subscriber button")
        public void clickSuspend() {
            suspendBtn.click();
        }

        @Step("Click resume subscriber button")
        public void clickResume() {
            resumeBtn.click();
        }

        @Step("Click remove subscriber button")
        public void clickRemove() {
            removeBtn.click();
        }

        @Step("Get subscriber name")
        public String getName() {
            return selenideElement().$x("./td[2]/a").getText();
        }

        @Step("Get subscriber email")
        public String getEmail() {
            return selenideElement().$x("./td[3]/a").getText();
        }

        @Step("Get subscriber status")
        public String getStatus() {
            return selenideElement().$x("./td[4]").getText();
        }
    }

    public static class ModalForm extends AbstractPageModule {

        private SelenideElement submitBtn = selenideElement().$("button[type='submit']");
        private SelenideElement cancelBtn = selenideElement().$("button[type='button']");

        ModalForm(SelenideElement selector) {
            super(selector);
        }

        ModalForm(Supplier<SelenideElement> selector) {
            super(selector);
        }

        @Override
        public boolean isDisplayed() {
            return selenideElement().isDisplayed();
        }

        public void submit() {
            submitBtn.shouldBe(Condition.visible);
            submitBtn.pressEnter();
        }

        public void cancel() {
            cancelBtn.shouldBe(Condition.visible);
            cancelBtn.click();
        }
    }
}
