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

package com.alibaba.nacos.microbench.config;

import com.alibaba.nacos.config.server.remote.ConfigChangeListenContext;
import com.alibaba.nacos.microbench.AbstractMicrobenchmark;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Setup;

import java.util.ArrayList;
import java.util.List;

/**
 * Benchmark for {@link ConfigChangeListenContext}.
 *
 * @author Weizhan▪Yun
 * @date 2022/8/18 12:09
 */
@BenchmarkMode(Mode.All)
public class ConfigChangeListenContextBenchmark extends AbstractMicrobenchmark {
    
    private List<String> connectionIds;
    
    private static final int ID_SIZE = 10_000;
    
    private static final ConfigChangeListenContext CONFIGCHANGELISTENCONTEXT = new ConfigChangeListenContext();
    
    /**
     * init method.
     *
     */
    @Setup
    public void init() {
        connectionIds = new ArrayList<>(ID_SIZE);
        for (int i = 0; i < ID_SIZE; i++) {
            connectionIds.add(String.valueOf(i));
        }
    }
    
    /**
     * addListen method benchmark.
     *
     */
    @Benchmark
    public void measureAddListen() {
        for (String id : connectionIds) {
            CONFIGCHANGELISTENCONTEXT.addListen("groupKey1", "md5-1", id);
        }
        for (String id : connectionIds) {
            CONFIGCHANGELISTENCONTEXT.addListen("groupKey2", "md5-2", id);
        }
        for (String id : connectionIds) {
            CONFIGCHANGELISTENCONTEXT.addListen("groupKey3", "md5-3", id);
        }
    }
}
