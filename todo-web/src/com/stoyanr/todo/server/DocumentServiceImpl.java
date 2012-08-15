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
import com.stoyanr.todo.client.DocumentService;
import com.stoyanr.todo.model.Document;
import com.stoyanr.todo.model.Item;

@SuppressWarnings("serial")
public class DocumentServiceImpl extends RemoteServiceServlet implements
    DocumentService {

    private static final PersistenceManagerFactory PMF = JDOHelper
        .getPersistenceManagerFactory("transactions-optional");

    @Override
    public Document loadDocument() {
        User user = getUser();
        assert (user != null);
        Document doc = getPersistedDocument(user.getUserId());
        return doc;
    }

    @Override
    public Document saveDocument(Document document) throws IllegalArgumentException {
        escapeItemTexts(document.getItems());
        User user = getUser();
        assert (user != null);
        if (!user.getUserId().equals(document.getUserId()))
            throw new IllegalArgumentException("Received document for wrong user.");
        Document doc = getPersistedDocument(user.getUserId());
        Date lastSaved = new Date();
        if (doc != null) {
            doc.setItems(document.getItems());
            doc.setLastSaved(lastSaved);
        } else {
            doc = document;
        }
        persistDocument(doc);
        return doc;
    }

    private static User getUser() {
        UserService userService = UserServiceFactory.getUserService();
        assert (userService != null);
        return userService.getCurrentUser();
    }

    private static void persistDocument(Document document) {
        PersistenceManager pm = PMF.getPersistenceManager();
        try {
            pm.makePersistent(document);
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

    private static void escapeItemTexts(List<Item> items) {
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
