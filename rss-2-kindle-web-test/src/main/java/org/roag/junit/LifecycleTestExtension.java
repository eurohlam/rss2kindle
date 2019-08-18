package org.roag.junit;

import org.junit.jupiter.api.extension.*;
import org.roag.config.Config;
import org.roag.pages.LoginPage;
import org.roag.pages.PageUtils;
import org.roag.pages.ProfilePage;

import static org.roag.pages.PageUtils.at;
import static org.roag.pages.PageUtils.to;

/**
 * Created by eurohlam on 18/08/19.
 */
public class LifecycleTestExtension implements BeforeEachCallback, AfterEachCallback {

    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        to(LoginPage.class).loginWith(Config.credentials());
        at(ProfilePage.class);
    }

    @Override
    public void afterEach(ExtensionContext extensionContext) throws Exception {
        PageUtils.closeWebDriver();
    }
}
