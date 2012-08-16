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

package com.stoyanr.todo.client.util;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(value = Parameterized.class)
public class LocalStorageNotPresentTest {

    private final String stringValue;
    private final long longValue;
    private final boolean booleanValue;
    private final Date dateValue;

    LocalStorage storage;
    
    private static Object[][] PARAMETERS = new Object[][] { 
        { "", 0L, false, new Date(0) }, 
        { "xxx", 1L, true, new Date() } 
    };
    
    public LocalStorageNotPresentTest(String stringValue, long longValue,
        boolean booleanValue, Date dateValue) {
        this.stringValue = stringValue;
        this.longValue = longValue;
        this.booleanValue = booleanValue;
        this.dateValue = dateValue;
    }

    @Parameters
    public static Collection<Object[]> parameters() {
        return Arrays.asList(PARAMETERS);
    }

    @Before
    public void setUp() {
        storage = new LocalStorage(null);
        storage.clearValues();
    }

    @Test
    public void testIsPresent() {
        assertEquals(false, storage.isPresent());
    }

    @Test
    public void testGetStringValue() {
        storage.setStringValue("s", stringValue);
        assertEquals("", storage.getStringValue("s"));
    }

    @Test
    public void testGetLongValue() {
        storage.setLongValue("l", longValue);
        assertEquals(0, storage.getLongValue("l"));
    }

    @Test
    public void testGetBooleanValue() {
        storage.setBooleanValue("b", booleanValue);
        assertEquals(false, storage.getBooleanValue("b"));
    }

    @Test
    public void testGetDateValue() {
        storage.setDateValue("d", dateValue);
        assertEquals(new Date(0), storage.getDateValue("d"));
    }

    @Test
    public void testRemoveValue() {
        storage.setStringValue("s", stringValue);
        storage.removeValue("s");
        assertEquals("", storage.getStringValue("s"));
    }

    @Test
    public void testClearValues() {
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
