/*-
 * #%L
 * BDBM Core
 * %%
 * Copyright (C) 2014 - 2017 Miguel Reboiro-Jato, Critina P. Vieira, Hugo López-Fdez, Noé Vázquez González, Florentino Fdez-Riverola and Jorge Vieira
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
import java.io.IOException;
import java.io.InputStreamReader;

import es.uvigo.ei.sing.bdbm.environment.binaries.BLASTBinaries;
import es.uvigo.ei.sing.bdbm.environment.execution.BLASTBinariesChecker;
import es.uvigo.ei.sing.bdbm.environment.execution.BinaryCheckException;

public class DefaultBLASTBinariesChecker implements BLASTBinariesChecker {
	private BLASTBinaries bBinaries;

	public DefaultBLASTBinariesChecker() {
	}

	public DefaultBLASTBinariesChecker(BLASTBinaries bBinaries) {
		this.bBinaries = bBinaries;
	}

	public static void checkAll(BLASTBinaries bBinaries)
	throws BinaryCheckException {
		new DefaultBLASTBinariesChecker(bBinaries).checkAll();
	}

	@Override
	public void setBinaries(BLASTBinaries bBinaries) {
		this.bBinaries = bBinaries;
	}

	@Override
	public void checkAll() throws BinaryCheckException {
		this.checkMakeBlastDB();
		this.checkBlastDBAliasTool();
		this.checkBlastDBCmd();
		this.checkBlastN();
		this.checkTBlastX();
		this.checkTBlastN();
	}

	@Override
	public void checkMakeBlastDB() throws BinaryCheckException {
		DefaultBLASTBinariesChecker.checkCommand(this.bBinaries.getMakeBlastDB());
	}

	@Override
	public void checkBlastDBAliasTool() throws BinaryCheckException {
		DefaultBLASTBinariesChecker.checkCommand(this.bBinaries.getBlastDBAliasTool());
	}

	@Override
	public void checkBlastDBCmd() throws BinaryCheckException {
		DefaultBLASTBinariesChecker.checkCommand(this.bBinaries.getBlastDBCmd());
	}

	@Override
	public void checkBlastN() throws BinaryCheckException {
		DefaultBLASTBinariesChecker.checkCommand(this.bBinaries.getBlastN());
	}

	@Override
	public void checkTBlastX() throws BinaryCheckException {
		DefaultBLASTBinariesChecker.checkCommand(this.bBinaries.getTBlastX());
	}

	@Override
	public void checkTBlastN() throws BinaryCheckException {
		DefaultBLASTBinariesChecker.checkCommand(this.bBinaries.getTBlastN());
	}

	protected static void checkCommand(String command) throws BinaryCheckException {
		final Runtime runtime = Runtime.getRuntime();

		command += " -version";

		try {
			final Process process = runtime.exec(command);

			final BufferedReader br = new BufferedReader(
				new InputStreamReader(process.getInputStream()));

			StringBuilder sb = new StringBuilder();

			String line;
			int countLines = 0;
			while ((line = br.readLine()) != null) {
				sb.append(line).append('\n');
				countLines++;
			}

			if (countLines != 2) {
				throw new BinaryCheckException("Unrecognized version output", command);
			}

			final String[] lines = sb.toString().split("\n");
			// TODO: Better parsing?
			if (!lines[1].trim().startsWith("Package")) {
				throw new BinaryCheckException("Unrecognized version output", command);
			}

			final int exitStatus = process.waitFor();
			if (exitStatus != 0) {
				throw new BinaryCheckException(
					"Invalid exit status: " + exitStatus,
					command
				);
			}
		} catch (IOException e) {
			throw new BinaryCheckException("Error while checking version", e, command);
		} catch (InterruptedException e) {
			throw new BinaryCheckException("Error while checking version", e, command);
		}
	}
}
