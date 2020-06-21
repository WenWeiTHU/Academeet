package com.mobilecourse.backend;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Configuration
@WebFilter(urlPatterns = "/api")
public class SimpleCORSFilter extends OncePerRequestFilter {
    private final static String ALLOWORIGIN_CORS_A = "https://localhost:8080";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Access-Control-Allow-Origin: 指定授权访问的域
        response.addHeader("Access-Control-Allow-Origin", ALLOWORIGIN_CORS_A);  //此优先级高于@CrossOrigin配置

        // Access-Control-Allow-Methods: 授权请求的方法（GET, POST, PUT, DELETE，OPTIONS等)
        response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");

        response.addHeader("Access-Control-Allow-Headers", "Content-Type");

        response.addHeader("Access-Control-Max-Age", "1800");//30 min
        filterChain.doFilter(request, response);
    }
}