function authenAdmin(){
    var TOKEN_LOCAL = localStorage.getItem("TOKEN");
    var ROLE_LOCAL = localStorage.getItem("ROLES");

    if(!TOKEN_LOCAL || TOKEN_LOCAL == null){
        window.location.href = "/account/login";
        return;
    }

    const decodedToken = jwt_decode(TOKEN_LOCAL);
    
    var expTime = decodedToken.exp;
    var currentTime = Math.floor(Date.now() / 1000);


    if (expTime < currentTime) {
        window.location.href = "/account/login";
        return;
    }
}

authenAdmin();
