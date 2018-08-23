/*-
 * #%L
 * BDBM Core
 * %%
 * Copyright (C) 2014 - 2018 Miguel Reboiro-Jato, Critina P. Vieira, Hugo
 *       López-Fdez, Noé Vázquez González, Florentino Fdez-Riverola and Jorge
 *       Vieira
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
package es.uvigo.ei.sing.bdbm.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import es.uvigo.ei.sing.bdbm.environment.execution.EMBOSSBinariesExecutor;
import es.uvigo.ei.sing.bdbm.environment.execution.ExecutionException;
import es.uvigo.ei.sing.bdbm.environment.execution.ExecutionResult;
import es.uvigo.ei.sing.bdbm.fasta.FastaParseException;
import es.uvigo.ei.sing.bdbm.fasta.FastaUtils;
import es.uvigo.ei.sing.bdbm.fasta.Sequence;
import es.uvigo.ei.sing.bdbm.persistence.entities.DefaultNucleotideFasta;
import es.uvigo.ei.sing.bdbm.persistence.entities.NucleotideFasta;
import es.uvigo.ei.sing.bdbm.util.DirectoryUtils;

public class RefineAnnotationPipeline {
	private EMBOSSBinariesExecutor embossBinaries;

	public RefineAnnotationPipeline(EMBOSSBinariesExecutor eBinaries) {
		this.embossBinaries = eBinaries;
	}

	public ExecutionResult refineAnnotation(
		NucleotideFasta genomeRegion,
		NucleotideFasta annotation, 
		int overlapping, int minSize, int maxSize,
		NucleotideFasta outputFasta
	)
		throws IOException, InterruptedException, ExecutionException, FastaParseException 
	{
		try (final DirectoryManager dirManager = new DirectoryManager()) {
			
			File step1OrfFile = dirManager.getStep1OrfFile();

			final ExecutionResult step1OrfResult = step1GetOrf(
				genomeRegion, 
				new DefaultNucleotideFasta(step1OrfFile), 
				minSize, maxSize
			);

			if (step1OrfResult != null) {
				return step1OrfResult;
			}

			FastaUtils.sortBySequenceLength(step1OrfFile, true);

			File firstGrowSequencesFile = dirManager.getFirstGrowSequencesFile();
			
			growSequencesWithOrf(
				annotation, 
				step1OrfFile, 
				overlapping, 
				firstGrowSequencesFile, 
				true
			);
			
			File firstGrowSequencesFile2 = dirManager.getFirstGrowSequencesFile2();
			
			growSequences(firstGrowSequencesFile, firstGrowSequencesFile2, overlapping);
			
			File step2OrfFile = dirManager.getStep2OrfFile();

			final ExecutionResult step2OrfResult = step2GetOrf(
				genomeRegion, 
				new DefaultNucleotideFasta(step2OrfFile), 
				minSize, maxSize
			);

			if (step2OrfResult != null) {
				return step2OrfResult;
			}
			
			File secondGrowSequencesFile = dirManager.getSecondGrowSequencesFile();
			
			FastaUtils.sortBySequenceLength(step2OrfFile, true);

			growSequencesWithOrf(
				new DefaultNucleotideFasta(firstGrowSequencesFile2), 
				step2OrfFile, 
				overlapping, 
				secondGrowSequencesFile,
				false
			);
		
			File secondGrowSequencesFile2 =	dirManager.getSecondGrowSequencesFile2();
			
			growSequences(secondGrowSequencesFile, secondGrowSequencesFile2, overlapping);
			
			Files.copy(secondGrowSequencesFile2.toPath(), outputFasta.getFile().toPath());
		}

		return null;
	}

	private ExecutionResult step1GetOrf(
		NucleotideFasta genomeRegion,
		DefaultNucleotideFasta output, 
		int minSize,
		int maxSize
	) throws InterruptedException, ExecutionException {
		return getOrf(genomeRegion, output,  minSize, maxSize, 2);
	}
	
	private ExecutionResult getOrf(
		NucleotideFasta input,
		DefaultNucleotideFasta output, 
		int minSize,
		int maxSize, 
		int find
	) throws InterruptedException, ExecutionException {
		Map<String, Optional<String>> orfAdditionalParams = new HashMap<>();
		orfAdditionalParams.put("-noreverse", Optional.empty());
		
		ExecutionResult result = this.embossBinaries.executeGetORF(
			input, output, 
			minSize, maxSize, 
			find, orfAdditionalParams
		);
		
		if (result.getExitStatus() != 0) {
			return result;
		}
		
		return null;
	}
	
	private ExecutionResult step2GetOrf(
		NucleotideFasta genomeRegion,
		DefaultNucleotideFasta output, 
		int minSize,
		int maxSize
	) throws InterruptedException, ExecutionException {
		return getOrf(genomeRegion, output,  minSize, maxSize, 3);
	}

	private void growSequencesWithOrf(
		NucleotideFasta annotation,
		File orf, 
		int overlapping,
		File outputFile,
		boolean rightGrow
	) throws FastaParseException, IOException {
		List<Sequence> orfSequences = FastaUtils.parseFasta(orf);
		List<Sequence> annotationSequences = FastaUtils.parseFasta(annotation.getFile());
		List<Sequence> newSequences = new LinkedList<>();
		
		for (Sequence annotationSeq : annotationSequences) {

			if (annotationSeq.getLength() < overlapping) {
				newSequences.add(annotationSeq);
				continue;
			}

			Sequence newSequence = annotationSeq;
			for (Sequence orfSeq : orfSequences) {
				if (orfSeq.getLength() < overlapping) {
					break;
				}

				String annotationChain = annotationSeq.getChain();
				String orfChain = orfSeq.getChain();

				String trustLeft = "";
				String toExpandRight = "";
				if (rightGrow) {
					trustLeft = orfChain;
					toExpandRight = annotationChain;
				} else {
					trustLeft = annotationChain;
					toExpandRight = orfChain;
				}

				String matchPattern = trustLeft.substring(0, overlapping);

				if (toExpandRight.contains(matchPattern)) {
					toExpandRight = toExpandRight.substring(
						0,
						toExpandRight.indexOf(matchPattern)
					);

					newSequence = new Sequence(annotationSeq.getName(),
						Arrays.asList(toExpandRight + trustLeft));

					break;
				}
			}

			newSequences.add(newSequence);
		}

		FastaUtils.writeFasta(newSequences, outputFile);
	}

	private void growSequences(File sequences, File output, int overlapping) 
		throws FastaParseException, IOException {
		List<Sequence> grownSequences = growSequences(
			FastaUtils.parseFasta(sequences), overlapping);

		FastaUtils.writeFasta(grownSequences, output);
	}
	
	private static List<Sequence> growSequences(
		List<Sequence> sequences, int overlapping
	) {
		boolean shouldIterate = false;
		do {
			shouldIterate = false;
			for (int i = 0; i < sequences.size(); i++) {
				Sequence reference = sequences.get(i);

				if (reference.getChain().length() < overlapping) {
					continue;
				}

				String referenceChain = reference.getChain();

				for (int j = 0; j < sequences.size(); j++) {
					if (i == j) {
						continue;
					}

					Sequence compare = sequences.get(j);

					if (compare.getChain().length() < overlapping) {
						continue;
					}

					String compareChain = compare.getChain();

					String trustLeft = referenceChain;
					String toExpandRight = compareChain;

					String matchPattern = trustLeft.substring(0, overlapping);

					if (toExpandRight.contains(matchPattern)) {
						toExpandRight = toExpandRight.substring(
							0,
							toExpandRight.indexOf(matchPattern)
						);

						Sequence merged = new Sequence(
							reference.getName(),
							Arrays.asList(toExpandRight + trustLeft)
						);

						sequences.remove(compare);
						sequences.remove(reference);
						sequences.add(merged);
						shouldIterate = true;
						break;
					}
				}

				if (shouldIterate) {
					break;
				}
			}
		} while (shouldIterate);

		return sequences;
	}

	protected static class DirectoryManager implements AutoCloseable {
		private final Path workingDirectory;

		public DirectoryManager() throws IOException {
			this.workingDirectory = Files.createTempDirectory("bdbm_refineannotation");
		}

		public File getStep1OrfFile() {
			return new File(this.workingDirectory.toFile(), "step1_orf.fa");
		}

		public File getStep2OrfFile() {
			return new File(this.workingDirectory.toFile(), "step2_orf.fa");
		}

		public File getPreparedNucleotidesFile() {
			return new File(this.workingDirectory.toFile(), "subject.fa");
		}
		
		public File getFirstGrowSequencesFile() {
			return new File(this.workingDirectory.toFile(), "sequences_1.fa");
		}

		public File getFirstGrowSequencesFile2() {
			return new File(this.workingDirectory.toFile(), "sequences_1_grown.fa");
		}

		public File getSecondGrowSequencesFile() {
			return new File(this.workingDirectory.toFile(), "sequences_2.fa");
		}
		
		public File getSecondGrowSequencesFile2() {
			return new File(this.workingDirectory.toFile(), "sequences_2_grown.fa");
		}

		@Override
		public void close() throws IOException {
			if (Boolean.valueOf(System.getProperty("refineannotation.deletetmpfiles", "true"))) {
				DirectoryUtils.deleteIfExists(this.workingDirectory);
			}
		}
	}
}
