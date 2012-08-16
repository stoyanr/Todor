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

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.stoyanr.todo.client.presenter.JsonSerializer;
import com.stoyanr.todo.client.util.LocalStorage;
import com.stoyanr.todo.model.Document;
import com.stoyanr.todo.model.Item;

public class MockUtils {

    // @formatter:off
    private static Map<String, Object> stored = new HashMap<String, Object>();
    private static Map<String, Object> serialized = new HashMap<String, Object>();
    // @formatter:on

    public static LocalStorage createMockStorage() {
        LocalStorage storage = mock(LocalStorage.class);
        when(storage.isPresent()).thenReturn(true);
        // @formatter:off
        when(storage.getStringValue(anyString())).thenAnswer(new Answer<String>() {
            public String answer(InvocationOnMock inv) {
                String value = (String) getValue(inv);
                return (value != null)? value : "";
            }
        });
        when(storage.getLongValue(anyString())).thenAnswer(new Answer<Long>() {
            public Long answer(InvocationOnMock inv) {
                Long value = (Long) getValue(inv);
                return (value != null)? value : 0L;
            }
        });
        when(storage.getBooleanValue(anyString())).thenAnswer(new Answer<Boolean>() {
            public Boolean answer(InvocationOnMock inv) {
                Boolean value = (Boolean) getValue(inv);
                return (value != null)? value : false;
            }
        });
        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock inv) {
                return setValue(inv);
            }
        }).when(storage).setStringValue(anyString(), anyString());
        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock inv) {
                return setValue(inv);
            }
        }).when(storage).setLongValue(anyString(), anyLong());
        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock inv) {
                return setValue(inv);
            }
        }).when(storage).setBooleanValue(anyString(), anyBoolean());
        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock inv) {
                stored.remove((String) inv.getArguments()[0]);
                return null;
            }
        }).when(storage).removeValue(anyString());
        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock inv) {
                stored.clear();
                return null;
            }
        }).when(storage).clearValues();
        // @formatter:on
        return storage;
    }

    public static void clearMockStorage() {
        stored.clear();
    }

    private static Void setValue(InvocationOnMock inv) {
        Object[] args = inv.getArguments();
        stored.put((String) args[0], args[1]);
        return null;
    }

    private static Object getValue(InvocationOnMock inv) {
        Object[] args = inv.getArguments();
        return stored.get((String) args[0]);
    }

    public static JsonSerializer createMockSerializer() {
        JsonSerializer ser = mock(JsonSerializer.class);
        // @formatter:off
        when(ser.toString(any(Document.class))).thenAnswer(new Answer<String>() {
            public String answer(InvocationOnMock inv) {
                Document doc = (Document) inv.getArguments()[0];
                serialized.put(doc.getUserId(), doc);
                return doc.getUserId();
            }
        });
        when(ser.toString(any(Item.class))).thenAnswer(new Answer<String>() {
            public String answer(InvocationOnMock inv) {
                Item item = (Item) inv.getArguments()[0];
                String key = String.valueOf(item.getId());
                serialized.put(key, item);
                return key;
            }
        });
        when(ser.getDocument(anyString())).thenAnswer(new Answer<Document>() {
            public Document answer(InvocationOnMock inv) {
                String key = (String) inv.getArguments()[0];
                return (Document) serialized.get(key);
            }
        });
        when(ser.getItem(anyString())).thenAnswer(new Answer<Item>() {
            public Item answer(InvocationOnMock inv) {
                String key = (String) inv.getArguments()[0];
                return (Item) serialized.get(key);
            }
        });
        // @formatter:on
        return ser;
    }

    public static void clearMockSerializer() {
        serialized.clear();
    }
}
