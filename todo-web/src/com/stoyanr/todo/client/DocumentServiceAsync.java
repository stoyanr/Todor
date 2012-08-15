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

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.stoyanr.todo.model.Document;

public interface DocumentServiceAsync {

    void loadDocument(AsyncCallback<Document> callback)
        throws IllegalArgumentException;

    void saveDocument(Document document, AsyncCallback<Document> callback);
}
