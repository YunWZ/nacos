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

import com.alibaba.nacos.common.executor.NameThreadFactory;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.runner.options.ChainedOptionsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Default implementation of the JMH microbenchmark adapter. There may be context switches introduced by this harness.
 *
 * @author Weizhan▪Yun
 * @date 2022/10/28 19:51
 */
@Fork(AbstractMicrobenchmark.DEFAULT_FORKS)
public abstract class AbstractMicrobenchmark extends AbstractMicrobenchmarkBase {
    
    protected static final int DEFAULT_FORKS = 2;
    
    public static final class HarnessExecutor extends ThreadPoolExecutor {
        
        public HarnessExecutor(int maxThreads, String prefix) {
            super(maxThreads, maxThreads, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(),
                    new NameThreadFactory(prefix));
            Logger logger = LoggerFactory.getLogger(AbstractMicrobenchmark.class);
            logger.debug("Using harness executor");
        }
    }
    
    private final String[] jvmArgs;
    
    public AbstractMicrobenchmark() {
        this(false, false);
    }
    
    public AbstractMicrobenchmark(boolean disableAssertions) {
        this(disableAssertions, false);
    }
    
    public AbstractMicrobenchmark(boolean disableAssertions, boolean disableHarnessExecutor) {
        final String[] customArgs;
        if (disableHarnessExecutor) {
            customArgs = new String[] {"-Xms768m", "-Xmx768m", "-XX:MaxDirectMemorySize=768m",
                    "-XX:BiasedLockingStartupDelay=0"};
        } else {
            customArgs = new String[] {"-Xms768m", "-Xmx768m", "-XX:MaxDirectMemorySize=768m",
                    "-XX:BiasedLockingStartupDelay=0", "-Djmh.executor=CUSTOM",
                    "-Djmh.executor.class=" + AbstractMicrobenchmark.HarnessExecutor.class.getName()};
        }
        String[] jvmArgs = new String[BASE_JVM_ARGS.length + customArgs.length];
        System.arraycopy(BASE_JVM_ARGS, 0, jvmArgs, 0, BASE_JVM_ARGS.length);
        System.arraycopy(customArgs, 0, jvmArgs, BASE_JVM_ARGS.length, customArgs.length);
        if (disableAssertions) {
            jvmArgs = removeAssertions(jvmArgs);
        }
        this.jvmArgs = jvmArgs;
    }
    
    @Override
    protected String[] jvmArgs() {
        return jvmArgs;
    }
    
    @Override
    protected ChainedOptionsBuilder newOptionsBuilder() throws Exception {
        ChainedOptionsBuilder runnerOptions = super.newOptionsBuilder();
        if (getForks() > 0) {
            runnerOptions.forks(getForks());
        }
        
        return runnerOptions;
    }
    
    protected int getForks() {
        return getProperty("forks", -1);
    }
}

