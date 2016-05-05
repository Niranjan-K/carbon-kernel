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

package org.wso2.carbon.p2.touchpoint.action;


import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.equinox.p2.engine.spi.ProvisioningAction;
import org.wso2.carbon.p2.touchpoint.utils.Constants;
import org.wso2.carbon.p2.touchpoint.utils.Utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Properties;

/**
 * Merges two properties file during feature installation.
 *
 * @since 5.1.0
 */
public class MergePropertiesAction extends ProvisioningAction {

    @Override
    public IStatus execute(Map<String, Object> parameters) {

        String targetFile = (String) parameters.get(Constants.PARAM_TARGET_FILE);
        String sourceFile = (String) parameters.get(Constants.PARAM_SOURCE_FILE);

        if (sourceFile == null) {
            return Utils.createError(Constants.PARAM_SOURCE_FILE + " parameter is not set.");
        } else if (Files.notExists(Paths.get(sourceFile))) {
            return Utils.createError(Constants.PARAM_SOURCE_FILE + " does not exist : " + sourceFile);
        }
        if (targetFile == null) {
            return Utils.createError(Constants.PARAM_TARGET_FILE + " parameter is not set.");
        } else if (Files.notExists(Paths.get(targetFile))) {
            return Utils.createError(Constants.PARAM_TARGET_FILE + " does not exist : " + targetFile);
        }

        try (InputStream targetInputStream = new FileInputStream(Paths.get(targetFile).toString());
             InputStream sourceInputStream = new FileInputStream(Paths.get(sourceFile).toString())) {
            Properties targetProperties = new Properties();
            Properties sourceProperties = new Properties();
            targetProperties.load(targetInputStream);
            sourceProperties.load(sourceInputStream);

            sourceProperties.entrySet()
                    .forEach(property -> {
                        if (targetProperties.getProperty(property.getKey().toString()) != null) {
                            String newValue = targetProperties.getProperty(property.getKey().toString()).concat(",")
                                    .concat(property.getValue().toString());
                            targetProperties.setProperty(property.getKey().toString(), newValue);
                        } else {
                            targetProperties.setProperty(property.getKey().toString(), property.getValue().toString());
                        }
                    });

            try (OutputStream outputStream = new FileOutputStream(targetFile)) {
                targetProperties.store(outputStream, null);
            }

        } catch (IOException e) {
            return Utils.createError("Failed to merge source file with target.");
        }
        return Status.OK_STATUS;
    }

    @Override
    public IStatus undo(Map<String, Object> parameters) {
        return Status.OK_STATUS;
    }

}
