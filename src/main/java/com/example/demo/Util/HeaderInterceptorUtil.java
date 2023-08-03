package com.example.demo.Util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//This class is used as an interceptor in a Java web application to modify the response headers before handling a request.
public class HeaderInterceptorUtil extends HandlerInterceptorAdapter
{
    private static Logger log = LoggerFactory.getLogger(HeaderInterceptorUtil.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//Conveniently record the access log of the request for debugging and tracking the processing of the request.
        log.info("[" + request.getRequestURI() + "]" + "[" + request.getMethod() + "]" );
        // Set response headers to disable caching
        //Ensures that responses are not cached by browsers or intermediate caches,
        // thus avoiding the problem of returning stale data
        response.setHeader("Cache-Control","no-cache");
        response.setHeader("Cache-Control","no-store");
        response.setHeader("Pragma","no-cache");
        response.setDateHeader ("Expires",0);

        return true;
    }


}
