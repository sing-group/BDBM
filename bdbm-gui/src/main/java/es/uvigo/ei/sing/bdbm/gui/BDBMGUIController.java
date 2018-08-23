/*-
 * #%L
 * BDBM GUI
 * %%
 * Copyright (C) 2014 - 2018 Miguel Reboiro-Jato, Critina P. Vieira, Hugo López-Fdez, Noé Vázquez González, Florentino Fdez-Riverola and Jorge Vieira
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

package es.uvigo.ei.sing.bdbm.gui;

import java.io.File;
import java.io.IOException;
import java.util.Observable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.uvigo.ei.sing.bdbm.BDBMManager;
import es.uvigo.ei.sing.bdbm.controller.BDBMController;
import es.uvigo.ei.sing.bdbm.environment.BDBMEnvironment;
import es.uvigo.ei.sing.bdbm.gui.command.input.FileInputComponentBuilder;
import es.uvigo.ei.sing.bdbm.gui.configuration.PathsConfiguration;

public class BDBMGUIController extends Observable {
	private static final Logger LOG = LoggerFactory.getLogger(BDBMGUIController.class);
	private static final String DEFAULT_DIRECTORY = "bdbm.default.directory";
	private final BDBMManager manager;

	public BDBMGUIController(BDBMManager manager) {
		this.manager = manager;
		
		if (this.manager.getEnvironment().hasProperty(DEFAULT_DIRECTORY)) {
			final File defaultDirectory = new File(
				this.manager.getEnvironment().getProperty(DEFAULT_DIRECTORY)
			);
			
			if (defaultDirectory.canRead() && defaultDirectory.isDirectory()) {
				FileInputComponentBuilder.setCurrentDirectory(defaultDirectory);
			} else {
				LOG.warn(String.format(
					"Default directory '%s' is not a valid directory. Please, "
					+ "check that the directory exists and can be read.",
					defaultDirectory.getAbsolutePath()
				));
			}
		}
	}

	public BDBMManager getManager() {
		return manager;
	}

	public BDBMController getController() {
		return this.manager.getController();
	}

	public BDBMEnvironment getEnvironment() {
		return this.manager.getEnvironment();
	}

	public boolean isAccessionInferEnabled() {
		return this.manager.getEnvironment().isAccessionInferEnabled();
	}
	
	public boolean showConfiguration() {
		final BDBMEnvironment environment = this.manager.getEnvironment();
		
		if (environment.hasProperty(GUI.SHOW_CONFIGURATION)) {
			return Boolean.parseBoolean(environment.getProperty(GUI.SHOW_CONFIGURATION));
		} else {
			return true;
		}
	}

	public boolean changePaths(PathsConfiguration configuration)
	throws IOException {
		if (this.getEnvironment().changePaths(
			configuration.getBaseRespository(), 
			configuration.getBaseBLAST(),
			configuration.getBaseEMBOSS(),
			configuration.getBaseBedTools(),
			configuration.getBaseSplign(),
			configuration.getBaseCompart(),
			configuration.getBaseProSplign(),
			configuration.getBaseProCompart()
		)) {
			this.setChanged();
			this.notifyObservers(configuration);
			
			return true;
		} else {
			return false;
		}
	}
}
