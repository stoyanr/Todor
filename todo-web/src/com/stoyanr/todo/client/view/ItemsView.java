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

import com.google.gwt.user.client.ui.Widget;

import java.util.List;

public interface ItemsView<T> {

    public interface Presenter<T> {

        void add(String text);

        void delete(int index, T t);

        void clearAll();

        String getText(T t);

        void updateText(T t, String value);

        String getPriority(T t);

        void updatePriority(T t, String value);

        int comparePriorities(T o1, T o2);

        String getStatus(T t);

        void updateStatus(T t, String value);

        void save();
    }

    Widget asWidget();

    void setPresenter(Presenter<T> presenter);

    void setData(List<T> data);

    List<T> getData();

    void onSaveFailure();

    void onSaveSuccess();
}
