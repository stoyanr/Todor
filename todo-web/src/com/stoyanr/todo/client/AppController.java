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

package com.stoyanr.todo.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.HasWidgets;
import com.stoyanr.todo.client.presenter.ItemsPresenter;
import com.stoyanr.todo.client.presenter.Presenter;
import com.stoyanr.todo.client.view.ItemsView;
import com.stoyanr.todo.client.view.ItemsViewImpl;
import com.stoyanr.todo.model.Item;

public class AppController implements Presenter, ValueChangeHandler<String> {

    private final ItemsServiceAsync svc;
    private final HandlerManager eventBus;
    private HasWidgets container;
    private ItemsViewImpl<Item> itemsView = null;

    public AppController(ItemsServiceAsync svc, HandlerManager eventBus) {
        this.svc = svc;
        this.eventBus = eventBus;
        bind();
    }

    private void bind() {
        History.addValueChangeHandler(this);
    }

    @Override
    public void go(final HasWidgets container) {
        this.container = container;
        if ("".equals(History.getToken())) {
            History.newItem("todo");
        } else {
            History.fireCurrentHistoryState();
        }
    }

    @Override
    public void onValueChange(ValueChangeEvent<String> event) {
        String token = event.getValue();
        if (token != null) {
            if (token.equals("todo")) {
                GWT.runAsync(new TodoRunAsyncCallback());
            }
        }
    }

    private ItemsView<Item> getItemsView() {
        if (itemsView == null) {
            itemsView = new ItemsViewImpl<Item>(ItemsPresenter.getPriorityNames(),
                ItemsPresenter.getStatusNames());
        }
        return itemsView;
    }

    final class TodoRunAsyncCallback implements RunAsyncCallback {

        @Override
        public void onFailure(Throwable reason) {
        }

        @Override
        public void onSuccess() {
            new ItemsPresenter(svc, eventBus, getItemsView()).go(container);
        }
    }
}
