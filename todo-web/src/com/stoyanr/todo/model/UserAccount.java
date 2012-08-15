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

package com.stoyanr.todo.model;

import com.google.gwt.user.client.rpc.IsSerializable;

public class UserAccount implements IsSerializable {

    private String userId;
    private String nickname;
    private String email;
    private boolean loggedIn;
    private String loginUrl;
    private String logoutUrl;

    public UserAccount() {
        this(null, null, null, false, null, null);
    }

    public UserAccount(String userId, String nickname, String email,
        boolean loggedIn, String loginUrl, String logoutUrl) {
        this.userId = userId;
        this.nickname = nickname;
        this.email = email;
        this.loggedIn = loggedIn;
        this.loginUrl = loginUrl;
        this.logoutUrl = logoutUrl;
    }

    public String getUserId() {
        return userId;
    }

    public String getNickname() {
        return nickname;
    }

    public String getEmail() {
        return email;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public String getLoginUrl() {
        return loginUrl;
    }

    public String getLogoutUrl() {
        return logoutUrl;
    }

}
