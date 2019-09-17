package org.roag.pages.modules;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import java.util.function.Supplier;

public class AlertPanel extends AbstractPageModule {

    private SelenideElement closeBtn = selenideElement().$("button.close");

    public AlertPanel(SelenideElement selector) {
        super(selector);
    }

    public AlertPanel(Supplier<SelenideElement> selector) {
        super(selector);
    }

    @Override
    public boolean isDisplayed() {
        return selenideElement().isDisplayed();
    }

    @Step("Close alert panel")
    public void close() {
        closeBtn.shouldBe(Condition.visible);
        closeBtn.click();
    }

    @Step("Get alert text")
    public String getText() {
        return selenideElement().getText();
    }
}
