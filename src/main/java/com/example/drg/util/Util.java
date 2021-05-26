package com.example.drg.util;

import com.example.drg.exception.DownloadFileFailException;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import static org.imgscalr.Scalr.*;

@Slf4j
public class Util {

	// [참고용] application.yml 내 해당 프로퍼티의 값을 리턴 (스프링 DI 없이)
	public static String getPropertyValueByName(String propertyName) {
		Properties properties = new Properties();

		try {
			properties.load(new FileReader(new ClassPathResource("application.yml").getFile()));
		} catch (IOException e) {
			log.error(e.getMessage());
		}

		String value = null;

		for (Object name : properties.keySet()) {
			if ((name.equals(propertyName))) {
				value = (String) properties.get(name);
				log.info("name = " + "\"" + value + "\"");

				break;
			}
		}

		return value;
	}

	// Obj to int
	public static int getAsInt(Object object, int fallback) {
		if (object instanceof BigInteger) {
			return ((BigInteger) object).intValue();
		} else if (object instanceof Double) {
			//return ((Double) object).intValue();
			return (int) Math.floor((double) object); //소수점 이하는 버릴거임
		} else if (object instanceof Float) {
			return (int) Math.floor((float) object); //소수점 이하는 버릴거임
		} else if (object instanceof Long) {
			return (int) object;
		} else if (object instanceof Integer) {
			return (int) object;
		} else if (object instanceof String) {
			return Integer.parseInt((String) object);
		}

		return fallback;
	}

	public static int getAsInt(Object object) {
		final int DEFAULT_VALUE = 0;
		return getAsInt(object, DEFAULT_VALUE);
	}

	// mapOf : K1, V1, K2, V2,... 패턴으로 입력되는 args 를 Map 으로 변환
	public static Map<String, Object> mapOf(Object... args) {
		if ( args.length % 2 != 0 ) {
			throw new IllegalArgumentException("인자를 짝수개 입력해주세요.");
		}

		int size = args.length / 2;

		Map<String, Object> map = new LinkedHashMap<>();

		for ( int i = 0; i < size; i++ ) {

			int keyIndex = i * 2;
			int valueIndex = keyIndex + 1;

			String key;
			Object value;

			try {
				key = (String) args[keyIndex];
			}
			catch ( ClassCastException e) {
				throw new IllegalArgumentException("Key는 String으로 입력해야 합니다."  + e.getMessage()
						+ "\n" + (keyIndex + 1) + "번째 파라미터의 유효하지 못한 입력값: " + args[keyIndex]);
			}

			value = args[valueIndex];
			map.put(key, value);
		}
		return map;
	}

	// 여러 Map 을 하나의 Map 으로 병합
	@SafeVarargs
	public static Map<String, Object> mapOf(Map<String, Object>... args) {

		if ( args.length == 0 ) {
			throw new IllegalArgumentException("인자를 입력해주세요.");
		}

		Map<String, Object> result = new LinkedHashMap<>();

		for(Map<String, Object> map : args) {
			for(String key : map.keySet()) {
				result.put(key, map.get(key));
			}
		}
		return result;
	}

	/* 파일 규격 (상위 규격) */
	public static String getFileExtTypeCodeFromFileName(String fileName) {
		String ext = getFileExtFromFileName(fileName);

		switch (ext) {
			case "jpeg":
			case "jpg":
			case "gif":
			case "png":
			case "svg":
			case "jfif":
				return "img";
			case "mp4":
			case "avi":
			case "mov":
				return "video";
			case "mp3":
				return "audio";
		}

		return "etc";
	}

	/* 파일 규격2 (하위 규격) */
	public static String getFileExtType2CodeFromFileName(String fileName) {
		String ext = getFileExtFromFileName(fileName);

		switch (ext) {
			case "jpeg":
			case "jpg":
				return "jpg";
			case "htm":
			case "html":
				return "html";
			case "gif":
			case "png":
			case "svg":
			case "jfif":
			case "mp4":
			case "mov":
			case "avi":
			case "mp3":
				return ext;
		}

		return "etc";
	}

	/* 파일명에서 확장자 추출 */
	public static String getFileExtFromFileName(String fileName) {
		// newFile.jpg
		int pos = fileName.lastIndexOf(".");

		// 확장자가 없는 경우는 빈문자열 리턴
		if (pos == -1) {
			return "";
		}

		String ext = fileName.substring(pos + 1).trim().toLowerCase(); // 공백제거 및 소문자로

		switch (ext) {
			case "jpeg":
				return "jpg";
			case "htm":
				return "html";
		}

		return ext;
	}

	/**
	 * fileDir 생성용
	 * - 리눅스에서 폴더 하나에 파일을 몇만개 이상 담을 수 없기 때문에 생성년월로 폴더 분리
	 * - 현재 년월을 yyyy_MM 문자열 포맷으로 리턴
	 */
	public static String getNowYearMonthDateStr() {
		SimpleDateFormat format1 = new SimpleDateFormat("yyyy_MM");

		String dateStr = format1.format(System.currentTimeMillis());

		return dateStr;
	}

	/**
	 * byte 용량을 환산하여 반환
	 * 용량의 크기에 따라 MB, KB, byte 단위로 환산함
	 * @param fileSize  byte 값
	 * @param fixed     환산된 용량의 소수점 자릿수
	 * @return {String}
	 */
	public static String byteSizeTo(float fileSize, Integer fixed) {
		String str;

		//MB 단위 이상일때 MB 단위로 환산
		if (fileSize >= 1024 * 1024) {
			fileSize = fileSize / (1024 * 1024);
			str = formatNumberWithComma(fileSize, fixed) + " MB";
			//str = toFixed(fileSize, fixed) + " MB";
		}
		//KB 단위 이상일때 KB 단위로 환산
		else if (fileSize >= 1024) {
			fileSize = fileSize / 1024;
			str = formatNumberWithComma(fileSize, fixed) + " KB";
		}
		//KB 단위보다 작을때 byte 단위로 환산
		else {
			str = formatNumberWithComma(fileSize, fixed) + " byte";
		}
		return str;
	}

	/**
	 * 숫자를 콤마(,)로 구분된 문자열로 포맷해서 반환
	 * @param num  변환할 숫자
	 * @param fixed  출력할 소수점 자릿수
	 * @return {String}
	 */
	public static String formatNumberWithComma(double num, Integer fixed) {
		// 기본 콤마 표기
		DecimalFormat df = new DecimalFormat("###,###,###");

		// 소수점 자릿수 표기
		// if value of 'fixed' is 2, then it is like using 'DecimalFormat df = new DecimalFormat("###,###,###.00");'
		if (fixed != null && fixed > 0) {
			df.setMaximumFractionDigits(fixed);
		}

		return df.format(num);
	}

	/* 파일 사이즈 추출 */
	public static int getFileSize(String filePath) {
		int size = 0;

		File file = new File(filePath);
		if (file.exists()) {
			size = (int) file.length();
		}

		log.info("File Size : " + size +" byte");

		return size;
	}

	/* 지정된 경로의 파일 삭제 */
	public static boolean deleteFile(String filePath) {
		File ioFile = new File(filePath);
		if (ioFile.exists()) {
			return ioFile.delete();
		}

		return false;
	}

	/* 파일 복사 : Copy source To dest */
	public static void copyFile(String sourceFilePath, String destFilePath) {
		File sourceFile = new File(sourceFilePath);
		File destFile = new File(destFilePath);

		// 경로에 해당하는 디렉터리가 없으면 생성
		File destFileDir = destFile.getParentFile();
		if (! destFileDir.exists()) {
			destFileDir.mkdirs();
		}

		try {
			Files.copy(sourceFile.toPath(), destFile.toPath());
			//Files.copy(sourceFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING); //덮어쓰기 옵션
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/* 파일 이동 : Move source To dest */
	public static void moveFile(String sourceFilePath, String destFilePath) {
		File sourceFile = new File(sourceFilePath);
		File destFile = new File(destFilePath);

		// 경로에 해당하는 디렉터리가 없으면 생성
		File destFileDir = destFile.getParentFile();
		if (! destFileDir.exists()) {
			destFileDir.mkdirs();
		}

		try {
			// Using Java NIO
			Files.move(sourceFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING); //덮어쓰기 옵션
			//Files.move(sourceFile.toPath(), destFile.toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String getUrlFromHttpServletRequest(HttpServletRequest req) {
		return req.getRequestURI() + "?" + req.getQueryString();
	}

	/* Full URL  */
	/*public static String getUrlFromHttpServletRequest(HttpServletRequest req) {
		String url =
				req.getScheme() + "://" +	// "http" + "://
						req.getServerName() +		// "myhost"
						":" + 						// ":"
						req.getServerPort() +		// "8080"
						req.getRequestURI() +		// "/people"
						"?" + 						// "?"
						req.getQueryString(); 		// "lastname=Fox&age=30"

		return url;
	}*/

	/*
		Downloader for use : Using Java NIO
	 */
	public static String downloadFileByHttp(String fileUrl, String outputDir) {
		String originFileName = getFileNameFromUrl(fileUrl);
		String fileExt = getFileExtFromFileName(originFileName);

		// 확장자가 없는 경우 일단 tmp 로
		fileExt = fileExt.length() == 0 ? "tmp" : fileExt;

		String newFileName = UUID.randomUUID() + "." +  fileExt;// 파일명은 랜덤하게 정하더라도 확장자는 유지
		String filePath = outputDir + "/" + newFileName;

		log.info("originFileName = " + originFileName);
		log.info("newFileName = " + newFileName);
		log.info("filePath = " + filePath);

		try (FileOutputStream fileOutputStream = new FileOutputStream(filePath)) {
			ReadableByteChannel readableByteChannel = Channels.newChannel(new URL(fileUrl).openStream());
			FileChannel fileChannel = fileOutputStream.getChannel();
			fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);

			fileChannel.close(); // 자원 해제
		} catch (FileNotFoundException | MalformedURLException | UnknownHostException | ConnectException e) {
			log.debug("Message : " + e.getMessage());
			log.debug("Exception : " + e.getClass());
			throw new DownloadFileFailException();
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}

		// 파일 확장자가 tmp 로 처리된 경우, 파일의 메타데이터를 분석해 적절한 확장자로 처리
		if (fileExt.equals("tmp")) {
			// 생성된 tmp 파일의 확장자 추출
			String newFileExt = getFileExt(new File(filePath));

			// 확장자 변경
			String newFilePath = filePath.replaceAll("\\.tmp", "\\." + newFileExt);
			moveFile(filePath, newFilePath);
			filePath = newFilePath;
		}

		// img, video, audio 와 같은 미디어 파일이 아닐 경우
		String fileExtTypeCode = getFileExtTypeCodeFromFileName(filePath);
		if (fileExtTypeCode.equals("etc")) {
			throw new DownloadFileFailException();
		}

		return filePath;
	}

	/* 지정 포맷으로 문자열 인코딩 */
	public static String getUriEncodedByCharset(String str, String charset) {
		try {
			return URLEncoder.encode(str, charset);
		} catch (UnsupportedEncodingException e) {
			return str;
		}
	}

	/* 지정 포맷으로 문자열 인코딩 */
	public static String getUriEncodedByCharset(String str, Charset charset) {
		return getUriEncodedByCharset(str, charset.toString());
	}

	/* UTF-8 문자열 인코딩 */
	public static String getUriEncodedAsUTF8(String str) {
		return getUriEncodedByCharset(str, StandardCharsets.UTF_8);
	}

	// 존재하는 특정 파일의 미디어 타입을 분석해 확장자를 추출, Tika 활용
	private static String getFileExt(File file) {
		Tika tika = new Tika();
		String mimeType = "";
		try {
			mimeType = tika.detect(file);
		} catch (IOException e) {
			e.printStackTrace();
		}

		String ext = mimeType.split("/")[1]; // 확장자에 해당하는 subtype 부분 추출

		switch (ext) {
			case "jpeg":
				return "jpg"; // ext = ext.replace("jpeg", "jpg");
			case "htm":
				return "html";
		}

		return ext;
	}

	// URL에서 파일명 추출
	public static String getFileNameFromUrl(String fileUrl) {
		try {
			return Paths.get(new URI(fileUrl).getPath()).getFileName().toString();
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return "";
		}
	}

	/* 경로에서 파일명 추출 */
	public static String getFileNameFromPath(String filePath) {
		return Paths.get(new File(filePath).getPath()).getFileName().toString();
	}

	/*
		Downloader v2 : Using Java IO
	 */
	public static String downloadFileByHttpV2(String fileUrl, String outputDir) {
		String originFileName = getFileNameFromUrlV2(fileUrl);
		String newFileName = UUID.randomUUID() + "." + getFileExtFromFileName(originFileName); // 파일명은 랜덤하게 정하더라도 확장자는 유지
		String filePath = outputDir + "/" + newFileName;
		// String filePath = new File(outputDir, newFileName).getPath();
		// String filePath = Paths.get(outputDir, newFileName).toString();
		// String filePath = outputDir + File.separatorChar + newFileName;

		log.info("originFileName = " + originFileName);
		log.info("newFileName = " + newFileName);
		log.info("filePath = " + filePath);

		InputStream in;
		try {
			in = new URL(fileUrl).openStream();
			Files.copy(in, Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING); //덮어쓰기 옵션

			in.close(); // 자원 해제
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


	// 테스트용 코드
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

				String newFileName = UUID.randomUUID() + "." + getFileExtFromFileName(originFileName); // 파일명은 랜덤하게 정하더라도 확장자는 유지
				File file = new File(outputDir, newFileName);
				filePath = file.getPath();

				log.info("Content-Type = " + contentType);
				log.info("Content-Disposition = " + disposition);
				log.info("originFileName = " + originFileName);
				log.info("newFileName = " + newFileName);
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

	/* [기본] 이미지 리사이징 & 크롭 (Using Imgscalr) */
	public static String resizeImg(String sourceFilePath, String destFilePath, int width, int height) {
		try {
			BufferedImage bufferedImage = ImageIO.read(new File(sourceFilePath));

			int originWidth = bufferedImage.getWidth();
			int originHeight = bufferedImage.getHeight();

			int newWidth = originWidth;
			int newHeight = (originWidth * height) / width;

			if (newHeight > originHeight) {
				newWidth = (originHeight * width) / height;
				newHeight = originHeight;
			}

			BufferedImage cropedBufferedImage = crop(bufferedImage, (originWidth - newWidth) / 2, (originHeight - newHeight) / 2, newWidth, newHeight);
			BufferedImage destBufferedImage = resize(cropedBufferedImage, Method.ULTRA_QUALITY, Mode.FIT_EXACT, width, height); // width, height 대로 정확히 변환
			// BufferedImage destBufferedImage = resize(cropedBufferedImage, Method.ULTRA_QUALITY, Mode.AUTOMATIC, width, height); // Mode.AUTOMATIC => width, height 사이가 크게 차이날 경우, 큰 쪽에 맞춰 작은 쪽이 보정됨

			String destFileExt = Util.getFileExtFromFileName(sourceFilePath); // 확장자 추출
			FileOutputStream fileOutputStream = new FileOutputStream(destFilePath); // 스트림 열고 !! (비동기 쓰레드 처리)
			ImageIO.write(destBufferedImage, destFileExt, fileOutputStream); // 쓰고 !!
			fileOutputStream.close(); // 반드시 닫기!!

		} catch (IOException e) {
			e.printStackTrace();
		}

		return destFilePath;
	}

	/* 이미지 리사이징 & 크롭 (Using Imgscalr) - width 입력 시, height 자동 환산 */
	public static String resizeImgWidth(String sourceFilePath, String destFilePath, int width) {

		// 지정된 높이(height)가 없으므로, 원본 종횡비 기준으로 산정
		int height = 0;

		try {
			BufferedImage bufferedImage = ImageIO.read(new File(sourceFilePath));

			int originWidth = bufferedImage.getWidth();
			int originHeight = bufferedImage.getHeight();

			height = originHeight * width / originWidth; // 비율에 맞게 산정된 높이
			log.info("height = " + height); // => if width is 536, then height is 354.

		} catch (IOException e) {
			e.printStackTrace();
		}

		return resizeImg(sourceFilePath, destFilePath, width, height);
	}

	/* 이미지 리사이징 & 크롭 (Using Imgscalr) - height 입력 시, width 자동 환산 */
	public static String resizeImgHeight(String sourceFilePath, String destFilePath, int height) {

		// 지정된 너비(width)가 없으므로, 원본 종횡비 기준으로 산정
		int width = 0;

		try {
			BufferedImage bufferedImage = ImageIO.read(new File(sourceFilePath));

			int originWidth = bufferedImage.getWidth();
			int originHeight = bufferedImage.getHeight();

			width = originWidth * height / originHeight; // 비율에 맞게 산정된 너비
			log.info("width = " + width); // => if height is 354, then width is 536.

		} catch (IOException e) {
			e.printStackTrace();
		}

		return resizeImg(sourceFilePath, destFilePath, width, height);
	}

	/*public static void resizeImgTest(String filePath, String destFilePath, int width) {
		try {
			BufferedImage bufferedImage = ImageIO.read(new File(filePath));
			Scalr.resize(bufferedImage, width); // 리사이징
			String destFileExt = Util.getFileExt(destFilePath); // 확장자 추출
			ImageIO.write(bufferedImage, destFileExt, new File(destFilePath)); // FileOutputStream 열지 않고 File 객체 지정해도 됨
		} catch (IOException e) {
			e.printStackTrace();
		}
	}*/
}