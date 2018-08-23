/*-
 * #%L
 * BDBM Core
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

package es.uvigo.ei.sing.bdbm.environment;

import es.uvigo.ei.sing.bdbm.environment.BLASTEnvironment;

public class WindowsBLASTEnvironment implements BLASTEnvironment {
	@Override
	public boolean isValidFor(String osName) {
		return osName.toLowerCase().contains("windows");
	}

	@Override
	public String getDefaultMakeBlastDB() {
		return "makeblastdb.exe";
	}

	@Override
	public String getDefaultBlastDBAliasTool() {
		return "blastdb_aliastool.exe";
	}

	@Override
	public String getDefaultBlastDBCmd() {
		return "blastdbcmd.exe";
	}

	@Override
	public String getDefaultBlastN() {
		return "blastn.exe";
	}

	@Override
	public String getDefaultBlastP() {
		return "blastp.exe";
	}

	@Override
	public String getDefaultTBlastX() {
		return "tblastx.exe";
	}

	@Override
	public String getDefaultTBlastN() {
		return "tblastn.exe";
	}
}
