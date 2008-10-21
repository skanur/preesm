/*
 * Copyright (c) 2008, IETR/INSA of Rennes
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *   * Neither the name of the IETR/INSA of Rennes nor the names of its
 *     contributors may be used to endorse or promote products derived from this
 *     software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 * WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 */
package org.ietr.preesm.core.ui.perspectives;

import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.ietr.preesm.core.log.PreesmLogger;
import org.ietr.preesm.core.ui.Activator;

/**
 * This class creates the layout associated with the preesm core perspective.
 * 
 * @author mpelcat
 * 
 */
public class CorePerspectiveFactory implements IPerspectiveFactory {

	public static final String ID = "org.ietr.preesm.core.ui.perspective";

	@Override
	 public void createInitialLayout(IPageLayout layout) {
			// Get the editor area.
			String editorArea = layout.getEditorArea();
			// Top left: Resource Navigator view and Bookmarks view placeholder
			IFolderLayout topLeft = layout.createFolder("topLeft", IPageLayout.LEFT, 0.20f,
				editorArea);
			topLeft.addView(IPageLayout.ID_RES_NAV);
			topLeft.addView(IPageLayout.ID_BOOKMARKS);

			// Bottom left: Outline view and Property Sheet view
			IFolderLayout bottomRight = layout.createFolder("bottomRight", IPageLayout.BOTTOM, 0.75f,
					editorArea);
			
			bottomRight.addView(IPageLayout.ID_PROGRESS_VIEW);
			
			// Creates a console for the logger
			PreesmLogger.getLogger().createConsole();
			bottomRight.addView("org.eclipse.ui.console.ConsoleView");
			
		}


}