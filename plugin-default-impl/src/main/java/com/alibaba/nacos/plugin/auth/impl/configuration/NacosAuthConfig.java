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

package com.alibaba.nacos.plugin.auth.impl.configuration;

import com.alibaba.nacos.auth.config.AuthConfigs;
import com.alibaba.nacos.common.utils.StringUtils;
import com.alibaba.nacos.core.code.ControllerMethodsCache;
import com.alibaba.nacos.plugin.auth.impl.CustomAuthenticationProvider;
import com.alibaba.nacos.plugin.auth.impl.JwtAuthenticationEntryPoint;
import com.alibaba.nacos.plugin.auth.impl.JwtTokenManager;
import com.alibaba.nacos.plugin.auth.impl.constant.AuthConstants;
import com.alibaba.nacos.plugin.auth.impl.constant.AuthSystemTypes;
import com.alibaba.nacos.plugin.auth.impl.filter.JwtAuthenticationTokenFilter;
import com.alibaba.nacos.plugin.auth.impl.users.NacosUserDetailsServiceImpl;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.ldap.LdapAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsUtils;

import java.util.Arrays;

/**
 * Spring security config.
 *
 * @author Nacos
 */
@Configuration(proxyBeanMethods = false)
@EnableAutoConfiguration(exclude = LdapAutoConfiguration.class)
//@EnableMethodSecurity
@EnableWebSecurity
@SuppressWarnings({"PMD.CommentsMustBeJavadocFormatRule", "PMD.RemoveCommentedCodeRule"})
public class NacosAuthConfig {
    
    private static final String SECURITY_IGNORE_URLS_SPILT_CHAR = ",";
    
    private static final String[] LOGIN_ENTRY_POINTS = {"/v1/auth/login", "/v1/auth/users/login"};
    
    private static final String TOKEN_BASED_AUTH_ENTRY_POINT = "/v1/auth/**";
    
    private static final String DEFAULT_ALL_PATH_PATTERN = "/**";
    
    private static final String PROPERTY_IGNORE_URLS = "nacos.security.ignore.urls";
    
    private final Environment env;
    
    private final JwtTokenManager tokenProvider;
    
    private final AuthConfigs authConfigs;
    
    private final ControllerMethodsCache methodsCache;
    
    public NacosAuthConfig(Environment env, JwtTokenManager tokenProvider, AuthConfigs authConfigs,
            NacosUserDetailsServiceImpl userDetailsService,
            
            ControllerMethodsCache methodsCache) {
        
        this.env = env;
        this.tokenProvider = tokenProvider;
        this.authConfigs = authConfigs;
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
    public ProviderManager providerManager(CustomAuthenticationProvider customAuthenticationProvider) {
        if (AuthConstants.AUTH_PLUGIN_TYPE.equalsIgnoreCase(authConfigs.getNacosAuthSystemType())) {
            return new ProviderManager(customAuthenticationProvider);
        } /*else if (AuthConstants.LDAP_AUTH_PLUGIN_TYPE.equalsIgnoreCase(authConfigs.getNacosAuthSystemType())) {
            return new ProviderManager(ldapAuthenticationProvider);
        }*/
        return new ProviderManager();
    }
    
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> {
            String ignoreUrls = null;
            if (AuthSystemTypes.NACOS.name().equalsIgnoreCase(authConfigs.getNacosAuthSystemType())) {
                ignoreUrls = DEFAULT_ALL_PATH_PATTERN;
            } else if (AuthSystemTypes.LDAP.name().equalsIgnoreCase(authConfigs.getNacosAuthSystemType())) {
                ignoreUrls = DEFAULT_ALL_PATH_PATTERN;
            }
            if (StringUtils.isBlank(authConfigs.getNacosAuthSystemType())) {
                ignoreUrls = env.getProperty(PROPERTY_IGNORE_URLS, DEFAULT_ALL_PATH_PATTERN);
            }
            if (StringUtils.isNotBlank(ignoreUrls)) {
                String[] strings = Arrays.stream(ignoreUrls.split(SECURITY_IGNORE_URLS_SPILT_CHAR)).map(String::trim)
                        .filter(StringUtils::isNotBlank).toArray(String[]::new);
                web.ignoring().requestMatchers(strings);
            }
        };
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authenticationManager)
            throws Exception {
        if (!StringUtils.isBlank(authConfigs.getNacosAuthSystemType())) {
            http.csrf().disable().cors()// We don't need CSRF for JWT based authentication
                    .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                    .authorizeHttpRequests().requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                    .requestMatchers(LOGIN_ENTRY_POINTS).permitAll().anyRequest().authenticated()
                    //.requestMatchers(TOKEN_BASED_AUTH_ENTRY_POINT).authenticated()
                    .and().authenticationManager(authenticationManager)
                    .addFilterBefore(new JwtAuthenticationTokenFilter(tokenProvider),
                            UsernamePasswordAuthenticationFilter.class).exceptionHandling()
                    .authenticationEntryPoint(new JwtAuthenticationEntryPoint()).and().headers().cacheControl();
        }
        return http.build();
    }
    
    @Bean
    public CustomAuthenticationProvider daoAuthenticationProvider(NacosUserDetailsServiceImpl userDetailsService) {
        return new CustomAuthenticationProvider(userDetailsService);
    }
}
