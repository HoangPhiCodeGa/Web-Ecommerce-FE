package com.backend.webecommercefe.controllers.admin;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class CategoryController {
    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private RestClient restClient;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String PRODUCT_SERVICE_URL = "http://localhost:9898/products";
    private static final String CATEGORY_SERVICE_URL = "http://localhost:8080/api/v1/categories";

    // Lấy JWT token từ session
    private String getJwtToken(HttpSession session) {
        String token = (String) session.getAttribute("jwtToken");
        if (token == null) {
            log.debug("No JWT token found in session");
            return null;
        }
        log.debug("JWT token found: {}", token);
        return "Bearer " + token;
    }

    // Hiển thị danh sách danh mục
    @GetMapping("/admin/category")
    public String getCategories(
            @RequestParam(value = "pageNo", defaultValue = "1") int pageNo,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "success", required = false) String success,
            @RequestParam(value = "error", required = false) String error,
            Model model) {
        try {
            StringBuilder url = new StringBuilder(CATEGORY_SERVICE_URL);
            if (keyword != null && !keyword.isEmpty()) {
                url.append("?keyword=").append(keyword);
            }

            log.info("Calling API: {}", url);

            String response = restClient.get()
                    .uri(url.toString())
                    .retrieve()
                    .body(String.class);

            log.info("Category service response: {}", response);

            List<Map<String, Object>> categories = objectMapper.readValue(response, new TypeReference<List<Map<String, Object>>>() {
            });
            if (categories == null) {
                categories = Collections.emptyList();
            }

            int totalElements = categories.size();
            int totalPages = (int) Math.ceil((double) totalElements / size);
            int startIndex = (pageNo - 1) * size;
            int endIndex = Math.min(startIndex + size, totalElements);
            List<Map<String, Object>> paginatedCategories = categories.subList(startIndex, endIndex);

            model.addAttribute("categories", paginatedCategories);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("currentPage", pageNo);
            model.addAttribute("size", totalElements);
            model.addAttribute("keyword", keyword);
//            model.addAttribute("success", success);
//            model.addAttribute("error", error);

        } catch (Exception e) {
            log.error("Error fetching categories: {}", e.getMessage(), e);
            model.addAttribute("categories", Collections.emptyList());
            model.addAttribute("totalPages", 1);
            model.addAttribute("currentPage", pageNo);
            model.addAttribute("size", 0);
            model.addAttribute("keyword", keyword);
//            model.addAttribute("error", "Unable to fetch categories: " + e.getMessage());
        }

        return "admin/category/index";
    }

    // Hiển thị form thêm danh mục mới
    @GetMapping("/admin/category/add-category")
    public String showAddCategoryForm(Model model) {
        model.addAttribute("category", new HashMap<String, Object>());
        return "admin/category/add";
    }

    // Hiển thị form chỉnh sửa danh mục
    @GetMapping("/admin/category/edit-category/{id}")
    public String showEditCategoryForm(@PathVariable("id") Long id, Model model) {
//        try {
//
//            String response = restClient.get()
//                    .uri(CATEGORY_SERVICE_URL + "/" + id)
//                    .header("Authorization", "Bearer " + token)
//                    .retrieve()
//                    .body(String.class);
//
//            log.info("Get category response: {}", response);
//
//            Map<String, Object> category = objectMapper.readValue(response, new TypeReference<Map<String, Object>>() {});
//            model.addAttribute("category", category);
//        } catch (Exception e) {
//            log.error("Error fetching category: {}", e.getMessage(), e);
//            model.addAttribute("error", "Unable to fetch category: " + e.getMessage());
//            model.addAttribute("category", new HashMap<String, Object>());
//        }

        return "admin/category/edit";
    }

    // Xử lý thêm danh mục mới
    @PostMapping("/admin/category/save-category")
    public String saveCategory(@RequestParam("tenLoai") String tenLoai) {
        try {
            if (tenLoai == null || tenLoai.trim().isEmpty()) {
                throw new IllegalArgumentException("Category name cannot be empty");
            }

            Map<String, Object> request = new HashMap<>();
            request.put("ten_loai", tenLoai); // Sửa từ "tenLoai" thành "ten_loai"

            log.info("Saving new category: {}", tenLoai);

            String response = restClient.post()
                    .uri(CATEGORY_SERVICE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(String.class);

            log.info("Save category response: {}", response);

            return "redirect:/admin/category?success=Category added successfully";
        } catch (Exception e) {
            log.error("Error saving category: {}", e.getMessage(), e);
            return "redirect:/admin/category/add-category?error=" + URLEncoder.encode("Unable to add category: " + e.getMessage(), StandardCharsets.UTF_8);
        }
    }

    // Xử lý cập nhật danh mục
    @PostMapping("/admin/category/update-category")
    public String updateCategory(
            @RequestParam("id") Long id,
            @RequestParam("tenLoai") String tenLoai) {
        try {
            if (tenLoai == null || tenLoai.trim().isEmpty()) {
                throw new IllegalArgumentException("Category name cannot be empty");
            }

            Map<String, Object> request = new HashMap<>();
            request.put("id", id);
            request.put("ten_loai", tenLoai); // Sửa từ "tenLoai" thành "ten_loai"

            log.info("Updating category with id: {}", id);

            String response = restClient.put()
                    .uri(CATEGORY_SERVICE_URL + "/" + id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(String.class);

            log.info("Update category response: {}", response);

            return "redirect:/admin/category?success=Category updated successfully";
        } catch (Exception e) {
            log.error("Error updating category: {}", e.getMessage(), e);
            return "redirect:/admin/category/edit-category/" + id + "?error=" + URLEncoder.encode("Unable to update category: " + e.getMessage(), StandardCharsets.UTF_8);
        }
    }

    // Xử lý xóa danh mục
    @GetMapping("/admin/category/delete-category/{id}")
    public String deleteCategory(@PathVariable("id") Long id) {
        try {
            log.info("Deleting category with id: {}", id);

            restClient.delete()
                    .uri(CATEGORY_SERVICE_URL + "/" + id)
                    .retrieve()
                    .toBodilessEntity();

            return "redirect:/admin/category?success=Category deleted successfully";
        } catch (HttpClientErrorException.Conflict e) {
            log.error("Conflict error deleting category: {}", e.getMessage(), e);
            return "redirect:/admin/category?error=" + URLEncoder.encode("Cannot delete category because it has associated products", StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("Error deleting category: {}", e.getMessage(), e);
            return "redirect:/admin/category?error=" + URLEncoder.encode("Unable to delete category: " + e.getMessage(), StandardCharsets.UTF_8);
        }
    }

}
