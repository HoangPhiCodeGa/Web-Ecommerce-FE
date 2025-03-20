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
 * @created: 20-March-2025 7:09 PM
 */

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Promotion {
    private String code;    // Mã khuyến mãi
    private String type;    // Loại khuyến mãi (giảm giá, quà tặng, v.v.)
    private double value;   // Giá trị khuyến mãi (giảm giá % hoặc số tiền)
    private Date startDate;
    private Date endDate;
    private boolean isActive;  // Trạng thái hoạt động của khuyến mãi
    private int usageLimit;    // Giới hạn số lần sử dụng khuyến mãi
    private int usageCount;    // Số lần đã sử dụng khuyến mãi
    private double minSpendAmount;  // Mức chi tiêu tối thiểu để áp dụng khuyến mãi
}
