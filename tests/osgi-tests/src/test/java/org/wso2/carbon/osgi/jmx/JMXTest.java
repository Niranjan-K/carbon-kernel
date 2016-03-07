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
package org.wso2.carbon.osgi.jmx;

import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.ops4j.pax.exam.testng.listener.PaxExam;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import javax.management.JMX;
import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;

@org.testng.annotations.Listeners(org.ops4j.pax.exam.testng.listener.PaxExam.class)
@org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy(org.ops4j.pax.exam.spi.reactors.PerClass.class)
public class JMXTest {
    @org.testng.annotations.Test
    public void testMBeanRegistration() throws Exception {
        JMXSample test = new JMXSample();
        javax.management.ObjectName mbeanName = new javax.management.ObjectName("org.wso2.carbon.osgi.jmx:type=JMXSample");
        javax.management.MBeanServer mBeanServer = java.lang.management.ManagementFactory.getPlatformMBeanServer();
        mBeanServer.registerMBean(test, mbeanName);

        org.testng.Assert.assertTrue(mBeanServer.isRegistered(mbeanName), "MBean is not registered");
    }

    @org.testng.annotations.Test(dependsOnMethods = {"testMBeanRegistration"})
    public void testAccessMBean() throws Exception {

        javax.management.remote.JMXServiceURL url = new javax.management.remote.JMXServiceURL("service:jmx:rmi://localhost:11111/jndi/rmi://localhost:9999/jmxrmi");
        java.util.Map<String, Object> environment = new java.util.HashMap<>();
        String[] credentials = {"admin", "password"};
        environment.put(javax.management.remote.JMXConnector.CREDENTIALS, credentials);
        javax.management.remote.JMXConnector jmxc = javax.management.remote.JMXConnectorFactory.connect(url, environment);
        javax.management.MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();

        javax.management.ObjectName mbeanName = new javax.management.ObjectName("org.wso2.carbon.osgi.jmx:type=JMXSample");
        JMXSampleMBean mbeanProxy = javax.management.JMX.newMBeanProxy(mbsc, mbeanName, JMXSampleMBean.class, true);

        org.testng.Assert.assertEquals(mbeanProxy.getCount(), 0, "Count is not zero");

        mbeanProxy.setCount(500);
        org.testng.Assert.assertEquals(mbeanProxy.getCount(), 500, "Count is not 500");

        mbeanProxy.reset();
        org.testng.Assert.assertEquals(mbeanProxy.getCount(), 0, "Count is not reset");
    }
}
