package com.backend.webecommercefe.controllers.admin;

import com.backend.webecommercefe.util.UtilVariable;
import com.backend.webecommercefe.util.Utilfunctions;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;

import java.util.*;

@Controller
public class ProductController {

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

    @GetMapping("/admin")
    public String getAdmin(){
        log.error("trang admin");
        return "/index";
    }

    // Hiển thị danh sách sản phẩm
    @GetMapping("/admin/product")
    public String getProducts(
            @RequestParam(value = "pageNo", defaultValue = "1") int pageNo,
            @RequestParam(value = "size", defaultValue = "5") int size,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "success", required = false) String success,
            @RequestParam(value = "error", required = false) String error,
            HttpSession session,
            Model model) {
        try {
            // Tạo URL với query parameters
            StringBuilder url = new StringBuilder(PRODUCT_SERVICE_URL);

            log.info("Calling API: {}", url);

            // Gọi API từ product-service mà không gửi token (vì permitAll)
            String response = restClient.get()
                    .uri(url.toString())
                    .header("Content-Type", "application/json")
                    .header("Cache-Control", "no-cache")
                    .retrieve()
                    .body(String.class);

            log.info("Product service response: {}", response);

            // Chuyển đổi JSON response thành Map
            Map<String, Object> responseMap = objectMapper.readValue(response, new TypeReference<Map<String, Object>>() {});

            // Lấy danh sách sản phẩm từ trường "data"
            List<Map<String, Object>> products = (List<Map<String, Object>>) responseMap.get("data");
            if (products == null) {
                products = Collections.emptyList();
            }

            //----finding
            if (keyword != null && !keyword.trim().isEmpty()) {
                String lowerKeyword = keyword.trim().toLowerCase();

                List<Map<String, Object>> findProduct = new ArrayList<>();

                for (Map<String, Object> product : products) {
                    String tensp = (String) product.get("tensp");
                    if (tensp != null && tensp.toLowerCase().contains(lowerKeyword)) {
                        findProduct.add(product);
                    }
                }

                products = findProduct;

            }

            log.error("Khong tim kiem");

            // Giả lập phân trang
            int totalElements = products.size();
            int totalPages = (int) Math.ceil((double) totalElements / size);
            int startIndex = (pageNo - 1) * size;
            int endIndex = Math.min(startIndex + size, totalElements);
            List<Map<String, Object>> paginatedProducts = products.subList(startIndex, endIndex);

            model.addAttribute("listProduct", paginatedProducts);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("currentPage", pageNo);
            model.addAttribute("size", totalElements);
            model.addAttribute("keyword", keyword);
            model.addAttribute("success", success);
            model.addAttribute("error", error);

        } catch (Exception e) {
            log.error("Error fetching products: {}", e.getMessage(), e);
            model.addAttribute("listProduct", Collections.emptyList());
            model.addAttribute("totalPages", 1);
            model.addAttribute("currentPage", pageNo);
            model.addAttribute("size", 0);
            model.addAttribute("keyword", keyword);
            model.addAttribute("error", "Unable to fetch products: " + e.getMessage());
        }

        return "admin/product/index"; // Trả về admin/product/index.html
    }

    // Hiển thị form thêm sản phẩm mới
    @GetMapping("/admin/product/add-product")
    public String showAddProductForm(Model model) {
        try {
            // Gọi API GET /api/categories để lấy danh sách danh mục
            String categoriesResponse = restClient.get()
                    .uri(CATEGORY_SERVICE_URL)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + Utilfunctions.GET_TOKEN_LOCAL())
                    .retrieve()
                    .body(String.class);

            log.info("Get categories response: {}", categoriesResponse);

            // Parse toàn bộ JSON thành Map
            Map<String, Object> responseMap = objectMapper.readValue(categoriesResponse, new TypeReference<Map<String, Object>>() {});
            Object dataRaw = responseMap.get("data");

            // Convert phần "data" sang JSON rồi parse lại thành danh sách map
            String dataJson = objectMapper.writeValueAsString(dataRaw);
            List<Map<String, Object>> categories = objectMapper.readValue(dataJson, new TypeReference<List<Map<String, Object>>>() {});

            if (categories == null) {
                categories = Collections.emptyList();
            }

            model.addAttribute("product", new HashMap<String, Object>());
            model.addAttribute("categories", categories); // Thêm danh sách danh mục vào model

        } catch (Exception e) {
            log.error("Error fetching categories: {}", e.getMessage(), e);
            model.addAttribute("error", "Unable to fetch categories: " + e.getMessage());
            model.addAttribute("product", new HashMap<String, Object>());
            model.addAttribute("categories", Collections.emptyList());
        }

        return "admin/product/add"; // Trả về admin/product/add.html
    }

    // Xử lý thêm sản phẩm mới
    @PostMapping(value = "/admin/product/add-product", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String addProduct(
            @RequestParam("ten_sp") String tenSp,
            @RequestParam("mo_ta") String moTa,
            @RequestParam("gia_ban") Double giaBan,
            @RequestParam("gia_nhap") Double giaNhap,
            @RequestParam("gia_goc") Double giaGoc,
            @RequestParam("so_luong") Integer soLuong,
            @RequestParam(value = "mau_sac", required = false) String mauSac,
            @RequestParam(value = "kich_co", required = false) String kichCo,
            @RequestParam("category_id") Long categoryId,
            @RequestParam(value = "hinhAnh", required = false) MultipartFile hinhAnh,
            @RequestParam(value = "mau_sac", required = false) List<String> mauSacList,
            @RequestParam(value = "kich_co", required = false) List<String> kichCoList,
            @RequestParam(value = "gia_ban", required = false) List<Double> giaBanList,
            @RequestParam(value = "gia_goc", required = false) List<Double> giaGocList,
            @RequestParam(value = "so_luong", required = false) List<Integer> soLuongList,
            HttpSession session,
            Model model) {
        try {
            // Tạo sản phẩm chính
            Map<String, Object> request = new HashMap<>();
            request.put("ten_sp", tenSp);
            request.put("moTa", moTa);
            request.put("giaBan", giaBan);
            request.put("giaNhap", giaNhap);
            request.put("giaGoc", giaGoc);
            request.put("soLuong", soLuong);
            request.put("mauSac", mauSac != null ? mauSac : "");
            request.put("kichCo", kichCo != null ? kichCo : "");
            request.put("category_id", categoryId);

            if (hinhAnh != null && !hinhAnh.isEmpty()) {
                var imagesName = Utilfunctions.uploadImage(hinhAnh);
                request.put("hinhAnh", imagesName);
            }

            // Chuyển Map thành chuỗi JSON
            String requestJson = objectMapper.writeValueAsString(request);

            // Xây dựng MultiValueMap cho multipart/form-data
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

            // Tạo phần "request" với Content-Type: application/json
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> requestPart = new HttpEntity<>(requestJson, requestHeaders);
            body.add("request", requestPart);

            // Gửi yêu cầu POST mà không cần token (vì permitAll)
            log.info("Sending POST request to {}", PRODUCT_SERVICE_URL);

            String bodyJson = objectMapper.writeValueAsString(body);
            log.info("Body JSON: " + bodyJson);


            String response = restClient.post()
                    .uri(PRODUCT_SERVICE_URL)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + Utilfunctions.GET_TOKEN_LOCAL())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestJson)
                    .retrieve()
                    .body(String.class);

            log.info("Add main product response: {}", response);

            // Xử lý các sản phẩm cùng loại (nếu có)

            return "redirect:/admin/product?success=Product added successfully";
        } catch (Exception e) {
            log.error("Error adding product: {}", e.getMessage(), e);
            model.addAttribute("error", "Unable to add product: " + e.getMessage());
            return "admin/product/add";
        }
    }

    // Hiển thị form chỉnh sửa sản phẩm
    @GetMapping("/admin/product/edit-product/{id}")
    public String showEditProductForm(@PathVariable("id") Long id, HttpSession session, Model model) {
        try {
            log.error("chỉnh sửa sản phẩm");
            // Gọi API GET /products/{id} để lấy thông tin sản phẩm
            String productResponse = restClient.get()
                    .uri(PRODUCT_SERVICE_URL + "/" + id)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + Utilfunctions.GET_TOKEN_LOCAL())
                    .header("Content-Type", "application/json")
                    .retrieve()
                    .body(String.class);

            log.info("Get product response: {}", productResponse);

            // Chuyển đổi JSON response thành Map
            Map<String, Object> productResponseMap = objectMapper.readValue(productResponse, new TypeReference<Map<String, Object>>() {});
            Map<String, Object> product = (Map<String, Object>) productResponseMap.get("data");

            if (product == null) {
                throw new Exception("Product not found");
            }

            // Gọi API GET /api/categories để lấy danh sách danh mục
            String categoriesResponse = restClient.get()
                    .uri(CATEGORY_SERVICE_URL)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + Utilfunctions.GET_TOKEN_LOCAL())
                    .retrieve()
                    .body(String.class);

            log.info("Get categories response: {}", categoriesResponse);

//            List<Map<String, Object>> categories = objectMapper.readValue(categoriesResponse, new TypeReference<List<Map<String, Object>>>() {});

            Map<String, Object> root = objectMapper.readValue(categoriesResponse, new TypeReference<Map<String, Object>>() {});
            List<Map<String, Object>> categories = (List<Map<String, Object>>) root.get("data");

            if (categories == null) {
                categories = Collections.emptyList();
            }

            model.addAttribute("product", product);
            model.addAttribute("categories", categories); // Thêm danh sách danh mục vào model
        } catch (Exception e) {
            log.error("Error fetching product or categories: {}", e.getMessage(), e);
            model.addAttribute("error", "Unable to fetch product or categories: " + e.getMessage());
            model.addAttribute("product", new HashMap<String, Object>());
            model.addAttribute("categories", Collections.emptyList());
        }

        return "admin/product/edit"; // Trả về admin/product/edit.html
    }
    // Xử lý cập nhật sản phẩm
    @PostMapping(value = "/admin/product/edit-product/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String updateProduct(
            @PathVariable("id") Long id,
            @RequestParam("ten_sp") String tenSp,
            @RequestParam("mo_ta") String moTa,
            @RequestParam("gia_ban") Double giaBan,
            @RequestParam("gia_nhap") Double giaNhap,
            @RequestParam("gia_goc") Double giaGoc,
            @RequestParam("so_luong") Integer soLuong,
            @RequestParam(value = "mau_sac", required = false) String mauSac,
            @RequestParam(value = "kich_co", required = false) String kichCo,
            @RequestParam("category_id") Long categoryId,
            @RequestParam("old_image") String oldImage,
            @RequestParam(value = "hinhAnh", required = false) MultipartFile hinhAnh,
            HttpSession session,
            Model model) {
        Map<String, Object> product = new HashMap<>();
        product.put("id", id);
        product.put("ten_sp", tenSp);
        product.put("moTa", moTa);
        product.put("giaBan", giaBan);
        product.put("giaNhap", giaNhap);
        product.put("giaGoc", giaGoc);
        product.put("soLuong", soLuong);
        product.put("mauSac", mauSac != null ? mauSac : "");
        product.put("kichCo", kichCo != null ? kichCo : "");
        product.put("category_id", categoryId);

        try {
            Map<String, Object> request = new HashMap<>();
            request.put("id", id);
            request.put("ten_sp", tenSp);
            request.put("moTa", moTa);
            request.put("giaBan", giaBan);
            request.put("giaNhap", giaNhap);
            request.put("giaGoc", giaGoc);
            request.put("soLuong", soLuong);
            request.put("mauSac", mauSac != null ? mauSac : "");
            request.put("kichCo", kichCo != null ? kichCo : "");
            request.put("category_id", categoryId);

//             Nếu có file ảnh, gửi yêu cầu cập nhật ảnh
            if (hinhAnh != null && !hinhAnh.isEmpty()) {
                log.error(">>>> anh name = " + hinhAnh.getOriginalFilename());
                var imagesName = Utilfunctions.uploadImage(hinhAnh);
                if (imagesName != null){
                    request.put("hinhAnh", imagesName);
                }else {
                    request.put("hinhAnh", oldImage);
                }

            }else {
                request.put("hinhAnh", oldImage);
            }

            String requestJson = objectMapper.writeValueAsString(request);

            // Gửi yêu cầu PUT mà không cần token (vì permitAll)
            log.info("Sending PUT request to {}", PRODUCT_SERVICE_URL + "/" + id);
            String response = restClient.put()
                    .uri(PRODUCT_SERVICE_URL + "/" + id)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + Utilfunctions.GET_TOKEN_LOCAL())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestJson)
                    .retrieve()
                    .body(String.class);

            log.info("Update product response: {}", response);

            return "redirect:/admin/product?success=Product updated successfully";
        } catch (Exception e) {
            log.error("Error updating product: {}", e.getMessage(), e);
            model.addAttribute("error", "Unable to update product: " + e.getMessage());
            model.addAttribute("product", product);
            return "admin/product/edit";
        }
    }

    // Xử lý xóa sản phẩm
    @GetMapping("/admin/product/delete-product/{id}")
    public String deleteProduct(@PathVariable("id") Long id, HttpSession session) {
        try {
            log.info("Sending DELETE request to {}", PRODUCT_SERVICE_URL + "/" + id);
            restClient.delete()
                    .uri(PRODUCT_SERVICE_URL + "/" + id)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + Utilfunctions.GET_TOKEN_LOCAL())
                    .retrieve()
                    .toBodilessEntity();
            return "redirect:/admin/product?success=Product deleted successfully";
        } catch (Exception e) {
            log.error("Error deleting product: {}", e.getMessage(), e);
            return "redirect:/admin/product?error=Unable to delete product: " + e.getMessage();
        }
    }



}