/*
 *  Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.wso2.carbon.roles.mgt;

import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.wso2.carbon.base.ServerConfiguration;
import org.wso2.carbon.context.CarbonContext;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.context.internal.OSGiDataHolder;
import org.wso2.carbon.registry.api.RegistryService;
import org.wso2.carbon.registry.core.Resource;
import org.wso2.carbon.registry.core.ResourceImpl;
import org.wso2.carbon.registry.core.session.UserRegistry;

import java.io.File;
import java.nio.file.Paths;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test Class for ServerRoles Manager.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({OSGiDataHolder.class})
public class ServerRolesManagerTest extends TestCase {

    private static final String basedir = Paths.get("").toAbsolutePath().toString();
    private static final String testDir = Paths.get(basedir, "src", "test", "resources").toString();
    private int tenantID = -1234;
    private String username = "wso2.system.user";
    private RegistryService registryService;

    @Before
    public void setup() throws Exception {
        if (System.getProperty("carbon.home") == null) {
            File file = new File("../../../distribution/kernel/carbon-home");
            if (file.exists()) {
                System.setProperty("carbon.home", file.getAbsolutePath());
            }
        }
        String serverConfigPath = Paths.get(testDir, "carbon.xml").toString();
        ServerConfiguration.getInstance().forceInit(serverConfigPath);
        PrivilegedCarbonContext.getThreadLocalCarbonContext().setTenantId(tenantID);

        registryService = mock(RegistryService.class);
        OSGiDataHolder osGiDataHolder = PowerMockito.mock(OSGiDataHolder.class);
        PowerMockito.mockStatic(OSGiDataHolder.class);
        PowerMockito.when(osGiDataHolder.getRegistryService()).thenReturn(registryService);
        Whitebox.setInternalState(CarbonContext.class, "dataHolder", osGiDataHolder);
    }

    @Test
    public void testReadServerRoles() throws Exception {

        ServerRolesManagerService serverRolesManager = new ServerRolesManager();
        UserRegistry userRegistry = mock(UserRegistry.class);
        when(userRegistry.getUserName()).thenReturn(username);
        when(userRegistry.getTenantId()).thenReturn(tenantID);
        when(registryService.getConfigSystemRegistry(tenantID)).thenReturn(userRegistry);
        Resource defaultResource = new ResourceImpl();

        when(userRegistry.get(ServerRoleConstants.DEFAULT_ROLES_PATH)).thenReturn(defaultResource);
        when(userRegistry.resourceExists(ServerRoleConstants.DEFAULT_ROLES_PATH)).thenReturn(true);
        Assert.assertArrayEquals(new String[]{"CarbonServer"}, serverRolesManager.readServerRoles(ServerRoleConstants
                .DEFAULT_ROLES_ID));

        defaultResource.setProperty(ServerRoleConstants.MODIFIED_TAG, ServerRoleConstants.MODIFIED_TAG_FALSE);
        Assert.assertArrayEquals(new String[]{"CarbonServer"}, serverRolesManager.readServerRoles(ServerRoleConstants
                .DEFAULT_ROLES_ID));

        defaultResource.setProperty(ServerRoleConstants.MODIFIED_TAG, ServerRoleConstants.MODIFIED_TAG_TRUE);
        Assert.assertArrayEquals(new String[]{"CarbonServer"}, serverRolesManager.readServerRoles(ServerRoleConstants
                .DEFAULT_ROLES_ID));

        when(userRegistry.get(ServerRoleConstants.DEFAULT_ROLES_PATH)).thenReturn(null);
        when(userRegistry.newResource()).thenReturn(defaultResource);
        Assert.assertArrayEquals(new String[]{"CarbonServer"}, serverRolesManager.readServerRoles(ServerRoleConstants
                .DEFAULT_ROLES_ID));

        Assert.assertArrayEquals(new String[0], serverRolesManager.readServerRoles(ServerRoleConstants
                .CUSTOM_ROLES_ID));

    }
}
