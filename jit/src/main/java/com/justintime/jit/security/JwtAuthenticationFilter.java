package com.justintime.jit.security;

import com.justintime.jit.service.JwtService;
import com.justintime.jit.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    ApplicationContext context;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        String authHeader = request.getHeader("Authorization");
        String token = null;
        String email = null;

        // Get token from cookie
        if(request.getCookies() != null) {
            for(var cookie : request.getCookies()) {
                if("accessToken".equals(cookie.getName())) {
                    token = cookie.getValue();
                }
            }
        }

        if(token != null) email = jwtService.extractEmail(token);

//        if(authHeader != null && authHeader.startsWith("Bearer")) {
//            token = authHeader.substring(7);
//            email = jwtService.extractEmail(token);
//        }

        if(email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = context.getBean(UserService.class).loadUserByUsername(email);
            String restaurantCode = (String) jwtService.extractRestaurantCode(token);
            if(jwtService.validateToken(token, userDetails)) {
                CustomAuthToken userPassToken =
                        new CustomAuthToken(restaurantCode, userDetails,null, userDetails.getAuthorities());
                userPassToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(userPassToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}

/**
 * package com.justintime.jit.config;
 *
 * import com.justintime.jit.service.JwtService;
 * import jakarta.servlet.FilterChain;
 * import jakarta.servlet.ServletException;
 * import jakarta.servlet.http.Cookie;
 * import jakarta.servlet.http.HttpServletRequest;
 * import jakarta.servlet.http.HttpServletResponse;
 * import jakarta.servlet.http.HttpSession;
 * import org.slf4j.Logger;
 * import org.slf4j.LoggerFactory;
 * import org.springframework.beans.factory.annotation.Autowired;
 * import org.springframework.lang.NonNull;
 * import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
 * import org.springframework.security.core.context.SecurityContext;
 * import org.springframework.security.core.context.SecurityContextHolder;
 * import org.springframework.security.core.userdetails.UserDetails;
 * import org.springframework.security.core.userdetails.UserDetailsService;
 * import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
 * import org.springframework.stereotype.Component;
 * import org.springframework.web.filter.OncePerRequestFilter;
 *
 * import java.io.IOException;
 *
 * @Component
 * public class JwtAuthenticationFilter extends OncePerRequestFilter {
 *
 *     private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
 *     private static final String AUTH_COOKIE_NAME = "accessToken";
 *     // A unique key to store the Authentication object in the HttpSession
 *     private static final String WEBSOCKET_AUTH_KEY = "WEBSOCKET_AUTHENTICATION";
 *
 *     @Autowired
 *     private JwtService jwtService;
 *
 *     @Autowired
 *     private UserDetailsService userDetailsService;
 *
 *     @Override
 *     protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
 *         if (request.getRequestURI().startsWith("/ws")) {
 *             handleWebSocketAuthentication(request);
 *         } else {
 *             handleRestApiAuthentication(request);
 *         }
 *         filterChain.doFilter(request, response);
 *     }
 *
 *     private void handleRestApiAuthentication(HttpServletRequest request) {
 *         authenticateWithJwt(request, null);
 *     }
 *
 *     private void handleWebSocketAuthentication(HttpServletRequest request) {
 *         HttpSession session = request.getSession();
 *         SecurityContext securityContext = SecurityContextHolder.getContext();
 *
 *         // 1. FAST PATH: Check if authentication is already stored in the session.
 *         Object sessionAuth = session.getAttribute(WEBSOCKET_AUTH_KEY);
 *         if (sessionAuth instanceof UsernamePasswordAuthenticationToken) {
 *             logger.trace("Found cached authentication for WebSocket session: {}", session.getId());
 *             securityContext.setAuthentication((UsernamePasswordAuthenticationToken) sessionAuth);
 *             return;
 *         }
 *
 *         // 2. EXPENSIVE PATH: No authentication in session, so validate the JWT.
 *         // This will run only on the *first* request of a SockJS session.
 *         authenticateWithJwt(request, session);
 *     }
 *
 *

private void authenticateWithJwt(HttpServletRequest request, HttpSession session) {
 *if (SecurityContextHolder.getContext().getAuthentication() != null) {
 *             // Already authenticated, perhaps by a previous filter. Nothing to do.
 *return;
 *}
 *
 *String jwt = null;
 *if (request.getCookies() != null) {
 *for (Cookie cookie : request.getCookies()) {
 *if (AUTH_COOKIE_NAME.equals(cookie.getName())) {
 *jwt = cookie.getValue();
 *break;
 *}
 *}
 *}
 *
 *if (jwt != null) {
 *try {
 *String username = jwtService.extractEmail(jwt);
 *if (username != null) {
 *logger.debug("Validating token for user: {}", username);
 *UserDetails userDetails = this.userDetailsService.loadUserByUsername(username); // DB HIT!
 *if (jwtService.validateToken(jwt, userDetails)) {
 *UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            * userDetails,
 *null,
 *userDetails.getAuthorities()
                            *                         );
 *authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
 *SecurityContextHolder.getContext().setAuthentication(authToken);
 *logger.info("Successfully authenticated user '{}' for WebSocket session.", username);
 *
 *                         // 3. CACHE IT: If this is a WebSocket request, store the successful authentication in the session.
 *if (session != null) {
 *session.setAttribute(WEBSOCKET_AUTH_KEY, authToken);
 *logger.debug("Authentication cached in WebSocket session: {}", session.getId());
 *}
 *}
 *}
 *} catch (Exception e) {
 *logger.warn("JWT validation failed for request {}: {}", request.getRequestURI(), e.getMessage());
 *}
 *}
 *}
 *}
 */