package com.example.drg.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DerivedRequest {
    private int id;
    private String regDate;
    private String updateDate;
    private String url;
    private String originUrl;
    private int width;
    private int height;
    private int maxWidth;
}
