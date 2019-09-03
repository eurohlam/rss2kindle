package org.roag.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by eurohlam on 17/08/19.
 */
public class ServicePage extends AbstractPage {

    private SelenideElement pollBtn = $("button#run_all");

    @Override
    public String getPath() {
        return "/view/service";
    }

    @Step("Click poll my subscriptions immediately")
    public ServicePage clickPollSubscriptionsImmediately() {
        pollBtn.shouldBe(Condition.visible);
        pollBtn.click();
        return this;
    }

}
