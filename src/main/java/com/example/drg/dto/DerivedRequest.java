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
    private boolean originStatus; //원본 파일을 가지고 있는 요청인지 여부
    private int width;
    private int height;
    private int maxWidth;
    private int genFileId; // 관련된 파일 아이디

    public static DerivedRequest create(String requestUrl, String originUrl, boolean originStatus, int width, int height, int maxWidth) {
        return DerivedRequest.builder().requestUrl(requestUrl).originUrl(originUrl).originStatus(originStatus).width(width).height(height).maxWidth(maxWidth).build();
    }
}
