package controller;

import db.DataBase;
import http.HttpRequest;
import http.HttpResponse;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;

import java.util.Collection;

public class ListUserController implements Controller {

    private static Logger log = LoggerFactory.getLogger(ListUserController.class);

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        if(HttpRequestUtils.isLogin(request.getHeader("Cookie"))){
            Collection<User> users = DataBase.findAll();
            StringBuilder sb = new StringBuilder();
            sb.append("<table border='1'>");
            for(User user : users){
                sb.append("<tr>");
                sb.append("<td>"+user.getUserId()+"</td>");
                sb.append("<td>"+user.getName()+"</td>");
                sb.append("<td>"+user.getEmail()+"</td>");
                sb.append("</tr>");
            }
            sb.append("</table>");
            response.forwardWithBody(sb.toString());
        }else{
            response.sendRedirect("/user/login.html");
        }
    }
}
