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

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@SuppressWarnings("serial")
@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "true")
public class Item implements Serializable {

    public enum Priority {
        HIGH, MEDIUM, LOW
    }

    public enum Status {
        NEW, IN_PROGRESS, FINISHED
    }

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    @Extension(vendorName = "datanucleus", key = "gae.encoded-pk", value = "true")
    private String key;
    @Persistent
    @Extension(vendorName = "datanucleus", key = "gae.pk-name", value = "true")
    private String id;
    @Persistent
    private String text;
    @Persistent
    private Priority priority;
    @Persistent
    private Status status;

    public Item() {
        this(null, -1, "(Empty)", Priority.MEDIUM, Status.NEW);
    }

    public Item(String key, long id, String text, Priority priority,
        Status status) {
        assert (id >= -1);
        assert (text != null);
        assert (priority != null);
        assert (status != null);
        this.key = key;
        this.id = String.valueOf(id);
        this.text = text;
        this.priority = priority;
        this.status = status;
    }

    public String getKey() {
        return key;
    }

    public long getId() {
        return Long.valueOf(id);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        assert (text != null);
        this.text = text;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        assert (priority != null);
        this.priority = priority;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        assert (status != null);
        this.status = status;
    }

    @Override
    public String toString() {
        return id + ":" + text + ":" + priority.toString() + ":"
            + status.toString();
    }

}
