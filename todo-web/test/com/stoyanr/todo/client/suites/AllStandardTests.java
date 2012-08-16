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

package com.stoyanr.todo.client.suites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.stoyanr.todo.client.presenter.DocumentDataTest;
import com.stoyanr.todo.client.presenter.DocumentPresenterTest;
import com.stoyanr.todo.client.util.LocalStorageNotPresentTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ LocalStorageNotPresentTest.class, DocumentDataTest.class,
    DocumentPresenterTest.class, })
public class AllStandardTests {
}
