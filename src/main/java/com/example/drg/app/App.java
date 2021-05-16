package com.example.drg.app;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class App {

	private static String genFileDirPath;
	private static String tmpDirPath;

	public static void init(String genFileDirPath, String tmpDirPath) {
		App.genFileDirPath = genFileDirPath;
		App.tmpDirPath = tmpDirPath;
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
