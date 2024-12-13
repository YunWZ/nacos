package com.alibaba.nacos.console.service;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.common.utils.StringUtils;
import com.alibaba.nacos.console.config.ConsoleConfig;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class NacosServerService {
    
    private final AtomicInteger atomicInteger = new AtomicInteger(0);
    
    @Autowired
    private ConsoleConfig consoleConfig;
    
    @Autowired
    private RestTemplate restTemplate;
    
    /**
     * forward to nacos server.
     *
     * @param request origin request
     * @param body    origin body
     * @param apiPath
     * @return nacos server resp
     * @throws NacosException aa
     */
    public <T> ResponseEntity<T> proxy(HttpServletRequest request, @Nullable String body, Class<T> responseType,
            String apiPath) throws NacosException {
        ResponseEntity<T> result = null;
        switch (HttpMethod.resolve(request.getMethod())) {
            case GET:
                result = restTemplate.exchange(generateUrl(request, apiPath), HttpMethod.GET,
                        generateEntity(request, body), responseType);
                break;
            case DELETE:
                result = restTemplate.exchange(generateUrl(request, apiPath), HttpMethod.DELETE,
                        generateEntity(request, body), responseType);
                break;
            case POST:
                result = restTemplate.exchange(generateUrl(request, apiPath), HttpMethod.POST,
                        generateEntity(request, body), responseType);
                break;
            case PUT:
                result = restTemplate.exchange(generateUrl(request, apiPath), HttpMethod.PUT,
                        generateEntity(request, body), responseType);
                break;
            case PATCH:
                result = restTemplate.exchange(generateUrl(request, apiPath), HttpMethod.PATCH,
                        generateEntity(request, body), responseType);
                break;
            default:
                throw new NacosException(NacosException.BAD_GATEWAY, "Unsupport Method.");
        }
        return result;
    }
    
    private HttpEntity<?> generateEntity(HttpServletRequest request, String body) {
        return new HttpEntity<>(body, convertToHttpHeaders(request));
    }
    
    private String generateUrl(HttpServletRequest request, String apiPath) {
        String baseUrl = selectServer();
        if (!baseUrl.toLowerCase().startsWith("http")) {
            baseUrl = "http://" + baseUrl;
        }
        StringBuilder requestUrl = new StringBuilder(baseUrl);
        
        if (StringUtils.isNotBlank(apiPath)) {
            requestUrl.append(apiPath);
        } else {
            requestUrl.append(request.getRequestURI());
        }
        
        String queryString = request.getQueryString(); // 获取查询参数
        if (queryString != null) {
            requestUrl.append("?").append(queryString);
        }
        return requestUrl.toString();
    }
    
    private String selectServer() {
        List<String> serverList = consoleConfig.getServerList();
        int index = atomicInteger.incrementAndGet() % serverList.size();
        return serverList.get(index);
    }
    
    private HttpHeaders convertToHttpHeaders(HttpServletRequest request) {
        HttpHeaders headers = new HttpHeaders();
        Enumeration<String> headerNames = request.getHeaderNames();
        
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            Enumeration<String> values = request.getHeaders(headerName);
            if (headerName.equalsIgnoreCase("Content-Type") || headerName.equalsIgnoreCase("Content-Length")
                    || headerName.equalsIgnoreCase("Connection") || headerName.equalsIgnoreCase("Host")) {
                continue;
            }
            while (values.hasMoreElements()) {
                headers.add(headerName, values.nextElement());
            }
        }
        return headers;
    }
}
