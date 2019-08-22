package org.roag.pages.modules;

import com.codeborne.selenide.ElementsCollection;

import java.util.ArrayList;
import java.util.function.Supplier;

public class PageModuleCollection<T extends PageModule> extends ArrayList<T> {

    public PageModuleCollection(Supplier<ElementsCollection> supplier) {
    }
}
