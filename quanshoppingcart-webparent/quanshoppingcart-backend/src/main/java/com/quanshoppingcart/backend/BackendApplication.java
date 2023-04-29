package com.quanshoppingcart.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication//@SpringBootApplication = @SpringBootConfiguration + @EnableAutoConfiguration + @ComponentScan -->nhờ 3 annotation này mà Spring Boot có thể tự cấu hình, nếu sử dụng SpringMVC thì phải tự cấu hình
@ComponentScan({ "com.quanshoppingcart.backend.*", "com.quanshoppingcart.backend" })//quét qua các package được khai báo để khởi tạo Spring Bean. Muốn khởi tạo Spring Bean ở cấp độ class dùng @Controller, @Service, @Repository, @Component. Muốn khởi tạo Spring Bean ở cấp độ phương thức dùng @Configuration + @Bean
@EnableJpaRepositories(basePackages = { "com.quanshoppingcart.backend.*" })//quét qua các package được khai báo để khởi tạo Spring Data JPA
@EntityScan({ "com.quanshoppingcart.common.*" })//quét qua các package được khai báo để khởi tạo entity -->sẽ tạo table tương ứng trong database
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

}
