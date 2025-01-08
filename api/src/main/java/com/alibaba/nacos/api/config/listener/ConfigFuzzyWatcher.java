/*
 * Copyright 1999-2023 Alibaba Group Holding Ltd.
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

package com.alibaba.nacos.api.config.listener;

import java.util.concurrent.Executor;

/**
 * AbstractFuzzyListenListener is an abstract class that provides basic functionality for listening to fuzzy
 * configuration changes in Nacos.
 *
 * @author stone-98
 * @date 2024/3/4
 */
public abstract class ConfigFuzzyWatcher {
    
    /**
     * Callback method invoked when a fuzzy configuration change event occurs.
     *
     * @param event The fuzzy configuration change event
     */
    public abstract void onEvent(ConfigFuzzyWatchChangeEvent event);
    
    /**
     * Get executor for execute this receive.
     *
     * @return Executor
     */
    public  Executor getExecutor(){
        return null;
    }
    
}
