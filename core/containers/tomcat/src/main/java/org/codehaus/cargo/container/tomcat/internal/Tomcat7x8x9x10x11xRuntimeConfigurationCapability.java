/*
 * ========================================================================
 *
 * Codehaus Cargo, copyright 2004-2011 Vincent Massol, 2012-2023 Ali Tokmen.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ========================================================================
 */
package org.codehaus.cargo.container.tomcat.internal;

import org.codehaus.cargo.container.tomcat.TomcatPropertySet;

/**
 * Capabilities of Tomcat's runtime configuration (versions 7.x onwards).
 */
public class Tomcat7x8x9x10x11xRuntimeConfigurationCapability extends
    Tomcat4x5x6xRuntimeConfigurationCapability
{
    /**
     * Initialize the configuration-specific supports Map.
     */
    public Tomcat7x8x9x10x11xRuntimeConfigurationCapability()
    {
        super();

        this.propertySupportMap.put(TomcatPropertySet.UNDEPLOY_ALL_VERSIONS, Boolean.TRUE);
    }
}
