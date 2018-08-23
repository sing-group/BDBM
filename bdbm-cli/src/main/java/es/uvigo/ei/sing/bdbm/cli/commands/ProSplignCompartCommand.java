/*-
 * #%L
 * BDBM CLI
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

package es.uvigo.ei.sing.bdbm.cli.commands;

import java.io.File;

import es.uvigo.ei.sing.bdbm.controller.BDBMController;
import es.uvigo.ei.sing.bdbm.persistence.entities.DefaultNucleotideFasta;
import es.uvigo.ei.sing.bdbm.persistence.entities.DefaultProteinFasta;
import es.uvigo.ei.sing.yaacli.command.option.FileOption;
import es.uvigo.ei.sing.yaacli.command.option.IntegerOption;
import es.uvigo.ei.sing.yaacli.command.option.StringOption;
import es.uvigo.ei.sing.yaacli.command.parameter.Parameters;

public class ProSplignCompartCommand extends BDBMCommand {
	public static final String OPTION_GENOME_FASTA_SHORT_NAME = "gfasta";
	public static final String OPTION_QUERY_SHORT_NAME = "queryfasta";
	public static final String OPTION_OUTPUT_SHORT_NAME = "output";
	public static final String OPTION_MAX_TARGET_SEQS_SHORT_NAME = "max_target_seqs";
	
	public static final FileOption OPTION_GENOME_FASTA = 
		new FileOption(
			"Nucleotides FASTA", OPTION_GENOME_FASTA_SHORT_NAME, "Nucleotides FASTA file",
			false, true
		);
	public static final FileOption OPTION_QUERY_FASTA = 
		new FileOption(
			"Query protein FASTA", OPTION_QUERY_SHORT_NAME, "Query protein FASTA file",
			false, true
		);
	public static final StringOption OPTION_OUTPUT_NAME = 
		new StringOption(
			"Output name", OPTION_OUTPUT_SHORT_NAME, "Output name", 
			false, true
		);
    public static final IntegerOption OPTION_MAX_TARGET_SEQS = 
      new IntegerOption(
          "Max. target seqs.", OPTION_MAX_TARGET_SEQS_SHORT_NAME, 
          "The maximum number of aligned sequences to keep.", 
          500000
      );
	
	public ProSplignCompartCommand() {
		super();
	}
	
	public ProSplignCompartCommand(BDBMController controller) {
		super(controller);
	}

	@Override
	public String getName() {
		return "prospligncompart";
	}
	
	@Override
	public String getDescriptiveName() {
		return "ProSplign-Compart (NCBI)";
	}

	@Override
	public String getDescription() {
		return "Applies the ProSplign-Compart pipeline.";
	}

	@Override
	public void execute(Parameters parameters) throws Exception {
		final File genomeFastaFile = parameters.getSingleValue(OPTION_GENOME_FASTA);
		final File cdsFastaFile = parameters.getSingleValue(OPTION_QUERY_FASTA);
		final String outputName = parameters.getSingleValue(OPTION_OUTPUT_NAME);
		final Integer maxTargetSeqs = parameters.getSingleValue(OPTION_MAX_TARGET_SEQS);
		
		this.controller.proSplignCompart(
			new DefaultNucleotideFasta(genomeFastaFile),
			new DefaultProteinFasta(cdsFastaFile),
			outputName,
			maxTargetSeqs
		);
	}
}
