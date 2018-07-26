package http;

import org.junit.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class RequestLineTest {

    @Test
    public void create_method(){
        //instance 만들고
        //in 넣어주고
        RequestLine line = new RequestLine("GET /index.html HTTP/1.1");
        assertThat(line.getMethod()).isEqualTo("GET");
        assertThat(line.getPath()).isEqualTo("/index.html");

        line = new RequestLine("POST /index.html HTTP/1.1");
        assertThat(line.getMethod()).isEqualTo("POST");
    }

    @Test
    public void create_path_params(){
        RequestLine line = new RequestLine("GET /user/create?userId=user&password=pass&name=username HTTP/1.1");
        assertThat(line.getPath()).isEqualTo("/user/create");
        Map<String, String> params = line.getParams();
        assertThat(params.size()).isEqualTo(3);
    }
}
