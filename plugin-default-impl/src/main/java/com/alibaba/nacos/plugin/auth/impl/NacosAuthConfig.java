/*
 * Copyright 1999-2021 Alibaba Group Holding Ltd.
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

package com.alibaba.nacos.plugin.auth.impl;

import com.alibaba.nacos.auth.config.AuthConfigs;
import com.alibaba.nacos.core.code.ControllerMethodsCache;
import com.alibaba.nacos.plugin.auth.impl.authenticate.AuthenticationNamagerDelegator;
import com.alibaba.nacos.plugin.auth.impl.authenticate.DefaultAuthenticationManager;
import com.alibaba.nacos.plugin.auth.impl.authenticate.IAuthenticationManager;
import com.alibaba.nacos.plugin.auth.impl.authenticate.LdapAuthenticationManager;
import com.alibaba.nacos.plugin.auth.impl.roles.NacosRoleServiceImpl;
import com.alibaba.nacos.plugin.auth.impl.users.NacosUserDetailsServiceImpl;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Spring security config.
 *
 * @author Nacos
 */
//@EnableGlobalMethodSecurity(prePostEnabled = true)
@Configuration
public class NacosAuthConfig {
    
    private final ControllerMethodsCache methodsCache;
    
    public NacosAuthConfig(ControllerMethodsCache methodsCache) {
        this.methodsCache = methodsCache;
    }
    
    /**
     * Init.
     */
    @PostConstruct
    public void init() {
        methodsCache.initClassMethod("com.alibaba.nacos.plugin.auth.impl.controller");
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    @Primary
    public IAuthenticationManager authenticationManager(
            ObjectProvider<LdapAuthenticationManager> ldapAuthenticatoinManagerObjectProvider,
            ObjectProvider<DefaultAuthenticationManager> defaultAuthenticationManagers, AuthConfigs authConfigs) {
        return new AuthenticationNamagerDelegator(defaultAuthenticationManagers,
                ldapAuthenticatoinManagerObjectProvider, authConfigs);
    }
    
    @Bean
    public IAuthenticationManager defaultAuthenticationManager(NacosUserDetailsServiceImpl userDetailsService,
            JwtTokenManager jwtTokenManager, NacosRoleServiceImpl roleService) {
        return new DefaultAuthenticationManager(userDetailsService, jwtTokenManager, roleService);
    }
}
