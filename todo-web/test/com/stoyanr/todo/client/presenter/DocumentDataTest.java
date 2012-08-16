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

import static com.stoyanr.todo.client.utils.TestUtils.ITEM_0;
import static com.stoyanr.todo.client.utils.TestUtils.ITEM_1;
import static com.stoyanr.todo.client.utils.TestUtils.NOW;
import static com.stoyanr.todo.client.utils.TestUtils.THEN;
import static com.stoyanr.todo.client.utils.TestUtils.U0;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.stoyanr.todo.client.util.LocalStorage;
import com.stoyanr.todo.client.utils.MockUtils;
import com.stoyanr.todo.client.utils.TestUtils;
import com.stoyanr.todo.model.Document;
import com.stoyanr.todo.model.Item;
import com.stoyanr.todo.model.Item.Priority;
import com.stoyanr.todo.model.Item.Status;

@RunWith(value = Parameterized.class)
public class DocumentDataTest {

    private final Document document;

    private DocumentData data;
    private Document managed;
    private LocalStorage storage;
    private JsonSerializer serializer;

    // @formatter:off
    private static final Object[][] PARAMETERS = new Object[][] { 
        { new Document(U0, new ArrayList<Item>(), THEN) }, 
        { new Document(U0, Arrays.asList(ITEM_0), NOW) }, 
        { new Document(U0, Arrays.asList(ITEM_1), NOW) }, 
    };
    // @formatter:on

    @Parameters
    public static Collection<Object[]> parameters() {
        return Arrays.asList(PARAMETERS);
    }

    public DocumentDataTest(Document document) {
        this.document = document;
    }

    @Before
    public void setUp() {
        MockUtils.clearMockStorage();
        MockUtils.clearMockSerializer();
        storage = MockUtils.createMockStorage();
        serializer = MockUtils.createMockSerializer();
        managed = new Document(U0, new ArrayList<Item>(), THEN);
        data = new DocumentData(managed, storage, serializer);
        verifyConstructorInvocations();
        assertEquals(0, data.getNextId());
        assertEquals(false, data.isDirty());
    }

    private void verifyConstructorInvocations() {
        verify(storage).isPresent();
        verify(serializer).toString(managed);
        verify(storage).setStringValue(anyString(), eq(U0));
    }

    @Test
    public void testSetDocument() {
        data.setDocument(document);
        verifySetDocumentInvocations();
        assertEquals(getMaxId() + 1, data.getNextId());
        assertEquals(true, data.isDirty());
        TestUtils.assertDocumentEquals(document, data.getDocument(), true);
    }

    private void verifySetDocumentInvocations() {
        verify(storage, atLeast(1)).isPresent();
        verify(storage).clearValues();
        verify(serializer, atLeast(1)).toString(managed);
        verify(storage, atLeast(1)).setStringValue(anyString(),
            eq(document.getUserId()));
        for (Item item : document.getItems()) {
            verify(serializer).toString(item);
            verify(storage).setStringValue(anyString(), eq("" + item.getId()));
        }
        verify(storage).setLongValue(anyString(), eq(getMaxId() + 1));
        verify(storage).setBooleanValue(anyString(), eq(true));
    }

    @Test
    public void testAddItem() {
        String text = getItemText();
        data.addItem(text);
        Item item = new Item(null, 0, text, Priority.MEDIUM, Status.NEW, NOW,
            NOW);
        verifyAddItemInvocations(item);
        assertEquals(1, data.getDocument().getItems().size());
        TestUtils.assertItemEquals(item, data.getDocument().getItems().get(0));
        assertEquals(1, data.getNextId());
        assertEquals(true, data.isDirty());
    }

    private void verifyAddItemInvocations(Item item) {
        verify(serializer).toString(any(Item.class));
        verify(storage).setStringValue(anyString(), eq("" + item.getId()));
        verify(storage).setLongValue(anyString(), eq(1L));
        verify(storage).setBooleanValue(anyString(), eq(true));
    }

    @Test
    public void testUpdateItemText() {
        String text = getItemText();
        data.addItem(text);
        data.setDirty(false);
        String newText = text + " (x)";
        data.updateItem(data.getDocument().getItems().get(0), newText);
        Item itemx = new Item(null, 0, newText, Priority.MEDIUM, Status.NEW,
            NOW, NOW);
        verifyUpdateItemInvocations(itemx);
        assertUpdateItemResults(itemx);
    }

    @Test(expected = AssertionError.class)
    public void testUpdateItemTextError() {
        String text = getItemText();
        Item item = new Item(null, 0, text, Priority.MEDIUM, Status.NEW, NOW,
            NOW);
        data.updateItem(item, text);
    }

    @Test
    public void testUpdateItemPriority() {
        data.addItem(getItemText());
        data.setDirty(false);
        Priority prio = Priority.LOW;
        data.updateItem(data.getDocument().getItems().get(0), prio);
        Item itemx = new Item(null, 0, getItemText(), prio, Status.NEW, NOW,
            NOW);
        verifyUpdateItemInvocations(itemx);
        assertUpdateItemResults(itemx);
    }

    @Test
    public void testUpdateItemStatus() {
        data.addItem(getItemText());
        data.setDirty(false);
        Status status = Status.IN_PROGRESS;
        data.updateItem(data.getDocument().getItems().get(0), status);
        Item itemx = new Item(null, 0, getItemText(), Priority.MEDIUM, status,
            NOW, NOW);
        verifyUpdateItemInvocations(itemx);
        assertUpdateItemResults(itemx);
    }

    private void verifyUpdateItemInvocations(Item item) {
        verify(serializer, times(2)).toString(any(Item.class));
        verify(storage, times(2)).setStringValue(anyString(),
            eq("" + item.getId()));
        verify(storage, times(2)).setBooleanValue(anyString(), eq(true));
        verify(storage, times(1)).setBooleanValue(anyString(), eq(false));
    }

    private void assertUpdateItemResults(Item item) {
        assertEquals(1, data.getDocument().getItems().size());
        TestUtils.assertItemEquals(item, data.getDocument().getItems().get(0));
        assertEquals(true, data.isDirty());
    }

    @Test
    public void testDeleteItem() {
        data.addItem(getItemText());
        data.setDirty(false);
        data.deleteItem(data.getDocument().getItems().get(0), 0);
        verify(storage).removeValue(anyString());
        assertEquals(0, data.getDocument().getItems().size());
        assertEquals(true, data.isDirty());
    }

    @Test(expected = AssertionError.class)
    public void testDeleteItemError() {
        String text = getItemText();
        Item item = new Item(null, 0, text, Priority.MEDIUM, Status.NEW, NOW,
            NOW);
        data.deleteItem(item, 0);
    }

    @Test
    public void testClearItems() {
        data.addItem(getItemText());
        data.addItem(getItemText() + " (x)");
        data.setDirty(false);
        data.clearItems();
        verify(storage).clearValues();
        assertEquals(0, data.getDocument().getItems().size());
        assertEquals(true, data.isDirty());
    }

    private String getItemText() {
        String itemText = "";
        if (document.getItems().size() > 0) {
            itemText = document.getItems().get(0).getText();
        }
        return itemText;
    }

    private long getMaxId() {
        long maxId = 0;
        for (Item item : document.getItems()) {
            maxId = Math.max(maxId, item.getId());
        }
        return maxId;
    }
}
