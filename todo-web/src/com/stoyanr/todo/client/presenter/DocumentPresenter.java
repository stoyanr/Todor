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

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;
import com.stoyanr.todo.client.DocumentServiceAsync;
import com.stoyanr.todo.client.view.ItemsView;
import com.stoyanr.todo.model.Document;
import com.stoyanr.todo.model.Item;
import com.stoyanr.todo.model.Item.Priority;
import com.stoyanr.todo.model.Item.Status;

public class DocumentPresenter implements Presenter, ItemsView.Presenter<Item> {

    private final DocumentServiceAsync svc;
    private final DocumentData data;
    private final ItemsView<Item> view;

    private static final String[] PRIO_NAMES = { "High", "Medium", "Low" };
    private static final String[] STATUS_NAMES = { "New", "In Progress",
        "Finished" };
    
    private static final DateTimeFormat FMT = DateTimeFormat.getFormat("dd/MM/yyyy HH:mm:ss");

    public DocumentPresenter(DocumentServiceAsync svc, DocumentData data,
        ItemsView<Item> view) {
        this.svc = svc;
        this.data = data;
        this.view = view;
        this.view.setPresenter(this);
    }
    
    public DocumentData getData() {
        return data;
    }

    public ItemsView<Item> getView() {
        return view;
    }

    @Override
    public void go(final HasWidgets container) {
        container.clear();
        container.add(view.asWidget());
        initializeDocument();
    }

    @Override
    public void add(String itemText) {
        if (!itemText.isEmpty()) {
            data.addItem(itemText);
        }
    }

    @Override
    public void delete(int index, Item item) {
        data.deleteItem(item, index);
    }

    @Override
    public void clearAll() {
        data.clearItems();
    }

    @Override
    public String getId(Item item) {
        return String.valueOf(item.getId());
    }

    @Override
    public int compareIds(Item o1, Item o2) {
        return (int) (o1.getId() - o2.getId());
    }

    @Override
    public String getText(Item item) {
        return item.getText();
    }

    @Override
    public void updateText(Item item, String value) {
        data.updateItem(item, value);
    }

    @Override
    public String getPriority(Item item) {
        return getPriorityName(item.getPriority());
    }

    @Override
    public void updatePriority(Item item, String value) {
        data.updateItem(item, getPriorityByName(value));
    }

    @Override
    public int comparePriorities(Item o1, Item o2) {
        return (o1.getPriority().compareTo(o2.getPriority()));
    }

    @Override
    public String getStatus(Item item) {
        return getStatusName(item.getStatus());
    }

    @Override
    public void updateStatus(Item item, String value) {
        data.updateItem(item, getStatusByName(value));
    }

    @Override
    public int compareStatuses(Item o1, Item o2) {
        return (o1.getStatus().compareTo(o2.getStatus()));
    }

    @Override
    public String getCreated(Item item) {
        return FMT.format(item.getCreated());
    }

    @Override
    public int compareCreated(Item o1, Item o2) {
        return (o1.getCreated().compareTo(o2.getCreated()));
    }

    @Override
    public String getUpdated(Item item) {
        return FMT.format(item.getUpdated());
    }

    @Override
    public int compareUpdated(Item o1, Item o2) {
        return (o1.getUpdated().compareTo(o2.getUpdated()));
    }

    @Override
    public void save() {
        saveDocument();
    }

    private void initializeDocument() {
        svc.loadDocument(new LoadAsyncCallback());
    }

    final class LoadAsyncCallback implements AsyncCallback<Document> {

        @Override
        public void onFailure(Throwable caught) {
        }

        @Override
        public void onSuccess(Document r) {
            Date lastSaved = data.getDocument().getLastSaved();
            if (r != null && r.getLastSaved().after(lastSaved)) {
                data.setDocument(r);
                data.setDirty(false);
            }
        }
    }

    private void saveDocument() {
        if (data.isDirty()) {
            svc.saveDocument(data.getDocument(), new SaveAsyncCallback());
        } else {
            view.onSaveSuccess();
        }
    }

    final class SaveAsyncCallback implements AsyncCallback<Document> {

        @Override
        public void onFailure(Throwable caught) {
            view.onSaveFailure();
        }

        @Override
        public void onSuccess(Document result) {
            data.setDocument(result);
            data.setDirty(false);
            view.onSaveSuccess();
        }
    }

    public static List<String> getPriorityNames() {
        return Arrays.asList(PRIO_NAMES);
    }

    public static List<String> getStatusNames() {
        return Arrays.asList(STATUS_NAMES);
    }

    private static String getPriorityName(Priority priority) {
        return PRIO_NAMES[priority.ordinal()];
    }

    private static Priority getPriorityByName(String name) {
        return Priority.values()[getFirstIndex(name, PRIO_NAMES)];
    }

    private static String getStatusName(Status status) {
        return STATUS_NAMES[status.ordinal()];
    }

    private static Status getStatusByName(String name) {
        return Status.values()[getFirstIndex(name, STATUS_NAMES)];
    }

    private static int getFirstIndex(String string, String[] strings) {
        int result = -1;
        for (int i = 0; i < strings.length; i++) {
            if (strings[i].equals(string)) {
                result = i;
                break;
            }
        }
        return result;
    }

}
