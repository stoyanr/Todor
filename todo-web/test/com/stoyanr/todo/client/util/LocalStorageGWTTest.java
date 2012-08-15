
package com.stoyanr.todo.client.util;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.storage.client.Storage;

// This test case succeeds only in production, Manual mode, 
// since local storage is not supported in HtmlUnit browser
// Run it with: -Dgwt.args="-prod -runStyle Manual:1"
public class LocalStorageGWTTest extends GWTTestCase {

    LocalStorage storage;
    
    @Override
    public String getModuleName() {
        return TestUtils.MODULE_NAME;
    }
    
    private static Object[][] PARAMETERS = new Object[][] { 
        { "", 0L, false, new Date(0) }, 
        { "xxx", 1L, true, new Date() } 
    };

    @Before
    public void gwtSetUp() {
        Storage ls = Storage.getLocalStorageIfSupported();
        storage = new LocalStorage(ls);
        storage.clearValues();
    }
    
    @Test
    public void testIsPresent() {
        assertEquals(true, storage.isPresent());
    }
    
    @Test
    public void testGetStringValue() {
        for (int i = 0; i < PARAMETERS.length; i++) {
            testGetStringValue((String) PARAMETERS[i][0]);
        }
    }

    @Test
    public void testGetLongValue() {
        for (int i = 0; i < PARAMETERS.length; i++) {
            testGetLongValue(((Long) PARAMETERS[i][1]).longValue());
        }
    }

    @Test
    public void testGetBooleanValue() {
        for (int i = 0; i < PARAMETERS.length; i++) {
            testGetBooleanValue(((Boolean) PARAMETERS[i][2]).booleanValue());
        }
    }

    @Test
    public void testGetDateValue() {
        for (int i = 0; i < PARAMETERS.length; i++) {
            testGetDateValue((Date) PARAMETERS[i][3]);
        }
    }

    @Test
    public void testRemoveValue() {
        for (int i = 0; i < PARAMETERS.length; i++) {
            testRemoveValue((String) PARAMETERS[i][0]);
        }
    }

    @Test
    public void testClearValues() {
        for (int i = 0; i < PARAMETERS.length; i++) {
            testClearValues((String) PARAMETERS[i][0],
                ((Long) PARAMETERS[i][1]).longValue(),
                ((Boolean) PARAMETERS[i][2]).booleanValue(),
                (Date) PARAMETERS[i][3]);
        }
    }

    private void testGetStringValue(String stringValue) {
        storage.setStringValue("s", stringValue);
        assertEquals(stringValue, storage.getStringValue("s"));
    }

    private void testGetLongValue(long longValue) {
        storage.setLongValue("l", longValue);
        assertEquals(longValue, storage.getLongValue("l"));
    }

    private void testGetBooleanValue(boolean booleanValue) {
        storage.setBooleanValue("b", booleanValue);
        assertEquals(booleanValue, storage.getBooleanValue("b"));
    }

    private void testGetDateValue(Date dateValue) {
        storage.setDateValue("d", dateValue);
        assertEquals(dateValue, storage.getDateValue("d"));
    }

    private void testRemoveValue(String stringValue) {
        storage.setStringValue("s", stringValue);
        storage.removeValue("s");
        assertEquals("", storage.getStringValue("s"));
    }

    private void testClearValues(String stringValue, long longValue,
        boolean booleanValue, Date dateValue) {
        storage.setStringValue("s", stringValue);
        storage.setLongValue("l", longValue);
        storage.setBooleanValue("b", booleanValue);
        storage.setDateValue("d", dateValue);
        storage.clearValues();
        assertEquals("", storage.getStringValue("s"));
        assertEquals(0, storage.getLongValue("l"));
        assertEquals(false, storage.getBooleanValue("b"));
        assertEquals(new Date(0), storage.getDateValue("d"));
    }
}
