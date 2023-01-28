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
import com.alibaba.nacos.plugin.auth.impl.jwt.NacosJwtParser;
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
public class JwtTokenManager32Benchmark extends AbstractMicrobenchmark {
    
    JwtTokenManager jwtTokenManager;
    
    NacosJwtParser jwtParser;
    
    NacosJwtParser jwtParser2;
    
    NacosJwtParser jwtParser3;
    
    public static final String BASE64EDKEY32 = "U2VjcmV0S2V5MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTI=";
    
    public static final String BASE64EDKEY48 = "U2VjcmV0S2V5MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTI3ODkwMTIzNDU2Nzg5MDEy";
    
    public static final String BASE64EDKEY64 = "U2VjcmV0S2V5MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTJTZWNyZXRLZXkwMTIzNDU2Nzg5MDEyMzQ1Njc4OTAxMmE=";
    
    private static final String ACCESS_TOKEN_HS256 = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJuYWNvcyIsImV4cCI6MTY3NDcyNzkwNX0.xYp-6tPpTZC-baP3SJeduaCIyqYJ4PLNFGVPfoqRijs";
    
    private static final String ACCESS_TOKEN_HS384 = "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJuYWNvcyIsImV4cCI6MTY3NDcyODAyMX0.vtMcU996i8O5FDQ3vaufggenU0_xpk19pKTuPiaqjh3RRRo-Yd9tPZKsEoizRal7";
    
    private static final String ACCESS_TOKEN_HS512 = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJuYWNvcyIsImV4cCI6MTY3NDcyODA0N30.J_A5KNcOeaVZSaoNRB5MOET9AwconyZ52Mj5DQ0sYQiiWPpQobxzMoQCEWu8W3RbFaZmIZTTVinq0xBsOFLtog";
    
    @Setup
    public void setUp() {
        initEnvironment();
        
        jwtTokenManager = new JwtTokenManager();
        jwtParser = new NacosJwtParser(BASE64EDKEY32);
        jwtParser2 = new NacosJwtParser(BASE64EDKEY48);
        jwtParser3 = new NacosJwtParser(BASE64EDKEY64);
    }
    
    private void initEnvironment() {
        MockEnvironment environment = new MockEnvironment();
        
        EnvUtil.setEnvironment(environment);
        
        environment.setProperty(Constants.Auth.NACOS_CORE_AUTH_ENABLED, "true");
        environment.setProperty(Constants.Auth.NACOS_CORE_AUTH_SERVER_IDENTITY_KEY, "");
        environment.setProperty(Constants.Auth.NACOS_CORE_AUTH_SERVER_IDENTITY_VALUE, "");
        environment.setProperty(Constants.Auth.NACOS_CORE_AUTH_SYSTEM_TYPE, "nacos");
        
        environment.setProperty("nacos.core.auth.plugin.nacos.token.secret.key", BASE64EDKEY32);
        environment.setProperty("nacos.core.auth.plugin.nacos.token.expire.seconds", "300000");
    }
    
    @Benchmark
    @BenchmarkMode({Mode.AverageTime, Mode.Throughput})
    public void measureCreateToken() {
        jwtTokenManager.createToken("username");
    }
    
    @Benchmark
    @BenchmarkMode({Mode.AverageTime, Mode.Throughput})
    public void measureValidOldToken() {
        jwtTokenManager.validateToken(ACCESS_TOKEN_HS256);
    }
    
    @Benchmark
    @BenchmarkMode({Mode.AverageTime, Mode.Throughput})
    public void measureCreateNewTokenForNacosJwtParser32() {
        jwtParser.jwtBuilder().setUserName("username").setExpiredTime(300000L).compact();
    }
    
    @Benchmark
    @BenchmarkMode({Mode.AverageTime, Mode.Throughput})
    public void measureValidNewTokenForNacosJwtParser32() throws AccessException {
        jwtParser.parse(ACCESS_TOKEN_HS256);
    }
    
    @Benchmark
    @BenchmarkMode({Mode.AverageTime, Mode.Throughput})
    public void measureCreateNewTokenForNacosJwtParser48() {
        jwtParser2.jwtBuilder().setUserName("username").setExpiredTime(300000L).compact();
    }
    
    @Benchmark
    @BenchmarkMode({Mode.AverageTime, Mode.Throughput})
    public void measureValidNewTokenForNacosJwtParser48() throws AccessException {
        jwtParser2.parse(ACCESS_TOKEN_HS384);
    }
    
    @Benchmark
    @BenchmarkMode({Mode.AverageTime, Mode.Throughput})
    public void measureCreateNewTokenForNacosJwtParser64() {
        jwtParser3.jwtBuilder().setUserName("username").setExpiredTime(300000L).compact();
    }
    
    @Benchmark
    @BenchmarkMode({Mode.AverageTime, Mode.Throughput})
    public void measureValidNewTokenForNacosJwtParser64() throws AccessException {
        jwtParser3.parse(ACCESS_TOKEN_HS512);
    }
    
}
