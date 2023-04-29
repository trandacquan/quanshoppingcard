package com.quanshoppingcart.backend;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

	@GetMapping("") //localhost:8085/QuanShoppingCartBackend
	public String viewHomePage() {
		return "index";
	}

	@GetMapping("/login")
	public String viewLoginPage() {
		//trường hợp vừa login xong, đang ở trang index, bấm back lại thì nó vẫn giữ ở trang index
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
			//nếu authentication == null thì có nghĩa là chưa login -->trả về trang login
			return "login";
		}

		return "redirect:/";
	}
}
