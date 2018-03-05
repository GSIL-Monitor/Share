package com.beta.api.helper;

import javax.servlet.http.HttpServletRequest;

/**
 * http request session
 * 
 * @author sucre
 *
 */
public class SessionHelper {

	public static void setAttribute(HttpServletRequest request, String key, Object value) {
		request.getSession().setAttribute(key, value);
	}

}
