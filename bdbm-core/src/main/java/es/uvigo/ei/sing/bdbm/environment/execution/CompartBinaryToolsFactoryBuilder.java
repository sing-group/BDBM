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

import java.util.ServiceLoader;

import es.uvigo.ei.sing.bdbm.environment.binaries.CompartBinaries;
import es.uvigo.ei.sing.bdbm.environment.execution.BinaryCheckException;
import es.uvigo.ei.sing.bdbm.environment.execution.CompartBinaryToolsFactory;

public class CompartBinaryToolsFactoryBuilder {
	private final static ServiceLoader<CompartBinaryToolsFactory> SERVICE_LOADER = 
		ServiceLoader.load(CompartBinaryToolsFactory.class);
	
	public static CompartBinaryToolsFactory newFactory(CompartBinaries nBinaries)
	throws BinaryCheckException {
		CompartBinaryToolsFactory selectedFactory = null;
		
		for (CompartBinaryToolsFactory factory : SERVICE_LOADER) {
			if (factory.isValidFor(nBinaries)) {
				selectedFactory = factory;
				SERVICE_LOADER.reload();
				
				break;
			}
		}
		
		if (selectedFactory == null) {
			selectedFactory = new DefaultCompartBinaryToolsFactory();
		}
		selectedFactory.setBinaries(nBinaries);
		
		return selectedFactory;
	}
}
