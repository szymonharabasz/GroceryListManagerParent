package com.szymonharabasz.grocerylistmanager.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;
import java.io.IOException;
import java.util.Collection;
import java.util.logging.Logger;

@WebFilter(filterName = "sameSiteFilter", urlPatterns = "/*")
public class SameSiteFilter implements Filter {

    private final Logger logger = Logger.getLogger(SameSiteFilter.class.getName());

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        logger.info("SAME SITE FILTER IS APPLIED");
        chain.doFilter(request, response);
        addSameSiteCookieAttribute((HttpServletResponse) response); // add SameSite=strict cookie attribute
    }

    private void addSameSiteCookieAttribute(HttpServletResponse response) {
        Collection<String> headers = response.getHeaders(HttpHeaders.SET_COOKIE);
        boolean firstHeader = true;
        for (String header : headers) { // there can be multiple Set-Cookie attributes
            if (firstHeader) {
                response.setHeader(HttpHeaders.SET_COOKIE, String.format("%s; %s", header, "SameSite=Strict"));
                firstHeader = false;
                continue;
            }
            response.addHeader(HttpHeaders.SET_COOKIE, String.format("%s; %s", header, "SameSite=Strict"));
        }
    }
}