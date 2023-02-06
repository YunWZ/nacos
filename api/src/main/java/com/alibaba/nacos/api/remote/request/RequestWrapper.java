/*
 * Copyright 1999-2022 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.nacos.api.remote.request;

import java.util.Map;
import java.util.TreeMap;

/**
 * RequestWrapper.
 *
 * @author Weizhan▪Yun
 * @date 2023/2/6 15:41
 */
public class RequestWrapper<T extends Request> {
    
    private final Map<String, String> headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    
    private final T wrappedRequest;
    
    private RequestWrapper(T wrappedRequest, Map<String, String> headers) {
        this.wrappedRequest = wrappedRequest;
        if (headers != null) {
            this.headers.putAll(headers);
        }
    }
    
    public static RequestWrapper wrap(Request request, Map<String, String> headers) {
        return new RequestWrapper(request, headers);
    }
    
    public static RequestWrapper wrap(Request request) {
        return new RequestWrapper(request, null);
    }
    
    public T getWrappedRequest() {
        return wrappedRequest;
    }
    
    /**
     * put header.
     *
     * @param key   key of value.
     * @param value value.
     */
    public void putHeader(String key, String value) {
        headers.put(key, value);
    }
    
    /**
     * put headers .
     *
     * @param headers headers to put.
     */
    public void putAllHeader(Map<String, String> headers) {
        if (headers == null || headers.isEmpty()) {
            return;
        }
        this.headers.putAll(headers);
    }
    
    /**
     * get a header value .
     *
     * @param key key of value.
     * @return return value of key. return null if not exist.
     */
    public String getHeader(String key) {
        return headers.get(key);
    }
    
    /**
     * get a header value of default value.
     *
     * @param key          key of value.
     * @param defaultValue default value if key is not exist.
     * @return return final value.
     */
    public String getHeader(String key, String defaultValue) {
        String value = headers.get(key);
        return (value == null) ? defaultValue : value;
    }
    
    /**
     * Getter method for property <tt>headers</tt>.
     *
     * @return property value of headers
     */
    public Map<String, String> getHeaders() {
        return headers;
    }
    
    public void clearHeaders() {
        this.headers.clear();
    }
    
    public void setRequestId(String requestId) {
        this.wrappedRequest.setRequestId(requestId);
    }
}
