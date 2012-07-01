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

package com.stoyanr.todo.client;

import java.util.Date;
import java.util.List;

import com.google.gwt.cell.client.ClickableTextCell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.ListDataProvider;
import com.stoyanr.todo.model.Item;

public class Todo implements EntryPoint {

    private static final String S_DESCRIPTION = "Description";
    private static final String S_DO_SOMETHING = "Do something ...";
    private static final String S_ADD = "Add";
    private static final String S_SAVE = "Save";
    private static final String S_CLEAR_ALL = "Clear All";
    private static final String S_CLOSE = "Close";
    private static final String S_FAILURE = "Failure";

    private static final String BUTTON_STYLE = "normalButton";
    private static final String X_BUTTON_STYLE = "xButton";
    private static final String DIALOG_PANEL_STYLE = "dialogPanel";
    private static final String MESSAGE_HTML_ERROR_STYLE = "messageHtmlError";

    private static final String ITEM_FIELD_CONTAINER = "itemFieldContainer";
    private static final String ADD_BUTTON_CONTAINER = "addButtonContainer";
    private static final String ERROR_LABEL_CONTAINER = "errorLabelContainer";
    private static final String SAVE_BUTTON_CONTAINER = "saveButtonContainer";
    private static final String CLEAR_ALL_BUTTON_CONTAINER = "clearAllButtonContainer";
    private static final String ITEMS_TABLE_CONTAINER = "itemsTableContainer";

    private static final String CLOSE_BUTTON_ID = "closeButton";

    private static final String ITEM_KEY = "todo-item-";
    private static final String NEXT_ID_KEY = "todo-count";
    private static final String DIRTY_KEY = "todo-dirty";
    private static final String LAST_SAVED_KEY = "todo-last-saved";

    // @formatter:off
    private static final String MSG_SERVER_ERROR = 
"An error occurred while attempting to contact the server. " + 
"Please check your network connection and try again.";
    // @formatter:on

    private final ItemsServiceAsync svc = GWT.create(ItemsService.class);

    private final Storage storage = Storage.getLocalStorageIfSupported();

    private TextBox itemField;
    private Button addButton;
    private Button saveButton;
    private Button clearAllButton;
    private Label errorLabel;
    private CellTable<Item> itemsTable;
    private HTML messageHtml;
    private Button closeButton;
    private VerticalPanel dialogPanel;
    private DialogBox dialogBox;
    private ListDataProvider<Item> dataProvider;

    private List<Item> items;
    private int nextId;
    private boolean dirty;
    private Date lastSaved;

    @Override
    public void onModuleLoad() {
        itemField = new TextBox();
        itemField.setText(S_DO_SOMETHING);
        itemField.setFocus(true);
        itemField.selectAll();
        itemField.addKeyUpHandler(new AddHandler());

        addButton = new Button(S_ADD);
        addButton.addStyleName(BUTTON_STYLE);
        addButton.addClickHandler(new AddHandler());

        saveButton = new Button(S_SAVE);
        saveButton.addStyleName(BUTTON_STYLE);
        saveButton.addClickHandler(new SaveHandler());

        clearAllButton = new Button(S_CLEAR_ALL);
        clearAllButton.addStyleName(BUTTON_STYLE);
        clearAllButton.addClickHandler(new ClearAllHandler());

        errorLabel = new Label();

        itemsTable = new CellTable<Item>();
        Column<Item, String> textColumn = new Column<Item, String>(
            new EditTextCell()) {

            @Override
            public String getValue(Item item) {
                return item.getText();
            }
        };
        textColumn.setFieldUpdater(new FieldUpdater<Item, String>() {

            @Override
            public void update(int index, Item item, String value) {
                updateItem(item, value);
                setDirty(true);
            }
        });
        Column<Item, String> xColumn = new Column<Item, String>(
            new ClickableTextCell()) {

            @Override
            public String getValue(Item item) {
                return "x";
            }
        };
        xColumn.setFieldUpdater(new FieldUpdater<Item, String>() {

            @Override
            public void update(int index, Item item, String value) {
                deleteItem(item, index);
                setDirty(true);
            }

        });
        xColumn.setCellStyleNames(X_BUTTON_STYLE);
        itemsTable.addColumn(textColumn, S_DESCRIPTION);
        itemsTable.addColumn(xColumn, "");

        messageHtml = new HTML();

        closeButton = new Button(S_CLOSE);
        closeButton.getElement().setId(CLOSE_BUTTON_ID);
        closeButton.addClickHandler(new CloseHandler());

        dialogPanel = new VerticalPanel();
        dialogPanel.addStyleName(DIALOG_PANEL_STYLE);
        dialogPanel.add(messageHtml);
        dialogPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
        dialogPanel.add(closeButton);

        dialogBox = new DialogBox();
        dialogBox.setText(S_FAILURE);
        dialogBox.setAnimationEnabled(true);
        dialogBox.setWidget(dialogPanel);

        RootPanel.get(ITEM_FIELD_CONTAINER).add(itemField);
        RootPanel.get(ADD_BUTTON_CONTAINER).add(addButton);
        RootPanel.get(SAVE_BUTTON_CONTAINER).add(saveButton);
        RootPanel.get(CLEAR_ALL_BUTTON_CONTAINER).add(clearAllButton);
        RootPanel.get(ERROR_LABEL_CONTAINER).add(errorLabel);
        RootPanel.get(ITEMS_TABLE_CONTAINER).add(itemsTable);

        dataProvider = new ListDataProvider<Item>();
        dataProvider.addDataDisplay(itemsTable);

        items = dataProvider.getList();
        nextId = 0;
        dirty = false;
        lastSaved = new Date(0);

        initializeItems();
    }

    private void showDialogBox() {
        dialogBox.setText(S_FAILURE);
        messageHtml.addStyleName(MESSAGE_HTML_ERROR_STYLE);
        messageHtml.setHTML(MSG_SERVER_ERROR);
        dialogBox.center();
        closeButton.setFocus(true);
    }

    class AddHandler implements ClickHandler, KeyUpHandler {

        @Override
        public void onClick(ClickEvent event) {
            add();
        }

        @Override
        public void onKeyUp(KeyUpEvent event) {
            if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                add();
            }
        }

        private void add() {
            String itemText = itemField.getText();
            if (!itemText.isEmpty()) {
                addItem(itemText);
                setDirty(true);
                itemField.setText("");
            }
        }

    }

    class SaveHandler implements ClickHandler {

        @Override
        public void onClick(ClickEvent event) {
            saveButton.setEnabled(false);
            messageHtml.setText("");
            saveItemsToServer();
        }
    }

    class ClearAllHandler implements ClickHandler {

        @Override
        public void onClick(ClickEvent event) {
            clearItems();
            setDirty(true);
        }
    }

    class CloseHandler implements ClickHandler {

        @Override
        public void onClick(ClickEvent event) {
            dialogBox.hide();
            saveButton.setEnabled(true);
            addButton.setFocus(true);
        }
    }

    private void initializeItems() {
        svc.getLastSaved(new InitializeAsyncCallback());
    }

    final class InitializeAsyncCallback implements AsyncCallback<Date> {

        @Override
        public void onFailure(Throwable caught) {
            loadStateFromLocalStorage();
        }

        @Override
        public void onSuccess(Date result) {
            Date ls = new Date(getIntegerValue(LAST_SAVED_KEY));
            if (result.compareTo(ls) <= 0) {
                loadStateFromLocalStorage();
            } else if (result.after(new Date(0))) {
                loadItemsFromServer();
                lastSaved = result;
            }
        }
    }

    private void loadItemsFromServer() {
        svc.loadItems(new LoadAsyncCallback());
    }

    final class LoadAsyncCallback implements AsyncCallback<Item[]> {

        @Override
        public void onFailure(Throwable caught) {
        }

        @Override
        public void onSuccess(Item[] result) {
            loadItemsFromArray(result);
            dirty = false;
            saveStateToLocalStorage();
        }
    }

    private void saveItemsToServer() {
        if (dirty) {
            svc.saveItems(saveItemsToArray(), new SaveAsyncCallback());
        }
    }

    final class SaveAsyncCallback implements AsyncCallback<Date> {

        @Override
        public void onFailure(Throwable caught) {
            showDialogBox();
        }

        @Override
        public void onSuccess(Date result) {
            setDirty(false);
            setLastSaved(result);
            saveButton.setEnabled(true);
        }
    }

    private void addItem(String text) {
        items.add(new Item(nextId, text));
        setStringValue(ITEM_KEY + nextId, text);
        nextId++;
        setIntegerValue(NEXT_ID_KEY, nextId);
    }

    private void updateItem(Item item, String text) {
        item.setText(text);
        setStringValue(ITEM_KEY + item.getId(), text);
    }

    private void deleteItem(Item item, int index) {
        items.remove(index);
        removeValue(ITEM_KEY + item.getId());
    }

    private void clearItems() {
        items.clear();
        clearValues();
    }

    private void setDirty(boolean value) {
        dirty = value;
        setBooleanValue(DIRTY_KEY, dirty);
    }

    private void setLastSaved(Date date) {
        lastSaved = date;
        setIntegerValue(LAST_SAVED_KEY, date.getTime());
    }

    private void loadItemsFromArray(Item[] itemsArray) {
        items.clear();
        int maxId = 0;
        for (Item item : itemsArray) {
            items.add(item);
            maxId = Math.max(maxId, item.getId());
        }
        nextId = maxId + 1;
    }

    private Item[] saveItemsToArray() {
        return items.toArray(new Item[items.size()]);
    }

    private void loadStateFromLocalStorage() {
        if (storage != null) {
            items.clear();
            nextId = (int) getIntegerValue(NEXT_ID_KEY);
            for (int i = 0; i < nextId; i++) {
                String value = getStringValue(ITEM_KEY + i);
                if (!value.isEmpty()) {
                    items.add(new Item(i, value));
                }
            }
            dirty = getBooleanValue(DIRTY_KEY);
            lastSaved.setTime(getIntegerValue(LAST_SAVED_KEY));
        }
    }

    private void saveStateToLocalStorage() {
        if (storage != null) {
            clearValues();
            for (int i = 0; i < items.size(); i++) {
                Item item = items.get(i);
                setStringValue(ITEM_KEY + item.getId(), item.getText());
            }
            setIntegerValue(NEXT_ID_KEY, nextId);
            setBooleanValue(DIRTY_KEY, dirty);
            setIntegerValue(LAST_SAVED_KEY, lastSaved.getTime());
        }
    }

    private String getStringValue(String key) {
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

    private void setStringValue(String key, String value) {
        assert (key != null);
        assert (value != null && !value.isEmpty());
        if (storage != null) {
            storage.setItem(key, value);
        }
    }

    private long getIntegerValue(String key) {
        assert (key != null);
        long result = 0;
        if (storage != null) {
            String s = storage.getItem(key);
            if (s != null) {
                result = Long.valueOf(s);
            }
        }
        return result;
    }

    private void setIntegerValue(String key, long value) {
        assert (key != null);
        if (storage != null) {
            storage.setItem(key, String.valueOf(value));
        }
    }

    private boolean getBooleanValue(String key) {
        assert (key != null);
        boolean result = false;
        if (storage != null) {
            String s = storage.getItem(key);
            if (s != null) {
                result = Boolean.valueOf(s);
            }
        }
        return result;
    }

    private void setBooleanValue(String key, boolean value) {
        assert (key != null);
        if (storage != null) {
            storage.setItem(key, String.valueOf(value));
        }
    }

    private void removeValue(String key) {
        assert (key != null);
        if (storage != null) {
            storage.removeItem(key);
        }
    }

    private void clearValues() {
        if (storage != null) {
            storage.clear();
        }
    }

}
