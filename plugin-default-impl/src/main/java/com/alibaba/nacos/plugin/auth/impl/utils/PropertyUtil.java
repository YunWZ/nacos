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

import com.alibaba.nacos.plugin.auth.impl.constant.AuthConstants;
import com.alibaba.nacos.sys.env.EnvUtil;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * PropertyUtil.
 *
 * @author Weizhan▪Yun
 * @date 2023/2/5 13:37
 */
public class PropertyUtil implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    
    private static boolean useExternalDB = false;
    
    private static boolean embeddedStorage = EnvUtil.getStandaloneMode();
    
    public static boolean isUseExternalDB() {
        return useExternalDB;
    }
    
    public static void setUseExternalDB(boolean useExternalDB) {
        PropertyUtil.useExternalDB = useExternalDB;
    }
    
    public static boolean isEmbeddedStorage() {
        return embeddedStorage;
    }
    
    public static void setEmbeddedStorage(boolean embeddedStorage) {
        PropertyUtil.embeddedStorage = embeddedStorage;
    }
    
    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        loadSetting();
    }
    
    private void loadSetting() {
        String platform = DatasourcePlatformUtil.getDatasourcePlatform("");
        boolean useExternalStorage = !AuthConstants.EMPTY_DATASOURCE_PLATFORM.equalsIgnoreCase(platform)
                && !AuthConstants.DERBY.equalsIgnoreCase(platform);
        setUseExternalDB(useExternalStorage);
        
        // must initialize after setUseExternalDB
        // This value is true in stand-alone mode and false in cluster mode
        // If this value is set to true in cluster mode, nacos's distributed storage engine is turned on
        // default value is depend on ${nacos.standalone}
        
        if (isUseExternalDB()) {
            setEmbeddedStorage(false);
        } else {
            boolean embeddedStorage =
                    PropertyUtil.embeddedStorage || Boolean.getBoolean(AuthConstants.EMBEDDED_STORAGE);
            setEmbeddedStorage(embeddedStorage);
            
            // If the embedded data source storage is not turned on, it is automatically
            // upgraded to the external data source storage, as before
            if (!embeddedStorage) {
                setUseExternalDB(true);
            }
        }
    }
}
