/*
 * @(#) $(NAME).java    1.0     3/20/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package com.backend.webecommercefe.enums;

/*
 * @description
 * @author: Tran Tan Dat
 * @version: 1.0
 * @created: 20-March-2025 7:04 PM
 */

public enum OrderStatus {
    CHO("Đang chờ"),
    DANG_XU_LY("Đang xử lý"),
    DA_GIAO("Đã giao"),
    DELIVERED("Đã nhận"),
    HUY("Đã hủy");

    private final String vietnameseLabel;

    OrderStatus(String vietnameseLabel) {
        this.vietnameseLabel = vietnameseLabel;
    }

    public String getVietnameseLabel() {
        return vietnameseLabel;
    }

    // Phương thức tìm kiếm từ nhãn tiếng Việt
    public static OrderStatus fromVietnameseLabel(String label) {
        for (OrderStatus status : values()) {
            if (status.vietnameseLabel.equalsIgnoreCase(label)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy trạng thái: " + label);
    }
}
