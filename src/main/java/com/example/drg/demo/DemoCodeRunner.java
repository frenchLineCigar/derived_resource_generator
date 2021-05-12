package com.example.drg.demo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

public class DemoCodeRunner {

    public static void main(String[] args) {
        String spec = "https://i.picsum.photos/id/152/536/354.jpg?hmac=Vh-3tACtfo0tExdnZBiHdzcsxRIS0Q-a8GN1QSC0b3U&name=4";
        // String spec = "https://file-examples-com.github.io/uploads/2017/02/file-sample_100kB.doc";
        String outputDir = "/2021/derived_resource_generator-file/tmp";

        fileDownloadByHttpUrlConnection(spec, outputDir);
//        DemoCodeRunner.extractUrl();
//        DemoCodeRunner.splitString();
//        DemoCodeRunner.indexOfString();
    }

    /*
    HttpUrlConnection 객체를 통해 오픈된 파일을 다운로드
    - 기본적인 흐름: 1. HttpUrlConnection 연결 -> 2. Stream 을 통해 다운로드
    - 주의: 중복된 파일명일 경우 덮어쓰기 처리함
     */
    public static void fileDownloadByHttpUrlConnection(String spec, String outputDir) {
        InputStream is = null;
        FileOutputStream os = null;

        try{
            // 1. HttpUrlConnection 연결
            URL url = new URL(spec); // URL 객체 생성
            HttpURLConnection conn = (HttpURLConnection) url.openConnection(); // URL 객체로부터 HttpUrlConnection 객체를 받아옴
            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode); // responseCode : 200

            // 2. Stream 을 통해 다운로드
            // 통신에 성공했으면 남은 작업은 Stream 을 통해 파일을 가져오는 것

            // Status 가 200 일 때
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Content-Disposition or URL 에서 파일명 가져오기
                String fileName = "";
                String disposition = conn.getHeaderField("Content-Disposition");
                String contentType = conn.getContentType();

                // 일반적으로 Content-Disposition 헤더에 있지만
                // 없을 경우 url 에서 추출해 내면 된다.
                if (disposition != null) {
                    String target = "filename=";
                    int index = disposition.indexOf(target);
                    if (index != -1) {
                        fileName = disposition.substring(index + target.length());
                        fileName = fileName.replaceAll("\"", ""); //앞뒤 따옴표 제거
                    }
                } else {
                    fileName = spec.substring(spec.lastIndexOf("/") + 1);
                }

                System.out.println("Content-Type = " + contentType);
                System.out.println("Content-Disposition = " + disposition);
                System.out.println("fileName = " + fileName);

                // 경로가 생성되지 않았다면 해당 경로를 생성
                File fileDir = new File(outputDir);
                if (! fileDir.exists()) {
                    fileDir.mkdirs();
                }

                // InputStream ➡️ FileOutputStream 을 통해 파일 다운로드
                is = conn.getInputStream();
                os = new FileOutputStream(new File(fileDir, fileName));
                //os = new FileOutputStream(new File(outputDir, fileName));

                final int BUFFER_SIZE = 4096;
                int bytesRead;
                byte[] buffer = new byte[BUFFER_SIZE];
                while ((bytesRead = is.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
                os.close();
                is.close();
                System.out.println("File downloaded");
            } else {
                System.out.println("No file to download. Server replied HTTP code: " + responseCode);
            }
            conn.disconnect();

        } catch (Exception e){
            System.out.println("An error occurred while trying to download a file.");
            e.printStackTrace();
            try {
                if (is != null){
                    is.close();
                }
                if (os != null){
                    os.close();
                }
            } catch (IOException e1){
                e1.printStackTrace();
            }
        }

    }

    public static void extractUrl() {
        String queryString = "url=https://i.picsum.photos/id/152/536/354.jpg?hmac=Vh-3tACtfo0tExdnZBiHdzcsxRIS0Q-a8GN1QSC0b3U&name=4";

        String url = queryString.split("url=")[1];
        System.out.println("url = " + url);
    }

    public static void splitString() {
        String queryString = "url=https://i.picsum.photos/id/152/536/354.jpg?hmac=Vh-3tACtfo0tExdnZBiHdzcsxRIS0Q-a8GN1QSC0b3U&name=4";

        // split
        // String[] split = queryString.split("");
        // String[] split = queryString.split("=");
        String[] split = queryString.split("url=");
        System.out.println("split = " + Arrays.toString(split));

        String elem1 = split[0];
        String elem2 = split[1];
        System.out.println("elem1 = " + elem1);
        System.out.println("elem2 = " + elem2);
    }

    public static void indexOfString() {
        String queryString = "url=https://i.picsum.photos/id/152/536/354.jpg?hmac=Vh-3tACtfo0tExdnZBiHdzcsxRIS0Q-a8GN1QSC0b3U&name=4";

        //indexOf
        int indexOfu = queryString.indexOf("u");
        int indexOfr = queryString.indexOf("r");
        int indexOfl = queryString.indexOf("l");
        int indexOfUrlStart = queryString.indexOf("url=");

        System.out.println("indexOfUrlStart = " + indexOfUrlStart);
    }

    public static void indexOfAndSubstring() {
        String url = "id=1&name=kanu123&age=100&address=";
        String target = "name=";
        int index = url.indexOf(target);
        System.out.println("index:" + index);
        String name = url.substring(index + target.length(), url.indexOf("&age="));
        System.out.println("name:" + name);
    }
}
