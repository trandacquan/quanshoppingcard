package com.quanshoppingcart.frontend.cart;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.quanshoppingcart.common.entity.Customer;
import com.quanshoppingcart.common.exception.CustomerNotFoundException;
import com.quanshoppingcart.frontend.Utility;
import com.quanshoppingcart.frontend.customer.CustomerService;

@RestController
public class ShoppingCartRestController {

	@Autowired
	private ShoppingCartService cartService;
	@Autowired
	private CustomerService customerService;

	/*
	 * Hàm thêm một product cart
	 **/
	@PostMapping("/cart/add/{productId}/{quantity}")
	public String addProductToCart(@PathVariable("productId") Integer productId, // Bắt productId trên URL
			@PathVariable("quantity") Integer quantity, // Bắt quantity trên URL
			HttpServletRequest request) {

		try {
			Customer customer = getAuthenticatedCustomer(request);
			Integer updatedQuantity = cartService.addProduct(productId, quantity, customer);

			return updatedQuantity + "items of this product were added to your shopping cart.";
		} catch (CustomerNotFoundException ex) {
			return "You must login to add this product to cart.";
		} catch (ShoppingCartException ex) {
			return ex.getMessage();
		}

	}

	private Customer getAuthenticatedCustomer(HttpServletRequest request) throws CustomerNotFoundException {
		String email = Utility.getEmailOfAuthenticatedCustomer(request);
		// phương thức này sẽ lấy ra customer đang đăng nhập, sau đó trả về email của
		// customer đó
		if (email == null) {
			throw new CustomerNotFoundException("No authenticated customer");
			// nếu customer = null -->chưa đăng nhập
		}

		return customerService.getCustomerByEmail(email);
	}

	@PostMapping("/cart/update/{productId}/{quantity}") // cập nhật lại quantity dưới db
	public String updateQuantity(@PathVariable("productId") Integer productId,
			@PathVariable("quantity") Integer quantity, HttpServletRequest request) {
		try {
			Customer customer = getAuthenticatedCustomer(request);
			float subtotal = cartService.updatedQuantity(productId, quantity, customer);

			return String.valueOf(subtotal);// trả về subtotal mới sau khi đã update quantity
		} catch (CustomerNotFoundException ex) {
			return "You must login to change quantity of product.";
		}
	}

	@DeleteMapping("/cart/remove/{productId}") // remove product trong cart
	public String removeProduct(@PathVariable("productId") Integer productId, HttpServletRequest request) {
		try {
			Customer customer = getAuthenticatedCustomer(request);
			cartService.removeProduct(productId, customer);// xóa cartItem trong db

			return "The product has been removed from your shopping cart.";

		} catch (CustomerNotFoundException e) {
			return "You must login to remove product.";
		}
	}

}
