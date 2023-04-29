package com.quanshoppingcart.backend.category;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.quanshoppingcart.backend.FileUploadUtil;
import com.quanshoppingcart.backend.brand.BrandNotFoundException;
import com.quanshoppingcart.backend.brand.BrandService;
import com.quanshoppingcart.common.entity.Brand;
import com.quanshoppingcart.common.entity.Category;

@Controller
public class CategoryController {

	private String defaultRedirectURL = "redirect:/categories";

	@Autowired
	private CategoryService service;
	
	@Autowired
	private BrandService brandService;

	/* Hàm load trang đầu Categories */
	@GetMapping("/categories")
	public String listFirstPage(String sortDir, Model model) {
		return listByPage(1, sortDir, null, model);
	}

	/* Hàm load trang Categories dựa vào số trang truyền vào */
	@GetMapping("/categories/page/{pageNum}")
	private String listByPage(@PathVariable(name = "pageNum") int pageNum, String sortDir, String keyword,
			Model model) {
		if (sortDir == null || sortDir.isEmpty()) {
			sortDir = "asc";
		}

		CategoryPageInfo pageInfo = new CategoryPageInfo();// CategoryPageInfo là đối tượng dùng để lưu trữ totalPages
															// và totalElements. Vì listByPage trả về List<Category> chứ
															// ko trả về Page<Category>
		List<Category> listCategories = service.listByPage(pageInfo, pageNum, sortDir, keyword);

		long startCount = (pageNum - 1) * CategoryService.ROOT_CATEGORIES_PER_PAGE + 1;
		long endCount = startCount + CategoryService.ROOT_CATEGORIES_PER_PAGE - 1;
		if (endCount > pageInfo.getTotalElements()) {
			endCount = pageInfo.getTotalElements();
		}

		String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

		model.addAttribute("currentPage", pageNum);
		model.addAttribute("totalPages", pageInfo.getTotalPages());
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalItems", pageInfo.getTotalElements());
		model.addAttribute("listCategories", listCategories);
		model.addAttribute("sortField", "name");
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDir", reverseSortDir);
		model.addAttribute("keyword", keyword);

		return "categories/categories";
	}

	/* Hàm tạo mới Categories */
	@GetMapping("/categories/new")
	public String newCategory(Model model) {
		List<Category> listCategories = service.listCategoriesUsedInForm();

		model.addAttribute("category", new Category());
		model.addAttribute("listCategories", listCategories);
		model.addAttribute("pageTitle", "Create New Category");

		return "categories/category_form";
	}

	/* Hàm lưu Categories */
	@PostMapping("/categories/save")
	public String saveCategory(Category category, @RequestParam("fileImage") MultipartFile multipartFile,
			RedirectAttributes ra) throws IOException {
		if (!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			category.setImage(fileName);

			Category savedCategory = service.save(category);
			String uploadDir = "../category-images/" + savedCategory.getId();

			FileUploadUtil.cleanDir(uploadDir);
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
		} else {
			service.save(category);
		}

		ra.addFlashAttribute("message", "The category has been saved successfully.");
		return "redirect:/categories";
	}

	/*Hàm lưu category mới từ Brand Form*/
//	@PostMapping("/categories/save/modalForm")
//	public String saveCategoryModalFrom(Category category,
//			@RequestParam("fileImage-newCategoryModal") MultipartFile multipartFile,
//			Model model,
//			RedirectAttributes ra) throws IOException {
//		
//		Brand brand = new Brand();
//		
//		if (!multipartFile.isEmpty()) {
//			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
//			category.setImage(fileName);
//
//			Category savedCategory = service.save(category);
//			String uploadDir = "../category-images/" + savedCategory.getId();
//
//			FileUploadUtil.cleanDir(uploadDir);
//			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
//		} else {
//			service.save(category);
//		}
//		
//		model.addAttribute("brand", brand);
//
//		//ra.addFlashAttribute("message", "The category has been saved successfully.");
//		return "brands/brand_form";
//		
//	}

	/* Hàm Edit */
	@GetMapping("/categories/edit/{id}")
	public String editCategory(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes ra) {
		try {
			Category category = service.get(id);
			List<Category> listCategories = service.listCategoriesUsedInForm();

			model.addAttribute("category", category);
			model.addAttribute("listCategories", listCategories);
			model.addAttribute("pageTitle", "Edit Category (ID: " + id + ")");

			return "categories/category_form";
		} catch (CategoryNotFoundException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
			return defaultRedirectURL;
		}
	}

	/* Hàm enable hoặc able categories */
	@GetMapping("/categories/{id}/enabled/{status}")
	public String updateCategoryEnabledStatus(@PathVariable("id") Integer id, @PathVariable("status") boolean enabled,
			RedirectAttributes redirectAttributes) {
		service.updateCategoryEnabledStatus(id, enabled);
		String status = enabled ? "enabled" : "disabled";
		String message = "The category ID " + id + " has been " + status;
		redirectAttributes.addFlashAttribute("message", message);

		return defaultRedirectURL;
	}

	/* Hàm xóa Categories */
	@GetMapping("/categories/delete/{id}")
	public String deleteCategory(@PathVariable(name = "id") Integer id, Model model,
			RedirectAttributes redirectAttributes) {
		try {
			service.delete(id);
			String categoryDir = "../category-images/" + id;
			FileUploadUtil.removeDir(categoryDir);

			redirectAttributes.addFlashAttribute("message", "The category ID " + id + " has been deleted successfully");
		} catch (CategoryNotFoundException ex) {
			redirectAttributes.addFlashAttribute("message", ex.getMessage());
		}

		return defaultRedirectURL;
	}

}
