package com.example.drg;

import com.example.drg.util.Util;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
public class UserImgController {

    @Value("${custom.tmpDirPath}")
    private String tmpDirPath;

    @RequestMapping("/img")
    public String showImg(HttpServletRequest req, @RequestParam Map<String, Object> param) {
        String queryString = req.getQueryString(); // 쿼리 스트링 추출
        String url = queryString.split("url=")[1]; //쿼리 파라미터로 전달된 파일 url 추출

        String filePath = Util.downloadFileByHttp(url, tmpDirPath);

        return filePath;
    }

}
