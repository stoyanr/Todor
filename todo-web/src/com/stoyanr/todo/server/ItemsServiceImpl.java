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

package com.stoyanr.todo.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.stoyanr.todo.client.ItemsService;
import com.stoyanr.todo.model.Item;
import com.stoyanr.todo.model.Item.Priority;
import com.stoyanr.todo.model.Item.Status;

@SuppressWarnings("serial")
public class ItemsServiceImpl extends RemoteServiceServlet implements
    ItemsService {

    private final List<Item> items = new ArrayList<Item>();
    private Date lastSaved = new Date(0);

    @Override
    public Item[] loadItems() {
        return this.items.toArray(new Item[] {});
    }

    @Override
    public Date saveItems(Item[] items) throws IllegalArgumentException {
        this.items.clear();
        for (Item item : items) {
            this.items.add(new Item(item.getId(), escapeHtml(item.getText()), 
                Priority.MEDIUM, Status.NEW));
        }
        lastSaved = new Date();
        return lastSaved;
    }

    @Override
    public Date getLastSaved() {
        return lastSaved;
    }

    /**
     * Escape an html string. Escaping data received from the client helps to
     * prevent cross-site script vulnerabilities.
     * 
     * @param html the html string to escape
     * @return the escaped string
     */
    private static String escapeHtml(String html) {
        if (html == null) {
            return null;
        }
        return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;")
            .replaceAll(">", "&gt;");
    }

}
