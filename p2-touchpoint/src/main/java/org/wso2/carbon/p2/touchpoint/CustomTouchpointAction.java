/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.carbon.p2.touchpoint;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.equinox.p2.engine.spi.ProvisioningAction;

import java.util.Map;

/**
 * CustomTouchpointAction class.
 */
public class CustomTouchpointAction extends ProvisioningAction {

    @Override
    public IStatus execute(Map<String, Object> parameters) {
        return Status.OK_STATUS;
    }

    @Override
    public IStatus undo(Map<String, Object> parameters) {
        return Status.OK_STATUS;
    }
}
