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

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;
import com.stoyanr.todo.client.ItemsServiceAsync;
import com.stoyanr.todo.client.view.ItemsView;
import com.stoyanr.todo.model.Item;

public class ItemsPresenter implements Presenter, ItemsView.Presenter<Item> {

    private final ItemsServiceAsync svc;
    private final HandlerManager eventBus;
    private final ItemsView<Item> view;
    private ItemsData data;

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
            data.addItem(itemText);
        }
    }

    @Override
    public void delete(int index, Item item) {
        data.deleteItem(item, index);
    }

    @Override
    public void clearAll() {
        data.clearItems();
    }

    @Override
    public String getText(Item item) {
        return item.getText();
    }

    @Override
    public void updateText(Item item, String value) {
        data.updateItem(item, value);
    }

    @Override
    public void save() {
        saveItemsToServer();
    }

    private void initializeItems() {
        data = new ItemsData(view.getData());
        svc.getLastSaved(new InitializeAsyncCallback());
    }

    final class InitializeAsyncCallback implements AsyncCallback<Date> {

        @Override
        public void onFailure(Throwable caught) {
        }

        @Override
        public void onSuccess(Date result) {
            if (result.after(data.getLastSaved()) && result.after(new Date(0))) {
                loadItemsFromServer();
                data.setLastSaved(result);
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
            data.setItemsFromArray(result);
            data.setDirty(false);
        }
    }

    private void saveItemsToServer() {
        if (data.isDirty()) {
            svc.saveItems(data.getItemsAsArray(), new SaveAsyncCallback());
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
            data.setDirty(false);
            data.setLastSaved(result);
            view.onSaveSuccess();
        }
    }

}
