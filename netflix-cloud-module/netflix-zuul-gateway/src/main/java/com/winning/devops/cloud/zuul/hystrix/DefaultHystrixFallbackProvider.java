package com.winning.devops.cloud.zuul.hystrix;

import com.alibaba.fastjson.JSON;
import com.netflix.hystrix.exception.HystrixTimeoutException;
import org.springframework.cloud.netflix.zuul.filters.route.FallbackProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author chensj
 * @title Default Hystrix zuul 默认熔断器
 * @project winning-spring-cloud
 * @package com.winning.devops.zuul.hystrix
 * @date: 2019-04-26 22:17
 */
@Component
public class DefaultHystrixFallbackProvider implements FallbackProvider {
    @Override
    public String getRoute() {
        return "*";
    }

    @Override
    public ClientHttpResponse fallbackResponse() {
       return new ClientHttpResponse() {
           @Override
           public HttpStatus getStatusCode() throws IOException {
               return HttpStatus.OK;
           }

           @Override
           public int getRawStatusCode() throws IOException {
               return 200;
           }

           @Override
           public String getStatusText() throws IOException {
               return "OK";
           }

           @Override
           public void close() {

           }

           @Override
           public InputStream getBody() throws IOException {
               return new ByteArrayInputStream("oooops! error, I'm the fallback.".getBytes());
           }

           @Override
           public HttpHeaders getHeaders() {
               HttpHeaders httpHeaders = new HttpHeaders();
               httpHeaders.setContentType(MediaType.APPLICATION_JSON);
               return httpHeaders;
           }
       };
    }

    @Override
    public ClientHttpResponse fallbackResponse(Throwable cause) {
        if (cause instanceof HystrixTimeoutException) {
            return response(HttpStatus.GATEWAY_TIMEOUT);
        } else {
            return response(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    public ClientHttpResponse fallbackResponse(String route, Throwable cause) {
        if (cause instanceof HystrixTimeoutException) {
            return response(HttpStatus.GATEWAY_TIMEOUT);
        } else {
            return response(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ClientHttpResponse response(final HttpStatus status) {
        return new ClientHttpResponse() {
            @Override
            public HttpStatus getStatusCode() throws IOException {
                return status;
            }

            @Override
            public int getRawStatusCode() throws IOException {
                return status.value();
            }

            @Override
            public String getStatusText() throws IOException {
                return status.getReasonPhrase();
            }

            @Override
            public void close() {
            }

            @Override
            public InputStream getBody() throws IOException {
                Map<String,Object> resultMap = new HashMap<>(5);
                resultMap.put("msg","Sorry, It's Error!");
                resultMap.put("status", "fail");
                return new ByteArrayInputStream(JSON.toJSONString(resultMap).getBytes());
            }

            @Override
            public HttpHeaders getHeaders() {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                return headers;
            }
        };
    }
}
