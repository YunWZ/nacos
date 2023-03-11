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

import com.alibaba.nacos.api.remote.Payload;

/**
 * AbstractRequestPayloadBody.
 *
 * @author Weizhan▪Yun
 * @date 2023/2/11 17:33
 */
public abstract class AbstractRequestPayloadBody implements Payload {
    
    private String requestId;
    
    /**
     * Getter method for property <tt>requestId</tt>.
     *
     * @return property value of requestId
     */
    public String getRequestId() {
        return requestId;
    }
    
    /**
     * Setter method for property <tt>requestId</tt>.
     *
     * @param requestId value to be assigned to property requestId
     */
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
    
    /*public Request toRequest() {
        return Request.of(this);
    }*/
    
    /**
     * Get module name of request.
     *
     * @return module name
     */
    public abstract String getModule();
    
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{requestId='" + this.requestId + "'}";
    }
}
