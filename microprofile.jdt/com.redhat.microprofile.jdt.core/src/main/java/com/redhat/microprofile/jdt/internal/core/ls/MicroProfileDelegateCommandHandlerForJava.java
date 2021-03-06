/*******************************************************************************
* Copyright (c) 2019 Red Hat Inc. and others.
* All rights reserved. This program and the accompanying materials
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v20.html
*
* Contributors:
*     Red Hat Inc. - initial API and implementation
*******************************************************************************/
package com.redhat.microprofile.jdt.internal.core.ls;

import static com.redhat.microprofile.jdt.internal.core.ls.ArgumentUtils.getBoolean;
import static com.redhat.microprofile.jdt.internal.core.ls.ArgumentUtils.getFirst;
import static com.redhat.microprofile.jdt.internal.core.ls.ArgumentUtils.getInt;
import static com.redhat.microprofile.jdt.internal.core.ls.ArgumentUtils.getString;

import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ls.core.internal.IDelegateCommandHandler;
import org.eclipse.lsp4j.CodeLens;
import org.eclipse.lsp4j.Position;

import com.redhat.microprofile.commons.MicroProfileJavaCodeLensParams;
import com.redhat.microprofile.commons.MicroProfileJavaHoverInfo;
import com.redhat.microprofile.commons.MicroProfileJavaHoverParams;
import com.redhat.microprofile.jdt.core.PropertiesManagerForJava;

/**
 * JDT LS delegate command handler for Java file..
 * 
 * @author Angelo ZERR
 *
 */
public class MicroProfileDelegateCommandHandlerForJava implements IDelegateCommandHandler {

	private static final String JAVA_CODELENS_COMMAND_ID = "microprofile/java/codeLens";
	private static final String JAVA_HOVER_COMMAND_ID = "microprofile/java/hover";

	public MicroProfileDelegateCommandHandlerForJava() {
	}

	@Override
	public Object executeCommand(String commandId, List<Object> arguments, IProgressMonitor progress) throws Exception {
		switch (commandId) {
		case JAVA_CODELENS_COMMAND_ID:
			return getCodeLensForJava(arguments, commandId, progress);
		case JAVA_HOVER_COMMAND_ID:
			return getHoverForJava(arguments, commandId, progress);
		default:
			throw new UnsupportedOperationException(String.format("Unsupported command '%s'!", commandId));
		}
	}

	/**
	 * Returns the code lenses for the given Java file.
	 * 
	 * @param arguments
	 * @param commandId
	 * @param monitor
	 * @return the code lenses for the given Java file.
	 * @throws CoreException
	 * @throws JavaModelException
	 */
	private static List<? extends CodeLens> getCodeLensForJava(List<Object> arguments, String commandId,
			IProgressMonitor monitor) throws JavaModelException, CoreException {
		// Create java code lens parameter<O
		MicroProfileJavaCodeLensParams params = createQuarkusJavaCodeLensParams(arguments, commandId);
		// return code lenses from the lens parameter
		return PropertiesManagerForJava.getInstance().codeLens(params, JDTUtilsLSImpl.getInstance(), monitor);
	}

	/**
	 * Create java code lens parameter from the given arguments map.
	 * 
	 * @param arguments
	 * @param commandId
	 * 
	 * @return java code lens parameter
	 */
	private static MicroProfileJavaCodeLensParams createQuarkusJavaCodeLensParams(List<Object> arguments,
			String commandId) {
		Map<String, Object> obj = getFirst(arguments);
		if (obj == null) {
			throw new UnsupportedOperationException(
					String.format("Command '%s' must be call with one QuarkusJavaCodeLensParams argument!", commandId));
		}
		String javaFileUri = getString(obj, "uri");
		if (javaFileUri == null) {
			throw new UnsupportedOperationException(String.format(
					"Command '%s' must be call with required QuarkusJavaCodeLensParams.uri (java URI)!", commandId));
		}
		MicroProfileJavaCodeLensParams params = new MicroProfileJavaCodeLensParams(javaFileUri);
		params.setUrlCodeLensEnabled(getBoolean(obj, "urlCodeLensEnabled"));
		params.setCheckServerAvailable(getBoolean(obj, "checkServerAvailable"));
		params.setOpenURICommand(getString(obj, "openURICommand"));
		params.setLocalServerPort(getInt(obj, "localServerPort"));
		return params;
	}

	/**
	 * Returns the <code>QuarkusJavaHoverInfo</code> for the hover described in
	 * <code>arguments</code>
	 * 
	 * @param arguments
	 * @param commandId
	 * @param monitor
	 * @return
	 * @throws JavaModelException
	 * @throws CoreException
	 */
	private static MicroProfileJavaHoverInfo getHoverForJava(List<Object> arguments, String commandId,
			IProgressMonitor monitor) throws JavaModelException, CoreException {
		// Create java hover parameter
		MicroProfileJavaHoverParams params = createQuarkusJavaHoverParams(arguments, commandId);
		// return hover info from hover parameter
		return PropertiesManagerForJava.getInstance().hover(params, JDTUtilsLSImpl.getInstance(), monitor);
	}

	/**
	 * Returns the java hover parameters from the given arguments map.
	 * 
	 * @param arguments
	 * @param commandId
	 * 
	 * @return the java hover parameters
	 */
	private static MicroProfileJavaHoverParams createQuarkusJavaHoverParams(List<Object> arguments, String commandId) {
		Map<String, Object> obj = getFirst(arguments);
		if (obj == null) {
			throw new UnsupportedOperationException(
					String.format("Command '%s' must be call with one QuarkusJavaHoverParams argument!", commandId));
		}
		String javaFileUri = getString(obj, "uri");
		if (javaFileUri == null) {
			throw new UnsupportedOperationException(String.format(
					"Command '%s' must be call with required QuarkusJavaHoverParams.uri (java URI)!", commandId));
		}

		Map<String, Object> hoverPosition = (Map<String, Object>) obj.get("position");
		int line = getInt(hoverPosition, "line");
		int character = getInt(hoverPosition, "character");

		return new MicroProfileJavaHoverParams(javaFileUri, new Position(line, character));
	}

}
