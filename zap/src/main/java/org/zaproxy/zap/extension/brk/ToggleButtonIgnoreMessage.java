/*
 * Zed Attack Proxy (ZAP) and its related class files.
 *
 * ZAP is an HTTP/HTTPS proxy for assessing web application security.
 *
 * Copyright 2012 The ZAP Development Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.zaproxy.zap.extension.brk;

import javax.swing.*;
import org.apache.commons.httpclient.URIException;
import org.parosproxy.paros.network.HttpMessage;
import org.zaproxy.zap.extension.httppanel.Message;

public class ToggleButtonIgnoreMessage implements BreakpointMessageInterface {

    private static final String HTTP_TYPE = "HTTP";

    private JToggleButton ignoreButton;
    private static final String TYPE = "ToggleButton";
    private String[] fileExtensions;

    public ToggleButtonIgnoreMessage(JToggleButton ignoreButton, String[] fileExtensions) {

        this.ignoreButton = ignoreButton;
        this.fileExtensions = fileExtensions;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public boolean match(Message aMessage, boolean isRequest, boolean onlyIfInScope) {

        if (isEnabled() && aMessage.getType().equals(HTTP_TYPE)) {
            String path = null;
            try {
                path = ((HttpMessage) aMessage).getRequestHeader().getURI().getPath();

                for (String extension : this.fileExtensions) {
                    if (path.endsWith(extension)) {
                        return true;
                    }
                }
            } catch (URIException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.ignoreButton.setEnabled(enabled);
    }

    @Override
    public boolean isEnabled() {
        return ignoreButton.isEnabled();
    }

    @Override
    public String getDisplayMessage() {
        return null;
    }
}
