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

package com.alibaba.nacos.plugin.auth.impl.persistence.sql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * EmbeddedStorageContextUtils.
 *
 * @author Weizhan▪Yun
 * @date 2023/2/5 12:36
 */
public class EmbeddedStorageContextUtils {
    
    private static final ThreadLocal<ArrayList<ModifyRequest>> SQL_CONTEXT = ThreadLocal.withInitial(ArrayList::new);
    
    private static final ThreadLocal<Map<String, String>> EXTEND_INFO_CONTEXT = ThreadLocal.withInitial(HashMap::new);
    
    /**
     * Add sql context.
     *
     * @param sql  sql
     * @param args argument list
     */
    public static void addSqlContext(String sql, Object... args) {
        ArrayList<ModifyRequest> requests = SQL_CONTEXT.get();
        ModifyRequest context = new ModifyRequest();
        context.setExecuteNo(requests.size());
        context.setSql(sql);
        context.setArgs(args);
        requests.add(context);
        SQL_CONTEXT.set(requests);
    }
    
    public static List<ModifyRequest> getCurrentSqlContext() {
        return SQL_CONTEXT.get();
    }
    
    public static void cleanAllContext() {
        SQL_CONTEXT.remove();
        EXTEND_INFO_CONTEXT.remove();
    }
    
}


