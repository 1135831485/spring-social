package org.springframework.social.facebook;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.context.request.NativeWebRequest;

/**
 * <p>
 * Web argument resolver that resolves arguments annotated with
 * {@link FacebookAccessToken} or {@link FacebookUserId}.
 * </p>
 * 
 * <p>
 * After a user has authenticated with Facebook via the XFBML
 * &lt;fb:login-button&gt; tag, their user ID and an access token are stored in
 * a cookie whose name is "fbs_{application key}". This web argument resolver
 * extracts that information from the cookie (if available) and supplies it to a
 * controller handler method as String values.
 * </p>
 * 
 * @author Craig Walls
 */
public class FacebookWebArgumentResolver implements WebArgumentResolver {

	private final String apiKey;

	public FacebookWebArgumentResolver(String apiKey) {
		this.apiKey = apiKey;
	}
	
	public Object resolveArgument(MethodParameter parameter, NativeWebRequest request) throws Exception {
		HttpServletRequest nativeRequest = (HttpServletRequest) request.getNativeRequest();
		Cookie[] cookies = nativeRequest.getCookies();
		if (cookies == null) {
			return WebArgumentResolver.UNRESOLVED;
		}
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals("fbs_" + apiKey)) {
            		return processParameterAnnotation(parameter, extractDataFromCookie(cookie.getValue()));
            }
        }
		return WebArgumentResolver.UNRESOLVED;
	}

	private Object processParameterAnnotation(MethodParameter parameter, Map<String, String> cookieData) {
	    if (parameter.getParameterAnnotation(FacebookUserId.class) != null) {
	    		return cookieData.get("uid");
	    } else if (parameter.getParameterAnnotation(FacebookAccessToken.class) != null) {
	    		String accessToken = cookieData.get("access_token");
			return accessToken != null ? accessToken.replaceAll("\\%7C", "|") : null;
	    } else {
	    		return WebArgumentResolver.UNRESOLVED;
	    }
    }
	
	/*
	 * Stuff you should expect from this cookie:
	 *   access_token
	 *   expires
	 *   secret
	 *   session_key
	 *   sig
	 *   uid
	 */
	private Map<String, String> extractDataFromCookie(String cookieValue) {
		HashMap<String, String> data = new HashMap<String, String>();
		String[] fields = cookieValue.split("\\&");
		for (String field : fields) {
	        String[] keyValue = field.split("\\=");
	        data.put(keyValue[0], keyValue[1]);
        }
		return data;
	}
	
}