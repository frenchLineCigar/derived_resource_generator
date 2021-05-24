package com.example.drg.resolver;

import com.example.drg.annotation.RequestURL;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Component
public class RequestURLArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        //GetMapping methodAnnotation = parameter.getMethodAnnotation(GetMapping.class);
        RequestURL parameterAnnotation = parameter.getParameterAnnotation(RequestURL.class);

        return parameterAnnotation != null && "requestUrl".equals(parameter.getParameterName());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest req = (HttpServletRequest) webRequest.getNativeRequest();

        String requestUrl = req.getRequestURI() + "?" + req.getQueryString(); // 현재 요청 url

        return requestUrl;
    }
}
