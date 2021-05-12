package com.example.drg.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;

import java.io.*;
import java.net.*;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.UUID;

@Slf4j
public class Util {

	/*
		Downloader for use : Using Java NIO
	 */
	public static String downloadFileByHttp(String fileUrl, String outputDir) {
		String originFileName = getFileNameFromUrl(fileUrl);
		String tempFileName = UUID.randomUUID() + "." + getFileExt(originFileName); // 파일명은 랜덤하게 정하더라도 확장자는 유지
		String filePath = outputDir + "/" + tempFileName;

		log.info("originFileName = " + originFileName);
		log.info("tempFileName = " + tempFileName);
		log.info("filePath = " + filePath);

		try (FileOutputStream fileOutputStream = new FileOutputStream(filePath)) {
			ReadableByteChannel readableByteChannel = Channels.newChannel(new URL(fileUrl).openStream());
			FileChannel fileChannel = fileOutputStream.getChannel();
			fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}

		return filePath;
	}

	// URL에서 파일명 추출
	private static String getFileNameFromUrl(String fileUrl) {
		try {
			return Paths.get(new URI(fileUrl).getPath()).getFileName().toString();
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return "";
		}
	}

	// 파일명에서 확장자 추출
	private static String getFileExt(String fileName) {
		int pos = fileName.lastIndexOf(".");
		String ext = fileName.substring(pos + 1);

		return ext;
	}

	/*
		Downloader v2 : Using Java IO
	 */
	public static String downloadFileByHttpV2(String fileUrl, String outputDir) {
		String originFileName = getFileNameFromUrlV2(fileUrl);
		String tempFileName = UUID.randomUUID() + "." + getFileExt(originFileName); // 파일명은 랜덤하게 정하더라도 확장자는 유지
		String filePath = outputDir + "/" + tempFileName;
		// String filePath = new File(outputDir, tempFileName).getPath();
		// String filePath = Paths.get(outputDir, tempFileName).toString();
		// String filePath = outputDir + File.separatorChar + tempFileName;

		log.info("originFileName = " + originFileName);
		log.info("tempFileName = " + tempFileName);
		log.info("filePath = " + filePath);

		InputStream in;
		try {
			in = new URL(fileUrl).openStream();
			Files.copy(in, Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING); //덮어쓰기 옵션
		} catch (IOException e) {
			e.printStackTrace();
		}

		return filePath;
	}

	private static String getFileNameFromUrlV2(String fileUrl) {
		fileUrl = fileUrl.split("\\?")[0]; // 쿼리 스트링을 제외한 URL (물음표는 Dangling meta character 이므로 \\ 를 통해 이스케이프)
		String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1); // 마지막 / 가 나오는 다음부터가 파일명
		return fileName;
	}




	/*private static String getFileNameFromUrl_EX(String fileUrl) {
		// 반반 치킨 코드
		// fileUrl = fileUrl.split("\\?")[0]; // 쿼리 스트링을 제외한 URL
		// fileUrl = fileUrl.substring(fileUrl.indexOf("://") + 3); // https:// 제거
		// Path path = Paths.get(fileUrl);
		// return path.getFileName().toString();

		// 위처럼 하면 반반 치킨임
		// 깔끔하게 Path 의 내장 메서드를 활용하거나, 확실히 노가다로 코드를 짜거나

		// Ex) 확실한 노가다로 파일명 추출
		// fileUrl = fileUrl.split("\\?")[0]; // 쿼리 스트링을 제외한 URL (물음표는 Dangling meta character 이므로 \\ 를 통해 이스케이프)
		// String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1); // 마지막 / 가 나오는 다음부터가 파일명

		// Ex) Path 의 내장 메서드를 활용한 파일명 추출
		try {
			return Paths.get(new URI(fileUrl).getPath()).getFileName().toString();
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return "";
		}
	}*/





	/*
		Downloader v1 : HttpUrlConnection 객체를 통해 오픈된 파일을 다운로드
    	- 기본적인 흐름: 1. HttpUrlConnection 연결 -> 2. Stream 을 통해 다운로드
     */
	public static String downloadFileByHttpV1(String fileUrl, String outputDir) {
		String filePath = null;

		InputStream is = null;
		FileOutputStream os = null;
		HttpURLConnection conn = null;

		try{
			// 1. HttpUrlConnection 연결
			URL url = new URL(fileUrl); // URL 객체 생성
			conn = (HttpURLConnection) url.openConnection(); // URL 객체로부터 HttpUrlConnection 객체를 받아옴
			int responseCode = conn.getResponseCode();
			log.info("responseCode : " + responseCode); // responseCode : 200

			// 2. Stream 을 통해 다운로드
			// 통신에 성공했으면 남은 작업은 Stream 을 통해 파일을 가져오는 것

			// Status 가 200 일 때
			if (responseCode == HttpURLConnection.HTTP_OK) {
				// Content-Disposition or URL 에서 파일명 가져오기
				String originFileName = "";
				String disposition = conn.getHeaderField(HttpHeaders.CONTENT_DISPOSITION);
				String contentType = conn.getContentType();

				// 일반적으로 Content-Disposition 헤더에 있지만
				// 없을 경우 url 에서 추출해 내면 된다.
				if (disposition != null) {
					String target = "filename=";
					int index = disposition.indexOf(target);
					if (index != -1) {
						originFileName = disposition.substring(index + target.length()); // "152-536x354.jpg"
						originFileName = originFileName.replaceAll("\"", ""); // 152-536x354.jpg (앞뒤 따옴표 제거)
					}
				} else {
					originFileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
				}

				String tempFileName = UUID.randomUUID() + "." + getFileExt(originFileName); // 파일명은 랜덤하게 정하더라도 확장자는 유지
				File file = new File(outputDir, tempFileName);
				filePath = file.getPath();

				log.info("Content-Type = " + contentType);
				log.info("Content-Disposition = " + disposition);
				log.info("originFileName = " + originFileName);
				log.info("tempFileName = " + tempFileName);
				log.info("filePath = " + filePath);

				// InputStream ➡️ FileOutputStream 을 통해 파일 다운로드
				is = conn.getInputStream();
				os = new FileOutputStream(file);

				final int BUFFER_SIZE = 4096;
				int bytesRead;
				byte[] buffer = new byte[BUFFER_SIZE];
				while ((bytesRead = is.read(buffer)) != -1) {
					os.write(buffer, 0, bytesRead);
				}

				os.close();
				is.close();
				log.info("File downloaded.");

			} else {
				log.info("No file to download. Server replied HTTP code: " + responseCode);
			}
			conn.disconnect();

		} catch (Exception e){
			log.info("An error occurred while trying to download a file.");
			e.printStackTrace();

			return "Download failed.";

		} finally {
			try {
				if (is != null) is.close();
				if (os != null) os.close();
				if (conn != null) conn.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return filePath;
	}
}