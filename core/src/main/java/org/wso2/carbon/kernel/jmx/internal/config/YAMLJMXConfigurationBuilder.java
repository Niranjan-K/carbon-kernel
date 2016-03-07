/*
 *  Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.wso2.carbon.kernel.jmx.internal.config;

import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

public class YAMLJMXConfigurationBuilder {
    public static JMXConfiguration build() throws JMXConfigurationException {
        java.nio.file.Path jmxConfigurationFilePath = java.nio.file.Paths.get(System.getProperty("carbon.home"), "conf", "jmx.yaml");
        try (java.io.Reader reader = new java.io.InputStreamReader(new java.io.FileInputStream(jmxConfigurationFilePath.toFile()),
                java.nio.charset.StandardCharsets.ISO_8859_1)) {
            org.yaml.snakeyaml.Yaml yaml = new org.yaml.snakeyaml.Yaml();
            JMXConfiguration jmxConfiguration = yaml.loadAs(reader, JMXConfiguration.class);
            return jmxConfiguration;
        } catch (java.io.IOException e) {
            throw new JMXConfigurationException("Failed to read the jmx.yaml file at [CARBON_HOME]/conf/jmx/jmx.yaml");
        }
    }
}
