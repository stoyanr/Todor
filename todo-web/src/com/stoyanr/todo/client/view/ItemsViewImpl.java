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

package com.stoyanr.todo.client.view;

import java.util.Comparator;
import java.util.List;

import com.google.gwt.cell.client.ClickableTextCell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.SelectionCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.stoyanr.todo.model.UserAccount;

public class ItemsViewImpl<T> extends Composite implements ItemsView<T> {

    private static final String X_BUTTON_STYLE = "xButton";

    // @formatter:off
    private static final String MSG_SERVER_ERROR = 
"An error occurred while attempting to contact the server. " + 
"Please check your network connection and try again.";
    // @formatter:on

    @SuppressWarnings("rawtypes")
    @UiTemplate("ItemsView.ui.xml")
    interface ItemsViewUiBinder extends UiBinder<Widget, ItemsViewImpl> {
    }

    private static ItemsViewUiBinder uiBinder = GWT
        .create(ItemsViewUiBinder.class);

    @UiField
    Label nicknameLabel;
    @UiField
    Anchor signOutLink;
    @UiField
    TextBox itemField;
    @UiField
    Button addButton;
    @UiField
    Button saveButton;
    @UiField
    Button clearAllButton;
    @UiField
    CellTable<T> itemsTable;
    @UiField
    DialogBox dialogBox;
    @UiField
    HTML messageHtml;
    @UiField
    Button closeButton;

    private ListDataProvider<T> dataProvider;
    private Presenter<T> presenter;

    private static enum CompareMode {
        ID, PRIORITY, STATUS, CREATED, UPDATED
    };

    public ItemsViewImpl(UserAccount userAccount, List<String> priorityNames,
        List<String> statusNames) {
        initWidget(uiBinder.createAndBindUi(this));

        nicknameLabel.setText(userAccount.getNickname());
        signOutLink.setHref(userAccount.getLogoutUrl());

        addButton.setFocus(true);

        dataProvider = new ListDataProvider<T>();
        dataProvider.addDataDisplay(itemsTable);
        List<T> list = dataProvider.getList();

        Column<T, String> idColumn = createIdColumn();
        Column<T, String> textColumn = createTextColumn();
        Column<T, String> prioColumn = createPriorityColumn(priorityNames);
        Column<T, String> statusColumn = createStatusColumn(statusNames);
        Column<T, String> createdColumn = createCreatedColumn();
        Column<T, String> updatedColumn = createUpdatedColumn();
        Column<T, String> xColumn = createXColumn();

        itemsTable.addColumn(idColumn, "Id");
        itemsTable.addColumn(textColumn, "Description");
        itemsTable.addColumn(prioColumn, "Priority");
        itemsTable.addColumn(statusColumn, "Status");
        itemsTable.addColumn(createdColumn, "Created");
        itemsTable.addColumn(updatedColumn, "Updated");
        itemsTable.addColumn(xColumn, "");

        itemsTable.setColumnWidth(idColumn, 3, Unit.PCT);
        itemsTable.setColumnWidth(textColumn, 64, Unit.PCT);
        itemsTable.setColumnWidth(prioColumn, 5, Unit.PCT);
        itemsTable.setColumnWidth(statusColumn, 5, Unit.PCT);
        itemsTable.setColumnWidth(createdColumn, 10, Unit.PCT);
        itemsTable.setColumnWidth(updatedColumn, 10, Unit.PCT);
        itemsTable.setColumnWidth(xColumn, 3, Unit.PCT);

        ListHandler<T> idHandler = createHandler(idColumn, list, CompareMode.ID);
        ListHandler<T> prioHandler = createHandler(prioColumn, list,
            CompareMode.PRIORITY);
        ListHandler<T> statusHandler = createHandler(statusColumn, list,
            CompareMode.STATUS);
        ListHandler<T> createdHandler = createHandler(createdColumn, list,
            CompareMode.CREATED);
        ListHandler<T> updatedHandler = createHandler(updatedColumn, list,
            CompareMode.UPDATED);

        itemsTable.addColumnSortHandler(idHandler);
        itemsTable.addColumnSortHandler(prioHandler);
        itemsTable.addColumnSortHandler(statusHandler);
        itemsTable.addColumnSortHandler(createdHandler);
        itemsTable.addColumnSortHandler(updatedHandler);

        itemsTable.getColumnSortList().push(idColumn);
    }

    private Column<T, String> createIdColumn() {
        Column<T, String> col = new Column<T, String>(new TextCell()) {

            @Override
            public String getValue(T t) {
                return presenter.getId(t);
            }
        };
        col.setSortable(true);
        return col;
    }

    private Column<T, String> createTextColumn() {
        Column<T, String> col = new Column<T, String>(new EditTextCell()) {

            @Override
            public String getValue(T t) {
                return presenter.getText(t);
            }
        };
        col.setFieldUpdater(new FieldUpdater<T, String>() {

            @Override
            public void update(int index, T t, String value) {
                presenter.updateText(t, value);
                dataProvider.refresh();
            }
        });
        return col;
    }

    private Column<T, String> createPriorityColumn(List<String> priorityNames) {
        Column<T, String> col = new Column<T, String>(new SelectionCell(
            priorityNames)) {

            @Override
            public String getValue(T t) {
                return presenter.getPriority(t);
            }
        };
        col.setFieldUpdater(new FieldUpdater<T, String>() {

            @Override
            public void update(int index, T t, String value) {
                presenter.updatePriority(t, value);
                dataProvider.refresh();
            }
        });
        col.setSortable(true);
        return col;
    }

    private Column<T, String> createStatusColumn(List<String> statusNames) {
        Column<T, String> col = new Column<T, String>(new SelectionCell(
            statusNames)) {

            @Override
            public String getValue(T t) {
                return presenter.getStatus(t);
            }
        };
        col.setFieldUpdater(new FieldUpdater<T, String>() {

            @Override
            public void update(int index, T t, String value) {
                presenter.updateStatus(t, value);
                dataProvider.refresh();
            }
        });
        col.setSortable(true);
        return col;
    }

    private Column<T, String> createCreatedColumn() {
        Column<T, String> col = new Column<T, String>(new TextCell()) {

            @Override
            public String getValue(T t) {
                return presenter.getCreated(t);
            }
        };
        col.setSortable(true);
        return col;
    }

    private Column<T, String> createUpdatedColumn() {
        Column<T, String> col = new Column<T, String>(new TextCell()) {

            @Override
            public String getValue(T t) {
                return presenter.getUpdated(t);
            }
        };
        col.setSortable(true);
        return col;
    }

    private Column<T, String> createXColumn() {
        Column<T, String> col = new Column<T, String>(new ClickableTextCell()) {

            @Override
            public String getValue(T t) {
                return "x";
            }
        };
        col.setFieldUpdater(new FieldUpdater<T, String>() {

            @Override
            public void update(int index, T t, String value) {
                presenter.delete(index, t);
            }

        });
        col.setCellStyleNames(X_BUTTON_STYLE);
        col.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
        return col;
    }

    private ListHandler<T> createHandler(Column<T, String> col, List<T> list,
        final CompareMode mode) {
        ListHandler<T> idHandler = new ListHandler<T>(list);
        idHandler.setComparator(col, new Comparator<T>() {

            @Override
            public int compare(T o1, T o2) {
                if (o1 == o2) {
                    return 0;
                } else if (o1 != null && o2 == null) {
                    return 1;
                } else if (o1 == null && o2 != null) {
                    return -1;
                } else {
                    return compareBy(o1, o2, mode);
                }
            }

        });
        return idHandler;
    }

    private int compareBy(T o1, T o2, CompareMode mode) {
        switch (mode) {
        case ID:
            return presenter.compareIds(o1, o2);
        case PRIORITY:
            return presenter.comparePriorities(o1, o2);
        case STATUS:
            return presenter.compareStatuses(o1, o2);
        case CREATED:
            return presenter.compareCreated(o1, o2);
        case UPDATED:
            return presenter.compareUpdated(o1, o2);
        }
        return 0;
    }

    @Override
    public Widget asWidget() {
        return this;
    }

    @Override
    public void setPresenter(Presenter<T> presenter) {
        this.presenter = presenter;
    }

    @Override
    public List<T> getData() {
        return dataProvider.getList();
    }

    @Override
    public void setData(List<T> data) {
        dataProvider.setList(data);
    }

    @UiHandler("addButton")
    void onAddButtonClicked(ClickEvent event) {
        if (presenter != null) {
            String itemText = itemField.getText();
            presenter.add(itemText);
            itemField.setText("");
        }
    }

    @UiHandler("itemField")
    void onKeyPress(KeyPressEvent event) {
        if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
            onAddButtonClicked(null);
        }
    }

    @UiHandler("saveButton")
    void onSaveButtonClicked(ClickEvent event) {
        saveButton.setEnabled(false);
        if (presenter != null) {
            presenter.save();
        }
    }

    @UiHandler("clearAllButton")
    void onClearAllButtonClicked(ClickEvent event) {
        if (presenter != null) {
            presenter.clearAll();
        }
    }

    @UiHandler("closeButton")
    void onCloseButtonClicked(ClickEvent event) {
        dialogBox.hide();
        saveButton.setEnabled(true);
        addButton.setFocus(true);
    }

    @Override
    public void onSaveFailure() {
        dialogBox.setText("Failure");
        messageHtml.setHTML(MSG_SERVER_ERROR);
        dialogBox.center();
        closeButton.setFocus(true);
    }

    @Override
    public void onSaveSuccess() {
        saveButton.setEnabled(true);
    }

}
