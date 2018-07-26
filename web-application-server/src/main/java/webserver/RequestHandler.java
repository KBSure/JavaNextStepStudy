package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Map;

import db.DataBase;
import http.HttpRequest;
import http.HttpResponse;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;
    private HttpRequest request;
    private String path;
    private HttpResponse response;


    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            //index.html 요청하는 path

            request = new HttpRequest(in);
            path = request.getPath(); // 하위 패스가 없고 /를 요청했으면 index.html로 path를 변경해주는 메서드 생성하자.
            response = new HttpResponse(out);

            if (path.startsWith("/user/create")) {
                    User user = new User(request.getParam("userId"),
                            request.getParam("password"),
                            request.getParam("name"),
                            request.getParam("email"));
                    log.debug("User : {}", user);
                    DataBase.addUser(user);
                    response.sendRedirect("/index.html");
                    return;
            }else if("/user/login".equals(path)){
                    //로그인 비교 해서 로그인 성공실패 유무 처리
                    if(isUserMatch(request.getParam("userId"), request.getParam("password"))){
                        response.addHeader("Set-Cookie", "logined=true");
                        response.sendRedirect("/index.html");
                    }else{
                        response.forwardWithPath("/user/login_failed.html", "txt/html");
                    }
                    return;
            }else if(path.startsWith("/user/list")){
                    //로그인 되어 있으면 넘기고 안되어있으면 로그인 페이지
                    if(isLogin(request.getHeader("Cookie"))){
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
            response.forwardWithPath(path, request.getHeader("Accept"));
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private boolean isLogin(String cookieValue){
        Map<String, String> cookies = HttpRequestUtils.parseCookies(cookieValue);
        return Boolean.parseBoolean(cookies.get("logined"));

    }

    private boolean isUserMatch(String userId, String password){
        User user = DataBase.findUserById(userId);
        if(user == null){
            log.debug("User Not Found!");
            return false;
        }else{
            if(password.equals(user.getPassword())){
                log.debug("User Match!");
                return true;
            }
            log.debug("Password Mismatch!");
            return false;
        }
    }
}
