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

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.ui.RootPanel;

public class Todo implements EntryPoint {

    private final DocumentServiceAsync itemsSvc = GWT.create(DocumentService.class);
    private final LoginServiceAsync loginSvc = GWT.create(LoginService.class);
    private final Storage storage = Storage.getLocalStorageIfSupported();

    @Override
    public void onModuleLoad() {
        AppController appCtrl = new AppController(itemsSvc, loginSvc, storage);
        appCtrl.go(RootPanel.get());
    }
}
