/*
 * Copyright 1999-2023 Alibaba Group Holding Ltd.
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

package com.alibaba.nacos.core.auth;

import com.alibaba.nacos.auth.HttpProtocolAuthService;
import com.alibaba.nacos.auth.annotation.Secured;
import com.alibaba.nacos.auth.config.NacosAuthConfig;
import com.alibaba.nacos.auth.serveridentity.ServerIdentityResult;
import com.alibaba.nacos.common.utils.ExceptionUtil;
import com.alibaba.nacos.core.code.ControllerMethodsCache;
import com.alibaba.nacos.core.context.RequestContext;
import com.alibaba.nacos.core.context.RequestContextHolder;
import com.alibaba.nacos.core.utils.Loggers;
import com.alibaba.nacos.plugin.auth.api.IdentityContext;
import com.alibaba.nacos.plugin.auth.api.Permission;
import com.alibaba.nacos.plugin.auth.api.Resource;
import com.alibaba.nacos.plugin.auth.exception.AccessException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;

/**
 * Abstract Auth filter.
 *
 * @author xiweng.yy
 */
public abstract class AbstractWebAuthFilter implements Filter {
    
    private final ControllerMethodsCache methodsCache;
    
    private final HttpProtocolAuthService protocolAuthService;
    
    protected AbstractWebAuthFilter(NacosAuthConfig authConfig, ControllerMethodsCache methodsCache) {
        this.methodsCache = methodsCache;
        this.protocolAuthService = new HttpProtocolAuthService(authConfig);
        this.protocolAuthService.initialize();
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (!isAuthEnabled()) {
            chain.doFilter(request, response);
            return;
        }
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        Method method = methodsCache.getMethod(req);
        if (method == null) {
            chain.doFilter(request, response);
            return;
        }
        if (!method.isAnnotationPresent(Secured.class)) {
            chain.doFilter(request, response);
            return;
        }
        try {
            if (Loggers.AUTH.isDebugEnabled()) {
                Loggers.AUTH.debug("auth start, request: {} {}", req.getMethod(), req.getRequestURI());
            }
            
            Secured secured = method.getAnnotation(Secured.class);
            
            ServerIdentityResult serverIdentityResult = checkServerIdentity(req, secured);
            switch (serverIdentityResult.getStatus()) {
                case FAIL:
                    resp.sendError(HttpServletResponse.SC_FORBIDDEN, serverIdentityResult.getMessage());
                    return;
                case MATCHED:
                    chain.doFilter(request, response);
                    return;
                default:
                    break;
            }
            
            if (!protocolAuthService.enableAuth(secured)) {
                chain.doFilter(request, response);
                return;
            }
            Resource resource = protocolAuthService.parseResource(req, secured);
            IdentityContext identityContext = protocolAuthService.parseIdentity(req);
            boolean result = protocolAuthService.validateIdentity(identityContext, resource);
            RequestContext requestContext = RequestContextHolder.getContext();
            requestContext.getAuthContext().setIdentityContext(identityContext);
            requestContext.getAuthContext().setResource(resource);
            if (null == requestContext.getAuthContext().getAuthResult()) {
                requestContext.getAuthContext().setAuthResult(result);
            }
            if (!result) {
                // TODO Get reason of failure
                throw new AccessException("Validate Identity failed.");
            }
            String action = secured.action().toString();
            result = protocolAuthService.validateAuthority(identityContext, new Permission(resource, action));
            if (!result) {
                // TODO Get reason of failure
                throw new AccessException("Validate Authority failed.");
            }
            chain.doFilter(request, response);
        } catch (AccessException e) {
            if (Loggers.AUTH.isDebugEnabled()) {
                Loggers.AUTH.debug("access denied, request: {} {}, reason: {}", req.getMethod(), req.getRequestURI(),
                        e.getErrMsg());
            }
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, e.getErrMsg());
        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, ExceptionUtil.getAllExceptionMsg(e));
        } catch (Exception e) {
            Loggers.AUTH.warn("[AUTH-FILTER] Server failed: ", e);
            
        }
    }
    
    protected ServerIdentityResult checkServerIdentity(HttpServletRequest request, Secured secured) {
        return protocolAuthService.checkServerIdentity(request, secured);
    }
    
    /**
     * Whether this auth filter is enabled.
     *
     * @return get value from {@link NacosAuthConfig#isAuthEnabled()}
     */
    protected abstract boolean isAuthEnabled();
}