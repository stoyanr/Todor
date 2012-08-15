package com.stoyanr.todo.client.presenter;

import java.util.Arrays;
import java.util.Date;

import org.junit.Test;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.junit.client.GWTTestCase;
import com.stoyanr.todo.client.util.TestUtils;
import com.stoyanr.todo.model.Document;
import com.stoyanr.todo.model.Item;
import com.stoyanr.todo.model.Item.Priority;
import com.stoyanr.todo.model.Item.Status;

public class JsonSerializerGWTTest extends GWTTestCase {
    
    // @formatter:off
    private static Object[][] PARAMETERS = new Object[][] { 
        { new Document("", Arrays.asList(new Item()), new Date(0)) }, 
        { new Document("me", Arrays.asList(new Item(null, 0, "xxx", Priority.MEDIUM, Status.NEW)), new Date()) }, 
        { new Document("you", Arrays.asList(new Item("abcd1234", 1, "!@#$%^&*()_+{}|[]\\:\";'<>,.?/+-_", Priority.HIGH, Status.IN_PROGRESS)), new Date()) }, 
    };
    // @formatter:on
    
    @Override
    public String getModuleName() {
        return TestUtils.MODULE_NAME;
    }
    
    @Test
    public void testGetDocumentFromJson() {
        for (int i = 0; i < PARAMETERS.length; i++) {
            testGetDocumentFromJson((Document) PARAMETERS[i][0]);
        }
    }

    @Test
    public void testGetItemFromJson() {
        for (int i = 0; i < PARAMETERS.length; i++) {
            Document document = (Document) PARAMETERS[i][0];
            for (Item item : document.getItems()) {
                testGetItemFromJson(item);
            }
        }
    }

    @Test
    public void testGetDocumentFromString() {
        for (int i = 0; i < PARAMETERS.length; i++) {
            testGetDocumentFromString((Document) PARAMETERS[i][0]);
        }
    }

    @Test
    public void testGetItemFromString() {
        for (int i = 0; i < PARAMETERS.length; i++) {
            Document document = (Document) PARAMETERS[i][0];
            for (Item item : document.getItems()) {
                testGetItemFromString(item);
            }
        }
    }
    
    private void testGetDocumentFromJson(Document document) {
        JSONObject json = JsonSerializer.toJson(document);
        Document documentx = JsonSerializer.getDocument(json);
        TestUtils.assertDocumentEquals(document, documentx, false);
    }

    private void testGetDocumentFromString(Document document) {
        String json = JsonSerializer.toString(document);
        Document documentx = JsonSerializer.getDocument(json);
        TestUtils.assertDocumentEquals(document, documentx, false);
    }

    private void testGetItemFromJson(Item item) {
        JSONObject json = JsonSerializer.toJson(item);
        Item itemx = JsonSerializer.getItem(json);
        TestUtils.assertItemEquals(item, itemx);
    }

    private void testGetItemFromString(Item item) {
        String json = JsonSerializer.toString(item);
        Item itemx = JsonSerializer.getItem(json);
        TestUtils.assertItemEquals(item, itemx);
    }
}
