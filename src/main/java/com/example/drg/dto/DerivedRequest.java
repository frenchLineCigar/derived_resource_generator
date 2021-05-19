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
    private String requestUrl;
    private String originUrl;
    private int width;
    private int height;
    private int maxWidth;

    public static DerivedRequest create(String requestUrl, String originUrl, int width, int height, int maxWidth) {
        return DerivedRequest.builder().requestUrl(requestUrl).originUrl(originUrl).width(width).height(height).maxWidth(maxWidth).build();
    }
}
