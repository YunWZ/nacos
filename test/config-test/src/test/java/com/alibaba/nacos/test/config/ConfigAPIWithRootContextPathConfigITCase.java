/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
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

package com.alibaba.nacos.test.config;

import com.alibaba.nacos.NacosConsole;
import com.alibaba.nacos.sys.env.EnvUtil;
import com.alibaba.nacos.test.base.ConfigCleanUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;

/**
 * Test context path is '/'.
 *
 * @see <a href="https://github.com/alibaba/nacos/issues/4181">#4171</a>
 */
@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = NacosConsole.class, properties = {"server.servlet.context-path=/",
        "server.port=7001"}, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class ConfigAPIWithRootContextPathConfigITCase extends AbstractConfigAPIConfigITCase {

    @BeforeAll
    static void beforeClass() throws IOException {
        ConfigCleanUtils.changeToNewTestNacosHome(ConfigAPIWithRootContextPathConfigITCase.class.getSimpleName());
        ConfigCleanUtils.cleanClientCache();
        EnvUtil.setPort(7001);

    }

    @AfterAll
    static void cleanClientCache() throws Exception {
        ConfigCleanUtils.cleanClientCache();
        EnvUtil.setPort(8848);
    }
}
