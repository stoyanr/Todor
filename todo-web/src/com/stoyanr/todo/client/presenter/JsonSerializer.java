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

import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.stoyanr.todo.model.Item;
import com.stoyanr.todo.model.Item.Priority;
import com.stoyanr.todo.model.Item.Status;

public class JsonSerializer {

    private static final String STATUS = "status";
    private static final String PRIORITY = "priority";
    private static final String TEXT = "text";
    private static final String ID = "id";
    
    public static String toString(Item item) {
        return toJson(item).toString();
    }
    
    public static Item fromString(String string) {
        return fromJson(JSONParser.parseStrict(string).isObject());
    }

    public static JSONObject toJson(Item item) {
        JSONObject result = new JSONObject();
        result.put(ID, new JSONNumber(item.getId()));
        result.put(TEXT, new JSONString(item.getText()));
        result.put(PRIORITY, new JSONString(item.getPriority().toString()));
        result.put(STATUS, new JSONString(item.getStatus().toString()));
        return result;
    }

    public static Item fromJson(JSONObject value) {
        long id = (long) value.get(ID).isNumber().doubleValue();
        String text = value.get(TEXT).isString().stringValue();
        Priority priority = Priority.valueOf(value.get(PRIORITY).isString()
            .stringValue());
        Status status = Status.valueOf(value.get(STATUS).isString()
            .stringValue());
        return new Item(id, text, priority, status);
    }

}
