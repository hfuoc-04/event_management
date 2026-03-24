package com.demo.demo.config;

import com.demo.demo.entity.Account;
import com.demo.demo.exception.exceptions.AuthenticationException;
import com.demo.demo.service.TokenService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.List;

@Component
public class Filter extends OncePerRequestFilter {

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver resolver;

    @Autowired
    private TokenService tokenService;

    // A list of public APIs that do not require authentication.
    // Format: "METHOD:/api/path"
    private static final List<String> PUBLIC_API = List.of(
            "POST:/api/register",
            "POST:/api/login",
            "PUT:/api/register-event/check-in/**"
    );

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    /**
     * Checks if a given request targets a public API.
     *
     * @param uri    The request URI.
     * @param method The request HTTP method.
     * @return true if the API is public, false otherwise.
     */
    public boolean isPublicAPI(String uri, String method) {
        // A specific rule to allow all GET requests without authentication.
        // Consider if this is the desired behavior for your application.
        if (method.equalsIgnoreCase("GET")) {
            return true;
        }

        // Check against the defined list of public APIs.
        return PUBLIC_API.stream().anyMatch(pattern -> {
            String[] parts = pattern.split(":", 2);
            if (parts.length != 2) {
                return false; // Invalid pattern format
            }
            String allowedMethod = parts[0];
            String allowedUri = parts[1];
            // Match both the HTTP method and the URI pattern.
            return method.equalsIgnoreCase(allowedMethod) && pathMatcher.match(allowedUri, uri);
        });
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        final String uri = request.getRequestURI();
        final String method = request.getMethod();

        if (isPublicAPI(uri, method)) {
            // If the API is public, let the request through without checking the token.
            filterChain.doFilter(request, response);
            return;
        }

        // For private APIs, authentication is required.
        final String token = getTokenFromHeader(request);

        if (token == null) {
            // If no token is provided, delegate the exception to the global handler.
            resolver.resolveException(request, response, null, new AuthenticationException("Authentication token is missing."));
            return;
        }

        try {
            // A token is present, now verify it.
            Account account = tokenService.extractAccount(token);

            // If the token is valid, set the authentication context.
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    account,
                    null, // Credentials are not needed as the user is already authenticated by token.
                    account.getAuthorities()
            );

            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);

            // Token is valid, proceed with the request.
            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            // Handle expired token.
            resolver.resolveException(request, response, null, new AuthenticationException("Expired Token!"));
        } catch (MalformedJwtException e) {
            // Handle invalid token format.
            resolver.resolveException(request, response, null, new AuthenticationException("Invalid Token!"));
        } catch (Exception e) {
            // Handle any other exceptions during token validation.
            resolver.resolveException(request, response, null, new AuthenticationException("Token validation failed."));
        }
    }

    /**
     * Extracts the JWT token from the "Authorization" header.
     *
     * @param request The HTTP request.
     * @return The token string or null if not found.
     */
    private String getTokenFromHeader(HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            // The token is the part after "Bearer ".
            return authHeader.substring(7);
        }
        return null;
    }
}