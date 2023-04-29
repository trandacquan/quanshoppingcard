package com.quanshoppingcart.frontend.cart;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.quanshoppingcart.common.entity.CartItem;
import com.quanshoppingcart.common.entity.Customer;
import com.quanshoppingcart.common.entity.product.Product;
import com.quanshoppingcart.frontend.product.ProductRepository;

@Service
@Transactional
public class ShoppingCartService {

	@Autowired
	private CartItemRepository cartRepo;
	@Autowired
	private ProductRepository productRepo;

	public Integer addProduct(Integer productId, Integer quantity, Customer customer) throws ShoppingCartException {
		Integer updatedQuantity = quantity;
		Product product = new Product(productId);

		CartItem cartItem = cartRepo.findByCustomerAndProduct(customer, product);
		// Kiểm tra cartItem tương ứng với customer và product đã tồn tại trong db chưa
		// , nếu đã tồn tại thì lấy ra cartItem này.

		if (cartItem != null) {// Nếu đã tồn tại cartItem trong DB thì lấy ra cartItem đó
			updatedQuantity = cartItem.getQuantity() + quantity;
			// Lấy giá trị quantity của cartItem vừa lấy từ DB + quantity.

			if (updatedQuantity > 5) {
				throw new ShoppingCartException("Could not add more " + quantity + "items" + "because there's already "
						+ cartItem.getQuantity() + "items" + "in your shopping cart. Maximum allowed quantity is 5.");
			}
		} else {// Nếu chưa có cartItem trong db thì tạo mới
			cartItem = new CartItem();
			cartItem.setCustomer(customer);// Gán customer cho cartItem mới tạo
			cartItem.setProduct(product);// gán product cho cartItem mới tạo
		}

		cartItem.setQuantity(updatedQuantity);// update lại quantity trong cartItem

		cartRepo.save(cartItem);// Lưu cartItem xuống DB

		return updatedQuantity;
	}

	public List<CartItem> listCartItems(Customer customer) {
		return cartRepo.findByCustomer(customer);
	}

	public float updatedQuantity(Integer productId, Integer quantity, Customer customer) {
		cartRepo.updateQuantity(quantity, customer.getId(), productId);
		// Update lại quantity dưới DB

		Product product = productRepo.findById(productId).get();
		float subtotal = product.getDiscountPrice() * quantity;
		return subtotal;
	}

	public void removeProduct(Integer productId, Customer customer) {
		cartRepo.deleteByCustomerAndProduct(customer.getId(), productId);// xóa cartItem trong db
	}

	// sau khi create order thì xóa cartItem này trong db
	public void deleteByCustomer(Customer customer) {
		cartRepo.deleteByCustomer(customer.getId());
	}

}
