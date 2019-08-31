package org.roag.pages.modules;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import java.util.function.Supplier;

/**
 * Created by eurohlam on 31/08/19.
 */
public class SubscriberRecord extends AbstractPageModule {

    private SelenideElement updateBtn = selenideElement().$("button#btn_update");
    private SelenideElement suspendBtn = selenideElement().$("button#btn_suspend");
    private SelenideElement resumeBtn = selenideElement().$("button#btn_resume");
    private SelenideElement removeBtn = selenideElement().$("button#btn_remove");

    public SubscriberRecord(Supplier selector) {
        super(selector);
    }

    public SubscriberRecord(SelenideElement selector) {
        super(selector);
    }

    @Override
    public boolean isDisplayed() {
        return selenideElement().isDisplayed();
    }

    @Step("Click update subscriber button")
    public void clickUpdate() {
        updateBtn.shouldBe(Condition.visible, Condition.enabled);
        updateBtn.click();
    }

    @Step("Click suspend subscriber button")
    public void clickSuspend() {
        suspendBtn.shouldBe(Condition.visible, Condition.enabled);
        suspendBtn.click();
    }

    @Step("Click resume subscriber button")
    public void clickResume() {
        resumeBtn.shouldBe(Condition.visible, Condition.enabled);
        resumeBtn.click();
    }

    @Step("Click remove subscriber button")
    public void clickRemove() {
        removeBtn.shouldBe(Condition.visible, Condition.enabled);
        removeBtn.click();
    }

    public void openSubscriberDetails(String name) {
        selenideElement().$x("./td[2]/a").click();
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

    @Step("Get number of subscriptions")
    public String getNumberOfSubscriptions() {
        return selenideElement().$x("./td[5]").getText();
    }
}
