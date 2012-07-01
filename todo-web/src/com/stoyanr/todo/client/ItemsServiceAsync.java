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

import java.util.Date;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.stoyanr.todo.model.Item;

public interface ItemsServiceAsync {

    void loadItems(AsyncCallback<Item[]> callback)
        throws IllegalArgumentException;

    void saveItems(Item[] items, AsyncCallback<Date> callback);

    void getLastSaved(AsyncCallback<Date> callback);
}
