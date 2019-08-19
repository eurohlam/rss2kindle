package org.roag.pages;

import com.codeborne.selenide.Condition;
import org.roag.pages.modules.SideBar;
import org.roag.pages.modules.HeaderNavigationBar;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by eurohlam on 17/08/19.
 */
abstract class AbstractPage implements Page {

    //TODO: implement navigation via header and sidebar
    private final HeaderNavigationBar headerNavigationBar = new HeaderNavigationBar($("nav#mainNav"));
    private final SideBar sideBar = new SideBar($("aside#sidebar-wrapper"));

    public HeaderNavigationBar header() {
        headerNavigationBar.selenideElement().shouldBe(Condition.visible);
        return headerNavigationBar;
    }

    public SideBar sidebar() {
        sideBar.selenideElement().shouldBe(Condition.visible);
        return sideBar;
    }
}
