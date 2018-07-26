package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Map;

import db.DataBase;
import http.HttpRequest;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;
    private HttpRequest request;
    private String path;


    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            //index.html 요청하는 path

            DataOutputStream dos = new DataOutputStream(out);
            request = new HttpRequest(in);
            path = request.getPath(); // 하위 패스가 없고 /를 요청했으면 index.html로 path를 변경해주는 메서드 생성하자.

            if (path.startsWith("/user/create")) {
                    User user = new User(request.getParam("userId"),
                            request.getParam("password"),
                            request.getParam("name"),
                            request.getParam("email"));
                    log.debug("User : {}", user);
                    DataBase.addUser(user);
                    response302Header(dos, "/index.html");
                    return;
            }else if(path.startsWith("/user/login")){
                    //로그인 비교 해서 로그인 성공실패 유무 처리
                    if(isUserMatch(request.getParam("userId"), request.getParam("password"))){
                        response302HeaderWithLogined(dos, true, "/index.html");
                    }else{
                        response302HeaderWithLogined(dos, false, "/user/login_failed.html"); // 그냥 일반 302로 보내면 될 듯
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
                        byte[] body = sb.toString().getBytes();
                        response200Header(dos, body.length);
                        responseBody(dos, body);
                    }else{
                        response302Header(dos, "/user/login.html");
                    }
            }

            byte[] body = Files.readAllBytes(new File("./webapp" + path).toPath());
            response200Header(dos, body.length); //수정 필요
            responseBody(dos, body);
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


//    private void response302Header(DataOutputStream dos, String path){
//        try {
//            dos.writeBytes("HTTP/1.1 302 OK \r\n");
//            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
//            dos.writeBytes("Location: " + path +"\r\n");
//            dos.writeBytes("\r\n");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void response302HeaderWithLogined(DataOutputStream dos, boolean isLogin, String path){
//        try {
//            dos.writeBytes("HTTP/1.1 302 OK \r\n");
//            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
//            dos.writeBytes("Location: " + path +"\r\n");
//            if(isLogin) {
//                dos.writeBytes("Set-Cookie: logined=true\r\n");
//            }
//            dos.writeBytes("\r\n");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
//        try {
//            dos.writeBytes("HTTP/1.1 200 OK \r\n");
//            dos.writeBytes("Content-Type: " + request.getHeader("Accept") + ";charset=utf-8\r\n");
//            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
//            dos.writeBytes("\r\n");
//        } catch (IOException e) {
//            log.error(e.getMessage());
//        }
//    }
//
//    private void responseBody(DataOutputStream dos, byte[] body) {
//        try {
//            dos.write(body, 0, body.length);
//            dos.flush();
//        } catch (IOException e) {
//            log.error(e.getMessage());
//        }
//    }
}
