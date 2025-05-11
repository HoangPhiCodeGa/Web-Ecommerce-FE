var CURRENT_PAGE = 1;
const PAGE_SIZE = 5;
var TOTALPAGES = 1;
var KEYWORD = '';

const TOKEN_LOCAL = localStorage.getItem("TOKEN");
const CONFIG_LOCAL = {
    headers: {
        'Authorization': `Bearer ${TOKEN_LOCAL}`
    }
};

loadCategory();

function loadCategory() {
    var token = localStorage.getItem("TOKEN");

    const config = {
        headers: {
            'Authorization': `Bearer ${token}`
        }
    };

    const urlParams = new URLSearchParams(window.location.search);
    CURRENT_PAGE = urlParams.get('page') ? parseInt(urlParams.get('page')) : 1;
    KEYWORD = urlParams.get('keyword') || '';

    axios.get('http://localhost:8080/api/v1/categories', config)
        .then(response => {
            console.log(response.data);

            var list_data = response.data.data;
            list_data.sort((a, b) => a.id - b.id);
            var status_code = response.data.status;

            console.log("list_data = ", list_data);
            console.log("status code = ", status_code);

            dataPagnition = pagnitionCustom(list_data);

            renderData(dataPagnition);
            loadPagnition();
        })
        .catch(error => {
            console.error('Lỗi khi gọi API:', error);
        });
}

function pagnitionCustom(listItem) {
    try {
        const filteredList = listItem.filter(category => {
            return category.tenLoai.toLowerCase().includes(KEYWORD.toLowerCase());
        });

        TOTALPAGES = Math.ceil(filteredList.length / PAGE_SIZE);

        const startIndex = (CURRENT_PAGE - 1) * PAGE_SIZE;
        const endIndex = startIndex + PAGE_SIZE;
        const paginatedList = filteredList.slice(startIndex, endIndex);


        return paginatedList;

    } catch (e) {
        return [];
    }
}

function renderData(list_data) {
    var tbody = document.querySelector("#category-table-body");

    tbody.innerHTML = "";

    list_data.forEach(category => {
        // Tạo một phần tử tr (table row) mới
        var row = document.createElement("tr");

        // Tạo các ô td cho id và tên loại sản phẩm
        var tdId = document.createElement("td");
        tdId.textContent = category.id;
        var tdName = document.createElement("td");
        tdName.textContent = category.tenLoai;

        // Thêm các ô vào dòng tr
        row.appendChild(tdId);
        row.appendChild(tdName);

        // // Tạo các ô action (sửa và xóa)
        var tdEdit = document.createElement("td");
        var editLink = document.createElement("a");
        editLink.href = `/admin/category/edit-category/${category.id}`;

        var editIcon = document.createElement("i");
        editIcon.classList.add("fas", "fa-edit");
        editIcon.style.color = "#1e90ff";
        editIcon.style.cursor = "pointer";
        editLink.appendChild(editIcon);
        tdEdit.appendChild(editLink);

        //----button delete 
        var tdDelete = document.createElement("td");
        var deleteLink = document.createElement("a");
        deleteLink.style.cursor = "pointer";

        deleteLink.onclick = function () {
            Swal.fire({
                title: 'Bạn có chắc chắn?',
                text: "Hành động này sẽ xóa loại sản phẩm khỏi hệ thống!",
                icon: 'warning',
                showCancelButton: true,
                confirmButtonColor: '#d33',
                cancelButtonColor: '#3085d6',
                confirmButtonText: 'Xóa',
                cancelButtonText: 'Hủy'
            }).then((result) => {
                if (result.isConfirmed) {
                    deleteItem(category.id);
                    Swal.fire(
                        'Đã xóa!',
                        'Loại sản phẩm đã được xóa.',
                        'success'
                    );
                }
            });
        };

        var deleteIcon = document.createElement("i");
        deleteIcon.classList.add("fas", "fa-trash-alt");
        deleteIcon.style.color = "#ff0000";
        deleteLink.appendChild(deleteIcon);
        tdDelete.appendChild(deleteLink);


        // // Thêm các ô action vào dòng
        row.appendChild(tdEdit);
        row.appendChild(tdDelete);

        // // Thêm dòng vào tbody
        tbody.appendChild(row);
    });
}

function deleteItem(idCate) {
    var token = localStorage.getItem("TOKEN");

    const config = {
        headers: {
            'Authorization': `Bearer ${token}`
        }
    };

    axios.delete(`http://localhost:8080/api/v1/categories/${idCate}`, config)
        .then(response => {
            Swal.fire({
                icon: 'success',
                title: 'Xóa thành công!',
                text: 'Loại sản phẩm đã được xóa khỏi hệ thống.',
                timer: 2000,
                showConfirmButton: false
            });

            // Reload lại danh sách hoặc trang sau khi xóa
            setTimeout(() => {
                this.loadCategory();
            }, 2000);
        })
        .catch(error => {
            console.error("Lỗi khi xóa:", error);
            Swal.fire({
                icon: 'error',
                title: 'Lỗi',
                text: 'Không thể xóa loại sản phẩm. Vui lòng thử lại.'
            });
        });
}

function addItem() {
    var token = localStorage.getItem("TOKEN");

    var categoryData = {
        tenLoai: document.getElementById("tenLoaiSP").value,
    };

    const config = {
        headers: {
            'Authorization': `Bearer ${token}`
        }
    };

    // console.log("data = ", categoryData);
    var addButton = document.getElementById("addButton");
    addButton.style.display = "none";

    axios.post('http://localhost:8080/api/v1/categories', categoryData, config)
        .then(response => {
            // Thông báo thành công
            Swal.fire({
                icon: 'success',
                title: 'Thêm mới thành công!',
                text: 'Loại sản phẩm đã được thêm vào hệ thống.',
                timer: 2000,
                showConfirmButton: false
            });

            setTimeout(() => {
                window.location.href = 'http://localhost:9090/admin/category';
            }, 1000);

        })
        .catch(error => {
            console.error("Lỗi khi thêm mới:", error);
            Swal.fire({
                icon: 'error',
                title: 'Lỗi',
                text: 'Không thể thêm loại sản phẩm. Vui lòng thử lại.'
            });

            addButton.style.display = "inline-block";
        });
}

function loadPagnition() {
    const paginationContainer = document.getElementById('pagination-container');
    var KEYWORDSEARCH = document.getElementById("keywordsearch").value;

    paginationContainer.innerHTML = '';

    // ul
    const paginationList = document.createElement('ul');
    paginationList.classList.add('pagination');

    //Previous
    const prevPage = document.createElement('li');

    if (CURRENT_PAGE > 1) {
        const hrefPrev = `/admin/category?page=${CURRENT_PAGE - 1}${KEYWORDSEARCH ? `&keyword=${KEYWORDSEARCH}` : ''}`;
        prevPage.innerHTML = `<a href="${hrefPrev}" class="page-link">Previous</a>`;
    } else {
        // disable
        prevPage.classList.add('disabled');
        prevPage.innerHTML = `<a href="#" class="page-link">Previous</a>`;
    }


    paginationList.appendChild(prevPage);

    // Tạo các nút số trang
    for (let i = 1; i <= TOTALPAGES; i++) {
        const pageItem = document.createElement('li');
        pageItem.classList.add('paginate_button', 'page-item');

        // active
        if (i === CURRENT_PAGE) {
            pageItem.classList.add('active');
        }

        const href = `/admin/category?page=${i}${KEYWORDSEARCH ? `&keyword=${KEYWORDSEARCH}` : ''}`;

        pageItem.innerHTML = `<a href="${href}" class="page-link">${i}</a>`;
        pageItem.onclick = () => changePage(i);
        paginationList.appendChild(pageItem);
    }

    // Next
    const nextPage = document.createElement('li');
    nextPage.classList.add('paginate_button', 'page-item', CURRENT_PAGE < TOTALPAGES ? 'enabled' : 'disabled');

    if (CURRENT_PAGE < TOTALPAGES) {
        const hrefNext = `/admin/category?page=${CURRENT_PAGE + 1}${KEYWORDSEARCH ? `&keyword=${KEYWORDSEARCH}` : ''}`;
        nextPage.innerHTML = `<a href="${hrefNext}" class="page-link">Next</a>`;
    } else {
        // disable
        nextPage.classList.add('disabled');
        nextPage.innerHTML = `<a href="#" class="page-link">Next</a>`;
    }

    paginationList.appendChild(nextPage);

    // add child
    paginationContainer.appendChild(paginationList);
}

function changePage(pageNumber) {
    if (pageNumber < 1 || pageNumber > TOTALPAGES) return;
    CURRENT_PAGE = pageNumber;
    loadPagnition();
}

function editItem() {
    var id = document.getElementById("idSP").value;

    var data = {
        id: document.getElementById("idSP").value,
        tenLoai: document.getElementById("tenLoaiSP").value
    };

    console.log("data = ", data);

    var urlDetails = `http://localhost:8080/api/v1/categories/${id}`

    var dataEdit = axios.put(urlDetails, data, CONFIG_LOCAL)
        .then(response => {

            var status_code = response.status;

            if (status_code == 200) {
                USE_TOAST_UTIL('success', 'Cập nhật sản phẩm thành công');

                REDERECT_URL_UTIL(1000, 'http://localhost:9090/admin/category');
            }
        })
        .catch(error => {

        })

}

function getItemEdit() {
    if (!window.location.pathname.includes('edit-category')) {
        return;
    }

    const pathParts = window.location.pathname.split('/');
    const categoryId = pathParts[pathParts.length - 1];

    var urlDetails = `http://localhost:8080/api/v1/categories/${categoryId}`

    var data = axios.get(urlDetails, CONFIG_LOCAL)
        .then(response => {
            console.log(response.data.data);

            var itemEdit = response.data.data;
            document.getElementById("idSP").value = itemEdit.id;
            document.getElementById("tenLoaiSP").value = itemEdit.tenLoai;

        })
        .catch(error => {
            console.error('Lỗi khi gọi API details category:', error);
        });
}

getItemEdit();
