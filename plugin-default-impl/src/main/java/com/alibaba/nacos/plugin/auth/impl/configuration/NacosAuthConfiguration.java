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

package com.alibaba.nacos.plugin.auth.impl.configuration;

import com.alibaba.nacos.auth.config.AuthConfigs;
import com.alibaba.nacos.core.code.ControllerMethodsCache;
import com.alibaba.nacos.plugin.auth.impl.JwtTokenManager;
import com.alibaba.nacos.plugin.auth.impl.authenticate.AuthenticationNamagerDelegator;
import com.alibaba.nacos.plugin.auth.impl.authenticate.DefaultAuthenticationManager;
import com.alibaba.nacos.plugin.auth.impl.authenticate.IAuthenticationManager;
import com.alibaba.nacos.plugin.auth.impl.authenticate.LdapAuthenticationManager;
import com.alibaba.nacos.plugin.auth.impl.configuration.condition.ConditionOnEmbeddedStorage;
import com.alibaba.nacos.plugin.auth.impl.configuration.condition.ConditionOnExternalStorage;
import com.alibaba.nacos.plugin.auth.impl.persistence.EmbeddedPermissionPersistServiceImpl;
import com.alibaba.nacos.plugin.auth.impl.persistence.EmbeddedRolePersistServiceImpl;
import com.alibaba.nacos.plugin.auth.impl.persistence.EmbeddedUserPersistServiceImpl;
import com.alibaba.nacos.plugin.auth.impl.persistence.ExternalPermissionPersistServiceImpl;
import com.alibaba.nacos.plugin.auth.impl.persistence.ExternalRolePersistServiceImpl;
import com.alibaba.nacos.plugin.auth.impl.persistence.ExternalUserPersistServiceImpl;
import com.alibaba.nacos.plugin.auth.impl.persistence.repository.extrnal.ExternalStoragePersistServiceImpl;
import com.alibaba.nacos.plugin.auth.impl.roles.NacosRoleServiceImpl;
import com.alibaba.nacos.plugin.auth.impl.users.NacosUserDetailsServiceImpl;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.annotation.PostConstruct;

/**
 * NacosAuthConfiguration.
 *
 * @author Weizhan▪Yun
 * @date 2023/2/3 10:34
 */
@Configuration(proxyBeanMethods = false)
public class NacosAuthConfiguration {
    
    private final ControllerMethodsCache methodsCache;
    
    public NacosAuthConfiguration(ControllerMethodsCache methodsCache) {
        this.methodsCache = methodsCache;
    }
    
    @PostConstruct
    public void init() {
        methodsCache.initClassMethod("com.alibaba.nacos.plugin.auth.impl.controller");
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
    
    @Conditional(value = ConditionOnEmbeddedStorage.class)
    @Configuration(proxyBeanMethods = false)
    public static class EmbeddedStorageConfiguration {
        
        @Bean
        public EmbeddedPermissionPersistServiceImpl embeddedPermissionPersistService() {
            return new EmbeddedPermissionPersistServiceImpl();
        }
        
        @Bean
        //        @Conditional(value = ConditionOnEmbeddedStorage.class)
        public EmbeddedRolePersistServiceImpl embeddedRolePersistService() {
            return new EmbeddedRolePersistServiceImpl();
        }
        
        @Bean
        //        @Conditional(value = ConditionOnEmbeddedStorage.class)
        public EmbeddedUserPersistServiceImpl embeddedUserPersistService() {
            return new EmbeddedUserPersistServiceImpl();
        }
    }
    
    @Conditional(value = ConditionOnExternalStorage.class)
    @Configuration(proxyBeanMethods = false)
    public static class ExternalStorageConfiguration {
        
        @Bean
        public ExternalPermissionPersistServiceImpl externalPermissionPersistService() {
            return new ExternalPermissionPersistServiceImpl();
        }
        
        @Bean
        public ExternalRolePersistServiceImpl externalRolePersistService() {
            return new ExternalRolePersistServiceImpl();
        }
        
        @Bean
        public ExternalUserPersistServiceImpl externalUserPersistService() {
            return new ExternalUserPersistServiceImpl();
        }
        
        @Bean
        public ExternalStoragePersistServiceImpl externalStoragePersistService() {
            return new ExternalStoragePersistServiceImpl();
        }
    }
}
