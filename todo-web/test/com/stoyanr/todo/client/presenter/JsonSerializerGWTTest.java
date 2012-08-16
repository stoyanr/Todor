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

package com.stoyanr.todo.client.presenter;

import java.util.Arrays;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.junit.client.GWTTestCase;
import com.stoyanr.todo.client.util.TestUtils;
import com.stoyanr.todo.model.Document;
import com.stoyanr.todo.model.Item;
import com.stoyanr.todo.model.Item.Priority;
import com.stoyanr.todo.model.Item.Status;

public class JsonSerializerGWTTest extends GWTTestCase {
    
    JsonSerializer ser;
    
    // @formatter:off
    private static Object[][] PARAMETERS = new Object[][] { 
        { new Document("", Arrays.asList(new Item()), new Date(0)) }, 
        { new Document("me", Arrays.asList(new Item(null, 0, "xxx", Priority.MEDIUM, Status.NEW)), new Date()) }, 
        { new Document("you", Arrays.asList(new Item("abcd1234", 1, "!@#$%^&*()_+{}|[]\\:\";'<>,.?/+-_", Priority.HIGH, Status.IN_PROGRESS)), new Date()) }, 
    };
    // @formatter:on
    
    @Override
    public String getModuleName() {
        return TestUtils.MODULE_NAME;
    }
    
    @Before
    public void gwtSetUp() {
        ser = new JsonSerializer();
    }
    
    @Test
    public void testGetDocumentFromJson() {
        for (int i = 0; i < PARAMETERS.length; i++) {
            testGetDocumentFromJson((Document) PARAMETERS[i][0]);
        }
    }

    @Test
    public void testGetItemFromJson() {
        for (int i = 0; i < PARAMETERS.length; i++) {
            Document document = (Document) PARAMETERS[i][0];
            for (Item item : document.getItems()) {
                testGetItemFromJson(item);
            }
        }
    }

    @Test
    public void testGetDocumentFromString() {
        for (int i = 0; i < PARAMETERS.length; i++) {
            testGetDocumentFromString((Document) PARAMETERS[i][0]);
        }
    }

    @Test
    public void testGetItemFromString() {
        for (int i = 0; i < PARAMETERS.length; i++) {
            Document document = (Document) PARAMETERS[i][0];
            for (Item item : document.getItems()) {
                testGetItemFromString(item);
            }
        }
    }
    
    private void testGetDocumentFromJson(Document document) {
        JSONObject json = ser.toJson(document);
        Document documentx = ser.getDocument(json);
        TestUtils.assertDocumentEquals(document, documentx, false);
    }

    private void testGetDocumentFromString(Document document) {
        String json = ser.toString(document);
        Document documentx = ser.getDocument(json);
        TestUtils.assertDocumentEquals(document, documentx, false);
    }

    private void testGetItemFromJson(Item item) {
        JSONObject json = ser.toJson(item);
        Item itemx = ser.getItem(json);
        TestUtils.assertItemEquals(item, itemx);
    }

    private void testGetItemFromString(Item item) {
        String json = ser.toString(item);
        Item itemx = ser.getItem(json);
        TestUtils.assertItemEquals(item, itemx);
    }
}
