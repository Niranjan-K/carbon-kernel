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
package org.wso2.carbon.kernel.jmx.internal;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.kernel.jmx.connection.SingleAddressRMIServerSocketFactory;
import org.wso2.carbon.kernel.jmx.internal.config.JMXConfiguration;
import org.wso2.carbon.kernel.jmx.internal.config.YAMLJMXConfigurationBuilder;
import org.wso2.carbon.kernel.jmx.security.CarbonJMXAuthenticator;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.rmi.registry.LocateRegistry;
import java.util.HashMap;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;
import javax.management.remote.rmi.RMIConnectorServer;

/**
 * CarbonJMXComponent
 *
 * @since 1.0.0
 */
@org.osgi.service.component.annotations.Component(
        name = "org.wso2.carbon.jmx.internal.CarbonJMXComponent",
        immediate = true
)
public class CarbonJMXComponent {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(org.wso2.carbon.kernel.jmx.internal.CarbonJMXComponent.class);
    private final static String JAVA_RMI_SERVER_HOSTNAME = "java.rmi.server.hostname";

    /**
     * This is the activation method of CarbonJMXComponent. This will be called when all the references are
     * satisfied.
     *
     * @param bundleContext the bundle context instance of this bundle.
     */
    @org.osgi.service.component.annotations.Activate
    protected void start(org.osgi.framework.BundleContext bundleContext) {
        try {
            JMXConfiguration jmxConfiguration = YAMLJMXConfigurationBuilder.build();
            if (!jmxConfiguration.isEnabled()) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Remote JMX is disabled.");
                }
                return;
            }

            String hostname = System.getProperty(JAVA_RMI_SERVER_HOSTNAME);
            if (hostname == null || hostname.isEmpty()) {
                hostname = jmxConfiguration.getHostName();
                System.setProperty(JAVA_RMI_SERVER_HOSTNAME, hostname);
            }

            java.net.InetAddress[] inetAddresses = java.net.InetAddress.getAllByName(hostname);
            if (inetAddresses == null || inetAddresses.length == 0) {
                if (logger.isDebugEnabled()) {
                    logger.debug("No network interface available for '{}' to start JMXConnectorServer", hostname);
                }
                return;
            }

            SingleAddressRMIServerSocketFactory singleAddressRMIServerSocketFactory =
                    new SingleAddressRMIServerSocketFactory(inetAddresses[0]);
            java.rmi.registry.LocateRegistry.createRegistry(jmxConfiguration.getRmiRegistryPort(), null,
                    singleAddressRMIServerSocketFactory);

            String jmxURL = "service:jmx:rmi://" + hostname + ":" + jmxConfiguration.getRmiServerPort()
                    + "/jndi/rmi://" + hostname + ":" + jmxConfiguration.getRmiRegistryPort() + "/jmxrmi";
            javax.management.remote.JMXServiceURL jmxServiceURL = new javax.management.remote.JMXServiceURL(jmxURL);

            java.util.HashMap<String, Object> environment = new java.util.HashMap<>();
            environment.put(javax.management.remote.JMXConnectorServer.AUTHENTICATOR, new CarbonJMXAuthenticator());
            environment.put(javax.management.remote.rmi.RMIConnectorServer.RMI_SERVER_SOCKET_FACTORY_ATTRIBUTE,
                    singleAddressRMIServerSocketFactory);

            javax.management.remote.JMXConnectorServer jmxConnectorServer =
                    javax.management.remote.JMXConnectorServerFactory.newJMXConnectorServer(jmxServiceURL, environment,
                            java.lang.management.ManagementFactory.getPlatformMBeanServer());
            jmxConnectorServer.start();
            logger.info("JMXServerManager JMX Service URL : " + jmxServiceURL.toString());
        } catch (Throwable throwable) {
            logger.error("Failed to start CarbonJMXComponent.", throwable);
        }
    }

    /**
     * This is the deactivation method of CarbonJMXComponent. This will be called when this component
     * is being stopped or references are un-satisfied during runtime.
     *
     * @throws Exception this will be thrown if an issue occurs while executing the de-activate method
     */
    @org.osgi.service.component.annotations.Deactivate
    protected void stop() throws Exception {

    }
}
