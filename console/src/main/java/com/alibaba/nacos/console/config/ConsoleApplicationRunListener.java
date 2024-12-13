package com.alibaba.nacos.console.config;

import com.alibaba.nacos.console.constant.ConsoleConstants;
import com.alibaba.nacos.sys.env.EnvUtil;
import com.alibaba.nacos.sys.utils.InetUtils;
import org.springframework.boot.ConfigurableBootstrapContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.boot.context.event.EventPublishingRunListener;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;

public class ConsoleApplicationRunListener implements SpringApplicationRunListener, Ordered {
    
    public ConsoleApplicationRunListener(SpringApplication application, String[] args) {
    }
    
    @Override
    public void environmentPrepared(ConfigurableBootstrapContext bootstrapContext,
            ConfigurableEnvironment environment) {
        EnvUtil.setEnvironment(environment);
        System.setProperty(ConsoleConstants.LOCAL_IP_PROPERTY_KEY, InetUtils.getSelfIP());
    }
    
    /**
     * Before {@link EventPublishingRunListener}.
     *
     * @return HIGHEST_PRECEDENCE
     */
    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }
}
