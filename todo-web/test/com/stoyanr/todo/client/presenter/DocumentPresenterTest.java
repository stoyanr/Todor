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

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;
import com.stoyanr.todo.client.DocumentServiceAsync;
import com.stoyanr.todo.client.util.LocalStorage;
import com.stoyanr.todo.client.util.TestUtils;
import com.stoyanr.todo.client.view.ItemsView;
import com.stoyanr.todo.model.Document;
import com.stoyanr.todo.model.Item;
import com.stoyanr.todo.model.Item.Priority;
import com.stoyanr.todo.model.Item.Status;

@RunWith(value = Parameterized.class)
public class DocumentPresenterTest {

    private final Document document;
    private final boolean success;

    private JsonSerializer serializer;
    private Document managed;
    private DocumentData data;
    private DocumentServiceAsync svc;
    private ItemsView<Item> view;
    private DocumentPresenter presenter;

    // @formatter:off
    private static Object[][] PARAMETERS = new Object[][] { 
        { new Document("me", Arrays.asList(new Item(null, 0, "xxx", Priority.MEDIUM, Status.NEW)), new Date()), true }, 
        { new Document("me", Arrays.asList(new Item(null, 1, "abc", Priority.HIGH, Status.IN_PROGRESS)), new Date()), false }, 
    };
    private LocalStorage storage;
    // @formatter:on

    @Parameters
    public static Collection<Object[]> parameters() {
        return Arrays.asList(PARAMETERS);
    }

    public DocumentPresenterTest(Document document, boolean success) {
        this.document = document;
        this.success = success;
    }

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        TestUtils.clearMockStorage();
        TestUtils.clearMockSerializer();
        storage = TestUtils.createMockStorage();
        serializer = TestUtils.createMockSerializer();
        managed = new Document("me", new ArrayList<Item>(), new Date(0));
        data = new DocumentData(managed, storage, serializer);
        svc = createMockService(success);
        view = (ItemsView<Item>) mock(ItemsView.class);
        presenter = new DocumentPresenter(svc, data, view);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGo() {
        HasWidgets container = mock(HasWidgets.class);
        presenter.go(container);
        verify(container).clear();
        verify(container).add(null);
        verify(view).asWidget();
        verify(svc).loadDocument(
            (AsyncCallback<Document>) any(AsyncCallback.class));
        if (success) {
            TestUtils.assertDocumentEquals(document, data.getDocument(), true);
            assertEquals(false, data.isDirty());
        } else {
            assertEquals(0, data.getDocument().getItems().size());
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSaveDirty() {
        data.setDocument(document);
        assertEquals(true, data.isDirty());
        presenter.save();
        verify(svc).saveDocument(any(Document.class),
            (AsyncCallback<Document>) any(AsyncCallback.class));
        if (success) {
            assertEquals(new Date(1000), data.getDocument().getLastSaved());
            assertEquals(false, data.isDirty());
            verify(view).onSaveSuccess();
        } else {
            assertEquals(document.getLastSaved(), data.getDocument()
                .getLastSaved());
            assertEquals(true, data.isDirty());
            verify(view).onSaveFailure();
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSaveClean() {
        assertEquals(false, data.isDirty());
        presenter.save();
        verify(svc, times(0)).saveDocument(any(Document.class),
            (AsyncCallback<Document>) any(AsyncCallback.class));
        verify(view).onSaveSuccess();
    }

    @SuppressWarnings("unchecked")
    private DocumentServiceAsync createMockService(final boolean success) {
        DocumentServiceAsync svcx = mock(DocumentServiceAsync.class);
        // @formatter:off
        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock inv) {
                AsyncCallback<Document> c = (AsyncCallback<Document>) inv.getArguments()[0];
                if (success) {
                    c.onSuccess(document);
                } else {
                    c.onFailure(null);
                }
                return null;
            }
        }).when(svcx).loadDocument((AsyncCallback<Document>) any(AsyncCallback.class));
        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock inv) {
                Document document = (Document) inv.getArguments()[0];
                document.setLastSaved(new Date(1000));
                AsyncCallback<Document> c = (AsyncCallback<Document>) inv.getArguments()[1];
                if (success) {
                    c.onSuccess(document);
                } else {
                    c.onFailure(null);
                }
                return null;
            }
        }).when(svcx).saveDocument(any(Document.class), (AsyncCallback<Document>) any(AsyncCallback.class));
        // @formatter:on
        return svcx;
    }

}
