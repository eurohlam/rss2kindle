package org.roag.pages.modules;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public class PageModuleCollection<T extends PageModule> implements Collection<T> {

    private Supplier<ElementsCollection> selector;
    private Function<SelenideElement, T> itemFunction;

    public PageModuleCollection(final ElementsCollection selector, final Function<SelenideElement, T> itemFunction) {
        this(() -> selector, itemFunction);
    }

    public PageModuleCollection(final Supplier<ElementsCollection> selector, final Function<SelenideElement, T> itemFunction) {
        this.selector = selector;
        this.itemFunction = itemFunction;
    }

    public T findBy(Condition condition) {
        return itemFunction.apply(selector.get().findBy(condition));
    }

    public T get(int index) {
        return itemFunction.apply(selector.get().get(index));
    }


    public T first() {
        return itemFunction.apply(selector.get().first());
    }

    public T last() {
        return itemFunction.apply(selector.get().last());
    }

    public List<String> getTexts() {
        return selector.get().texts();
    }

    public PageModuleCollection<T> shouldHave(CollectionCondition condition) {
        selector.get().shouldHave(condition);
        return this;
    }

    public PageModuleCollection<T> shouldHaveSize(int expectedSize) {
        selector.get().shouldHaveSize(expectedSize);
        return this;
    }

    public PageModuleCollection<T> shouldBe(CollectionCondition condition) {
        selector.get().shouldBe(condition);
        return this;
    }

    @Override
    public int size() {
        return selector.get().size();
    }

    @Override
    public boolean isEmpty() {
        return selector.get().isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return false;
    }

    @Override
    @SuppressWarnings("PMD")
    public Iterator<T> iterator() {
        final Iterator<SelenideElement> iterator = selector.get().iterator();
        return new Iterator<T>() {
            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public T next() {
                SelenideElement element = iterator.next();
                return itemFunction.apply(element);
            }
        };
    }

    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean add(T t) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }
}
