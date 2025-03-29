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
 * @created: 20-March-2025 6:54 PM
 */

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    private Long id;
    private String tenSanPham;
    private String moTa;
    private String hinhAnh;
    private Double giaBan;
    private Double giaNhap;
    private Double giaGoc;
    private int soLuong;
    private String mauSac;
    private String kichCo;
    private Category category;
}
