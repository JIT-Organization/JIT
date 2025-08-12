package com.justintime.jit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class JitApplication {

	public static void main(String[] args) {
		SpringApplication.run(JitApplication.class, args);
	}

}
