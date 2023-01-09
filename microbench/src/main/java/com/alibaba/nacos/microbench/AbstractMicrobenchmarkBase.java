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

package com.alibaba.nacos.microbench;

import io.grpc.netty.shaded.io.netty.util.ResourceLeakDetector;
import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.ChainedOptionsBuilder;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Base class for all JMH benchmarks.
 *
 * @author Weizhan▪Yun
 * @date 2022/10/28 19:43
 */
@Warmup(iterations = AbstractMicrobenchmarkBase.DEFAULT_WARMUP_ITERATIONS)
@Measurement(iterations = AbstractMicrobenchmarkBase.DEFAULT_MEASURE_ITERATIONS)
@State(Scope.Thread)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public abstract class AbstractMicrobenchmarkBase {
    
    protected static final int DEFAULT_WARMUP_ITERATIONS = 5;
    
    protected static final int DEFAULT_MEASURE_ITERATIONS = 10;
    
    protected static final String[] BASE_JVM_ARGS = {"-server", "-XX:+HeapDumpOnOutOfMemoryError"};
    
    static {
        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.DISABLED);
    }
    
    protected ChainedOptionsBuilder newOptionsBuilder() throws Exception {
        String className = getClass().getSimpleName();
        
        ChainedOptionsBuilder runnerOptions = new OptionsBuilder().include(className).jvmArgs(jvmArgs());
        
        if (getWarmupIterations() > 0) {
            runnerOptions.warmupIterations(getWarmupIterations());
        }
        
        if (getMeasureIterations() > 0) {
            runnerOptions.measurementIterations(getMeasureIterations());
        }
        
        if (getReportDir() != null) {
            String filePath = getReportDir() + className + ".json";
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
            } else {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            
            runnerOptions.resultFormat(ResultFormatType.JSON);
            runnerOptions.result(filePath);
        }
        
        return runnerOptions;
    }
    
    /**
     * custom jvm arguments.
     *
     * @return jvm arguments.
     */
    protected abstract String[] jvmArgs();
    
    protected static String[] removeAssertions(String[] jvmArgs) {
        List<String> customArgs = new ArrayList<>(jvmArgs.length);
        for (String arg : jvmArgs) {
            if (!arg.startsWith("-ea")) {
                customArgs.add(arg);
            }
        }
        if (jvmArgs.length != customArgs.size()) {
            jvmArgs = customArgs.toArray(new String[0]);
        }
        return jvmArgs;
    }
    
    @Test
    public void run() throws Exception {
        new Runner(newOptionsBuilder().build()).run();
    }
    
    protected int getWarmupIterations() {
        return getProperty("warmupIterations", -1);
    }
    
    protected int getMeasureIterations() {
        return getProperty("measureIterations", -1);
    }
    
    protected String getReportDir() {
        return getProperty("perfReportDir");
    }
    
    protected int getProperty(String key, int defaultValue) {
        String property = getProperty(key);
        if (property != null) {
            return Integer.parseInt(property);
        }
        return defaultValue;
    }
    
    protected String getProperty(String key) {
        String property = System.getProperty(key);
        return property != null ? property : System.getenv(key);
    }
    
    public static void handleUnexpectedException(Throwable t) {
        assertNull(t);
    }
}

