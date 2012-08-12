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

package com.stoyanr.todo.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@SuppressWarnings("serial")
@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "true")
public class Document implements Serializable {

    @PrimaryKey
    @Persistent
    private String userId;
    @Persistent(defaultFetchGroup = "true")
    private List<Item> items;
    @Persistent
    private Date lastSaved;

    public Document() {
        this("unknown", new ArrayList<Item>(), new Date(0));
    }

    public Document(String userId, List<Item> items, Date lastSaved) {
        assert (userId != null);
        assert (items != null);
        assert (lastSaved != null);
        this.userId = userId;
        this.items = items;
        this.lastSaved = lastSaved;
    }

    public String getUserId() {
        return userId;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        assert (items != null);
        this.items = items;
    }

    public Date getLastSaved() {
        return lastSaved;
    }

    public void setLastSaved(Date lastSaved) {
        assert (lastSaved != null);
        this.lastSaved = lastSaved;
    }
    
    @Override
    public String toString() {
        return userId + ":" + items.toString() + ":" + lastSaved.toString();
    }

}
