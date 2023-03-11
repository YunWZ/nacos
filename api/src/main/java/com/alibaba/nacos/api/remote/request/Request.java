/*
 * Copyright 1999-2020 Alibaba Group Holding Ltd.
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

import com.alibaba.nacos.api.remote.Payload;

import java.util.Map;
import java.util.TreeMap;

/**
 * Request.
 *
 * @author liuzunfei
 */
public class Request<T extends AbstractRequestPayloadBody> implements Payload {

    private final Map<String, String> headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    private final T payloadBody;

    private Request(T payloadBody) {
        this.payloadBody = payloadBody;
    }
    
    /**
     * Wrap request payload body.
     *
     * @param body request payload body
     * @param <R> subclass of AbstractRequestPayloadBody
     * @return Request instance
     */
    public static <R extends AbstractRequestPayloadBody> Request<R> of(R body) {
        return new Request<>(body);
    }
    
    /**
     * Wrap request payload body with headers.
     *
     * @param body request payload body
     * @param <R> subclass of AbstractRequestPayloadBody
     * @return Request instance
     */
    public static <R extends AbstractRequestPayloadBody> Request<R> of(R body, Map<String, String> headersMap) {
        Request<R> request = new Request<>(body);
        request.putAllHeader(headersMap);
        return request;
    }

    public T getPayloadBody() {
        return payloadBody;
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
     * Getter method for property <tt>requestId</tt>.
     *
     * @return property value of requestId
     */
    public String getRequestId() {
        return payloadBody.getRequestId();
    }

    /**
     * Setter method for property <tt>requestId</tt>.
     *
     * @param requestId value to be assigned to property requestId
     */
    public void setRequestId(String requestId) {
        payloadBody.setRequestId(requestId);
    }

    /**
     * Getter method for property <tt>type</tt>.
     *
     * @return property value of type
     */
    public String getModule() {
        return payloadBody.getModule();
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

    public String getPayloadBodyType() {
        return payloadBody.getClass().getSimpleName();
    }

    @Override
    public String toString() {
        return getPayloadBodyType() + "{" + "headers=" + headers + ", requestId='" + payloadBody.getRequestId() + '\'' + '}';
    }
}
