/*
 * #%L
 * BDBM CLI
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
package es.uvigo.ei.sing.bdbm.cli.commands;

import java.io.File;

import es.uvigo.ei.sing.bdbm.cli.commands.converters.DefaultValueBooleanOption;
import es.uvigo.ei.sing.bdbm.cli.commands.converters.FileOption;
import es.uvigo.ei.sing.bdbm.controller.BDBMController;
import es.uvigo.ei.sing.bdbm.persistence.entities.DefaultNucleotideFasta;
import es.uvigo.ei.sing.yaacli.Parameters;
import es.uvigo.ei.sing.yaacli.StringOption;

public class SplignCompartCommand extends BDBMCommand {
	public static final FileOption OPTION_GENOME_FASTA = 
		new FileOption(
			"Genome Fasta", "gfasta", "Genome fasta file",
			false, true
		);
	public static final FileOption OPTION_CDS_FASTA = 
		new FileOption(
			"CDS Fasta", "cfasta", "CDS fasta file",
			false, true
		);
	public static final DefaultValueBooleanOption OPTION_CONCATENATE_EXONS = new DefaultValueBooleanOption(
		"Concatenate Exons", "conc_exons", "Concatenate the exons that are, apparently, from the same genes", true
	);
	public static final StringOption OPTION_OUTPUT_NAME = 
		new StringOption(
			"Output name", "output", "Output name", 
			false, true
		);
	
	public SplignCompartCommand() {
		super();
	}
	
	public SplignCompartCommand(BDBMController controller) {
		super(controller);
	}

	@Override
	public String getName() {
		return "spligncompart";
	}
	
	@Override
	public String getDescriptiveName() {
		return "Splign-Compart (NCBI)";
	}

	@Override
	public String getDescription() {
		return "Applies the Splign-Compart-Bedtools pipeline";
	}

	@Override
	public void execute(Parameters parameters) throws Exception {
		final File genomeFastaFile = parameters.getSingleValue(OPTION_GENOME_FASTA);
		final File cdsFastaFile = parameters.getSingleValue(OPTION_CDS_FASTA);
		final boolean concatenateExons = parameters.getSingleValue(OPTION_CONCATENATE_EXONS);
		final String outputName = parameters.getSingleValue(OPTION_OUTPUT_NAME);
		
		this.controller.splignCompart(
			new DefaultNucleotideFasta(genomeFastaFile),
			new DefaultNucleotideFasta(cdsFastaFile),
			concatenateExons,
			outputName
		);
	}
}