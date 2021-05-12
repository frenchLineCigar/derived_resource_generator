package com.example.drg.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ApplicationContextEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;

@Profile("demo")
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class DemoApplicationListener implements ApplicationListener<ApplicationReadyEvent>, CommandLineRunner, ApplicationRunner, InitializingBean {

	@Value("${custom.genFileDirPath}")
	private String genFileDirPath;
	
	@Value("${custom.tmpDirPath}")
	private String tmpDirPath;

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		log.info("ApplicationListener.onApplicationEvent");
		log.info("genFileDirPath = " + genFileDirPath);
		//new File(genFileDirPath).mkdirs();
		//new File(tmpDirPath).mkdirs();
	}

	@PostConstruct
	public void afterConstructor() {
		log.info("PostConstruct");
		log.info("genFileDirPath = " + genFileDirPath);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		log.info("InitializingBean.afterPropertiesSet");
		log.info("genFileDirPath = " + genFileDirPath);
	}

	@Override
	public void run(String... args) throws Exception {
		log.info("CommandLineRunner");
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		log.info("ApplicationRunner");
	}

	@EventListener(ApplicationReadyEvent.class)
	public void doSomethingAfterStartup() {
		log.info("EventListener - ApplicationReadyEvent");
	}

	@EventListener(ApplicationStartedEvent.class)
	public void doSomethingAfterStartup2() {
		log.info("EventListener - ApplicationStartedEvent");
	}

	@EventListener(ApplicationContextEvent.class)
	public void doSomethingAfterStartup3() {
		log.info("EventListener - ApplicationContextEvent");
	}
}