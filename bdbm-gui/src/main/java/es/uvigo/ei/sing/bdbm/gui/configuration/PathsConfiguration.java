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

package es.uvigo.ei.sing.bdbm.gui.configuration;

import java.io.File;

public class PathsConfiguration {
	private final File baseRespository;
	private final File baseBLAST;
	private final File baseEMBOSS;
	private final File baseBedTools;
	private final File baseSplign;
	private final File baseCompart;
	private final File baseProSplign;
	private final File baseProCompart;
	
	public PathsConfiguration(
		File baseRespository, File baseBLAST, File baseEMBOSS,
		File baseBedTools, File baseSplign, File baseCompart,
		File baseProSplign, File baseProCompart
	) {
		this.baseRespository = baseRespository;
		this.baseBLAST = baseBLAST;
		this.baseEMBOSS = baseEMBOSS;
		this.baseBedTools = baseBedTools;
		this.baseSplign = baseSplign;
		this.baseCompart = baseCompart;
		this.baseProSplign = baseProSplign;
		this.baseProCompart = baseProCompart;
	}
	
	public File getBaseRespository() {
		return baseRespository;
	}
	
	public File getBaseBLAST() {
		return baseBLAST;
	}
	
	public File getBaseEMBOSS() {
		return baseEMBOSS;
	}
	
	public File getBaseBedTools() {
		return baseBedTools;
	}
	
	public File getBaseSplign() {
		return baseSplign;
	}
	
	public File getBaseCompart() {
		return baseCompart;
	}

	public File getBaseProSplign() {
		return baseProSplign;
	}
	
	public File getBaseProCompart() {
		return baseProCompart;
	}
}
