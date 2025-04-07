package com.justintime.jit.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CorsProcessor {
    public boolean processRequest(HttpServletRequest request, HttpServletResponse response) {

        return true; // Return true if the request is valid, false otherwise
    }
}
