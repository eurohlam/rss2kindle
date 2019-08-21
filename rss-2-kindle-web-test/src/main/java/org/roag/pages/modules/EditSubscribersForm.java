package org.roag.pages.modules;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import java.util.function.Supplier;


public class EditSubscribersForm extends AbstractPageModule {

    //TODO: think about implementation collection of page modules
    private ElementsCollection subscriberList = selenideElement().$$x("//table/tbody/tr");

    public EditSubscribersForm(SelenideElement selector) {
        super(selector);
    }

    public EditSubscribersForm(Supplier<SelenideElement> selector) {
        super(selector);
    }

    @Override
    public boolean isDisplayed() {
        return selenideElement().isDisplayed();
    }

    public ElementsCollection getSubscriberList() {
        return subscriberList;
    }

    private static class SubscriberRecord extends AbstractPageModule {

        private SelenideElement updateBtn = selenideElement().$("button#btn_update");
        private SelenideElement suspendBtn = selenideElement().$("button#btn_suspend");
        private SelenideElement resumeBtn = selenideElement().$("button#btn_resume");
        private SelenideElement removeBtn = selenideElement().$("button#btn_remove");

        SubscriberRecord(SelenideElement selector) {
            super(selector);
        }

        @Override
        public boolean isDisplayed() {
            return false;
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
}
