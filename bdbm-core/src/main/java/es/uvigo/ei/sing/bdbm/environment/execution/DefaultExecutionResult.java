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

import es.uvigo.ei.sing.bdbm.environment.execution.ExecutionResult;

public class DefaultExecutionResult implements ExecutionResult {
	private final int exitStatus;
	private final String output;
	private final String error;
	
	public DefaultExecutionResult(int exitStatus) {
		this(exitStatus, null, null);
	}
	
	public DefaultExecutionResult(int exitStatus, String output, String error) {
		this.exitStatus = exitStatus;
		this.output = output;
		this.error = error;
	}
	
	@Override
	public int getExitStatus() {
		return exitStatus;
	}
	
	@Override
	public String getOutput() {
		return output;
	}
	
	@Override
	public String getError() {
		return error;
	}
}
