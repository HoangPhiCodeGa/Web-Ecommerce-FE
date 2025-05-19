const TOKEN_LOCAL = localStorage.getItem("TOKEN");
const CONFIG_LOCAL = {
    headers: {
        'Authorization': `Bearer ${TOKEN_LOCAL}`
    }
};

const ID_LOCAL = localStorage.getItem("ID");

var CART_ID = null;

async function getCountCart() {
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
            // console.log("giỏ hàng ==== ", response.data.cartItems.length);
            var countCart = response.data.count;
            CART_ID = response.data.id;
            console.log("CART_ID === ", CART_ID);


            document.getElementById('cart-count').innerText = countCart;
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
            renderCartItems(response.data);

            renderCartItems(response.data);
        })
        .catch(error => {
            console.error('Lỗi khi lấy chi tiết giỏ hàng:', error);
        });
}

function renderCartItems(cartItems) {
    const container = document.getElementById('cart-items-container');
    container.innerHTML = ''; // clear cũ nếu có

    let totalPrice = 0;

    cartItems.forEach(item => {
        const itemTotal = item.price * item.quantity;
        totalPrice += itemTotal;

        const cartItemHTML = `
      <div class="card mb-3 shadow-sm">
        <div class="row g-0 align-items-center">
          <div class="col-md-4">
            <img src="${item.image || 'https://res.cloudinary.com/khnguo/image/upload/v1747043211/upload/file_ozvd1q.png'}" class="img-fluid rounded-start" alt="${item.name}">
          </div>
          <div class="col-md-8">
            <div class="card-body">
              <h5 class="card-title">${item.name}</h5>
              <p class="card-text">Giá: ${item.price.toLocaleString()} VND</p>
              <p class="card-text">Số lượng: ${item.quantity}</p>
              <p class="card-text fw-bold">Thành tiền: ${itemTotal.toLocaleString()} VND</p>
              
              <div class="mb-3">
                <label for="colorSelect" class="form-label">Chọn size:</label>
                <select class="form-select" id="colorSelect" name="color">
                    <option value="do">X</option>
                    <option value="vang">L</option>
                    <option value="xanh">M</option>
                    <option value="xanh">XL</option>
                </select>
            </div>

              <div class="d-flex align-items-center">
                <button class="btn btn-sm btn-outline-secondary me-2" onclick="updateQuantity(${item.id}, -1)">-</button>
                <span class="mx-2" id="quantity-${item.id}">${item.quantity}</span>
                <button class="btn btn-sm btn-outline-secondary ms-2" onclick="updateQuantity(${item.id}, 1)">+</button>
              </div>
            </div>
          </div>
        </div>
      </div>
    `;

        container.innerHTML += cartItemHTML;
    });

    // Cập nhật tổng tiền
    document.getElementById('total-price').innerText = `${totalPrice.toLocaleString()} VND`;

    const shippingFee = 30000; // hoặc tính theo logic riêng
    document.getElementById('shipping-fee').innerText = `${shippingFee.toLocaleString()} VND`;

    const totalWithShipping = totalPrice + shippingFee;
    document.getElementById('total-with-shipping').innerText = `${totalWithShipping.toLocaleString()} VND`;
}


async function initCart() {
    await getCountCart();
    await getCartDetails();
}

async function updateQuantity(cariId, soLuong) {
    console.log("config = ", TOKEN_LOCAL);

    var URL_PLUS = `http://localhost:8080/api/cart-items/plus/${cariId}`;

    if (soLuong == -1) {
        URL_PLUS = `http://localhost:8080/api/cart-items/minus/${cariId}`;
    }
    axios.post(URL_PLUS, null, CONFIG_LOCAL)
        .then(response => {
            console.log("Cập nhật số lượng thành công");
            initCart();
        })
        .catch(error => {
            console.error('Lỗi khi cập nhật số lượng:', error);
        });

}

// Gọi function async
initCart();