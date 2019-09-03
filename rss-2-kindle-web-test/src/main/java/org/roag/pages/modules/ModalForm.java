package org.roag.pages.modules;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import java.util.function.Supplier;

public class ModalForm extends AbstractPageModule {

    private SelenideElement submitBtn = selenideElement().$("button[type='submit']");
    private SelenideElement cancelBtn = selenideElement().$("button[type='button'].btn-default");
    private SelenideElement closeBtn = selenideElement().$("button.close");

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
    public void submit() {
        submitBtn.shouldBe(Condition.visible, Condition.enabled);
        submitBtn.click();
        //TODO: something wrong with closing modal form after clicking
        if (submitBtn.isDisplayed() && closeBtn.isDisplayed()) {
            //if a modal form is still open we want just to close the modal form
            closeBtn.click();
        }
    }

    @Step("Click cancel")
    public void cancel() {
        cancelBtn.shouldBe(Condition.visible, Condition.enabled);
        cancelBtn.click();
        if (cancelBtn.isDisplayed()) {
            cancelBtn.click();
        }
    }

    @Step("Click close")
    public void close() {
        closeBtn.shouldBe(Condition.visible, Condition.enabled);
        closeBtn.click();
        if (closeBtn.isDisplayed()) {
            closeBtn.click();
        }
    }
}
