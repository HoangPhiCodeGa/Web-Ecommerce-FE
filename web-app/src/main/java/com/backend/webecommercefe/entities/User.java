///*
// * @(#) $(NAME).java    1.0     3/20/2025
// *
// * Copyright (c) 2025 IUH. All rights reserved.
// */
//
//package com.backend.webecommercefe.entities;
//
///*
// * @description
// * @author: Tran Tan Dat
// * @version: 1.0
// * @created: 20-March-2025 6:25 PM
// */
//
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//public class User {
//    private Long userId;
//    private String fullName;
//    private String phoneNumber;
//    private String dateOfBirth;
//    private String gender;
//    private Account account;
//}
package com.backend.webecommercefe.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private Long userId;
    private String fullName;
    private String phoneNumber;
    private LocalDate dateOfBirth; // Đổi từ String sang LocalDate
    private Boolean gender; // Đổi từ String sang Boolean
    private String address; // Thêm thuộc tính địa chỉ
    private String email; // Thêm thuộc tính email
    // Bỏ thuộc tính account nếu không cần thiết
}