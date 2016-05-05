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
 *
 */

package org.wso2.carbon.p2.touchpoint.utils;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * Carbon P2 Touchpoint utility methods.
 *
 * @since 5.1.0
 */
public class Utils {

    /**
     * Returns an Object with a message and status as ERROR
     *
     * @param message   Error message that should be returned
     * @return          Status Object with Severity as Error
     */
    public static IStatus createError(String message) {
        return new Status(IStatus.ERROR, Constants.PLUGIN_ID, message);
    }
}
