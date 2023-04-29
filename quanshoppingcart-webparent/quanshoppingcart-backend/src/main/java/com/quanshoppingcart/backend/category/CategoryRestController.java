package com.quanshoppingcart.backend.category;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.quanshoppingcart.backend.FileUploadUtil;
import com.quanshoppingcart.common.entity.Brand;
import com.quanshoppingcart.common.entity.Category;

@RestController
public class CategoryRestController {

	@Autowired
	private CategoryService service;

	@PostMapping("/categories/check_unique")
	public String checkUnique(@Param("id") Integer id, @Param("name") String name, @Param("alias") String alias) {//params = {id: catId, name: catName, alias: catAlias} -->khi dùng @Param thì các tham số ko cần đúng thứ tự
		return service.checkUnique(id, name, alias);
	}
	
}
