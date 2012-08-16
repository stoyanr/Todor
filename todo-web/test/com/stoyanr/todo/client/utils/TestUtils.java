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

package com.stoyanr.todo.client.utils;

import static junit.framework.Assert.assertEquals;

import java.util.Date;

import com.stoyanr.todo.model.Document;
import com.stoyanr.todo.model.Item;
import com.stoyanr.todo.model.Item.Priority;
import com.stoyanr.todo.model.Item.Status;

public class TestUtils {

    // Module name
    public static final String MODULE_NAME = "com.stoyanr.todo.Todo";
    
    // User names
    public static final String U0 = "me";
    public static final String U1 = "you";
    
    // Date constants
    public static final Date NOW = new Date();
    public static final Date THEN = new Date(0);
    
    // Item objects
    // @formatter:off
    public static final Item ITEM_0 = new Item(null, 0, "xxx", Priority.MEDIUM, Status.NEW, NOW, NOW);
    public static final Item ITEM_1 = new Item("abcd1234", 1, "!@#$%^&*()_+{}|[]\\:\";'<>,.?/+-_", Priority.HIGH, Status.IN_PROGRESS, NOW, NOW);
    public static final Item ITEM_2 = new Item(null, 2, "abc", Priority.HIGH, Status.IN_PROGRESS, NOW, NOW);
    // @formatter:on

    public static void assertDocumentEquals(Document expected, Document actual,
        boolean compareItems) {
        assertEquals(expected.getUserId(), actual.getUserId());
        assertEquals(expected.getLastSaved(), actual.getLastSaved());
        if (compareItems) {
            assertEquals(expected.getItems().size(), actual.getItems().size());
            for (int i = 0; i < actual.getItems().size(); i++) {
                assertItemEquals(expected.getItems().get(i), actual.getItems()
                    .get(i));
            }
        }
    }

    public static void assertItemEquals(Item expected, Item actual) {
        assertEquals(expected.getKey(), actual.getKey());
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getText(), actual.getText());
        assertEquals(expected.getPriority(), actual.getPriority());
        assertEquals(expected.getStatus(), actual.getStatus());
    }
    

}
