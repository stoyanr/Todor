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

import java.util.Date;
import java.util.List;

import com.stoyanr.todo.client.util.LocalStorage;
import com.stoyanr.todo.model.Item;

public class ItemsData {

    private static final String ITEM_KEY = "todo-item-";
    private static final String NEXT_ID_KEY = "todo-count";
    private static final String DIRTY_KEY = "todo-dirty";
    private static final String LAST_SAVED_KEY = "todo-last-saved";

    private List<Item> items;
    private long nextId;
    private boolean dirty;
    private Date lastSaved;

    private final LocalStorage storage = new LocalStorage();

    public ItemsData(List<Item> items) {
        this.items = items;
        this.nextId = 0;
        this.dirty = false;
        this.lastSaved = new Date(0);
        loadFromStorage();
    }

    public void setItemsFromArray(Item[] itemsArray) {
        items.clear();
        long maxId = 0;
        for (Item item : itemsArray) {
            items.add(item);
            maxId = Math.max(maxId, item.getId());
        }
        nextId = maxId + 1;
        dirty = true;
        saveToStorage();
    }

    public Item[] getItemsAsArray() {
        return items.toArray(new Item[items.size()]);
    }

    public void addItem(String text) {
        Item item = new Item(nextId, text);
        items.add(item);
        saveItemToStorage(item);
        nextId++;
        storage.setLongValue(NEXT_ID_KEY, nextId);
        setDirty(true);
    }

    public void updateItem(Item item, String text) {
        item.setText(text);
        saveItemToStorage(item);
        setDirty(true);
    }

    public void deleteItem(Item item, int index) {
        items.remove(index);
        deleteItemFromStorage(item);
        setDirty(true);
    }

    public void clearItems() {
        items.clear();
        storage.clearValues();
        setDirty(true);
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean value) {
        dirty = value;
        storage.setBooleanValue(DIRTY_KEY, dirty);
    }

    public Date getLastSaved() {
        return lastSaved;
    }

    public void setLastSaved(Date date) {
        lastSaved = date;
        storage.setDateValue(LAST_SAVED_KEY, lastSaved);
    }

    private void loadFromStorage() {
        if (storage.isPresent()) {
            items.clear();
            nextId = storage.getLongValue(NEXT_ID_KEY);
            for (long i = 0; i < nextId; i++) {
                Item item = loadItemFromStorage(i);
                if (item != null) {
                    items.add(item);
                }
            }
            dirty = storage.getBooleanValue(DIRTY_KEY);
            lastSaved = storage.getDateValue(LAST_SAVED_KEY);
        }
    }

    private void saveToStorage() {
        if (storage.isPresent()) {
            storage.clearValues();
            for (int i = 0; i < items.size(); i++) {
                saveItemToStorage(items.get(i));
            }
            storage.setLongValue(NEXT_ID_KEY, nextId);
            storage.setBooleanValue(DIRTY_KEY, dirty);
            storage.setDateValue(LAST_SAVED_KEY, lastSaved);
        }
    }

    private Item loadItemFromStorage(long id) {
        Item item = null;
        String value = storage.getStringValue(ITEM_KEY + id);
        if (!value.isEmpty()) {
            item = new Item(id, value);
        }
        return item;
    }

    private void saveItemToStorage(Item item) {
        storage.setStringValue(ITEM_KEY + item.getId(), item.getText());
    }

    private void deleteItemFromStorage(Item item) {
        storage.removeValue(ITEM_KEY + item.getId());
    }

}
