package com.backend.webecommercefe.controllers.client;

import com.backend.webecommercefe.entities.Account;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClient;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("")
@Slf4j
public class IndexController {

    @Autowired
    private RestClient restClient;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String PRODUCT_SERVICE_URL = "http://localhost:9898/products";
    private static final String CATEGORY_SERVICE_URL = "http://localhost:8080/api/v1/categories";
    @Autowired
    private ListableBeanFactory listableBeanFactory;

    @GetMapping("/index")
    public String index(@RequestParam(value = "pageNo", defaultValue = "1") int pageNo,
                        @RequestParam(value = "size", defaultValue = "6") int size,
                        @RequestParam(value = "keyword", required = false) String keyword,
                        Model model) {
        try {
            StringBuilder url = new StringBuilder(PRODUCT_SERVICE_URL);

            String response = restClient.get()
                    .uri(url.toString())
                    .header("Content-Type", "application/json")
                    .header("Cache-Control", "no-cache")
                    .retrieve()
                    .body(String.class);

            log.info("Product service response: {}", response);

            // Chuyển đổi JSON response thành Map
            Map<String, Object> responseMap = objectMapper.readValue(response, new TypeReference<Map<String, Object>>() {
            });

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

            log.error(">>>>> list product = " + paginatedProducts);

            model.addAttribute("listProduct", paginatedProducts);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("currentPage", pageNo);
            model.addAttribute("size", totalElements);
            model.addAttribute("keyword", keyword);
        } catch (Exception e) {
            log.error("Error fetching products: {}", e.getMessage(), e);
            model.addAttribute("listProduct", Collections.emptyList());
            model.addAttribute("totalPages", 1);
            model.addAttribute("currentPage", pageNo);
            model.addAttribute("size", 0);
            model.addAttribute("keyword", keyword);
        }


        return "user/home/index";
    }

    @GetMapping("/cart")
    public String cart(){
        return "user/cart/cart";
    }

    @GetMapping("/checkout")
    public String checkout(){
        return "user/checkouts/index";
    }
}
