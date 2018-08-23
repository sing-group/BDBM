/*-
 * #%L
 * BDBM API
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

import es.uvigo.ei.sing.bdbm.environment.binaries.BedToolsBinaries;

public interface BedToolsBinariesExecutor extends BinariesExecutor<BedToolsBinaries> {
	public boolean checkBedToolsBinaries(BedToolsBinaries bBinaries);
	
	public ExecutionResult getfasta(
		String fi, String bed, String fo, InputLineCallback ... callbacks
	) throws ExecutionException, InterruptedException;
}
