
package com.stoyanr.todo.client.util;

import com.stoyanr.todo.model.Document;
import com.stoyanr.todo.model.Item;

import static org.junit.Assert.assertEquals;

public class TestUtils {
    
    public static final String MODULE_NAME = "com.stoyanr.todo.Todo";

    public static void assertDocumentEquals(Document expected, Document actual,
        boolean compareItems) {
        assertEquals(expected.getUserId(), actual.getUserId());
        assertEquals(expected.getLastSaved(), actual.getLastSaved());
        if (compareItems) {
            assertEquals(expected.getItems().size(), actual.getItems().size());
            for (int i = 0; i < actual.getItems().size(); i++) {
                assertItemEquals(expected.getItems().get(i), actual.getItems()
                    .get(i));
            }
        }
    }

    public static void assertItemEquals(Item expected, Item actual) {
        assertEquals(expected.getKey(), actual.getKey());
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getText(), actual.getText());
        assertEquals(expected.getPriority(), actual.getPriority());
        assertEquals(expected.getStatus(), actual.getStatus());
    }
}
