package com.example.drg.dto;

import com.example.drg.util.Util;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;

@Component
public class DerivedRequestArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return DerivedRequest.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest req = (HttpServletRequest) webRequest.getNativeRequest();

        String requestUrl = req.getRequestURI() + "?" + req.getQueryString(); // 현재 요청 url
        String originUrl = req.getParameter("url"); // 파일 url

        int width = Util.getAsInt(req.getParameter("width"), 0);
        int height = Util.getAsInt(req.getParameter("height"), 0);
        int maxWidth = Util.getAsInt(req.getParameter("maxWidth"), 0);

        return DerivedRequest.create(requestUrl, originUrl, false, width, height, maxWidth);
    }
}
