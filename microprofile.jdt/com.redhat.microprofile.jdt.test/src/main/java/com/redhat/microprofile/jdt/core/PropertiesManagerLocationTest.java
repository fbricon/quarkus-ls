/*******************************************************************************
* Copyright (c) 2019 Red Hat Inc. and others.
* All rights reserved. This program and the accompanying materials
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v20.html
*
* Contributors:
*     Red Hat Inc. - initial API and implementation
*******************************************************************************/
package com.redhat.microprofile.jdt.core;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.ls.core.internal.JavaLanguageServerPlugin;
import org.eclipse.lsp4j.ClientCapabilities;
import org.eclipse.lsp4j.Location;
import org.junit.Assert;
import org.junit.Test;

import com.redhat.microprofile.jdt.internal.core.ls.JDTUtilsLSImpl;

/**
 * Test with find Quarkus definition.
 * 
 * @author Angelo ZERR
 *
 */
public class PropertiesManagerLocationTest extends BasePropertiesManagerTest {

	@Test
	public void usingVertxTest() throws Exception {
		// Enable classFileContentsSupport to generate jdt Location
		enableClassFileContentsSupport();

		IJavaProject javaProject = loadMavenProject(MavenProjectName.using_vertx);
		IFile file = javaProject.getProject().getFile(new Path("src/main/resources/application.properties"));

		// Test with JAR
		// quarkus.datasource.url
		Location location = PropertiesManager.getInstance().findPropertyLocation(file,
				"io.quarkus.reactive.pg.client.runtime.DataSourceConfig", "url", null, JDTUtilsLSImpl.getInstance(),
				new NullProgressMonitor());
		Assert.assertNotNull("Definition from JAR", location);

		// Test with deployment JAR
		// quarkus.arc.auto-inject-fields
		location = PropertiesManager.getInstance().findPropertyLocation(file, "io.quarkus.arc.deployment.ArcConfig",
				"autoInjectFields", null, JDTUtilsLSImpl.getInstance(), new NullProgressMonitor());
		Assert.assertNotNull("Definition deployment from JAR", location);

		// Test with Java sources
		// myapp.schema.create
		location = PropertiesManager.getInstance().findPropertyLocation(file, "org.acme.vertx.FruitResource",
				"schemaCreate", null, JDTUtilsLSImpl.getInstance(), new NullProgressMonitor());
		Assert.assertNotNull("Definition from Java Sources", location);
	}

	@Test
	public void configPropertiesTest() throws Exception {
		// Enable classFileContentsSupport to generate jdt Location
		enableClassFileContentsSupport();

		IJavaProject javaProject = loadMavenProject(MavenProjectName.config_properties);
		IFile file = javaProject.getProject().getFile(new Path("src/main/resources/application.properties"));

		// Test with method
		// greetingInterface.name
		Location location = PropertiesManager.getInstance().findPropertyLocation(file,
				"org.acme.config.IGreetingConfiguration", null, "getName()QOptional<QString;>;",
				JDTUtilsLSImpl.getInstance(), new NullProgressMonitor());
		Assert.assertNotNull("Definition from IGreetingConfiguration#getName() method", location);
	}

	private static void enableClassFileContentsSupport() {
		Map<String, Object> extendedClientCapabilities = new HashMap<>();
		extendedClientCapabilities.put("classFileContentsSupport", "true");
		JavaLanguageServerPlugin.getPreferencesManager().updateClientPrefences(new ClientCapabilities(),
				extendedClientCapabilities);
	}

}
