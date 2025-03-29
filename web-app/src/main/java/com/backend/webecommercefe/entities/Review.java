/*
 * @(#) $(NAME).java    1.0     3/20/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package com.backend.webecommercefe.entities;

/*
 * @description
 * @author: Tran Tan Dat
 * @version: 1.0
 * @created: 20-March-2025 7:06 PM
 */

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Review {
    private Long id;  // ID của đánh giá
    private Date ngayDanhGia;  // Ngày đánh giá
    private int soSao;  // Số sao đánh giá (thường từ 1 đến 5)
    private User user;  // Khách hàng thực hiện đánh giá, thay đổi kiểu từ `int` thành `Long`
    private Product product;  // Sản phẩm được đánh giá, thay đổi kiểu từ `int` thành `Long`
    private String noiDung;  // Nội dung đánh giá
}
