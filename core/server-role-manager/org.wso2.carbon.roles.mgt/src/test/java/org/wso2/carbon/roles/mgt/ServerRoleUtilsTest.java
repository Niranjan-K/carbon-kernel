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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.wso2.carbon.base.ServerConfiguration;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Test Class for ServerRoles Utils.
 */
public class ServerRoleUtilsTest {

    private static final String basedir = Paths.get("").toAbsolutePath().toString();
    private static final String testDir = Paths.get(basedir, "src", "test", "resources").toString();

    @Before
    public void setup() throws Exception {
        String serverConfigPath = Paths.get(testDir, "carbon.xml").toString();
        ServerConfiguration.getInstance().forceInit(serverConfigPath);
    }

    @Test
    public void testReadProductServerRoles() {
        List<String> serverRoles = new ArrayList<>();
        serverRoles.add("CarbonServer");
        Assert.assertEquals(serverRoles, ServerRoleUtils.readProductServerRoles());
        System.setProperty(ServerRoleConstants.SERVER_ROLES_CMD_OPTION, "CarbonServer");
        Assert.assertEquals(serverRoles, ServerRoleUtils.readProductServerRoles());
        serverRoles.add("ApplicationServer");
        System.setProperty(ServerRoleConstants.SERVER_ROLES_CMD_OPTION, "CarbonServer,ApplicationServer");
        Assert.assertEquals(serverRoles, ServerRoleUtils.readProductServerRoles());
        System.clearProperty(ServerRoleConstants.SERVER_ROLES_CMD_OPTION);
    }

    @Test
    public void testListToArray() {
        Assert.assertNull(ServerRoleUtils.listToArray(null));
        Assert.assertNull(ServerRoleUtils.listToArray(new ArrayList<String>()));
        List<String> serverRoles = new ArrayList<>();
        serverRoles.add("CarbonServer");
        String[] serverRolesArray = new String[]{"CarbonServer"};
        Assert.assertEquals(serverRolesArray, ServerRoleUtils.listToArray(serverRoles));
    }

    @Test
    public void testArrayToList() {
        Assert.assertNull(ServerRoleUtils.arrayToList(null));
        Assert.assertNull(ServerRoleUtils.arrayToList(new String[0]));
        String[] serverRolesArray = new String[]{"CarbonServer"};
        List<String> serverRoles = new ArrayList<>();
        serverRoles.add("CarbonServer");
        Assert.assertEquals(serverRoles, ServerRoleUtils.arrayToList(serverRolesArray));
    }

    @Test
    public void testMergeLists() {
        List<String> list1 = Collections.singletonList("CarbonServer");
        List<String> list2 = Collections.singletonList("ApplicationServer");
        List<String> list3 = new ArrayList<>();
        list3.addAll(list2);
        list3.addAll(list1);
        Assert.assertNull(ServerRoleUtils.mergeLists(null, null));
        Assert.assertEquals(list1, ServerRoleUtils.mergeLists(null, list1));
        Assert.assertEquals(null, ServerRoleUtils.mergeLists(null, new ArrayList<String>()));
        Assert.assertEquals(null, ServerRoleUtils.mergeLists(new ArrayList<String>(), null));
        Assert.assertEquals(list1, ServerRoleUtils.mergeLists(new ArrayList<String>(), list1));
        Assert.assertEquals(list2, ServerRoleUtils.mergeLists(list2, null));
        Assert.assertEquals(list2, ServerRoleUtils.mergeLists(list2, new ArrayList<String>()));
        Assert.assertEquals(list3, ServerRoleUtils.mergeLists(list2, list1));
    }

    @Test
    public void testGetRegistryPath() {
        Assert.assertEquals("serverroles/default/", ServerRoleUtils.getRegistryPath(ServerRoleConstants.DEFAULT_ROLES_ID));
        Assert.assertEquals("serverroles/custom/", ServerRoleUtils.getRegistryPath(ServerRoleConstants.CUSTOM_ROLES_ID));
        Assert.assertNull(null, ServerRoleUtils.getRegistryPath("Random"));
    }
}
