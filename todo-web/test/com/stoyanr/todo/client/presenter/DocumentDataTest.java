
package com.stoyanr.todo.client.presenter;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.stoyanr.todo.client.util.LocalStorage;
import com.stoyanr.todo.client.util.TestUtils;
import com.stoyanr.todo.model.Document;
import com.stoyanr.todo.model.Item;

@RunWith(value = Parameterized.class)
public class DocumentDataTest {

    private final Document document;
    private LocalStorage storage;
    private DocumentData data;

    // @formatter:off
    private static Object[][] PARAMETERS = new Object[][] { 
        { new Document("me", new ArrayList<Item>(), new Date(0)) }, 
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
        mockStorage();
        Document doc = new Document("me", new ArrayList<Item>(), new Date(0));
        data = new DocumentData(storage, doc);
    }

    private void mockStorage() {
        storage = mock(LocalStorage.class);
        when(storage.isPresent()).thenReturn(false);
        when(storage.getStringValue(anyString())).thenReturn("");
        when(storage.getLongValue(anyString())).thenReturn(0L);
        when(storage.getBooleanValue(anyString())).thenReturn(false);
        when(storage.getDateValue(anyString())).thenReturn(new Date(0));
    }

    @Test
    public void testGetDocument() {
        data.setDocument(document);
        TestUtils.assertDocumentEquals(document, data.getDocument(), true);
        assertEquals(true, data.isDirty());
    }

}
