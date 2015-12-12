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
import es.uvigo.ei.sing.yaacli.command.option.StringOption;
import es.uvigo.ei.sing.yaacli.command.parameter.Parameters;

public class SplignCompartCommand extends BDBMCommand {
	public static final String OPTION_GENOME_FASTA_SHORT_NAME = "gfasta";
	public static final String OPTION_CDS_SHORT_NAME = "cfasta";
	public static final String OPTION_CONCATENATE_SHORT_NAME = "conc_exons";
	public static final String OPTION_OUTPUT_SHORT_NAME = "output";
	
	public static final FileOption OPTION_GENOME_FASTA = 
		new FileOption(
			"Genome Fasta", OPTION_GENOME_FASTA_SHORT_NAME, "Genome fasta file",
			false, true
		);
	public static final FileOption OPTION_CDS_FASTA = 
		new FileOption(
			"CDS Fasta", OPTION_CDS_SHORT_NAME, "CDS fasta file",
			false, true
		);
	public static final DefaultValueBooleanOption OPTION_CONCATENATE_EXONS = new DefaultValueBooleanOption(
		"Concatenate Exons", OPTION_CONCATENATE_SHORT_NAME, "Concatenate the exons that are, apparently, from the same genes", true
	);
	public static final StringOption OPTION_OUTPUT_NAME = 
		new StringOption(
			"Output name", OPTION_OUTPUT_SHORT_NAME, "Output name", 
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
