function USE_TOAST_UTIL(type, message, duration = 2000) {
    Swal.fire({
        icon: type, // 'success', 'error', 'warning', 'info', 'question'
        title: message,
        toast: true,
        position: 'top-end',
        showConfirmButton: false,
        timer: duration,
        timerProgressBar: true
    });
}


function REDERECT_URL_UTIL(time, url) {
    setTimeout(() => {
        window.location.href = url;

    }, time);
}