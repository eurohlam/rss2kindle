package org.roag.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.roag.pages.modules.NewSubscriberForm;

import java.util.function.Consumer;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by eurohlam on 17/08/19.
 */
public class SubscribersPage extends AbstractPage {

    private NewSubscriberForm newSubscriberForm = new NewSubscriberForm($("form#new_subscriber_form"));
    private SelenideElement newSubscriberLink=$("a#new-tab");
    private SelenideElement editSubscriberLink=$("a#edit-tab");

    @Override
    public String getPath() {
        return "/view/subscribers";
    }

    @Step("Add new subscriber")
    public SubscribersPage addNewSubscriber(Consumer<NewSubscriberForm> consumer) {
        newSubscriberLink.click();
        newSubscriberForm.selenideElement().shouldBe(Condition.visible);
        consumer.accept(newSubscriberForm);
        return this;
    }

}
