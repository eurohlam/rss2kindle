package org.roag.pages;

import org.junit.jupiter.api.extension.ExtendWith;
import org.roag.junit.LifecycleTestExtension;

/**
 * Created by eurohlam on 17/08/19.
 */
@ExtendWith(LifecycleTestExtension.class)
public class ProfilePage extends AbstractPage {

    @Override
    public String getPath() {
        return "/view/profile";
    }

}
