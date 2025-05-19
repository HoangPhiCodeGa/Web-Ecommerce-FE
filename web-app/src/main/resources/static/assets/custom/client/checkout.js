console.log("checkout .js");


const TOKEN_LOCAL = localStorage.getItem("TOKEN");
const CONFIG_LOCAL = {
    headers: {
        'Authorization': `Bearer ${TOKEN_LOCAL}`
    }
};

const ID_LOCAL = localStorage.getItem("ID");

var CART_ID = null;
var CART_ITEMS = null;

async function getCountNow() {
    console.log("get count now");

    const GET_CART_BY_USER_ID = `http://localhost:8080/api/carts/${ID_LOCAL}`;
    const CREATE_CART_BY_CUSTOMER_ID = `http://localhost:8080/api/carts`;

    //----tạo giỏ hàng
    await axios.post(CREATE_CART_BY_CUSTOMER_ID, { customer_id: ID_LOCAL }, CONFIG_LOCAL)
        .then(response => {
            console.log(response.data.data);
            console.log("Tạo giỏ hàng thành công");
        })
        .catch(error => {
            console.error('Lỗi khi gọi tạo giỏ hàng:', error);
        });

    //---lấy ra giỏ hàng hiện tại 
    await axios.get(GET_CART_BY_USER_ID, CONFIG_LOCAL)
        .then(response => {
            console.log("data giỏ hàng ==== ", response.data);
            var TOTAL_PRICE = response.data.totalPrice;

            CART_ID = response.data.id;
            console.log("CART_ID === ", CART_ID);
            var countCart = response.data.count;

            document.getElementById('cart-count').innerText = countCart;
            document.getElementById('total-price').innerText = TOTAL_PRICE;
            document.getElementById('total-with-shipping').innerText = TOTAL_PRICE;

        })
        .catch(error => {
            console.error('Lỗi khi lấy giỏ hàng:', error);
        });

}

async function getCartDetails() {
    var URL_CART_DETAILS = `http://localhost:8080/api/cart-items/${CART_ID}`;
    await axios.get(URL_CART_DETAILS, CONFIG_LOCAL)
        .then(response => {
            console.log("data chi tiết giỏ hàng ==== ", response.data);
            CART_ITEMS = response.data;

        })
        .catch(error => {
            console.error('Lỗi khi lấy chi tiết giỏ hàng:', error);
        });
}


async function checkoutCustom() {
    console.log("size = ", CART_ITEMS.length);
    if(CART_ITEMS.length === 0 || CART_ITEMS === null){
        alert("Chưa có sản phẩm nào trong giỏ hàng");
        return;
    }
    
    console.log("Gia tri gio hang = ", CART_ITEMS);
    

    console.log("checkout custom nhé");
    const METHOD_PAYMENT = document.querySelector("select[name='methodPayment']").value;
    const TOTAL_PRICE = document.getElementById('total-price').innerText;

    var URL_CREATE_ORDER = `http://localhost:9292/api/v1/orders/create`;

    var totalPrice = parseFloat(document.getElementById('total-price').innerText); // cần chuyển từ string sang số
    var ngayDatHang = new Date().toISOString().split('T')[0]; // format thành "yyyy-MM-dd"
    var status = "Chưa xử lý";
    var customerId = ID_LOCAL;

    // var data = {
    //     ngayDatHang: ngayDatHang,
    //     tongTien: totalPrice,
    //     status: status,
    //     customerId: customerId
    // };

    var data = {
    ngayDatHang: ngayDatHang,       
    tongTien: totalPrice,           
    status: status,                 
    customerId: customerId,         
    orderDetailDTOS: CART_ITEMS.map(item => ({
        orderId: -1,
        productId: item.productId,  
        soLuong: item.quantity,     
        giaBan: item.price,        
        giaGoc: item.price          
    }))
}

    var ID_ORDER = -1;
    const URL_DELETE_CART = `http://localhost:8080/api/cart-items/delete/`;

    try {
        const response = await axios.post(URL_CREATE_ORDER, data, CONFIG_LOCAL);
        console.log("data order ==== ", response.data);
        console.log("Đặt hàng thành công");
        ID_ORDER = response.data.id;

        CART_ITEMS.map(item => {
            axios.delete(URL_DELETE_CART + item.id, CONFIG_LOCAL)
                .then(response => {
                    console.log("Xóa sản phẩm trong giỏ hàng thành công");
                })
                .catch(error => {
                    console.error('Lỗi khi xóa sản phẩm trong giỏ hàng:', error);
                });
        })

        initCheckout();
        
    } catch (error) {
        console.error('Lỗi khi tạo đơn hàng:', error);
    }

    if (METHOD_PAYMENT === "VNPAY" && ID_ORDER !== -1) {
        var URL_VNPAY = `http://localhost:9997/api/payment/vn-pay/create-payment?amount=${TOTAL_PRICE}&idOrder=${ID_ORDER}`;
        let URL = axios.post(URL_VNPAY, null, CONFIG_LOCAL)
            .then(response => {
                console.log("data vnpay ==== ", response.data);
                window.location.href = response.data;
            })
            .catch(error => {
                console.error('Lỗi khi lấy url vnpay:', error);
            });
    }else{
        alert("Thanh toán thành công");
        window.location.href = "/index";
    }
}

function checkStatusCheckout() {
    const params = new URLSearchParams(window.location.search);
    const transactionStatus = params.get("vnp_TransactionStatus");
    const idorder = params.get("vnp_OrderInfo");
    
    const URL_UPDATE_ORDER = `http://localhost:9292/api/v1/orders/updateStatus/${idorder}?status=Đã thanh toán`;

    if (transactionStatus === "00") {
        axios.put(URL_UPDATE_ORDER, null, CONFIG_LOCAL)
            .then(response => {
                console.log("Cập nhật trạng thái đơn hàng thành công");
            })
            .catch(error => {
                console.error('Lỗi khi cập nhật trạng thái đơn hàng:', error);
            });
        console.log("Giao dịch thành công");
    } else {
        console.log("Giao dịch thất bại");
    }
}

async function initCheckout() {
    await getCountNow();
    await getCartDetails();
}

initCheckout();
checkStatusCheckout();