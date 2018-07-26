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
                String[] splited = line.split(":");
                headers.put(splited[0].trim(), splited[1].trim());
                line = br.readLine();
            }

            if("POST".equals(requestLine.getMethod())){
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

    public String getMethod() {
        return requestLine.getMethod();
    }

    public String getHeader(String fieldName){
        return headers.get(fieldName);
    }

    public String getParameter(String paramName){
        return params.get(paramName);
    }

}
