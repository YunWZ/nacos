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

import com.alibaba.nacos.plugin.auth.impl.persistence.model.Page;
import com.alibaba.nacos.plugin.auth.impl.persistence.repository.PaginationHelper;
import com.alibaba.nacos.plugin.auth.impl.persistence.sql.EmbeddedStorageContextUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;

/**
 * ExternalStoragePaginationHelperImpl.
 *
 * @author Weizhan▪Yun
 * @date 2023/2/5 11:48
 */

class ExternalStoragePaginationHelperImpl<E> implements PaginationHelper {
    
    private final JdbcTemplate jdbcTemplate;
    
    public ExternalStoragePaginationHelperImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    /**
     * Take paging.
     *
     * @param sqlCountRows query total SQL
     * @param sqlFetchRows query data sql
     * @param args         query parameters
     * @param pageNo       page number
     * @param pageSize     page size
     * @param rowMapper    {@link RowMapper}
     * @return Paginated data {@code <E>}
     */
    @Override
    public Page<E> fetchPage(final String sqlCountRows, final String sqlFetchRows, final Object[] args,
            final int pageNo, final int pageSize, final RowMapper rowMapper) {
        return fetchPage(sqlCountRows, sqlFetchRows, args, pageNo, pageSize, null, rowMapper);
    }
    
    @Override
    public Page<E> fetchPage(final String sqlCountRows, final String sqlFetchRows, final Object[] args,
            final int pageNo, final int pageSize, final Long lastMaxId, final RowMapper rowMapper) {
        if (pageNo <= 0 || pageSize <= 0) {
            throw new IllegalArgumentException("pageNo and pageSize must be greater than zero");
        }
        
        // Query the total number of current records.
        Integer rowCountInt = jdbcTemplate.queryForObject(sqlCountRows, args, Integer.class);
        if (rowCountInt == null) {
            throw new IllegalArgumentException("fetchPageLimit error");
        }
        
        // Compute pages count
        int pageCount = rowCountInt / pageSize;
        if (rowCountInt > pageSize * pageCount) {
            pageCount++;
        }
        
        // Create Page object
        final Page<E> page = new Page<>();
        page.setPageNumber(pageNo);
        page.setPagesAvailable(pageCount);
        page.setTotalCount(rowCountInt);
        
        if (pageNo > pageCount) {
            return page;
        }
        
        List<E> result = jdbcTemplate.query(sqlFetchRows, args, rowMapper);
        for (E item : result) {
            page.getPageItems().add(item);
        }
        return page;
    }
    
    @Override
    public Page<E> fetchPageLimit(final String sqlCountRows, final String sqlFetchRows, final Object[] args,
            final int pageNo, final int pageSize, final RowMapper rowMapper) {
        if (pageNo <= 0 || pageSize <= 0) {
            throw new IllegalArgumentException("pageNo and pageSize must be greater than zero");
        }
        // Query the total number of current records
        Integer rowCountInt = jdbcTemplate.queryForObject(sqlCountRows, Integer.class);
        if (rowCountInt == null) {
            throw new IllegalArgumentException("fetchPageLimit error");
        }
        
        // Compute pages count
        int pageCount = rowCountInt / pageSize;
        if (rowCountInt > pageSize * pageCount) {
            pageCount++;
        }
        
        // Create Page object
        final Page<E> page = new Page<>();
        page.setPageNumber(pageNo);
        page.setPagesAvailable(pageCount);
        page.setTotalCount(rowCountInt);
        
        if (pageNo > pageCount) {
            return page;
        }
        
        List<E> result = jdbcTemplate.query(sqlFetchRows, args, rowMapper);
        for (E item : result) {
            page.getPageItems().add(item);
        }
        return page;
    }
    
    @Override
    public Page<E> fetchPageLimit(final String sqlCountRows, final Object[] args1, final String sqlFetchRows,
            final Object[] args2, final int pageNo, final int pageSize, final RowMapper rowMapper) {
        if (pageNo <= 0 || pageSize <= 0) {
            throw new IllegalArgumentException("pageNo and pageSize must be greater than zero");
        }
        // Query the total number of current records
        Integer rowCountInt = jdbcTemplate.queryForObject(sqlCountRows, args1, Integer.class);
        if (rowCountInt == null) {
            throw new IllegalArgumentException("fetchPageLimit error");
        }
        
        // Compute pages count
        int pageCount = rowCountInt / pageSize;
        if (rowCountInt > pageSize * pageCount) {
            pageCount++;
        }
        
        // Create Page object
        final Page<E> page = new Page<>();
        page.setPageNumber(pageNo);
        page.setPagesAvailable(pageCount);
        page.setTotalCount(rowCountInt);
        
        if (pageNo > pageCount) {
            return page;
        }
        List<E> result = jdbcTemplate.query(sqlFetchRows, args2, rowMapper);
        for (E item : result) {
            page.getPageItems().add(item);
        }
        return page;
    }
    
    @Override
    public Page<E> fetchPageLimit(final String sqlFetchRows, final Object[] args, final int pageNo, final int pageSize,
            final RowMapper rowMapper) {
        if (pageNo <= 0 || pageSize <= 0) {
            throw new IllegalArgumentException("pageNo and pageSize must be greater than zero");
        }
        // Create Page object
        final Page<E> page = new Page<>();
        List<E> result = jdbcTemplate.query(sqlFetchRows, args, rowMapper);
        for (E item : result) {
            page.getPageItems().add(item);
        }
        return page;
    }
    
    @Override
    public void updateLimit(final String sql, final Object[] args) {
        try {
            jdbcTemplate.update(sql, args);
        } finally {
            EmbeddedStorageContextUtils.cleanAllContext();
        }
    }
    
    public int updateLimitWithResponse(final String sql, final Object[] args) {
        String sqlUpdate = sql;
        
        try {
            return jdbcTemplate.update(sqlUpdate, args);
        } finally {
            EmbeddedStorageContextUtils.cleanAllContext();
        }
    }
    
}


