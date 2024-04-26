package org.kau.kkoolbeeServer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(exclude = { UserDetailsServiceAutoConfiguration.class })
@EnableFeignClients
public class KkoolbeeServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(KkoolbeeServerApplication.class, args);
	}

}
