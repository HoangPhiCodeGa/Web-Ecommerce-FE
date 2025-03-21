$(document).ready(function() {
    // Lấy danh sách tỉnh https://cors-anywhere.herokuapp.com/
    var dataValue = $('select[name="province_id"]').data('value'); // Lấy data-value từ select

    $.get("https://provinces.open-api.vn/api/", function(data) {
        $.each(data, function(index, province) {
            // Thêm các tỉnh vào <select>
            $('select[name="province_id"]').append(
                $('<option></option>').val(province.code).text(province.name)
            );
        });

        // Kiểm tra data-value và tìm kiếm nếu có
        if (typeof dataValue !== 'undefined' && dataValue.trim() !== '') {
            $.get('https://provinces.open-api.vn/api/p/search/?q=' + dataValue, function(searchData) {
                if (searchData.length > 0) {
                    var firstCode = searchData[0].code;
                    $('select[name="province_id"]').val(firstCode).change(); // Chọn tỉnh đầu tiên
                    $('select[name="province_id"]').data('value', searchData[0].name);
                }
            });
        }

    });

    // Xử lý thay đổi tỉnh
    $('select[name="province_id"]').change(function() {
        var provinceId = $(this).val();
        $('select[name="district_id"]').empty().append('<option value="0">[Chọn Quận/Huyện]</option>');

        if (provinceId != 0) {
            $.get(`https://provinces.open-api.vn/api/p/${provinceId}?depth=2`, function(data) {
                $.each(data.districts, function(index, district) {
                    $('select[name="district_id"]').append(
                        $('<option></option>').val(district.code).text(district.name)
                    );
                });
            });
        }

        var dataValueDistrict = $('select[name="district_id"]').data('value');
        if (typeof dataValueDistrict !== 'undefined' && dataValueDistrict.trim() !== '') {

            var provinceId = $('select[name="province_id"]').val();
            $.get('https://provinces.open-api.vn/api/d/search/?q=' + dataValueDistrict + '&p=' + provinceId, function(searchData) {
                if (searchData.length > 0) {
                    var firstCode = searchData[0].code;
                    $('select[name="district_id"]').val(firstCode).change();
                    $('select[name="district_id"]').data('value', searchData[0].name);
                }
            });

        }

    });

    // Xử lý thay đổi quận/huyện
    $('select[name="district_id"]').change(function() {
        var districtId = $(this).val();
        $('select[name="ward_id"]').empty().append('<option value="0">[Chọn Phường/Xã]</option>');

        if (districtId != 0) {
            $.get(`https://provinces.open-api.vn/api/d/${districtId}?depth=2`, function(data) {
                $.each(data.wards, function(index, ward) {
                    $('select[name="ward_id"]').append(
                        $('<option></option>').val(ward.code).text(ward.name)
                    );
                });
            });
        }

        var dataValueWard = $('select[name="ward_id"]').data('value');
        if (typeof dataValueWard !== 'undefined' && dataValueWard.trim() !== '') {

            var provinceId = $('select[name="province_id"]').val();
            var districtId = $('select[name="district_id"]').val();
            $.get('https://provinces.open-api.vn/api/w/search/?q=' + dataValueWard + '&d=' + districtId + '&p=' + provinceId, function(searchData) {
                if (searchData.length > 0) {
                    var firstCode = searchData[0].code;
                    $('select[name="ward_id"]').val(firstCode).change();
                    $('select[name="ward_id"]').data('value', searchData[0].name);
                }
            });

        }
    });

    $('select[name="ward_id"]').change(function() {
        fillAddress();
    });
});

function fillAddress() {

    var dataValueWard = $('select[name="ward_id"] option:selected').val() === '0' ? '' : $('select[name="ward_id"] option:selected').text();
    var dataValueProvince = $('select[name="province_id"] option:selected').val() === '0' ? '' : $('select[name="province_id"] option:selected').text();
    var dataValueDistrict = $('select[name="district_id"] option:selected').val() === '0' ? '' : $('select[name="district_id"] option:selected').text();

    var address = [dataValueWard, dataValueDistrict, dataValueProvince].filter(Boolean).join(', ');

    $('input[name="address"]').val(address);

}