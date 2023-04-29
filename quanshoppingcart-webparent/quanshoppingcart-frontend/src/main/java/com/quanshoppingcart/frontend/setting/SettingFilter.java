package com.quanshoppingcart.frontend.setting;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(-123)
public class SettingFilter implements Filter {

	//tất cả các request trước khi gửi đến controller đều phải chạy qua phương thức doFilter
	//có thể sử dụng phương thức doFilter để làm 1 số tác vụ nào đó trước khi request được gửi đến controller 
	
	/*Ví dụ: 
	kiểm tra thông tin đăng nhập, nếu chưa đăng nhập thì chuyển hướng qua trang đăng nhập hoặc chặn nếu user ko được phép truy cập
	lấy dữ liệu trong db và gán vào ServletRequest -->nhờ cách này mà sau khi request gửi đến controller, controller có thể lấy ra các data nằm trong ServletRequest 
	ghi logs,...
	*/
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		HttpServletRequest servletRequest = (HttpServletRequest) request;
		String url = servletRequest.getRequestURL().toString();
		
		//tất cả request đến file css,js,images đều cho phép
		if (url.endsWith(".css") || url.endsWith(".js") || url.endsWith(".png") ||	url.endsWith(".jpg")) {
			chain.doFilter(request, response);//cho phép request gửi đến controller
			return;
		}
		
		chain.doFilter(request, response);//cho phép request gửi đến controller

	}

}
