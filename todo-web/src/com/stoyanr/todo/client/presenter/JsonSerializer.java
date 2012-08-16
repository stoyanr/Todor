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

import java.util.ArrayList;
import java.util.Date;

import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.stoyanr.todo.model.Document;
import com.stoyanr.todo.model.Item;
import com.stoyanr.todo.model.Item.Priority;
import com.stoyanr.todo.model.Item.Status;

public class JsonSerializer {

    private static final String NULL = "NULL";
    
    private static final String KEY = "key";
    private static final String ID = "id";
    private static final String TEXT = "text";
    private static final String PRIORITY = "priority";
    private static final String STATUS = "status";
    private static final String USER_ID = "userId";
    private static final String LAST_SAVED = "lastSaved";
    
    public String toString(Item item) {
        return toJson(item).toString();
    }
    
    public Item getItem(String string) {
        return getItem(JSONParser.parseStrict(string).isObject());
    }

    public String toString(Document document) {
        return toJson(document).toString();
    }
    
    public Document getDocument(String string) {
        return getDocument(JSONParser.parseStrict(string).isObject());
    }

    public JSONObject toJson(Item item) {
        JSONObject result = new JSONObject();
        result.put(KEY, new JSONString(toNullableValue(item.getKey())));
        result.put(ID, new JSONNumber(item.getId()));
        result.put(TEXT, new JSONString(item.getText()));
        result.put(PRIORITY, new JSONString(item.getPriority().toString()));
        result.put(STATUS, new JSONString(item.getStatus().toString()));
        return result;
    }

    public Item getItem(JSONObject value) {
        String key = fromNullableValue(value.get(KEY).isString().stringValue());
        long id = (long) value.get(ID).isNumber().doubleValue();
        String text = value.get(TEXT).isString().stringValue();
        Priority priority = Priority.valueOf(value.get(PRIORITY).isString()
            .stringValue());
        Status status = Status.valueOf(value.get(STATUS).isString()
            .stringValue());
        return new Item(key, id, text, priority, status);
    }

    public JSONObject toJson(Document document) {
        JSONObject result = new JSONObject();
        result.put(USER_ID, new JSONString(document.getUserId()));
        result.put(LAST_SAVED, new JSONNumber(document.getLastSaved().getTime()));
        return result;
    }

    public Document getDocument(JSONObject value) {
        String userId = value.get(USER_ID).isString().stringValue();
        Date lastSaved = new Date((long) value.get(LAST_SAVED).isNumber().doubleValue());
        return new Document(userId, new ArrayList<Item>(), lastSaved);
    }

    private String toNullableValue(String value) {
        return (value != null)? value : NULL;
    }

    private String fromNullableValue(String keyValue) {
        return keyValue.equals(NULL)? null : keyValue;
    }

}
