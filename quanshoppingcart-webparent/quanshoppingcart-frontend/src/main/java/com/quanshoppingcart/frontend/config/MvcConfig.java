package com.quanshoppingcart.frontend.config;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {//phải khai báo folder chứa hình thì mới truy cập được đến các folder hình này
		exposeDirectory("../category-images", registry);// ../sẽ trỏ đến folder trước file hiện tại
		exposeDirectory("../brand-logos", registry); 
		exposeDirectory("../product-images", registry);
		
		exposeDirectory("src/main/resources/static/images", registry);
	}

	private void exposeDirectory(String pathPattern, ResourceHandlerRegistry registry) {
		Path path = Paths.get(pathPattern);

		String absolutePath = path.toFile().getAbsolutePath();//đường dẫn đến folder chứa hình

		String logicalPath = pathPattern.replace("../", "") + "/**";

		registry.addResourceHandler(logicalPath).addResourceLocations("file:/" + absolutePath + "/");
	}

}
