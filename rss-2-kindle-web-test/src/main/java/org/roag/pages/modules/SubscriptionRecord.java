package org.roag.pages.modules;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import java.util.function.Supplier;

public class SubscriptionRecord extends AbstractPageModule {

    public SubscriptionRecord(SelenideElement selector) {
        super(selector);
    }

    public SubscriptionRecord(Supplier<SelenideElement> selector) {
        super(selector);
    }

    @Override
    public boolean isDisplayed() {
        return selenideElement().isDisplayed();
    }

    @Step("Check subscription")
    public SubscriptionRecord check() {
        selenideElement().$x("./td[1]/input").setSelected(true);
        return this;
    }

    @Step("Uncheck subscription")
    public SubscriptionRecord uncheck() {
        selenideElement().$x("./td[1]/input").setSelected(false);
        return this;
    }

    @Step("Get number")
    public String geNumber() {
        return selenideElement().$x("./td[2]/a").getText();
    }

    @Step("Get RSS")
    public String getRss() {
        return selenideElement().$x("./td[3]/a").getText();
    }

    @Step("Get status")
    public String getStatus() {
        return selenideElement().$x("./td[4]").getText();
    }

    @Step("Get last polling date")
    public String getLastPollingDate() {
        return selenideElement().$x("./td[5]").getText();
    }

    @Step("Get error message")
    public String getErrorMessage() {
        return selenideElement().$x("./td[6]").getText();
    }

    @Step("Get retry count")
    public String getRetryCount() {
        return selenideElement().$x("./td[7]").getText();
    }
}
