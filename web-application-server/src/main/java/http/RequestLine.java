package http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;

import java.util.Map;

public class RequestLine {
    private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);

    private Map<String, String> params;
    private String path;
    private String method;

    public RequestLine(String line){
        log.debug("request line : {}", line);
        String[] tokens = line.split(" ");
        method = tokens[0];

        if("POST".equals(method)){
            path = tokens[1];
            return;
        }

        int index = tokens[1].indexOf("?");
        if(index == -1){
            path = tokens[1];
        }else{
            path = tokens[1].substring(0, index);
            String queryString = tokens[1].substring(index + 1);
            params = HttpRequestUtils.parseQueryString(queryString);
        }
    }

    public Map<String, String> getParams() {
        return params;
    }

    public String getPath() {
        return path;
    }

    public String getMethod() {
        return method;
    }
}
