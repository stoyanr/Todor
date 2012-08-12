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

package com.stoyanr.todo.client.util;

import java.util.Date;

import com.google.gwt.storage.client.Storage;

public class LocalStorage {

    private final Storage storage = null; // Storage.getLocalStorageIfSupported();

    public boolean isPresent() {
        return (storage != null);
    }

    public String getStringValue(String key) {
        assert (key != null);
        String result = "";
        if (storage != null) {
            String s = storage.getItem(key);
            if (s != null) {
                result = s;
            }
        }
        return result;
    }

    public void setStringValue(String key, String value) {
        assert (key != null);
        assert (value != null && !value.isEmpty());
        if (storage != null) {
            storage.setItem(key, value);
        }
    }

    public long getLongValue(String key) {
        String stringValue = getStringValue(key);
        return (!stringValue.isEmpty())? Long.valueOf(stringValue) : 0;
    }

    public void setLongValue(String key, long value) {
        setStringValue(key, String.valueOf(value));
    }

    public boolean getBooleanValue(String key) {
        String stringValue = getStringValue(key);
        return (!stringValue.isEmpty())? Boolean.valueOf(stringValue) : false;
    }

    public void setBooleanValue(String key, boolean value) {
        setStringValue(key, String.valueOf(value));
    }

    public Date getDateValue(String key) {
        return new Date(getLongValue(key));
    }

    public void setDateValue(String key, Date value) {
        setLongValue(key, value.getTime());
    }

    public void removeValue(String key) {
        assert (key != null);
        if (storage != null) {
            storage.removeItem(key);
        }
    }

    public void clearValues() {
        if (storage != null) {
            storage.clear();
        }
    }

}
