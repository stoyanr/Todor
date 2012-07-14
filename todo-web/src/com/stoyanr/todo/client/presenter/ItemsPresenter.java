/*
 * $Id: $
 *
 * Copyright (C) 2012 Stoyan Rachev (stoyanr@gmail.com)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation; either version 2, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 */

package com.stoyanr.todo.client.presenter;

import java.util.Date;
import java.util.List;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;
import com.stoyanr.todo.client.ItemsServiceAsync;
import com.stoyanr.todo.client.view.ItemsView;
import com.stoyanr.todo.model.Item;

public class ItemsPresenter implements Presenter, ItemsView.Presenter<Item> {

    private static final String ITEM_KEY = "todo-item-";
    private static final String NEXT_ID_KEY = "todo-count";
    private static final String DIRTY_KEY = "todo-dirty";
    private static final String LAST_SAVED_KEY = "todo-last-saved";

    private final ItemsServiceAsync svc;
    private final HandlerManager eventBus;
    private final ItemsView<Item> view;

    private List<Item> items;
    private int nextId;
    private boolean dirty;
    private Date lastSaved;

    private final Storage storage = Storage.getLocalStorageIfSupported();

    public ItemsPresenter(ItemsServiceAsync svc, HandlerManager eventBus,
        ItemsView<Item> view) {
        this.svc = svc;
        this.eventBus = eventBus;
        this.view = view;
        this.view.setPresenter(this);
    }

    @Override
    public void go(final HasWidgets container) {
        container.clear();
        container.add(view.asWidget());
        initializeItems();
    }

    @Override
    public void add(String itemText) {
        if (!itemText.isEmpty()) {
            addItem(itemText);
            setDirty(true);
        }
    }

    @Override
    public void delete(int index, Item item) {
        deleteItem(item, index);
        setDirty(true);
    }

    @Override
    public void clearAll() {
        clearItems();
        setDirty(true);
    }

    @Override
    public String getText(Item item) {
        return item.getText();
    }

    @Override
    public void updateText(Item item, String value) {
        updateItem(item, value);
        setDirty(true);
    }

    @Override
    public void save() {
        saveItemsToServer();
    }

    private void initializeItems() {
        items = view.getData();
        nextId = 0;
        dirty = false;
        lastSaved = new Date(0);
        svc.getLastSaved(new InitializeAsyncCallback());
    }

    final class InitializeAsyncCallback implements AsyncCallback<Date> {

        @Override
        public void onFailure(Throwable caught) {
            loadStateFromLocalStorage();
        }

        @Override
        public void onSuccess(Date result) {
            Date ls = new Date(getIntegerValue(LAST_SAVED_KEY));
            if (result.compareTo(ls) <= 0) {
                loadStateFromLocalStorage();
            } else if (result.after(new Date(0))) {
                loadItemsFromServer();
                lastSaved = result;
            }
        }
    }

    private void loadItemsFromServer() {
        svc.loadItems(new LoadAsyncCallback());
    }

    final class LoadAsyncCallback implements AsyncCallback<Item[]> {

        @Override
        public void onFailure(Throwable caught) {
        }

        @Override
        public void onSuccess(Item[] result) {
            loadItemsFromArray(result);
            dirty = false;
            saveStateToLocalStorage();
        }
    }

    private void saveItemsToServer() {
        if (dirty) {
            svc.saveItems(saveItemsToArray(), new SaveAsyncCallback());
        } else {
            view.onSaveSuccess();
        }
    }

    final class SaveAsyncCallback implements AsyncCallback<Date> {

        @Override
        public void onFailure(Throwable caught) {
            view.onSaveFailure();
        }

        @Override
        public void onSuccess(Date result) {
            setDirty(false);
            setLastSaved(result);
            view.onSaveSuccess();
        }
    }

    private void addItem(String text) {
        items.add(new Item(nextId, text));
        setStringValue(ITEM_KEY + nextId, text);
        nextId++;
        setIntegerValue(NEXT_ID_KEY, nextId);
    }

    private void updateItem(Item item, String text) {
        item.setText(text);
        setStringValue(ITEM_KEY + item.getId(), text);
    }

    private void deleteItem(Item item, int index) {
        items.remove(index);
        removeValue(ITEM_KEY + item.getId());
    }

    private void clearItems() {
        items.clear();
        clearValues();
    }

    private void setDirty(boolean value) {
        dirty = value;
        setBooleanValue(DIRTY_KEY, dirty);
    }

    private void setLastSaved(Date date) {
        lastSaved = date;
        setIntegerValue(LAST_SAVED_KEY, date.getTime());
    }

    private void loadItemsFromArray(Item[] itemsArray) {
        items.clear();
        int maxId = 0;
        for (Item item : itemsArray) {
            items.add(item);
            maxId = Math.max(maxId, item.getId());
        }
        nextId = maxId + 1;
    }

    private Item[] saveItemsToArray() {
        return items.toArray(new Item[items.size()]);
    }

    private void loadStateFromLocalStorage() {
        if (storage != null) {
            items.clear();
            nextId = (int) getIntegerValue(NEXT_ID_KEY);
            for (int i = 0; i < nextId; i++) {
                String value = getStringValue(ITEM_KEY + i);
                if (!value.isEmpty()) {
                    items.add(new Item(i, value));
                }
            }
            dirty = getBooleanValue(DIRTY_KEY);
            lastSaved.setTime(getIntegerValue(LAST_SAVED_KEY));
        }
    }

    private void saveStateToLocalStorage() {
        if (storage != null) {
            clearValues();
            for (int i = 0; i < items.size(); i++) {
                Item item = items.get(i);
                setStringValue(ITEM_KEY + item.getId(), item.getText());
            }
            setIntegerValue(NEXT_ID_KEY, nextId);
            setBooleanValue(DIRTY_KEY, dirty);
            setIntegerValue(LAST_SAVED_KEY, lastSaved.getTime());
        }
    }

    private String getStringValue(String key) {
        assert (key != null);
        String result = "";
        if (storage != null) {
            String s = storage.getItem(key);
            if (s != null) {
                result = s;
            }
        }
        return result;
    }

    private void setStringValue(String key, String value) {
        assert (key != null);
        assert (value != null && !value.isEmpty());
        if (storage != null) {
            storage.setItem(key, value);
        }
    }

    private long getIntegerValue(String key) {
        assert (key != null);
        long result = 0;
        if (storage != null) {
            String s = storage.getItem(key);
            if (s != null) {
                result = Long.valueOf(s);
            }
        }
        return result;
    }

    private void setIntegerValue(String key, long value) {
        assert (key != null);
        if (storage != null) {
            storage.setItem(key, String.valueOf(value));
        }
    }

    private boolean getBooleanValue(String key) {
        assert (key != null);
        boolean result = false;
        if (storage != null) {
            String s = storage.getItem(key);
            if (s != null) {
                result = Boolean.valueOf(s);
            }
        }
        return result;
    }

    private void setBooleanValue(String key, boolean value) {
        assert (key != null);
        if (storage != null) {
            storage.setItem(key, String.valueOf(value));
        }
    }

    private void removeValue(String key) {
        assert (key != null);
        if (storage != null) {
            storage.removeItem(key);
        }
    }

    private void clearValues() {
        if (storage != null) {
            storage.clear();
        }
    }

}
