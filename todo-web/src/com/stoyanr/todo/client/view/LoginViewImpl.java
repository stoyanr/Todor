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

package com.stoyanr.todo.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.stoyanr.todo.model.UserAccount;

public class LoginViewImpl extends Composite {

    @UiTemplate("LoginView.ui.xml")
    interface LoginViewUiBinder extends UiBinder<Widget, LoginViewImpl> {
    }

    private static LoginViewUiBinder uiBinder = GWT
        .create(LoginViewUiBinder.class);

    @UiField
    Anchor signInLink;

    public LoginViewImpl(UserAccount userAccount) {
        initWidget(uiBinder.createAndBindUi(this));
        signInLink.setHref(userAccount.getLoginUrl());
    }
}
