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

package com.alibaba.nacos.common.remote.client.grpc;

import com.alibaba.nacos.api.grpc.auto.Payload;
import com.alibaba.nacos.api.remote.request.Request;
import com.alibaba.nacos.api.remote.request.ServerCheckRequest;
import com.alibaba.nacos.common.remote.PayloadRegistry;
import com.alibaba.nacos.microbench.AbstractMicrobenchmark;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import java.util.concurrent.TimeUnit;

/**
 * GrpcUtilsBenchmark.
 *
 * @author Weizhan▪Yun
 * @date 2023/2/5 19:03
 */
@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class GrpcUtilsBenchmark extends AbstractMicrobenchmark {
    
    private Payload payload;
    
    private Request request;
    
    @Setup(Level.Iteration)
    public void setup() {
        PayloadRegistry.init();
        request = createRequest();
        payload = GrpcUtils.convert(request);
    }
    
    private static ServerCheckRequest createRequest() {
        ServerCheckRequest serverCheckRequest = new ServerCheckRequest();
        serverCheckRequest.putHeader("module", "naming");
        serverCheckRequest.putHeader("source1", "sdk");
        serverCheckRequest.putHeader("source2", "sdk");
        serverCheckRequest.putHeader("source3", "sdk");
        serverCheckRequest.putHeader("source4", "sdk");
        serverCheckRequest.putHeader("source5", "sdk");
        serverCheckRequest.putHeader("source6", "sdk");
        serverCheckRequest.putHeader("source7", "sdk");
        serverCheckRequest.putHeader("source8", "sdk");
        serverCheckRequest.putHeader("source9", "sdk");
        serverCheckRequest.putHeader("source10", "sdk");
        serverCheckRequest.putHeader("source11", "sdk");
        serverCheckRequest.putHeader("source12", "sdk");
        
        return serverCheckRequest;
    }
    
    @Benchmark
    @BenchmarkMode({Mode.AverageTime, Mode.Throughput})
    public void measureParse() {
        GrpcUtils.parse(payload);
    }
    
    @Benchmark
    @BenchmarkMode({Mode.AverageTime, Mode.Throughput})
    public void measureConvertRequest() {
        GrpcUtils.convert(request);
    }
    
}
