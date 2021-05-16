package com.example.drg.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DerivedRequest {
    private int id;
    private String regDate;
    private String updateDate;
    private String url;
    private String originUrl;
    private int width;
    private int height;
    private int maxWidth;

    public boolean matchUrl(String url) {
        return this.url.equals(url);
    }

    public boolean matchOriginUrl(String originUrl) {
        return this.originUrl.equals(originUrl);
    }

    public static DerivedRequest create(String url, String originUrl, int width, int height, int maxWidth) {
        return DerivedRequest.builder().url(url).originUrl(originUrl).width(width).height(height).maxWidth(maxWidth).build();
    }
}
