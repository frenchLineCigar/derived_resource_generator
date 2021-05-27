package com.example.drg.exception;

public class DownloadFileFailException extends RuntimeException {

	public DownloadFileFailException() {
		super();
	}

	public DownloadFileFailException(String message) {
		super(message);
	}

	public DownloadFileFailException(String message, Throwable cause) {
		super(message, cause);
	}

}
