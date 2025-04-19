package com.backend.webecommercefe.services.impl;

import com.backend.webecommercefe.dto.ApiResponseDTO;
import com.backend.webecommercefe.entities.Product;
import com.backend.webecommercefe.services.ProductService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.core.ParameterizedTypeReference;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final RestTemplate restTemplate; // Tiêm RestTemplate vào

    @Value("${product.api.url}")
    private String baseUrl;

    // Constructor để tiêm RestTemplate
    public ProductServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate; // Cấu hình RestTemplate
    }

    @Override
    public List<Product> getAllProducts() {
        // Log URL đang gọi
        System.out.println("CALLING: " + baseUrl + "/products");

        // Gửi yêu cầu GET và nhận về ApiResponseDTO
        ResponseEntity<ApiResponseDTO<List<Product>>> response = restTemplate.exchange(
                baseUrl + "/products",  // URL của API
                HttpMethod.GET,  // Phương thức HTTP là GET
                null,  // Không có thân request cho GET
                new ParameterizedTypeReference<ApiResponseDTO<List<Product>>>() {} // Xác định kiểu dữ liệu trả về
        );

        // Log tổng số sản phẩm nhận được từ FE
        if (response.getBody() != null && response.getBody().getData() != null) {
            System.out.println("Tổng sản phẩm từ FE: " + response.getBody().getData().size());

            for (Product product : response.getBody().getData()) {
                System.out.println("ID: " + product.getId());
                System.out.println("Tên sản phẩm: " + product.getTenSanPham());
                System.out.println("Giá bán: " + product.getGiaBan());
                System.out.println("Giá gốc: " + product.getGiaGoc());
                System.out.println("Màu sắc: " + product.getMauSac());
                System.out.println("Kích cỡ: " + product.getKichCo());
                System.out.println("Số lượng: " + product.getSoLuong());
                System.out.println("Mô tả: " + product.getMoTa());
                System.out.println("Hình ảnh: " + product.getHinhAnh());
                System.out.println("-------------------------------");
            }
        } else {
            System.out.println("Dữ liệu sản phẩm không hợp lệ.");
        }

        System.out.println("Dữ liệu trả về từ API: " + response.getBody());

        return response.getBody() != null ? response.getBody().getData() : new ArrayList<>();
    }
}
