package com.example.drg.app;

import lombok.extern.slf4j.Slf4j;

import java.io.File;

@Slf4j
public class App {

	public static String genFileDirPath;
	public static String tmpDirPath;

	public static void init(String genFileDirPath, String tmpDirPath) {
		App.genFileDirPath = genFileDirPath;
		App.tmpDirPath = tmpDirPath;
		App.createDir();
	}

	public static void createDir() {
		File genFileDir = new File(genFileDirPath);
		File tmpDir = new File(tmpDirPath);
		if (! genFileDir.exists()) genFileDir.mkdirs();
		if (! tmpDir.exists()) tmpDir.mkdirs();
	}

	public static String getGenFileDirPath() {
		return genFileDirPath;
	}

	public static String getTmpDirPath() {
		return tmpDirPath;
	}

	public static boolean existsInTmpDir(String filePath) {
		return filePath.contains(tmpDirPath); // => filePath.indexOf(tmpDirPath) != -1;
	}

	public static boolean existsInGenFileDir(String filePath) {
		return filePath.contains(genFileDirPath); // => filePath.indexOf(genFileDirPath) != -1;
	}
}
