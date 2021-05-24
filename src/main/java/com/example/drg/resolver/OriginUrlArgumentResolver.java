package com.example.drg.resolver;

import com.example.drg.annotation.OriginUrl;
import com.example.drg.annotation.RequestUrl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Component
public class OriginUrlArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        OriginUrl parameterAnnotation = parameter.getParameterAnnotation(OriginUrl.class);
        String parameterName = parameter.getParameterName();

        return parameterAnnotation != null && "originUrl".equals(parameterName);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest req = (HttpServletRequest) webRequest.getNativeRequest();
        String requestUrl = req.getRequestURI() + "?" + req.getQueryString();

        String originUrl = getOriginUrl(requestUrl); // 이미지 URL

        return originUrl;
    }

    private String getOriginUrl(String requestUrl) {

        String originUrl = requestUrl.split("url=")[1];

        boolean discriminator = originUrl.contains("&width") || originUrl.contains("&height") || originUrl.contains("&maxWidth");

        if (discriminator) {
            int widthIndex = originUrl.indexOf("&width");
            int heightIndex = originUrl.indexOf("&height");
            int maxWidthIndex = originUrl.indexOf("&maxWidth");

            int[] indexes = {widthIndex, heightIndex, maxWidthIndex};
            int minIndex = originUrl.length();

            for (int value : indexes) {

                if (value == -1) {
                    continue;
                }

                if (value < minIndex) {
                    minIndex = value;
                }
            }

            originUrl = originUrl.substring(0, minIndex);
        }

        return originUrl;
    }

//    public static void main(String[] args) {
//        String requestUrl = "http://localhost:8022/img?url=https://i.picsum.photos/id/152/536/354.jpg?hmac=Vh-3tACtfo0tExdnZBiHdzcsxRIS0Q-a8GN1QSC0b3U&name=4&width=300&height=300";
//        String originUrl = getOriginUrl(requestUrl);
//        log.info("originUrl = " + originUrl);
//    }

}
