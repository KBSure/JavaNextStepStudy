package controller;

import db.DataBase;
import http.HttpRequest;
import http.HttpResponse;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;

public class LoginController extends AbstractController {

    private static Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        if(HttpRequestUtils.isUserMatch(request.getParam("userId"), request.getParam("password"))){
            response.addHeader("Set-Cookie", "logined=true");
            response.sendRedirect("/");
        }else{
            response.forwardWithPath("/user/login_failed.html", "txt/html");
        }
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {

    }


}
