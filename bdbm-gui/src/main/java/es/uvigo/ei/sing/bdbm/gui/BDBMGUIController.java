/*
 * #%L
 * BDBM GUI
 * %%
 * Copyright (C) 2014 - 2015 Miguel Reboiro-Jato, Critina P. Vieira, Hugo López-Fdez, Florentino Fdez-Riverola and Jorge Vieira
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

import java.io.IOException;
import java.util.Observable;

import es.uvigo.ei.sing.bdbm.BDBMManager;
import es.uvigo.ei.sing.bdbm.controller.BDBMController;
import es.uvigo.ei.sing.bdbm.environment.BDBMEnvironment;
import es.uvigo.ei.sing.bdbm.gui.configuration.PathsConfiguration;

public class BDBMGUIController extends Observable {
	private final BDBMManager manager;

	public BDBMGUIController(BDBMManager manager) {
		this.manager = manager;
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

	public boolean changePaths(PathsConfiguration configuration)
	throws IOException {
		if (this.getEnvironment().changePaths(
			configuration.getBaseRespository(), 
			configuration.getBaseBLAST(),
			configuration.getBaseEMBOSS(),
			configuration.getBaseBedTools(),
			configuration.getBaseSplign(),
			configuration.getBaseCompart()
		)) {
			this.setChanged();
			this.notifyObservers(configuration);
			
			return true;
		} else {
			return false;
		}
	}
}
