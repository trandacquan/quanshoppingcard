package com.quanshoppingcart.frontend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan({"com.quanshoppingcart.frontend.*", "com.quanshoppingcart.frontend" })
@EnableJpaRepositories(basePackages = { "com.quanshoppingcart.frontend.*" })
@EntityScan({ "com.quanshoppingcart.common.*" })
public class FrontendApplication {

	public static void main(String[] args) {
		SpringApplication.run(FrontendApplication.class, args);
	}

}
