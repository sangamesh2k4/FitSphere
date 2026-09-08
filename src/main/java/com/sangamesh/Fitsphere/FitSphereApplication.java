package com.sangamesh.Fitsphere;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class FitSphereApplication {

	public static void main(String[] args) {
		SpringApplication.run(FitSphereApplication.class, args);
	}

}
