package com.szymonharabasz.grocerylistmanager.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.logging.Logger;

@WebFilter(filterName = "logHeadersFilter", urlPatterns = "/*")
public class LogHeadersFilter implements Filter {
    private final Logger logger = Logger.getLogger(LogHeadersFilter.class.getName());

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (!((HttpServletRequest) servletRequest).getRequestURI().contains("resource")) {
            StringBuilder requestHeadersBuilder = new StringBuilder();
            Enumeration<String> requestHeaderNames = ((HttpServletRequest) servletRequest).getHeaderNames();

            requestHeadersBuilder.append("Headers for request to ").append(((HttpServletRequest) servletRequest).getRequestURI()).append(":\n");
            while (requestHeaderNames.hasMoreElements()) {
                String header = requestHeaderNames.nextElement();
                Enumeration<String> headerValues = ((HttpServletRequest) servletRequest).getHeaders(header);
                while (headerValues.hasMoreElements()) {
                    requestHeadersBuilder.append(header).append(" = ").append(headerValues.nextElement()).append("\n");
                }
            }
            logger.info(requestHeadersBuilder.toString());
        }
        filterChain.doFilter(servletRequest, servletResponse);
        if (!((HttpServletRequest) servletRequest).getRequestURI().contains("resource")) {
            StringBuilder responseHeadersBuilder = new StringBuilder();
            Collection<String> responseHeaderNames = ((HttpServletResponse) servletResponse).getHeaderNames();
            responseHeadersBuilder.append("Headers for response to ").append(((HttpServletRequest) servletRequest).getRequestURI()).append(":\n");
            for (String header : responseHeaderNames) {
                for (String headerValue : ((HttpServletResponse) servletResponse).getHeaders(header)) {
                    responseHeadersBuilder.append(header).append(" = ").append(headerValue).append("\n");
                }
            }
            logger.info(responseHeadersBuilder.toString());
        }
    }

    @Override
    public void destroy() {

    }
}
