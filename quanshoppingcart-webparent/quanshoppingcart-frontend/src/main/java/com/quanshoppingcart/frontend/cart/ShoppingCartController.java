package com.quanshoppingcart.frontend.cart;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.quanshoppingcart.common.entity.CartItem;
import com.quanshoppingcart.common.entity.Customer;
import com.quanshoppingcart.frontend.ControllerHelper;

@Controller
public class ShoppingCartController {

	@Autowired
	private ControllerHelper controllerHelper;
	@Autowired
	private ShoppingCartService cartService;

	@GetMapping("/cart")
	public String viewCart(Model model, HttpServletRequest request) {
		Customer customer = controllerHelper.getAuthenticatedCustomer(request);
		// Dùng email để lấy ra customer tương ứng

		List<CartItem> cartItems = cartService.listCartItems(customer);

		float estimatedTotal = 0.0F;
		// Tính tổng số tiền của tất cả các cartItems

		for (CartItem item : cartItems) {
			estimatedTotal += item.getSubtotal();
			// Số tiền của mỗi cartItem = discountPrice * quantity
		}

		model.addAttribute("cartItems", cartItems);
		model.addAttribute("estimatedTotal", estimatedTotal);

		return "cart/shopping_cart";
	}

}
