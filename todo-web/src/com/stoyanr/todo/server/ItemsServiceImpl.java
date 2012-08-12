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

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.stoyanr.todo.client.ItemsService;
import com.stoyanr.todo.model.Item;

@SuppressWarnings("serial")
public class ItemsServiceImpl extends RemoteServiceServlet implements
    ItemsService {

    private static final PersistenceManagerFactory PMF = JDOHelper
        .getPersistenceManagerFactory("transactions-optional");

    private Date lastSaved = new Date(0);

    @Override
    public Item[] loadItems() {
        PersistenceManager pm = PMF.getPersistenceManager();
        try {
            return getPersistedItems(pm).toArray(new Item[] {});
        } finally {
            pm.close();
        }
    }

    @Override
    public Date saveItems(Item[] items) throws IllegalArgumentException {
        PersistenceManager pm = PMF.getPersistenceManager();
        try {
            deletePersistedItems(pm);
            for (Item item : items) {
                item.setText(escapeHtml(item.getText()));
                persistItem(pm, item);
            }
        } finally {
            pm.close();
        }
        lastSaved = new Date();
        return lastSaved;
    }

    @Override
    public Date getLastSaved() {
        return lastSaved;
    }

    @SuppressWarnings("unchecked")
    private static List<Item> getPersistedItems(PersistenceManager pm) {
        List<Item> items = new ArrayList<Item>();
        Query q = pm.newQuery(Item.class);
        q.setOrdering("id");
        items = (List<Item>) q.execute();
        return items;
    }

    private static void persistItem(PersistenceManager pm, Item item) {
        pm.makePersistent(item);
    }

    private static void deletePersistedItems(PersistenceManager pm) {
        pm.deletePersistentAll(getPersistedItems(pm));
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
