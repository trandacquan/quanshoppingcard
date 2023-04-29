package com.quanshoppingcart.frontend.customer;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.quanshoppingcart.frontend.Utility;
import com.quanshoppingcart.common.entity.Country;
import com.quanshoppingcart.common.entity.Customer;
import com.quanshoppingcart.frontend.security.CustomerUserDetails;
import com.quanshoppingcart.frontend.setting.EmailSettingBag;
import com.quanshoppingcart.frontend.setting.SettingService;

@Controller
public class CustomerController {
	
	@Autowired private CustomerService customerService;
	@Autowired private SettingService settingService;
	
	//http://localhost:8083/ShoppingCartClient/customers/create?firstName=Nguyen&lastName=Tuyen
	@GetMapping("/customers/create")
	@ResponseBody
	public String create(@RequestParam("firstName") String firstName, @RequestParam("lastName") String lastName,
							HttpServletRequest request) {
		String requestURL = request.getRequestURL().toString();// http://localhost:8083/ShoppingCartClient/customers/create
		String requestURI = request.getRequestURI();// /ShoppingCartClient/customers/create
		String contextPath = request.getContextPath();// /ShoppingCartClient
		String serverName = request.getServerName();// localhost
		int serverPort = request.getServerPort();// 8083
		String servletPath = request.getServletPath();// /customers/create
		String queryString = request.getQueryString();// firstName=Nguyen&lastName=Tuyen
		String parameter1 = request.getParameter("firstName");// Nguyen
		String parameter2 = request.getParameter("lastName");// Tuyen
		
		String info = ("Request URL: " + requestURL
				+ "\nRequest URI: " + requestURI
				+ "\nContext Path: " + contextPath
				+ "\nServer Name: " + serverName
				+ "\nServer Port: " + serverPort
				+ "\nServlet Path: " + servletPath
				+ "\nQuery String: " + queryString
				+ "\nParameter 1: " + parameter1
				+ "\nParameter 2: " + parameter2
				);
		
		return info;
	}
	
	/*Hàm Register một Customer mới 
	 * - Khi bấm vào Nav Register thì sẽ gọi xuống hàm này*/
	@GetMapping("/register")
	public String showRegisterForm(Model model) {
		List<Country> listCountries = customerService.listAllCountries();//lấy tất cả Countries để đổ lên dropdown
		
		model.addAttribute("listCountries", listCountries);
		model.addAttribute("pageTitle", "Customer Registration");
		model.addAttribute("customer", new Customer());//->Phải tạo ra một th:object="${customer}" để truyền object này vào form
		
		return "register/register_form";//Trả về form register
	}
	
	/*Hàm Tạo một Customer mới
	 * - Sau khi đã nhập thông tin form, bấm save thì sẽ gọi xuống hàm này*/
	@PostMapping("/create_customer")
	public String createCustomer(Customer customer,
			Model model,
			HttpServletRequest request) throws UnsupportedEncodingException, MessagingException {
		
		customerService.registerCustomer(customer);//Làm các bước enCoder password, tạo random code để veryfy,.. lưu xuống DB
		sendVerificationEmail(request, customer);//gửi mail xác thực
		
		model.addAttribute("pageTitle", "Registration Succeeded!");
		
		return "register/register_success";//trả về trang đăng ký thành công
	}

	/*Hàm Gửi Email xác thực đến Customer*/
	private void sendVerificationEmail(HttpServletRequest request, Customer customer) 
			throws UnsupportedEncodingException, MessagingException {
		EmailSettingBag emailSettings = settingService.getEmailSettings();//Send to...
		JavaMailSenderImpl mailSender = Utility.prepareMailSender(emailSettings);//Send from...
		
		String toAddress = customer.getEmail();
		String subject = "Please verify your registration to continue shopping";
		
		String content = "<p>Dear [[name]],</p>"
				+ "<br>"
				+ "<p>Click the link below to verify your account:</p>"
				+ "<br>"
				+ "<p><a href=\"" + "[[URL]]" + "\">VERIFY</a></p>"
				+ "<br>"
				+ "<p>Thanks,</p>"
				+ "<p>The Shopping Team.</p>";
		
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		
		helper.setFrom(emailSettings.getFromAddress(), emailSettings.getSenderName());//nhbtuyen2702@gmail.com, Shopping Team
		helper.setTo(toAddress);//email của customer
		helper.setSubject(subject);//tiêu đề email
		
		content = content.replace("[[name]]", customer.getFullName());//thay [[name]] trong content bằng fullname của customer
		
		String verifyURL = Utility.getSiteURL(request) + "/verify?code=" + customer.getVerificationCode();//http://localhost:8083/ShoppingCartClient/verify?code=fwOtOrKtbx2u7yI7ExYyVLPfaBXXv3vudzfqaF909CT1KEL8I5MVB2iyxX4n4i0l
		
		content = content.replace("[[URL]]", verifyURL);//thay [[URL]] trong content bằng verifyURL
		
		helper.setText(content, true);//nội dung email
		
		mailSender.send(message);//gửi mail cho customer
		
		System.out.println("to Address: " + toAddress);
		System.out.println("Verify URL: " + verifyURL);
	}	
	
	//khi customer bấm vào link verify thì sẽ gửi request đe
	@GetMapping("/verify")
	public String verifyAccount(String code, Model model) {
		boolean verified = customerService.verify(code);
		
		return "register/" + (verified ? "verify_success" : "verify_fail");
	}

	@GetMapping("/account_details")
	public String viewAccountDetails(Model model, HttpServletRequest request) {
		String email = Utility.getEmailOfAuthenticatedCustomer(request);//phương thức này sẽ lấy ra customer đang đăng nhập
		Customer customer = customerService.getCustomerByEmail(email);
		List<Country> listCountries = customerService.listAllCountries();
		
		model.addAttribute("customer", customer);
		model.addAttribute("listCountries", listCountries);
		
		return "customer/account_form";
	}
	
	@PostMapping("/update_account_details")
	public String updateAccountDetails(Model model, Customer customer, RedirectAttributes ra,
			HttpServletRequest request) {
		customerService.update(customer);
		ra.addFlashAttribute("message", "Your account details have been updated.");
		
		updateNameForAuthenticatedCustomer(customer, request);
		
		String redirectURL = "redirect:/account_details";
		
		return redirectURL;
	}

	private void updateNameForAuthenticatedCustomer(Customer customer, HttpServletRequest request) {
		Object principal = request.getUserPrincipal();
		
		if (principal instanceof UsernamePasswordAuthenticationToken 
				|| principal instanceof RememberMeAuthenticationToken) {
			CustomerUserDetails userDetails = getCustomerUserDetailsObject(principal);//lấy ra customer đang login để thay đổi firstName và lastName -->fullName của customer hiển thị trên html cũng sẽ thay đổi theo
			Customer authenticatedCustomer = userDetails.getCustomer();
			authenticatedCustomer.setFirstName(customer.getFirstName());
			authenticatedCustomer.setLastName(customer.getLastName());
			
		}
	}
	
	private CustomerUserDetails getCustomerUserDetailsObject(Object principal) {
		CustomerUserDetails userDetails = null;
		if (principal instanceof UsernamePasswordAuthenticationToken) {
			UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
			userDetails = (CustomerUserDetails) token.getPrincipal();
		} else if (principal instanceof RememberMeAuthenticationToken) {
			RememberMeAuthenticationToken token = (RememberMeAuthenticationToken) principal;
			userDetails = (CustomerUserDetails) token.getPrincipal();
		}
		
		return userDetails;
	}
}
