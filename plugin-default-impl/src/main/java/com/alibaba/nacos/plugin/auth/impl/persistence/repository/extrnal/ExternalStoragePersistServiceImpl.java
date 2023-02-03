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

package com.alibaba.nacos.plugin.auth.impl.persistence.repository.extrnal;

import com.alibaba.nacos.config.server.service.datasource.DynamicDataSource;
import com.alibaba.nacos.plugin.auth.impl.persistence.repository.PaginationHelper;
import com.alibaba.nacos.plugin.datasource.MapperManager;
import com.alibaba.nacos.sys.env.EnvUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import java.io.IOException;

/**
 * ExternalStoragePersistServiceImpl.
 *
 * @author Weizhan▪Yun
 * @date 2023/2/4 16:39
 */
@Deprecated
public class ExternalStoragePersistServiceImpl {
    
    private DataSourceService dataSourceService;
    
    protected JdbcTemplate jt;
    
    protected TransactionTemplate tjt;
    
    private MapperManager mapperManager;
    
    
    /**
     * init datasource.
     */
    @PostConstruct
    public void init() {
        dataSourceService = DynamicDataSource.getInstance().getDataSource();
        
        jt = getJdbcTemplate();
        tjt = getTransactionTemplate();
        Boolean isDataSourceLogEnable = EnvUtil.getProperty(Constants.NACOS_PLUGIN_DATASOURCE_LOG, Boolean.class,
                false);
        mapperManager = MapperManager.instance(isDataSourceLogEnable);
    }
    
    public synchronized void reload() throws IOException {
        this.dataSourceService.reload();
    }
    
    /**
     * For unit testing.
     */
    public JdbcTemplate getJdbcTemplate() {
        return this.dataSourceService.getJdbcTemplate();
    }
    
    public TransactionTemplate getTransactionTemplate() {
        return this.dataSourceService.getTransactionTemplate();
    }
    
    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public String getCurrentDBUrl() {
        return this.dataSourceService.getCurrentDbUrl();
    }
    
    public <E> PaginationHelper<E> createPaginationHelper() {
        return new ExternalStoragePaginationHelperImpl<>(jt);
    }
    
    
}

