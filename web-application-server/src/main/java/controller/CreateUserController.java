package controller;

import db.DataBase;
import http.HttpRequest;
import http.HttpResponse;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateUserController extends AbstractController{

    private static final Logger log = LoggerFactory.getLogger(CreateUserController.class);

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        User user = new User(request.getParam("userId"),
                request.getParam("password"),
                request.getParam("name"),
                request.getParam("email"));
        log.debug("User : {}", user);
        DataBase.addUser(user);
        response.sendRedirect("/");
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {

    }
}
