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

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import com.stoyanr.todo.client.presenter.DocumentPresenter;
import com.stoyanr.todo.client.presenter.Presenter;
import com.stoyanr.todo.client.view.ItemsView;
import com.stoyanr.todo.client.view.ItemsViewImpl;
import com.stoyanr.todo.client.view.LoginViewImpl;
import com.stoyanr.todo.model.Item;
import com.stoyanr.todo.model.UserAccount;

public class AppController implements Presenter, ValueChangeHandler<String> {

    private final DocumentServiceAsync itemsSvc;
    private final LoginServiceAsync loginSvc;
    private final HandlerManager eventBus;
    private HasWidgets container;
    private ItemsViewImpl<Item> itemsView = null;
    private LoginViewImpl loginView = null;
    private UserAccount userAccount;

    public AppController(DocumentServiceAsync itemsSvc,
        LoginServiceAsync loginSvc, HandlerManager eventBus) {
        this.itemsSvc = itemsSvc;
        this.loginSvc = loginSvc;
        this.eventBus = eventBus;
        bind();
    }

    private void bind() {
        History.addValueChangeHandler(this);
    }

    @Override
    public void go(final HasWidgets container) {
        this.container = container;
        loginSvc.login(GWT.getHostPageBaseURL(), new LoginAsyncCallback());
    }

    final class LoginAsyncCallback implements AsyncCallback<UserAccount> {

        @Override
        public void onFailure(Throwable error) {
        }

        @Override
        public void onSuccess(UserAccount result) {
            userAccount = result;
            if (userAccount.isLoggedIn()) {
                goMain();
            } else {
                goLogin();
            }
        }

    }

    private void goMain() {
        if ("".equals(History.getToken())) {
            History.newItem("todo");
        } else {
            History.fireCurrentHistoryState();
        }
    }

    private void goLogin() {
        container.add(getLoginView());
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
            List<String> priorityNames = DocumentPresenter.getPriorityNames();
            List<String> statusNames = DocumentPresenter.getStatusNames();
            itemsView = new ItemsViewImpl<Item>(userAccount, priorityNames,
                statusNames);
        }
        return itemsView;
    }

    private Widget getLoginView() {
        if (loginView == null) {
            loginView = new LoginViewImpl(userAccount);
        }
        return loginView;
    }

    final class TodoRunAsyncCallback implements RunAsyncCallback {

        @Override
        public void onFailure(Throwable reason) {
        }

        @Override
        public void onSuccess() {
            new DocumentPresenter(itemsSvc, eventBus, userAccount, getItemsView())
                .go(container);
        }
    }
}
