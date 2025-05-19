package com.backend.webecommercefe.util;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Slf4j
public class Utilfunctions {
    private static final RestClient restClient = RestClient.create();

    public static String GET_TOKEN_LOCAL() {
        try {
//            InputStream is = Utilfunctions.class.getClassLoader().getResourceAsStream("file/token.txt");

            Path path = Paths.get("src/main/resources/file/token.txt");
            if (!Files.exists(path)) {
                log.error("Không tìm thấy file token.txt");
                return null;
            }
//            if (is == null) {
//                log.error("không tìm thấy file này");
//                return null;
//            }

            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            String firstLine = lines.isEmpty() ? null : lines.get(0);
            log.info("First line: " + firstLine);
            return firstLine;

//            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
//            String firstLine = reader.readLine();
//
//            log.error("first Line: " + firstLine);
//            return firstLine;
        } catch (Exception e) {

        }

        return null;
    }

    public static void WRITE_TOKEN_LOCAL(String tokenContent) {
        try {
            Path path = Paths.get("src/main/resources/file/token.txt");
//            Files.createDirectories(path.toAbsolutePath());
            Files.createDirectories(path.getParent());
            log.error("token >>>>> = " + path);

            Files.write(path, tokenContent.getBytes(StandardCharsets.UTF_8));

            log.info("Ghi token thành công vào token.txt");
        } catch (IOException e) {
            log.error("Lỗi khi ghi vào file token.txt", e);
        }
    }

    public static String GET_BEAR_LOCAL() {
        String res = "Bearer " + Utilfunctions.GET_TOKEN_LOCAL();
        return res;
    }

    public static String uploadImage(MultipartFile hinhAnh) {
        if (hinhAnh == null || hinhAnh.isEmpty()) {
            log.warn("Không có file ảnh để upload");
            return null;
        }

        try {
            // Tạo file resource
            ByteArrayResource fileAsResource = new ByteArrayResource(hinhAnh.getBytes()) {
                @Override
                public String getFilename() {
                    return hinhAnh.getOriginalFilename();
                }
            };

            // Tạo multipart body
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("image", fileAsResource);

            // Header multipart
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            // Gửi request
            String imageUrl = restClient.post()
                    .uri(UtilVariable.URL_UPDATE_IMAGE)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + Utilfunctions.GET_TOKEN_LOCAL())
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(body)
                    .retrieve()
                    .body(String.class);

            log.info("Upload ảnh thành công: {}", imageUrl);
            return imageUrl;

        } catch (Exception ex) {
            log.error("Lỗi khi upload ảnh: {}", ex.getMessage(), ex);
            return null;
        }
    }

    public static void main(String[] args) {
        String token = Utilfunctions.GET_TOKEN_LOCAL();
        System.out.println("token = " + token);
    }

}
