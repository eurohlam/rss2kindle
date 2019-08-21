package org.roag.pages;

import com.codeborne.selenide.Condition;
import org.roag.pages.modules.SideBar;
import org.roag.pages.modules.MenuBar;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by eurohlam on 17/08/19.
 */
abstract class AbstractPage implements Page {

    private final MenuBar menuBar = new MenuBar($("nav#mainNav"));
    private final SideBar sideBar = new SideBar($("aside#sidebar-wrapper"));

    public MenuBar menubar() {
        menuBar.selenideElement().shouldBe(Condition.visible);
        return menuBar;
    }

    public SideBar sidebar() {
        sideBar.selenideElement().shouldBe(Condition.visible);
        return sideBar;
    }
}
