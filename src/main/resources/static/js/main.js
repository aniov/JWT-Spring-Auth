/**
 * Created by Marius on 6/15/2017.
 */

var TOKEN_KEY = "jwtToken";

//On window load we check if we have a token, and assume that is valid so we are logged
document.addEventListener('DOMContentLoaded', function () {
    if (localStorage.getItem(TOKEN_KEY) != null) {
        $("#loginPanel").hide();
        displayToken();
        showTokenPayloadInformation();
    }
}, false);

function login() {

    event.preventDefault();
    var loginData = {
        username: document.getElementById("inputEmail").value,
        password: document.getElementById("inputPassword").value,
    };
    clearLoginForm();

    $.ajax({
        url: "/login",
        type: "POST",
        data: JSON.stringify(loginData),
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function (data, textStatus, jqXHR) {
            localStorage.setItem(TOKEN_KEY, data.token);
            $("#loginPanel").hide();
            displayToken();
            showTokenPayloadInformation();
        },
        error: function (jqXHR, textStatus, errorThrown) {
            if (jqXHR.status === 401 || jqXHR.status === 400) {
                $("#loginError")
                    .show().fadeTo(2000, 500).slideUp(500, function () {
                    $("#loginError").slideUp(500);
                });
            } else {
                throw new Error("an unexpected error occured: " + errorThrown);
            }
        }
    });
}

function displayToken() {
    $("#tokenContent").text(localStorage.getItem(TOKEN_KEY));
    $("#loggedIn").show();
}

function showTokenPayloadInformation() {

    var userInfoBody = $("#userInfoBody");
    var tokenPayload = parseJwt(localStorage.getItem(TOKEN_KEY));

    var $authorityList = $("<ul>");

    tokenPayload.role.forEach(function (authorityItem) {
        $authorityList.append($("<li>").text(authorityItem.authority));
    });

    var $authorities = $("<div>").text("Authorities:");
    $authorities.append($authorityList);

    userInfoBody.append($("<div>").text("Username: " + tokenPayload.sub));
    userInfoBody.append($authorities);
    $("#userLoggedInfo").show();
}

function parseJwt(token) {
    var base64Url = token.split('.')[1];
    var base64 = base64Url.replace('-', '+').replace('_', '/');
    return JSON.parse(window.atob(base64));
};

function getAuthTokenFromLocalStorage() {
    var token = localStorage.getItem(TOKEN_KEY);
    if (token) {
        return {"Authorization": token}; //this is the name after we will be looking for token in the back-end
    } else {
        return {};
    }
}

function logout() {
    localStorage.removeItem(TOKEN_KEY);

    $("#loggedIn").hide();
    $("#userLoggedInfo").hide();
    $("#userInfoBody").empty();
    $("#loginPanel").show();
}

function getTodayDate() {

    $.ajax({
        url: "/date",
        type: "GET",
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function (data, textStatus, jqXHR) {

            $("#requestModal")
                .modal("show")
                .find(".modal-body")
                .html("<a> Status code: </a>" + createResponseStatus(jqXHR)
                    + "<p><h2>Time is: </h2></p>" + new Date(data));
        }
    });
}

function getUserDetails() {

    $.ajax({
        url: "/user",
        type: "GET",
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        headers: getAuthTokenFromLocalStorage(),
        success: function (data, textStatus, jqXHR) {

            var authList = [];
            data.authorities.forEach(function (authItem) {
                authList.push(authItem.authority);
            });

            $("#requestModal")
                .modal("show")
                .find(".modal-body")
                .html("<a> Status code: </a>" + createResponseStatus(jqXHR)
                    + "<p><h2>Current User: </h2></p><a>username: </a> " + data.username
                    + "<br/><a>first name : </a> " + data.firstname
                    + "<br/><a>last name : </a> " + data.lastname
                    + "<br/><a>authorities : </a> " + authList
                );
        },
        error: function (jqXHR, textStatus, errorThrown) {
            $("#requestModal")
                .modal("show")
                .find(".modal-body")
                .html("<a> Status code: </a>" + createResponseStatus(jqXHR));

            if (jqXHR.status === 401) {
                logout();
            }
        }
    });
}

function getAllUsers() {

    $.ajax({
        url: "/users",
        type: "GET",
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        headers: getAuthTokenFromLocalStorage(),
        success: function (data, textStatus, jqXHR) {

            var users = [];
            data.forEach(function (user) {

                var authList = [];
                user.authorities.forEach(function (authItem) {
                    authList.push(authItem.authority);
                });

                users.push("<a>username: </a> " + user.username
                    + "<br/><a>first name : </a> " + user.firstname
                    + "<br/><a>last name : </a> " + user.lastname
                    + "<br/><a>authorities : </a>" + authList
                    + "<p>-------------------------------</p>")
            });

            $("#requestModal")
                .modal("show")
                .find(".modal-body")
                .html("<a> Status code: </a>" + createResponseStatus(jqXHR)
                    + "<p><h2>All users: </h2></p>"
                    + "<br/>" + users
                );
        },
        error: function (jqXHR, textStatus, errorThrown) {

            $("#requestModal")
                .modal("show")
                .find(".modal-body")
                .html("<a> Status code: </a>" + createResponseStatus(jqXHR));

            if (jqXHR.status === 401) {
                logout();
            }
        }
    });
}

function createResponseStatus(jqXHR) {

    var responseStatus = jqXHR.status;
    if (responseStatus === 401) {
        responseStatus += " UNAUTHORIZED. The request has not been applied because it lacks valid authentication credentials for the target resource."
    } else if (responseStatus === 403) {
        responseStatus += " FORBIDDEN. The server understood the request but refuses to authorize it. Insufficient credentials"
    } else if (responseStatus === 200) {
        responseStatus += " The request has succeeded."
    }
    return responseStatus;
}

function clearLoginForm() {
    document.getElementById("inputEmail").value = '';
    document.getElementById("inputPassword").value = '';
}


