package com.quanshoppingcart.frontend.category;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.quanshoppingcart.common.entity.Category;
import com.quanshoppingcart.common.exception.CategoryNotFoundException;

@Service
public class CategoryService {

	@Autowired
	private CategoryRepository repo;

	public List<Category> listNoChildrenCategories() {
		// Tạo list rỗng để chứa category không có children
		List<Category> listNoChildrenCategories = new ArrayList<>();

		// Truy cập DB trả về list các category có enabled
		List<Category> listEnabledCategories = repo.findAllEnabled();

		for (Category category : listEnabledCategories) {// Duyệt qua list các category có enabled
			// Tạo một Set children của mỗi category khi duyệt qua
			Set<Category> children = category.getChildren();
			// Nếu Set children là null hoặc kích thước là 0 thì thêm vào List
			// listNoChildrenCategories
			if (children == null || children.size() == 0) {
				listNoChildrenCategories.add(category);
			}
		}
		return listNoChildrenCategories;
	}

	public Category getCategory(String alias) throws CategoryNotFoundException {
		Category category = repo.findByAliasEnabled(alias);
		if (category == null) {
			throw new CategoryNotFoundException("Could not find any categories with alias " + alias);
		}

		return category;
	}

	public List<Category> getCategoryParents(Category child) {
		List<Category> listParents = new ArrayList<>();

		Category parent = child.getParent();

		while (parent != null) {
			listParents.add(0, parent);
			parent = parent.getParent();
		}

		listParents.add(child);

		return listParents;
	}

}
