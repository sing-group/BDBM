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

package es.uvigo.ei.sing.bdbm.fasta.naming;

import java.util.concurrent.atomic.AtomicInteger;

import es.uvigo.ei.sing.bdbm.fasta.naming.configuration.PrefixSequenceRenameConfiguration;

public class PrefixSequenceNameSummarizer implements SequenceNameSummarizer<PrefixSequenceRenameConfiguration> {
	private final AtomicInteger prefixCounter;
	
	public PrefixSequenceNameSummarizer() {
		this.prefixCounter = new AtomicInteger(1);
	}
	
	public PrefixSequenceNameSummarizer(int initialCounterValue) {
		this.prefixCounter = new AtomicInteger(initialCounterValue);
	}
	
	public int getPrefixCounterValue() {
		return this.prefixCounter.get();
	}
	
	public void setPrefixCounterValue(int value) {
		this.prefixCounter.set(value);
	}
	
	public void resetPrefixCounterValue() {
		this.prefixCounter.set(1);
	}
	
	@Override
	public boolean recognizes(String sequenceName, PrefixSequenceRenameConfiguration configuration) {
		return sequenceName.matches(">.+");
	}

	@Override
	public String summarize(String sequenceName, PrefixSequenceRenameConfiguration configuration) {
		final StringBuilder reformattedName = new StringBuilder(">");
		
		boolean needsJoiner = false;
		if (configuration.hasPrefix()) {
			reformattedName.append(configuration.getPrefix());
			needsJoiner = true;
		}
		
		if (configuration.isAddIndex()) {
			if (needsJoiner)
				reformattedName.append(configuration.getJoinerString());
			
			reformattedName.append(this.prefixCounter.getAndIncrement());
			needsJoiner = true;
		}
		
		final String[] nameParts = sequenceName.substring(1).split("\\s+", 2);
		if (configuration.isKeepNames()) {
			if (needsJoiner)
				reformattedName.append(configuration.getJoinerString());
			
			reformattedName.append(nameParts[0]);
		}
		
		if (nameParts.length == 2 && configuration.isKeepDescription()) {
			reformattedName.append(" ");
			reformattedName.append(nameParts[1]);
		}
		
		return reformattedName.toString();
	}
}
