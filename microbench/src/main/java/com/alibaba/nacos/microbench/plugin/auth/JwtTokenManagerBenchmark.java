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

package com.alibaba.nacos.microbench.plugin.auth;

import com.alibaba.nacos.microbench.AbstractMicrobenchmark;
import com.alibaba.nacos.plugin.auth.constant.Constants;
import com.alibaba.nacos.plugin.auth.exception.AccessException;
import com.alibaba.nacos.plugin.auth.impl.JwtTokenManager;
import com.alibaba.nacos.sys.env.EnvUtil;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.springframework.mock.env.MockEnvironment;

import java.util.concurrent.TimeUnit;

/**
 * Benchmark for {@link JwtTokenManager}.
 *
 * @author Weizhan▪Yun
 * @date 2022/10/23 22:32
 */
@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class JwtTokenManagerBenchmark extends AbstractMicrobenchmark {
    
    JwtTokenManager jwtTokenManager;
    
    public static final String CHAR32 = "U2VjcmV0S2V5MDEyMyQ1Njc4OTAkMjM0NTY3ODkwMTI=";
    
    private String accessToken;
    
    @Setup
    public void setUp() {
        initEnvironment();
        
        jwtTokenManager = new JwtTokenManager();
        accessToken = jwtTokenManager.createToken("nacos");
    }
    
    private void initEnvironment() {
        MockEnvironment environment = new MockEnvironment();
        
        EnvUtil.setEnvironment(environment);
        
        environment.setProperty(Constants.Auth.NACOS_CORE_AUTH_ENABLED, "true");
        environment.setProperty(Constants.Auth.NACOS_CORE_AUTH_SERVER_IDENTITY_KEY, "");
        environment.setProperty(Constants.Auth.NACOS_CORE_AUTH_SERVER_IDENTITY_VALUE, "");
        environment.setProperty(Constants.Auth.NACOS_CORE_AUTH_SYSTEM_TYPE, "nacos");
        
        environment.setProperty("nacos.core.auth.plugin.nacos.token.secret.key", CHAR32);
        environment.setProperty("nacos.core.auth.plugin.nacos.token.expire.seconds", "300000");
    }
    
    @Benchmark
    @BenchmarkMode({Mode.AverageTime, Mode.Throughput})
    public void measureCreateToken() {
        jwtTokenManager.createToken("username");
    }
    
    @Benchmark
    @BenchmarkMode({Mode.AverageTime, Mode.Throughput})
    public void measureValidToken() throws AccessException {
        jwtTokenManager.validateToken(accessToken);
    }
}
