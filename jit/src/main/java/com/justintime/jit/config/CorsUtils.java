package com.justintime.jit.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import java.net.*;
import org.springframework.security.web.util.UrlUtils;
import org.apache.commons.validator.routines.UrlValidator;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

public class CorsUtils {
    public static final String INVALID_URL="Invalid URL format";
    public static final String INVALID_PATH_EXCEPTION="Invalid Origin-path is not allowed on an Origin URL";
    public static final String INVALID_PROTOCOL_EXCEPTION="Invalid protocol";

    //return true if the request exists and does not match the source url , false otherwise.
    public static boolean isCorsRequest(HttpServletRequest request){
       return request.getHeader(HttpHeaders.ORIGIN) != null && !isSameOrigin(request);
    }

    public static boolean isPreflightRequest(HttpServletRequest request){
        return isCorsRequest(request) && HttpMethod.OPTIONS.matches(request.getMethod())&&
                request.getHeader(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD) != null;
    }

    public static boolean isSameOrigin(HttpServletRequest request){
        String origin = request.getHeader(HttpHeaders.ORIGIN);
        String requestUrl = request.getRequestURL().toString();
        return origin != null && requestUrl.startsWith(origin);
    }

    private static  boolean isURLEqual(URL url1, URL url2){
        return url1.getProtocol().equals(url2.getProtocol()) &&
                url1.getHost().equals(url2.getHost()) &&
                url1.getPort() == url2.getPort();
    }

    private static boolean isPortEqual(URL url1, URL url2){
       if(url1.getPort() == -1 && url2.getPort() == -1) {
            return true;
        }
        return url1.getPort() == url2.getPort();
    }

    public static void validateOrigin(String origin) throws MalformedURLException,URISyntaxException {
        String[] schemes = {"http", "https"};
        UrlValidator validator = new UrlValidator(schemes);

        if (!validator.isValid(origin)) {
            throw new MalformedURLException(INVALID_URL);
        }

        URL url = new URL(origin);

        if (!url.getPath().isEmpty() && !url.getPath().equals("/")) {
            throw new MalformedURLException(INVALID_PATH_EXCEPTION);
        }

        if (!"http".equalsIgnoreCase(url.getProtocol()) && !"https".equalsIgnoreCase(url.getProtocol())) {
            throw new MalformedURLException(INVALID_PROTOCOL_EXCEPTION);
        }}

}
