/*-
 * #%L
 * BDBM CLI
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

package es.uvigo.ei.sing.bdbm.cli.commands;

import java.io.File;

import es.uvigo.ei.sing.bdbm.controller.BDBMController;
import es.uvigo.ei.sing.bdbm.persistence.entities.DefaultNucleotideFasta;
import es.uvigo.ei.sing.yaacli.command.option.FileOption;
import es.uvigo.ei.sing.yaacli.command.option.IntegerOption;
import es.uvigo.ei.sing.yaacli.command.option.StringOption;
import es.uvigo.ei.sing.yaacli.command.parameter.Parameters;

public class RefineAnnotationCommand extends BDBMCommand {
  public static final String OPTION_GENOME_REGION_FASTA_SHORT_NAME = "genomeregion";
  public static final String OPTION_ANNOTATION_FASTA_SHORT_NAME = "annotation";
  public static final String OPTION_OVERLAPPING_SHORT_NAME = "overlapping";
  public static final String OPTION_MIN_SHORT_NAME = "min";
  public static final String OPTION_MAX_SHORT_NAME = "max";
  public static final String OPTION_OUTPUT_SHORT_NAME = "output";
  
  public static final FileOption OPTION_GENOME_REGION_FASTA = 
      new FileOption(
          "FASTA genome region", OPTION_GENOME_REGION_FASTA_SHORT_NAME, 
          "Input FASTA (nucleotide) file for the genome region of interest",
          false, true
      );
  public static final FileOption OPTION_ANNOTATION_FASTA = 
    new FileOption(
        "FASTA annotation", OPTION_ANNOTATION_FASTA_SHORT_NAME, 
        "Input FASTA (nucleotide) file for the annotation to be refined",
        false, true
    );
  public static final IntegerOption OPTION_OVERLAPPING =
    new IntegerOption(
        "Overlapping", OPTION_OVERLAPPING_SHORT_NAME, 
        "Overlapping to grow sequences", "100"
    );
  public static final IntegerOption OPTION_MIN_SIZE =
    new IntegerOption(
        "Min. Size", OPTION_MIN_SHORT_NAME, "Minimum ORF size", "300"
    );
  public static final IntegerOption OPTION_MAX_SIZE =
    new IntegerOption(
        "Max. Size", OPTION_MAX_SHORT_NAME, "Maximum ORF size", "10000"
    );
  public static final StringOption OPTION_OUTPUT_NAME = 
    new StringOption(
        "Output name", OPTION_OUTPUT_SHORT_NAME, "Output name", 
        false, true
    );
	
	public RefineAnnotationCommand() {
		super();
	}
	
	public RefineAnnotationCommand(BDBMController controller) {
		super(controller);
	}

	@Override
	public String getName() {
		return "refineannotation";
	}
	
	@Override
	public String getDescriptiveName() {
		return "Refine annotation";
	}

	@Override
	public String getDescription() {
		return "Refines an annotation fastas.";
	}

	@Override
	public void execute(Parameters parameters) throws Exception {
      final File genomeRegionFastaFile = parameters.getSingleValue(OPTION_GENOME_REGION_FASTA);
      final File annotationFastaFile = parameters.getSingleValue(OPTION_ANNOTATION_FASTA);
      final Integer overlapping = parameters.getSingleValue(OPTION_OVERLAPPING);
      final Integer minSize = parameters.getSingleValue(OPTION_MIN_SIZE);
      final Integer maxSize = parameters.getSingleValue(OPTION_MAX_SIZE);
      final String outputName = parameters.getSingleValue(OPTION_OUTPUT_NAME);
      
      this.controller.refineAnnotation(
          new DefaultNucleotideFasta(genomeRegionFastaFile),
          new DefaultNucleotideFasta(annotationFastaFile),
          overlapping,
          minSize,
          maxSize,
          outputName
      );
	}
}
