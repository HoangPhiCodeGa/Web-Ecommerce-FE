package com.backend.webecommercefe.untils;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
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
import java.util.UUID;

@Slf4j
public class Utilfunctions {
    public static String GET_TOKEN_LOCAL(){
        try {
            InputStream is = Utilfunctions.class.getClassLoader().getResourceAsStream("file/token.txt");
            if (is == null) {
                log.error("không tìm thấy file này");
                return null;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String firstLine = reader.readLine();

            log.error("first Line: " + firstLine);
            return firstLine;
        }catch (Exception e){

        }

        return  null;
    }

    public static void WRITE_TOKEN_LOCAL(String tokenContent) {
        try {
            Path path = Paths.get("src/main/resources/file/token.txt");

            Files.write(path, tokenContent.getBytes(StandardCharsets.UTF_8));

            log.info("Ghi token thành công vào token.txt");
        } catch (IOException e) {
            log.error("Lỗi khi ghi vào file token.txt", e);
        }
    }

    public static String GET_BEAR_LOCAL(){
        String res = "Bearer " + Utilfunctions.GET_TOKEN_LOCAL();
        return res;
    }

    private static final String RESOURCE_DIR  = System.getProperty("user.dir") + "/src/main/resources/static/assets/images/products";
    private static final String TARGET_DIR = System.getProperty("user.dir") + "/target/classes/static/uploads/assets/images/products";

    public static String saveFileToStaticFolder(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File empty");
        }

        // Tạo tên file duy nhất
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";

        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String newFileName = UUID.randomUUID() + fileExtension;

        // Ghi vào cả RESOURCE và TARGET
        saveTo(file, RESOURCE_DIR, newFileName);
        saveTo(file, TARGET_DIR, newFileName);

        return "assets\\images\\products\\" + newFileName;
    }

    private static void saveTo(MultipartFile file, String dirPath, String fileName) throws IOException {
        Path path = Paths.get(dirPath);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
        Path filePath = path.resolve(fileName);
        Files.write(filePath, file.getBytes());
    }
}
