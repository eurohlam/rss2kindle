package org.roag.pages.modules;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.codeborne.selenide.Selenide.$;


public class EditSubscribersForm extends AbstractPageModule {

    private PageModuleCollection<SubscriberRecord> subscriberList = new PageModuleCollection<>(
            selenideElement().$$x(".//table/tbody/tr"), SubscriberRecord::new);

    private ModalForm suspendModalForm = new ModalForm($("div#suspendModal"));
    private ModalForm resumeModalForm = new ModalForm($("div#resumeModal"));
    private ModalForm removeModalForm = new ModalForm($("div#removeModal"));
    private UpdateSubscriberModalForm updateModalForm = new UpdateSubscriberModalForm($("div#updateModal"));

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

    @Step("Get subscribers list")
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

    @Step("Update subscriber {subscriber}")
    public EditSubscribersForm updateSubscriber(String subscriber, Consumer<UpdateSubscriberModalForm> consumer) {
        subscriberList.findBy(Condition.text(subscriber)).clickUpdate();
        updateModalForm.shouldBe(Condition.visible);
        updateModalForm.shouldHave(Condition.text(subscriber));
        consumer.accept(updateModalForm);
        return this;
    }

    @Step("Navigate to details page for subscriber: {subscriber}")
    public void navigateToSubscriberDetails(String subscriber) {
        subscriberList.findBy(Condition.text(subscriber)).navigateToSubscriberDetails();
    }
}
