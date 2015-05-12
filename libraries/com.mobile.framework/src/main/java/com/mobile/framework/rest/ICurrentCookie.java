package com.mobile.framework.rest;

/**
 * Copyright (C) 2015 Africa Internet Group - All Rights Reserved
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential.
 *
 * @author Ricardo Soares
 * @version 1.0
 * @date 2015/04/23
 */
public interface ICurrentCookie {
    public void addCookie(String encodedCookie);
    public String getCurrentCookie();
}
