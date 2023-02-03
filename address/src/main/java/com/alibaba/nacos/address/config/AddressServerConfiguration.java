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

package com.alibaba.nacos.address.config;

import com.alibaba.nacos.core.code.ControllerMethodsCache;
import org.springframework.context.annotation.Configuration;

/**
 * AddressServerConfiguration.
 *
 * @author Weizhan▪Yun
 * @date 2023/2/3 15:47
 */
@Configuration
public class AddressServerConfiguration {
    
    private ControllerMethodsCache methodsCache;
    
    public AddressServerConfiguration(ControllerMethodsCache methodsCache) {
        this.methodsCache = methodsCache;
        methodsCache.initClassMethod("com.alibaba.nacos.address.controller");
    }
}
