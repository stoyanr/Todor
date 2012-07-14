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

import java.util.List;

import com.google.gwt.cell.client.ClickableTextCell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;

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

    public ItemsViewImpl() {
        initWidget(uiBinder.createAndBindUi(this));

        addButton.setFocus(true);

        itemsTable.addColumn(createTextColumn(), "Description");
        itemsTable.addColumn(createXColumn(), "");

        dataProvider = new ListDataProvider<T>();
        dataProvider.addDataDisplay(itemsTable);
    }

    private Column<T, String> createTextColumn() {
        Column<T, String> textColumn = new Column<T, String>(new EditTextCell()) {

            @Override
            public String getValue(T t) {
                return presenter.getText(t);
            }
        };
        textColumn.setFieldUpdater(new FieldUpdater<T, String>() {

            @Override
            public void update(int index, T t, String value) {
                presenter.updateText(t, value);
            }
        });
        return textColumn;
    }

    private Column<T, String> createXColumn() {
        Column<T, String> xColumn = new Column<T, String>(
            new ClickableTextCell()) {

            @Override
            public String getValue(T t) {
                return "x";
            }
        };
        xColumn.setFieldUpdater(new FieldUpdater<T, String>() {

            @Override
            public void update(int index, T t, String value) {
                presenter.delete(index, t);
            }

        });
        xColumn.setCellStyleNames(X_BUTTON_STYLE);
        xColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
        return xColumn;
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
