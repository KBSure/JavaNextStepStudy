package http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);
    private Map<String, String> headers;
    private Map<String, String> params;
    private RequestLine requestLine;

    public HttpRequest(InputStream in){
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String line = br.readLine();
            if(line == null){
                return;
            }
            requestLine = new RequestLine(line);
            headers = new HashMap<>();
            line = br.readLine();
            while(!"".equals(line)){
                log.debug("headers > {}", line);
                String[] splited = line.split(": "); //localhost:8080 처리는 못한다. 첫 ":"의 인덱스를 찾아서 subString으로 header 구성하는 것이 좋을지도.
                headers.put(splited[0].trim(), splited[1].trim());
//                int index = line.indexOf(":");
//                String fieldName = line.substring(0, index).trim();
//                String fiendValue = line.substring(index + 1).trim();
//                headers.put(fieldName, fiendValue);
                line = br.readLine();
            }

            if(getMethod().isPost()){
              //body length 만큼 읽어서 querryString!
                String queryString = IOUtils.readData(br,
                        Integer.parseInt(headers.get("Content-Length")));
                params = HttpRequestUtils.parseQueryString(queryString);
            }else{
                params = requestLine.getParams();
            }
        } catch (IOException e) {
            log.debug(e.getMessage());
        }
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public String getHeader(String fieldName){
        return headers.get(fieldName);
    }

    public String getParam(String paramName){
        return params.get(paramName);
    }

}
