package com.seproje.hospital;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;

// database'i şimdilik umursamaması için exclue ediyoruz
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class HospitalApplication {

	public static void main(String[] args) {
		SpringApplication.run(HospitalApplication.class, args);
	}

}
