package com.quanshoppingcart.frontend;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.quanshoppingcart.common.entity.Category;
import com.quanshoppingcart.frontend.category.CategoryService;

@Controller
public class MainController {

	@Autowired
	private CategoryService categoryService;

	@GetMapping("")
	public String viewHomePage(Model model) {
		// Tạo một List listCategories sử dụng hàm listNoChildrenCategories từ service
		List<Category> listCategories = categoryService.listNoChildrenCategories();
		// Map listCategories từ Controller vào "listCategories" bên View
		model.addAttribute("listCategories", listCategories);

		return "index";
	}

	/*Hàm viewLoginPage khi chọn trong Navigation/Login*/
	@GetMapping("/login")
	public String viewLoginPage() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
			return "login";
		}

		return "redirect:/";
	}

}
