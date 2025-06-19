package com.software.software_program;

import com.software.software_program.core.configuration.AppConfigurationProperties;
import com.software.software_program.core.setup.EntityInitializer;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

@SpringBootApplication
@EnableConfigurationProperties(AppConfigurationProperties.class)
@RequiredArgsConstructor
@EnableAsync
public class SoftwareProgramApplication implements CommandLineRunner {
	private final EntityInitializer initializer;

	public static void main(String[] args) {
		SpringApplication.run(SoftwareProgramApplication.class, args);
	}

	@Override
	@Transactional
	public void run(String... args) {


			initializer.initializeAll();

	}

}
