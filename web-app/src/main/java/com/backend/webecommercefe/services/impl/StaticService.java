package com.backend.webecommercefe.services.impl;

import com.backend.webecommercefe.untils.Utilfunctions;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

@Service
public class StaticService {
    private static final Logger log = LoggerFactory.getLogger(StaticService.class);
    @Autowired
    private RestClient restClient;

    @Autowired
    private ObjectMapper objectMapper;

    public Long countUser(){
        String apiUrl = "http://localhost:8080/api/user";
        String bearer = Utilfunctions.GET_BEAR_LOCAL();

        String response = restClient.get()
                .uri(apiUrl.toString())
                .header("Authorization", bearer)
                .header("Content-Type", "application/json")
                .header("Cache-Control", "no-cache")
                .retrieve()
                .body(String.class);


        log.error("data = " + response);
        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode users = root.path("data").path("users");

            long count = users.size();

            log.error("Số lượng user = " + count);
            return count;
        } catch (Exception e) {
            log.error("Lỗi khi phân tích JSON", e);
            return 0L;
        }
    }

    public Double countRenue(){
        String apiUrl = "http://localhost:9292/api/v1/orders";
        String bearer = Utilfunctions.GET_BEAR_LOCAL();

        String response = restClient.get()
                .uri(apiUrl.toString())
                .header("Authorization", bearer)
                .header("Content-Type", "application/json")
                .header("Cache-Control", "no-cache")
                .retrieve()
                .body(String.class);

        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode dataArray = root.get("data");

            double total = 0;
            if (dataArray.isArray()) {
                for (JsonNode item : dataArray) {
                    total += item.get("tongTien").asDouble();
                }
            }

            log.error("Tổng doanh số = " + total);
            return total;
        }catch (Exception e){
            return 0.0;
        }
    }

    public Long countOrder(){
        String apiUrl = "http://localhost:9292/api/v1/orders";
        String bearer = Utilfunctions.GET_BEAR_LOCAL();

        String response = restClient.get()
                .uri(apiUrl.toString())
                .header("Authorization", bearer)
                .header("Content-Type", "application/json")
                .header("Cache-Control", "no-cache")
                .retrieve()
                .body(String.class);

        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode dataArray = root.get("data");

            // Kiểm tra nếu mảng "data" là một mảng
            if (dataArray.isArray()) {
                // Trả về số lượng đơn hàng trong mảng
                log.error("so don hang = " + dataArray.size());
                return (long) dataArray.size();
            }
            return 0L;
        }catch (Exception e){
            return 0L;
        }
    }

}
