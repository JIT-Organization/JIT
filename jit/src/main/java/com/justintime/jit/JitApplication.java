package com.justintime.jit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableCaching
@EnableFeignClients(basePackages = "com.justintime.jit")
public class JitApplication {

	public static void main(String[] args) {
		SpringApplication.run(JitApplication.class, args);
	}

}
