package http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HttpResponse {
    private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);

    private DataOutputStream dos = null;
    private Map<String, String> headers = new HashMap<>();//header를 이 클래스의 인스턴스에 주입할 수 있다.

    public HttpResponse(OutputStream out){
        this.dos = new DataOutputStream(out);
    }

    public void addHeader(String key, String value){ //addHeader 덕분에, 응답 메서드를 특정 Header가 포함되었는지 안되었는지 구별할 필요가 없어졌다
        headers.put(key, value);
    }

    public void forwardWithPath(String path, String accept) {
        byte[] body;
        try {
            body = Files.readAllBytes(new File("./webapp" + path).toPath());
            headers.put("Content-Type", accept + ";charset=utf-8");
            headers.put("Content-Length", body.length + "");
            response200Header();
            responseBody(body);
        } catch (IOException e) {
            log.debug(e.getMessage());
        }

        //header에 필요 필드와 값을 넣어서 header를 dos에 쓸 수 있도록 이용!
        //header에 accept 이용해서 content-type put
    }

    public void forwardWithBody(String body){
        byte[] bodyAsByte = body.getBytes();
        headers.put("Content-Type", "text/html;charset=utf-8");
        headers.put("Content-Length", bodyAsByte.length + "");
        response200Header();
        responseBody(bodyAsByte);
    }

    public void sendRedirect(String path){
        try {
            dos.writeBytes("HTTP/1.1 302 \n");
            headers.put("Location", path);
            processHeaders();
            dos.writeBytes("\n");
        } catch (IOException e) {
            log.debug(e.getMessage());
        }
}

    private void response200Header() {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            //직접 쓸 필요가 없다. header 내용을 불러와서 쓰기 작업하면 된다.
            processHeaders();
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }

    }

    private void processHeaders() throws IOException {
//        dos.writeBytes("Content-Type: " + accept + ";charset=utf-8\r\n");
//        dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
        //headers 내용 이용해서 dos.writeBytes 작성!
        Set<String> keySet = headers.keySet();
        for (String key : keySet) {
            dos.writeBytes(key + ": " + headers.get(key) + "\n");
        }
    }

    private void responseBody(byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
