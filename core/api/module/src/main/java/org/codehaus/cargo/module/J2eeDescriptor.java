/*
 * ========================================================================
 *
 * Copyright 2003-2004 The Apache Software Foundation. Code from this file
 * was originally imported from the Jakarta Cactus project.
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
package org.codehaus.cargo.module;

import java.util.List;

/**
 * Common interface for all standard J2EE deployment descriptors (<code>web.xml</code>,
 * <code>ejb-jar.xml</code>, etc).
 */
public interface J2eeDescriptor extends Descriptor
{
    /**
     * Returns all vendor descriptors associated with this J2EE deployment descriptor. For example
     * for JBoss the vendor descriptor associated with <code>web.xml</code> is
     * <code>jboss-web.xml</code>.
     * 
     * @return List of Descriptor objects
     */
    List<Descriptor> getVendorDescriptors();
}
