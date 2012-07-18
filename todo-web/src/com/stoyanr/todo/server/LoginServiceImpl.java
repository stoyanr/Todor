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

package com.stoyanr.todo.server;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.stoyanr.todo.client.LoginService;
import com.stoyanr.todo.model.UserAccount;

@SuppressWarnings("serial")
public class LoginServiceImpl extends RemoteServiceServlet implements
    LoginService {

    @Override
    public UserAccount login(String requestUri) {
        UserAccount userAccount = null;
        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();
        if (user != null) {
            String logoutUrl = userService.createLogoutURL(requestUri);
            userAccount = new UserAccount(user.getNickname(), user.getEmail(),
                true, null, logoutUrl);
        } else {
            String loginUrl = userService.createLoginURL(requestUri);
            userAccount = new UserAccount(null, null, false, loginUrl, null);
        }
        return userAccount;
    }
}