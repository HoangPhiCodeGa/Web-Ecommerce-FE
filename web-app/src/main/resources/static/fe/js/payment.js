$(document).ready(function() {

    $("#provinceSelect").change(function() {
        const provinceID = $(this).val();

        if (provinceID !== "0") {
            getFeeShip(provinceID);
            loadAmount();
        } else {
            $("#cost").text("Chưa chọn địa chỉ.");
        }
    });

    // Gán sự kiện click cho nút giảm
    $('.btn-decrement').click(function() {
        updateQuantity($(this).closest('.cart-product-quantity').find('input[name="quantity"]'), "decrement", $(this).closest('tr'));
    });

    // Gán sự kiện click cho nút tăng
    $('.btn-increment').click(function() {
        updateQuantity($(this).closest('.cart-product-quantity').find('input[name="quantity"]'), "increment", $(this).closest('tr'));
    });

    $("form[id='checkoutValidate']").validate({
        rules: {
            name: {
                minlength: 8,
                required: true,
            },
            email: {
                required: true,
                email: true
            },
            phone: {
                required: true,
                number: true,
                minlength: 8,
                maxlength: 15,
            },
            address: {
                required: true,
            },
            province_id: {
                min: 1,
            },
            district_id: {
                min: 1,
            },
            ward_id: {
                min: 1,
            },
            methodPayment: {
                required: true
            }
        },
        messages: {
            name: {
                required: "Vui lòng nhập Họ Tên",
                minlength: "Họ Tên phải nhiều hơn 8 ký tự"
            },
            email: {
                required: "Vui lòng nhập địa chỉ Email",
                email: "Địa chỉ Email không hợp lệ (vd:abc@gmail.com)",
            },
            phone: {
                required: "Vui lòng nhập Số điện thoại",
                number: "Số điện thoại không hợp lệ (độ dài từ 8 - 15 ký tự, không chứa ký tự đặc biệt và khoảng trắng)",
                minlength: "Số điện thoại không hợp lệ (độ dài từ 8 - 15 ký tự, không chứa ký tự đặc biệt và khoảng trắng)",
                maxlength: "Số điện thoại không hợp lệ (độ dài từ 8 - 15 ký tự, không chứa ký tự đặc biệt và khoảng trắng)",
            },
            address: "Vui lòng nhập Địa chỉ",
            province_id: {
                min: "Vui lòng chọn Thành Phố",
            },
            district_id: {
                min: "Vui lòng chọn Quận/Huyện",
            },
            ward_id: {
                min: "Vui lòng chọn Phường/Xã",
            },
            methodPayment: {
                required: "Vui lòng chọn Hình thức thanh toán"
            }
        },
        submitHandler: function(form) {
            form.submit();
        }
    });

    $(".btn-order").on('click', function(event) {
        // Ngăn chặn hành động gửi mặc định
        event.preventDefault();

        // Kiểm tra số lượng sản phẩm trong giỏ hàng
        var productCount = $("#cartTable tr").length; // Đếm số lượng hàng trong giỏ
        var paymentMethod = $('select[name="methodPayment"]').val(); // Lấy giá trị hình thức thanh toán

        // Kiểm tra điều kiện
        if (productCount === 0) {
            Swal.fire({
                icon: 'warning',
                title: 'Thông báo',
                text: 'Chưa có sản phẩm nào trong giỏ hàng!',
                showCancelButton: true, // Hiển thị nút hủy
                confirmButtonColor: '#3085d6', // Màu nút xác nhận
                cancelButtonColor: '#d33', // Màu nút hủy
                confirmButtonText: 'Đi chọn sản phẩm',
                cancelButtonText: 'Hủy'
            }).then((result) => {
                // Xử lý kết quả từ hộp thoại
                if (result.isConfirmed) {
                    window.location.href = "/";
                    return;
                } else {
                    return;
                }
            });
        } else if (paymentMethod === "0") {
            Swal.fire({
                icon: 'warning',
                title: 'Thông báo',
                text: 'Vui lòng cho hình thức thanh toán hợp lệ!',
                confirmButtonText: 'OK'
            });
        } else {
            Swal.showLoading();
            $(this).closest("form").submit(); // Gọi submit để gửi biểu mẫu
        }
    });

    $('#couponForm').click(function(event) {
        event.preventDefault(); // Ngăn chặn gửi form mặc định

        var couponCode = $("input[name='coupon']").val();

        $.ajax({
            url: '/checkout/check-coupon',
            type: 'POST',
            data: { coupon: couponCode }, // Gửi dữ liệu từ form
            success: function(response) {
                if(!response.valid) {
                    $("input[name='coupon']").val('');
                    Swal.fire({
                        icon: 'warning',
                        title: 'Thông báo',
                        text: 'Không tồn tại mã giảm giá này!',
                        confirmButtonText: 'OK'
                    });
                }else {
                    Swal.fire({
                        title: 'Đang xử lý...',
                        onBeforeOpen: () => {
                            Swal.showLoading();
                        }
                    });
                    location.reload();
                }
            },
            error: function(xhr) {
                $("input[name='coupon']").val('');
                Swal.fire({
                    icon: 'warning',
                    title: 'Thông báo',
                    text: 'Không tồn tại mã giảm giá này!',
                    confirmButtonText: 'OK'
                });
            }
        });
    });

});

function updateQuantity(input, change, row) {
    // Lấy idCart từ thuộc tính data-id của input
    var idCart = input.data('id');
    var maxQuantity = $('input[name="quantity"]').attr('max');

    var currentValue = parseInt(input.val()) || 0;

    if (currentValue >= maxQuantity) {
        Swal.fire({
            icon: 'warning',
            title: 'Cảnh báo',
            text: 'Số lượng sản phẩm trong kho hàng không đủ!',
            confirmButtonText: 'OK'
        });
        return;
    }

    // Đối với hành động giảm số lượng
    if (currentValue == 1 && change == "decrement") {
        Swal.fire({
            title: 'Xác nhận',
            text: "Bạn có chắc xóa sản phẩm này khỏi giỏ hàng?",
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#3085d6',
            cancelButtonColor: '#d33', // Màu nút hủy
            confirmButtonText: 'Có, xác nhận!',
            cancelButtonText: 'Hủy xóa'
        }).then((result) => {
            if (result.isConfirmed) {
                // Nếu người dùng xác nhận, xóa row và gửi yêu cầu AJAX
                row.remove();
                $.ajax({
                    url: '/cart/update/' + idCart, // Đường dẫn đến phương thức trong controller
                    type: 'POST', // Sử dụng phương thức POST
                    data: {
                        action: change // Gửi hành động (increment hoặc decrement)
                    },
                    success: function(data) {
                        console.log("Cập nhật thành công");
                        getCartTotal();
                        getFeeShip($("#provinceSelect").val());
                        loadAmount();
                    },
                    error: function(error) {
                        console.error('Lỗi:', error);
                    }
                });
            }
            // Không cần xử lý gì thêm nếu người dùng chọn "Hủy xóa"
        });
        return; // Dừng lại ở đây, không thực hiện tiếp
    }

    // Cập nhật giá trị tổng nếu không xóa sản phẩm
    var priceText = row.find('.price-col').attr('value');
    var price = parseFloat(priceText);
    var totalNew = price * currentValue;

    row.find('#total-col').attr('value', totalNew.toFixed(0));
    row.find('#total-col').text(totalNew.toLocaleString('en-US', { minimumFractionDigits: 0, maximumFractionDigits: 0 }));

    // Gửi yêu cầu AJAX cho trường hợp khác (tăng số lượng hoặc cập nhật số lượng)
    $.ajax({
        url: '/cart/update/' + idCart, // Đường dẫn đến phương thức trong controller
        type: 'POST', // Sử dụng phương thức POST
        data: {
            action: change // Gửi hành động (increment hoặc decrement)
        },
        success: function(data) {
            console.log("Cập nhật thành công");
            getCartTotal();
            getFeeShip($("#provinceSelect").val());
            loadAmount();
        },
        error: function(error) {
            console.error('Lỗi:', error);
        }
    });
}

function getCartTotal() {
    fetch("/checkout/get/cart")
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            const totalAmount = data.totalAmount;
            $("#total").text(totalAmount.toLocaleString('en-US', { minimumFractionDigits: 0, maximumFractionDigits: 0 }) + "đ").data("value", totalAmount);
            loadAmount();
        })
        .catch(error => {
            console.error("Có lỗi xảy ra:", error);
        });
}

function getFeeShip(provinceID) {
    fetch(`/checkout/get/fee-ship/${provinceID}`)
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            const fee = data.fee;
            $("#cost").text(fee.toLocaleString('en-US', { minimumFractionDigits: 0, maximumFractionDigits: 0 }) + "đ");
            $("#cost").data('value', fee);
            $("#hiddenCost").val(fee);
            loadAmount();
        })
        .catch(error => {
            console.error("Có lỗi xảy ra:", error);
        });
}

function loadAmount() {

    // Lấy giá trị từ các thẻ <td>
    var totalValue = parseFloat($('#total').data('value')) || 0; // Lấy giá trị từ thẻ total

    var couponValuePhanTram = $('.coupon').data('value') || 0;
    var tinhCoupon = totalValue*couponValuePhanTram;

    $("#total-coupon").text(tinhCoupon.toLocaleString("en-US", { minimumFractionDigits: 0, maximumFractionDigits: 0 }) + "đ").attr("data-value", tinhCoupon);

    var couponValue = parseFloat($('#total-coupon').data('value')) || 0; // Lấy giá trị từ thẻ total-coupon
    var costValue = parseFloat($('#cost').data('value')) || 0; // Lấy giá trị từ thẻ cost, mặc định là 0 nếu không có

    var result = totalValue - couponValue + costValue;
    var temp_result = totalValue - couponValue;

    $("#temp-total-checkout").text(temp_result.toLocaleString('en-US', { minimumFractionDigits: 0, maximumFractionDigits: 0 }) + "đ").attr("data-value", temp_result);
    $("#temp-total-checkout").data('value',temp_result);
    $("#total-checkout").text(result.toLocaleString('en-US', { minimumFractionDigits: 0, maximumFractionDigits: 0 }) + "đ").attr("data-value", result);
    $("#total-checkout").data('value',result);

}