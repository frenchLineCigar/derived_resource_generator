package com.example.drg.config;

import com.example.drg.app.App;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.File;

@Slf4j
@Component
@Order(0)
class MyApplicationListener implements ApplicationListener<ApplicationReadyEvent> {

	@Value("${custom.genFileDirPath}")
	private String genFileDirPath;

	@Value("${custom.tmpDirPath}")
	private String tmpDirPath;

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		log.info("MyApplicationListener.onApplicationEvent");

		App.init(genFileDirPath, tmpDirPath);
	}

}