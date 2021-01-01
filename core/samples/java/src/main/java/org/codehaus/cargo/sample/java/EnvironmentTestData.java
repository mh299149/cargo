/*
 * ========================================================================
 *
 * Codehaus CARGO, copyright 2004-2011 Vincent Massol, 2012-2021 Ali Tokmen.
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
package org.codehaus.cargo.sample.java;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.codehaus.cargo.container.ContainerException;
import org.codehaus.cargo.container.ContainerType;
import org.codehaus.cargo.container.installer.Proxy;

/**
 * Groups together all environmental test data (i.e. data that depends on how the user has
 * configured its tests to run in Maven).
 */
public class EnvironmentTestData
{
    /**
     * Containers that can only use Jakarta EE deployables.
     */
    public static final List<String> JAKARTA_EE_CONTAINERS = Arrays.asList(new String[]
    {
        "jetty11x",
        "glassfish6x",
        "tomcat10x",
        "tomee9x"
    });

    /**
     * Name of container to run (this is the container ID, see
     * {@link org.codehaus.cargo.container.Container#getId()}. We use it guess the XML name of the
     * Ant task.
     */
    public String containerId;

    /**
     * The container type (local, remote, etc)
     */
    public ContainerType containerType;

    /**
     * URL where the zipped container is located.
     */
    public URL installURL;

    /**
     * Location where to put the zipped distributions.
     */
    public String downloadDir;

    /**
     * Location where to extract the zipped distributions.
     */
    public String extractDir;

    /**
     * Port on which to start the web container.
     */
    public int port;

    /**
     * Port on which to start RMI.
     */
    public int rmiPort;

    /**
     * Timeout to test if a container is correctly started or stopped.
     */
    public long containerTimeout;

    /**
     * Target directory where build results are stored and where container execution will happen.
     */
    public String targetDir;

    /**
     * Home for the already installed container (in that case extractDir and installURL are
     * ignored).
     */
    public String home;

    /**
     * Java Home used to start the container.
     */
    public String javaHome;

    /**
     * Proxy properties if defined (can be null).
     */
    public Proxy proxy;

    /**
     * Test data artifacts.
     */
    public Map<String, String> testDataArtifacts = new HashMap<String, String>();

    /**
     * @param containerId the container's name (eg "resin3x")
     * @param containerType the container's type
     * @param targetDirSuffix relative directory from the base target dir where to put
     * test-generated files
     */
    public EnvironmentTestData(String containerId, ContainerType containerType,
        String targetDirSuffix)
    {
        this.containerId = containerId;
        this.containerType = containerType;
        this.targetDir = new File(getFileFromString(
            getSystemProperty("cargo.target.dir")), targetDirSuffix).getPath();
        this.downloadDir = getSystemProperty("cargo.download.dir");
        this.extractDir = new File(getFileFromString(
            getSystemProperty("cargo.target.dir")), "cargo/container").getPath();
        this.proxy = createProxyElement();
        this.installURL = createInstallURL(containerId);
        this.port = createPort(containerId, "servlet", 8080);
        this.rmiPort = createPort(containerId, "rmi", 1099);
        this.home = getSystemProperty("cargo." + containerId + ".home");
        this.javaHome = getSystemProperty("cargo." + containerId + ".java.home");
        this.containerTimeout =
            Long.parseLong(getSystemProperty("cargo.containers.timeout", "60000"));

        String deployablesLocation = System.getProperty("cargo.testdata.deployables");
        if (deployablesLocation == null)
        {
            throw new ContainerException("Property cargo.testdata.deployables not set");
        }
        File deployables = new File(deployablesLocation);
        if (!deployables.isDirectory())
        {
            throw new ContainerException(
                "Property cargo.testdata.deployables does not point to a directory: "
                    + deployables);
        }
        if (EnvironmentTestData.JAKARTA_EE_CONTAINERS.contains(containerId))
        {
            // CARGO-1514: Add the Jakarta EE converter to samples of affected containers
            File convertedDeployables =
                new File(deployables.getParentFile(), "deployables-jakarta-ee");

            if (!convertedDeployables.isDirectory())
            {
                // Use the Jakarta EE migrator using reflection to avoid "polluting" embedded
                // containers' class loaders during our tests
                File jakartaEeMigratorFile =
                    new File(System.getProperty("cargo.testdata.test-jars"),
                        "jakartaee-migration-tool.jar");
                if (!jakartaEeMigratorFile.isFile())
                {
                    throw new RuntimeException(
                        "Cannot find the Jakarta EE converter " + jakartaEeMigratorFile);
                }
                try
                {
                    URL[] jakartaEeMigratorUrl = new URL[1];
                    jakartaEeMigratorUrl[0] = jakartaEeMigratorFile.toURI().toURL();
                    ClassLoader jakartaEeMigratorClassLoader =
                        new URLClassLoader(jakartaEeMigratorUrl);
                    Class jakartaEeMigratorClass =
                        jakartaEeMigratorClassLoader.loadClass(
                            "org.apache.tomcat.jakartaee.Migration");
                    Object jakartaEeMigrator =
                        jakartaEeMigratorClass.getConstructor().newInstance();
                    Class eeSpecProfileClass =
                        jakartaEeMigratorClassLoader.loadClass(
                            "org.apache.tomcat.jakartaee.EESpecProfile");
                    Method setEESpecProfile =
                        jakartaEeMigratorClass.getMethod("setEESpecProfile", eeSpecProfileClass);
                    // The Jakarta EE migration tool cannot convert JMS resources to Jakarta EE yet:
                    // https://github.com/apache/tomcat-jakartaee-migration/issues/6
                    Object eeSpecProfile = eeSpecProfileClass.getField("EE").get(null);
                    Field patternField = eeSpecProfile.getClass().getDeclaredField("pattern");
                    patternField.setAccessible(true);
                    Pattern pattern = (Pattern) patternField.get(eeSpecProfile);
                    String patternString = pattern.pattern();
                    if (!patternString.contains("|jms|"))
                    {
                        patternString = patternString.replace(
                            "|enterprise|json|", "|enterprise|jms|json|");
                        pattern = Pattern.compile(patternString);
                        patternField.set(eeSpecProfile, pattern);
                    }
                    // End of workaround for the Jakarta EE migration tool's JMS-related issue
                    setEESpecProfile.invoke(jakartaEeMigrator, eeSpecProfile);
                    Method setSource = jakartaEeMigratorClass.getMethod("setSource", File.class);
                    Method setDestination =
                        jakartaEeMigratorClass.getMethod("setDestination", File.class);
                    Method execute = jakartaEeMigratorClass.getMethod("execute");

                    convertedDeployables.mkdir();
                    for (File deployable : deployables.listFiles())
                    {
                        setSource.invoke(jakartaEeMigrator, deployable);
                        setDestination.invoke(jakartaEeMigrator,
                            new File(convertedDeployables, deployable.getName()));
                        execute.invoke(jakartaEeMigrator);
                    }
                }
                catch (Exception e)
                {
                    throw new RuntimeException("Cannot convert deployable to Jakarta EE", e);
                }
            }

            deployablesLocation = convertedDeployables.getAbsolutePath();
            deployables = convertedDeployables;
        }
        for (File deployable : deployables.listFiles())
        {
            if (deployable.isFile())
            {
                String name = deployable.getName();
                if (name.contains("."))
                {
                    name = name.substring(0, name.lastIndexOf('.'));
                }
                this.testDataArtifacts.put(name, deployable.getAbsolutePath());
            }
        }
    }

    /**
     * @param containerId the container's id
     * @param type Port type
     * @param defaultValue Default value
     * @return the port to use for the specified container
     */
    private int createPort(String containerId, String type, int defaultValue)
    {
        String portString = getSystemProperty("cargo." + containerId + "." + type + ".port");
        if (portString == null)
        {
            portString = getSystemProperty("cargo." + type + ".port");
        }
        if (portString == null)
        {
            return defaultValue;
        }
        return Integer.parseInt(portString);
    }

    /**
     * @param containerName the container's name
     * @return the install URL for the specified container
     */
    private URL createInstallURL(String containerName)
    {
        URL installURL = null;
        String url = getSystemProperty("cargo." + containerName + ".url");
        if (url != null)
        {
            try
            {
                installURL = new URL(url);
            }
            catch (MalformedURLException e)
            {
                throw new ContainerException("invalid install URL [" + url + "]", e);
            }
        }
        return installURL;
    }

    /**
     * @param name the property name
     * @return the System property for the specified name or null if the System property does not
     * exist or is empty
     */
    private String getSystemProperty(String name)
    {
        String result = System.getProperty(name);
        if (result == null || result.isEmpty())
        {
            result = null;
        }
        return result;
    }

    /**
     * @param name the property name
     * @param defaultValue the default value to return if the System property does not exist or is
     * empty
     * @return the System property for the specified name or the default value
     */
    private String getSystemProperty(String name, String defaultValue)
    {
        String result = System.getProperty(name);
        return result == null || result.isEmpty() ? defaultValue : result;
    }

    /**
     * @param fileName the file name as a string
     * @return a File object wrapping the files passed as parameter or null if the file name is null
     */
    private File getFileFromString(String fileName)
    {
        File result = null;
        if (fileName != null)
        {
            result = new File(fileName);
        }
        return result;
    }

    /**
     * @return a configured {@link Proxy}, using the System properties defining the proxy
     * configuration
     */
    private Proxy createProxyElement()
    {
        String proxyHost = getSystemProperty("cargo.proxy.host");
        String proxyPort = getSystemProperty("cargo.proxy.port", "80");
        String proxyUser = getSystemProperty("cargo.proxy.user");
        String proxyPassword = System.getProperty("cargo.proxy.password");
        String proxyExcludeHosts = getSystemProperty("cargo.proxy.excludehosts", "");

        Proxy proxy = null;
        if (proxyHost != null)
        {
            proxy = new Proxy();
            proxy.setHost(proxyHost);
            proxy.setPort(Integer.parseInt(proxyPort));
            if (proxyUser != null)
            {
                proxy.setUser(proxyUser);
                proxy.setPassword(proxyPassword);
            }
            proxy.setExcludeHosts(proxyExcludeHosts);
        }
        return proxy;
    }

    /**
     * @param artifactName the artifact for which to return the location (eg "simple-war").
     * @return the location of the artifact as a string
     */
    public String getTestDataFileFor(String artifactName)
    {
        String result = this.testDataArtifacts.get(artifactName);
        if (result == null)
        {
            throw new ContainerException("Test data artifact not found [" + artifactName + "]");
        }
        return result;
    }
}
