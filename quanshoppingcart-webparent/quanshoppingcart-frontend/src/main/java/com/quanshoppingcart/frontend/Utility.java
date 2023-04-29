package com.quanshoppingcart.frontend;

import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import com.quanshoppingcart.frontend.setting.EmailSettingBag;

public class Utility {
	
	//HttpServletRequest có thể lấy ra tất cả thông tin của request gửi đến
	public static String getSiteURL(HttpServletRequest request) {
		String siteURL = request.getRequestURL().toString();//http://localhost:8083/ShoppingCartClient/create_customer	http://localhost:8083/ShoppingCartClient/forgot_password
		
		return siteURL.replace(request.getServletPath(), "");//thay /create_customer bằng ""	thay /forgot_password bằng "" 
	}
	
	public static JavaMailSenderImpl prepareMailSender(EmailSettingBag settings) {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();//tạo đối tượng JavaMailSenderImpl và gán các thông tin để gửi mail
		
		mailSender.setHost(settings.getHost());//smtp.gmail.com
		mailSender.setPort(settings.getPort());//587
		mailSender.setUsername(settings.getUsername());//nhbtuyen2702@gmail.com
		mailSender.setPassword(settings.getPassword());//gpctiolgpwrabzxn
		
		Properties mailProperties = new Properties();
		mailProperties.setProperty("mail.smtp.auth", settings.getSmtpAuth());//true
		mailProperties.setProperty("mail.smtp.starttls.enable", settings.getSmtpSecured());//true
		
		mailSender.setJavaMailProperties(mailProperties);
		
		return mailSender;
	}
	
	//phương thức này sẽ lấy ra customer đang đăng nhập, sau đó trả về email của customer đó
	public static String getEmailOfAuthenticatedCustomer(HttpServletRequest request) {
		Object principal = request.getUserPrincipal();
		if (principal == null) return null;
		
		String customerEmail = null;
		
		if (principal instanceof UsernamePasswordAuthenticationToken 
				|| principal instanceof RememberMeAuthenticationToken) {
			customerEmail = request.getUserPrincipal().getName();
		}
		
		return customerEmail;
	}	
	
}
