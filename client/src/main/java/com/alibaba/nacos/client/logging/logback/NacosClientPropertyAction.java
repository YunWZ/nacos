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

package com.alibaba.nacos.client.logging.logback;

import ch.qos.logback.core.joran.action.BaseModelAction;
import ch.qos.logback.core.joran.spi.SaxEventInterpretationContext;
import ch.qos.logback.core.model.Model;
import ch.qos.logback.core.model.NamedModel;
import com.alibaba.nacos.client.env.NacosClientProperties;
import org.xml.sax.Attributes;

/**
 * support logback read properties from NacosClientProperties. just like springProperty. for example:
 * <nacosClientProperty scope="context" name="logPath" source="system.log.path" defaultValue="/root" />
 *
 * @author onewe
 */
class NacosClientPropertyAction extends BaseModelAction {
    
    private static final String DEFAULT_VALUE_ATTRIBUTE = "defaultValue";
    
    private static final String SOURCE_ATTRIBUTE = "source";
    
    private String getValue(String source, String defaultValue) {
        return NacosClientProperties.PROTOTYPE.getProperty(source, defaultValue);
    }
    
    private String getValue(String source) {
        return NacosClientProperties.PROTOTYPE.getProperty(source);
    }
    
    @Override
    protected Model buildCurrentModel(SaxEventInterpretationContext interpretationContext, String name,
            Attributes attributes) {
        NacosClientPropertyModel model = new NacosClientPropertyModel();
        model.setName(attributes.getValue(NAME_ATTRIBUTE));
        model.setScope(attributes.getValue(SCOPE_ATTRIBUTE));
        model.setSource(getValue(attributes.getValue(SOURCE_ATTRIBUTE)));
        model.setDefaultValue(attributes.getValue(DEFAULT_VALUE_ATTRIBUTE));
        return model;
    }
    
    public class NacosClientPropertyModel extends NamedModel {
        
        private String scope;
        
        private String defaultValue;
        
        private String source;
        
        String getScope() {
            return this.scope;
        }
        
        void setScope(String scope) {
            this.scope = scope;
        }
        
        String getDefaultValue() {
            return this.defaultValue;
        }
        
        void setDefaultValue(String defaultValue) {
            this.defaultValue = defaultValue;
        }
        
        String getSource() {
            return this.source;
        }
        
        void setSource(String source) {
            this.source = source;
        }
        
    }
}