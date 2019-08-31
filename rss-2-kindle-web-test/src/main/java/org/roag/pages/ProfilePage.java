package org.roag.pages;


import com.codeborne.selenide.Condition;
import io.qameta.allure.Step;
import org.junit.jupiter.api.extension.ExtendWith;
import org.roag.junit.LifecycleTestExtension;
import org.roag.pages.modules.PageModuleCollection;
import org.roag.pages.modules.SubscriberRecord;

import static com.codeborne.selenide.Selenide.$$x;

/**
 * Created by eurohlam on 17/08/19.
 */
@ExtendWith(LifecycleTestExtension.class)
public class ProfilePage extends AbstractPage {

    private PageModuleCollection<SubscriberRecord> subscriberList = new PageModuleCollection<>(
            $$x("div[id='subscribers_view']/table/tbody/tr"), SubscriberRecord::new);

    @Override
    public String getPath() {
        return "/view/profile";
    }


    private SubscriberRecord findSubscriberBy(Condition condition) {
        return subscriberList.findBy(condition);
    }

    @Step("Get subscriber by name {name}")
    public SubscriberRecord getSubscriberByName(String name) {
        return findSubscriberBy(Condition.text(name));
    }

    @Step("Get subscriber by email {email}")
    public SubscriberRecord getSubscriberByEmail(String email) {
        return findSubscriberBy(Condition.text(email));
    }

    @Step("Get first subscriber")
    public SubscriberRecord getFirstSubscriber() {
        return subscriberList.first();
    }

    @Step("Get last subscriber")
    public SubscriberRecord getLastSubscriber() {
        return subscriberList.last();
    }

}
