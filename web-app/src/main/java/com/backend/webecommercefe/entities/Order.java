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
 * @created: 20-March-2025 7:01 PM
 */

import com.backend.webecommercefe.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    private Long id;
    private LocalDate ngayDatHang;
    private Double tongTien;
//    @Convert(converter = OrderStatusConverter.class)
    private OrderStatus status;
    private Long userId;
    private List<OrderDetail> orderDetails;

}
