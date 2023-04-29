package com.quanshoppingcart.frontend.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.quanshoppingcart.common.entity.product.Product;

public interface ProductRepository extends PagingAndSortingRepository<Product, Integer>{
	
	@Query("SELECT p FROM Product p WHERE p.enabled = true"
			+ " AND (p.category.id = ?1 OR p.category.allParentIDs LIKE %?2%)"
			+ " ORDER BY p.name ASC")
	public Page<Product>listByCategory(Integer categoryId, String categoryIDMatch, Pageable pageable);
	
	public Product findByAlias(String alias);
	
	//Full Text Search giúp tìm kiếm nhanh chóng và hiệu quả
	//Full Text Search được sử dụng khi tìm kiếm trên nhiều cột và dữ liệu tìm kiếm lớn
	
	//Thực hiện Full Text Search:
	//alter table products, chọn indexes
		//Index Name: products_FTS và Type: FULLTEXT
		//Index Columns chọn name, short_description, full_description
		/* câu SQL tương ứng sẽ là: 
		ALTER TABLE `shoppingcart`.`products` 
		ADD FULLTEXT INDEX `products_FTS` (`name`, `short_description`, `full_description`);
		*/
	
	@Query(value = "SELECT * FROM products WHERE enabled = true AND "
			+ "MATCH(name, short_description, full_description) AGAINST (?1)", 
			nativeQuery = true)
	public Page<Product> search(String keyword, Pageable pageable);
	
}
