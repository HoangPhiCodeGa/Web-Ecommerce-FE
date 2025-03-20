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
 * @created: 20-March-2025 6:59 PM
 */

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cart {
    private Long cartId;
    private double totalPrice;
    private List<CartItem> cartItems;
}
