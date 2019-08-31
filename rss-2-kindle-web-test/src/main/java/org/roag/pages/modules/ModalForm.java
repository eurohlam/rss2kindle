package org.roag.pages.modules;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import java.util.function.Supplier;

public class ModalForm extends AbstractPageModule {

    private SelenideElement submitBtn = selenideElement().$("button[type='submit']");
    private SelenideElement cancelBtn = selenideElement().$("button[type='button']");

    public ModalForm(SelenideElement selector) {
        super(selector);
    }

    public ModalForm(Supplier<SelenideElement> selector) {
        super(selector);
    }

    @Override
    public boolean isDisplayed() {
        return selenideElement().isDisplayed();
    }

    @Step("Click submit")
    void submit() {
        submitBtn.shouldBe(Condition.visible, Condition.enabled);
        submitBtn.click();
        if (submitBtn.isDisplayed()) {
            submitBtn.click();
        }
    }

    @Step("Click cancel")
    void cancel() {
        cancelBtn.shouldBe(Condition.visible, Condition.enabled);
        cancelBtn.click();
        if (cancelBtn.isDisplayed()) {
            cancelBtn.click();
        }
    }
}
