package com.abovebytes.stack.sentinel.interceptors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Locale;

public class LocaleChangeInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String lang = request.getParameter("lang");
        Locale defaultLocale = Locale.FRENCH;
        Locale locale;
        if (lang != null) {
            locale = new Locale(lang);
        } else {
            locale = defaultLocale;
        }

        LocaleContextHolder.setLocale(locale);

        return true;
    }
}
