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

import static com.stoyanr.todo.client.utils.TestUtils.ITEM_0;
import static com.stoyanr.todo.client.utils.TestUtils.ITEM_1;
import static com.stoyanr.todo.client.utils.TestUtils.NOW;
import static com.stoyanr.todo.client.utils.TestUtils.THEN;
import static com.stoyanr.todo.client.utils.TestUtils.U0;
import static com.stoyanr.todo.client.utils.TestUtils.U1;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.junit.client.GWTTestCase;
import com.stoyanr.todo.client.utils.TestUtils;
import com.stoyanr.todo.model.Document;
import com.stoyanr.todo.model.Item;

public class JsonSerializerGWTTest extends GWTTestCase {
    
    JsonSerializer ser;
    
    // @formatter:off
    private static Object[][] PARAMETERS = new Object[][] { 
        { new Document(U0, new ArrayList<Item>(), THEN) }, 
        { new Document(U0, Arrays.asList(ITEM_0), NOW) }, 
        { new Document(U1, Arrays.asList(ITEM_1), NOW) }, 
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
