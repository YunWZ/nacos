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

package com.alibaba.nacos.plugin.auth.impl.utils;

import com.alibaba.nacos.common.utils.StringUtils;
import com.alibaba.nacos.plugin.auth.impl.constant.AuthConstants;
import com.alibaba.nacos.sys.env.EnvUtil;

/**
 * DatasourcePlatformUtil.
 *
 * @author Weizhan▪Yun
 * @date 2023/2/5 13:43
 */
public class DatasourcePlatformUtil {
    
    public static String getDatasourcePlatform(String defaultPlatform) {
        //Todo: maybe has a graceful implementation with spring api?
        String platform = EnvUtil.getProperty(AuthConstants.DATASOURCE_PLATFORM_PROPERTY, defaultPlatform);
        if (StringUtils.isBlank(platform)) {
            platform = EnvUtil.getProperty(AuthConstants.DATASOURCE_PLATFORM_PROPERTY_OLD, defaultPlatform);
        }
        return platform;
    }
}
