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
import java.util.List;

import com.stoyanr.todo.client.util.LocalStorage;
import com.stoyanr.todo.model.Document;
import com.stoyanr.todo.model.Item;
import com.stoyanr.todo.model.Item.Priority;
import com.stoyanr.todo.model.Item.Status;

public class DocumentData {

    private static final String DOCUMENT_KEY = "document";
    private static final String ITEM_KEY = "item-";
    private static final String NEXT_ID_KEY = "count";
    private static final String DIRTY_KEY = "dirty";

    private final LocalStorage storage;
    private final Document document;
    private final JsonSerializer ser;
    private long nextId;
    private boolean dirty;

    public DocumentData(Document document, LocalStorage storage,
        JsonSerializer ser) {
        assert (document != null);
        assert (storage != null);
        assert (ser != null);
        this.document = document;
        this.storage = storage;
        this.ser = ser;
        this.nextId = 0;
        this.dirty = false;
        saveDocumentToStorageOnCreation();
        loadFromStorage();
    }

    private void saveDocumentToStorageOnCreation() {
        if (!loadDocumentFromStorage()) {
            saveDocumentToStorage();
        }
    }

    public Document getDocument() {
        List<Item> items = new ArrayList<Item>();
        items.addAll(document.getItems());
        return new Document(document.getUserId(), items,
            document.getLastSaved());
    }

    public void setDocument(Document document) {
        assert (document != null);
        assert (this.document.getUserId().equals(document.getUserId()));
        this.document.getItems().clear();
        this.document.getItems().addAll(document.getItems());
        this.document.setLastSaved(document.getLastSaved());
        nextId = getMaxId(document) + 1;
        dirty = true;
        saveToStorage();
    }

    private static long getMaxId(Document document) {
        long maxId = 0;
        for (Item item : document.getItems()) {
            maxId = Math.max(maxId, item.getId());
        }
        return maxId;
    }

    public void addItem(String text) {
        assert (text != null);
        assert (!hasItemWithId(nextId));
        Item item = new Item(null, nextId, text, Priority.MEDIUM, Status.NEW);
        document.getItems().add(item);
        saveItemToStorage(item);
        nextId++;
        storage.setLongValue(getKey(NEXT_ID_KEY), nextId);
        setDirty(true);
    }

    public void updateItem(Item item, String text) {
        assert (item != null && hasItem(item));
        assert (text != null);
        item.setText(text);
        saveItemToStorage(item);
        setDirty(true);
    }

    public void updateItem(Item item, Priority prio) {
        assert (item != null && hasItem(item));
        assert (prio != null);
        item.setPriority(prio);
        saveItemToStorage(item);
        setDirty(true);
    }

    public void updateItem(Item item, Status status) {
        assert (item != null && hasItem(item));
        assert (status != null);
        item.setStatus(status);
        saveItemToStorage(item);
        setDirty(true);
    }

    public void deleteItem(Item item, int index) {
        assert (item != null && index < document.getItems().size() && document
            .getItems().get(index) == item);
        document.getItems().remove(index);
        deleteItemFromStorage(item);
        setDirty(true);
    }

    public void clearItems() {
        document.getItems().clear();
        storage.clearValues();
        setDirty(true);
    }

    public long getNextId() {
        return nextId;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean value) {
        dirty = value;
        storage.setBooleanValue(getKey(DIRTY_KEY), dirty);
    }

    private void loadFromStorage() {
        if (storage.isPresent()) {
            loadDocumentFromStorage();
            nextId = storage.getLongValue(getKey(NEXT_ID_KEY));
            loadDocumentItemsFromStorage();
            dirty = storage.getBooleanValue(getKey(DIRTY_KEY));
        }
    }

    private void saveToStorage() {
        if (storage.isPresent()) {
            storage.clearValues();
            saveDocumentToStorage();
            saveDocumentItemsToStorage();
            storage.setLongValue(getKey(NEXT_ID_KEY), nextId);
            storage.setBooleanValue(getKey(DIRTY_KEY), dirty);
        }
    }

    private boolean loadDocumentFromStorage() {
        boolean result = false;
        String value = storage.getStringValue(getKey(DOCUMENT_KEY));
        if (!value.isEmpty()) {
            Document doc = ser.getDocument(value);
            assert (document.getUserId().equals(doc.getUserId()));
            document.setLastSaved(doc.getLastSaved());
            result = true;
        }
        return result;
    }

    private void saveDocumentToStorage() {
        String json = ser.toString(document);
        storage.setStringValue(getKey(DOCUMENT_KEY), json);
    }

    private void saveDocumentItemsToStorage() {
        for (int i = 0; i < document.getItems().size(); i++) {
            saveItemToStorage(document.getItems().get(i));
        }
    }

    private void loadDocumentItemsFromStorage() {
        document.getItems().clear();
        for (long i = 0; i < nextId; i++) {
            Item item = loadItemFromStorage(i);
            if (item != null) {
                document.getItems().add(item);
            }
        }
    }

    private Item loadItemFromStorage(long id) {
        Item item = null;
        String value = storage.getStringValue(getKey(ITEM_KEY) + id);
        if (!value.isEmpty()) {
            item = ser.getItem(value);
        }
        return item;
    }

    private void saveItemToStorage(Item item) {
        String json = ser.toString(item);
        storage.setStringValue(getKey(ITEM_KEY) + item.getId(), json);
    }

    private void deleteItemFromStorage(Item item) {
        storage.removeValue(getKey(ITEM_KEY) + item.getId());
    }

    private String getKey(String key) {
        return "todo-" + document.getUserId() + "-" + key;
    }

    private boolean hasItem(Item item) {
        return document.getItems().contains(item);
    }

    private boolean hasItemWithId(long id) {
        boolean result = false;
        for (Item item : document.getItems()) {
            if (item.getId() == id) {
                result = true;
                break;
            }
        }
        return result;
    }

}
