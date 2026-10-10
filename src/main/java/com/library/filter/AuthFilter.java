package com.library.filter;

import com.library.entity.Role;
import com.library.entity.User;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Very simple session-based guard:
 * - /librarian/**  -> must be logged in as LIBRARIAN
 * - /member/**     -> must be logged in as MEMBER
 * Everything else (login, register, static assets, public book search) is open.
 */
@Component
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        String path = request.getRequestURI();

        boolean needsLibrarian = path.startsWith("/librarian");
        boolean needsMember = path.startsWith("/member");

        if (needsLibrarian || needsMember) {
            HttpSession session = request.getSession(false);
            User user = session != null ? (User) session.getAttribute("user") : null;

            if (user == null) {
                response.sendRedirect("/login");
                return;
            }
            if (needsLibrarian && user.getRole() != Role.LIBRARIAN) {
                response.sendRedirect("/login?error=unauthorized");
                return;
            }
            if (needsMember && user.getRole() != Role.MEMBER) {
                response.sendRedirect("/login?error=unauthorized");
                return;
            }
        }

        chain.doFilter(req, res);
    }
}
