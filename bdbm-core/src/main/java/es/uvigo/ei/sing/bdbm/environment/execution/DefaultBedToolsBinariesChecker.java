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

package es.uvigo.ei.sing.bdbm.environment.execution;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import es.uvigo.ei.sing.bdbm.environment.binaries.BedToolsBinaries;
import es.uvigo.ei.sing.bdbm.environment.execution.BedToolsBinariesChecker;
import es.uvigo.ei.sing.bdbm.environment.execution.BinaryCheckException;

public class DefaultBedToolsBinariesChecker implements BedToolsBinariesChecker {
	private BedToolsBinaries bBinaries;
	
	public DefaultBedToolsBinariesChecker() {
	}
	
	public DefaultBedToolsBinariesChecker(BedToolsBinaries eBinaries) {
		this.bBinaries = eBinaries;
	}
	
	public static void checkAll(BedToolsBinaries bBinaries)
	throws BinaryCheckException {
		new DefaultBedToolsBinariesChecker(bBinaries).checkAll();
	}

	@Override
	public void setBinaries(BedToolsBinaries bBinaries) {
		this.bBinaries = bBinaries;
	}

	@Override
	public void checkAll() throws BinaryCheckException {
		this.checkBedtools();
	}

	@Override
	public void checkBedtools() throws BinaryCheckException {
		checkCommand(this.bBinaries.getBedtools(), "bedtools");
		
	}
	
	protected static void checkCommand(String command, String program) throws BinaryCheckException {
		final Runtime runtime = Runtime.getRuntime();
		
		try {
			final Process process = runtime.exec(new String[] { command, "-version" });
			
			final BufferedReader br = new BufferedReader(
				new InputStreamReader(process.getInputStream()));
			
			final StringBuilder sb = new StringBuilder();
			
			String line;
			int countLines = 0;
			while ((line = br.readLine()) != null) {
				sb.append(line).append('\n');
				countLines++;
			}
			
			if (countLines != 1) {
				throw new BinaryCheckException("Unrecognized version output", command);
			}

			// TODO: Better parsing?
			if (!sb.toString().startsWith(program)) {
				throw new BinaryCheckException("Unrecognized version output", command);
			}
			
			final int exitStatus = process.waitFor();
			if (exitStatus != 0) {
				throw new BinaryCheckException(
					"Invalid exit status: " + exitStatus, 
					command
				);
			}
		} catch (Exception e) {
			throw new BinaryCheckException("Error while checking version", e, command);
		}
	}
}
