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
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.jdo.JDOHelper;
import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.stoyanr.todo.client.ItemsService;
import com.stoyanr.todo.model.Item;
import com.stoyanr.todo.model.Document;

@SuppressWarnings("serial")
public class ItemsServiceImpl extends RemoteServiceServlet implements
    ItemsService {

    private static final PersistenceManagerFactory PMF = JDOHelper
        .getPersistenceManagerFactory("transactions-optional");

    @Override
    public Item[] loadItems() {
        List<Item> items = new ArrayList<Item>();
        User user = getUser();
        assert (user != null);
        Document doc = getPersistedDocument(user.getNickname());
        if (doc != null) {
            items = doc.getItems();
            assert (items != null);
        }
        return items.toArray(new Item[items.size()]);
    }

    @Override
    public Date saveItems(Item[] items) throws IllegalArgumentException {
        escapeItemTexts(items);
        User user = getUser();
        assert (user != null);
        Document doc = getPersistedDocument(user.getNickname());
        List<Item> itemsx = Arrays.asList(items);
        Date lastSaved = new Date();
        if (doc != null) {
            doc.setItems(itemsx);
            doc.setLastSaved(lastSaved);
        } else {
            doc = new Document(user.getNickname(), itemsx, lastSaved);
        }
        persistDocument(doc);
        return lastSaved;
    }

    @Override
    public Date getLastSaved() {
        Date lastSaved = new Date(0);
        User user = getUser();
        assert (user != null);
        Document doc = getPersistedDocument(user.getNickname());
        if (doc != null) {
            lastSaved = doc.getLastSaved();
            assert (lastSaved != null);
        }
        return lastSaved;
    }

    private static User getUser() {
        UserService userService = UserServiceFactory.getUserService();
        assert (userService != null);
        return userService.getCurrentUser();
    }

    private static void persistDocument(Document doc) {
        PersistenceManager pm = PMF.getPersistenceManager();
        try {
            pm.makePersistent(doc);
        } finally {
            pm.close();
        }
    }

    private static Document getPersistedDocument(String userId) {
        PersistenceManager pm = PMF.getPersistenceManager();
        Document doc = null;
        try {
            Document docx = pm.getObjectById(Document.class, userId);
            assert (docx != null);
            doc = pm.detachCopy(docx);
        } catch (JDOObjectNotFoundException e) {
            // Do nothing
        } finally {
            pm.close();
        }
        return doc;
    }

    private static void escapeItemTexts(Item[] items) {
        for (Item item : items) {
            item.setText(escapeHtml(item.getText()));
        }
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
