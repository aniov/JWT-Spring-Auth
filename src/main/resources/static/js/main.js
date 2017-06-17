/**
 * Created by Marius on 6/15/2017.
 */

var TOKEN_KEY = "jwtToken";

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
            // $notLoggedIn.hide();
            // showTokenInformation()
            showUserInformation();
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

function showUserInformation() {
    $.ajax({
        url: "/user",
        type: "GET",
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        headers: getAuthTokenFromLocalStorage(),
        success: function (data, textStatus, jqXHR) {
            var $userInfoBody = $("#userInfoBody");

            $userInfoBody.append($("<div>").text("Username: " + data.username));

            var $authorityList = $("<ul>");
            data.authorities.forEach(function (authorityItem) {
                $authorityList.append($("<li>").text(authorityItem.authority));
            });
            var $authorities = $("<div>").text("Authorities:");
            $authorities.append($authorityList);

            $userInfoBody.append($authorities);
            $("#userLoggedInfo").show();
        }
    });
}

function getAuthTokenFromLocalStorage() {
    var token = localStorage.getItem(TOKEN_KEY);
    if (token) {
        return {"Authorization": token};
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
                .html("<a> Status code: </a>" + jqXHR.status
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
                .html("<a> Status code: </a>" + jqXHR.status
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
                .html("<a> Status code: </a>" + jqXHR.status);
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
                .html("<a> Status code: </a>" + jqXHR.status
                    + "<p><h2>All users: </h2></p>"
                    + "<br/>" + users
                );
        },
        error: function (jqXHR, textStatus, errorThrown) {
            $("#requestModal")
                .modal("show")
                .find(".modal-body")
                .html("<a> Status code: </a>" + jqXHR.status);
        }
    });

}

function clearLoginForm() {
    document.getElementById("inputEmail").value = '';
    document.getElementById("inputPassword").value = '';
}


