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

package es.uvigo.ei.sing.bdbm.fasta;

import static es.uvigo.ei.sing.bdbm.fasta.naming.SequenceNameSummarizerFactory.createSplitterNameSummarizer;
import static es.uvigo.ei.sing.bdbm.fasta.naming.SequenceNameSummarizerFactory.createStandardNameSummarizer;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import es.uvigo.ei.sing.bdbm.fasta.naming.FastaSequenceRenameMode;
import es.uvigo.ei.sing.bdbm.fasta.naming.GenericComposedSequenceNameSummarizer;
import es.uvigo.ei.sing.bdbm.fasta.naming.PrefixSequenceNameSummarizer;
import es.uvigo.ei.sing.bdbm.fasta.naming.configuration.ComposedSequenceRenameConfiguration;
import es.uvigo.ei.sing.bdbm.fasta.naming.configuration.PrefixSequenceRenameConfiguration;
import es.uvigo.ei.sing.bdbm.fasta.naming.standard.StandardSequenceNameSummarizer;

public class FastaUtils {
	public static File[] splitFastaIntoFiles(File fastaFile)
	throws IOException, FastaParseException {
		final List<File> files = new ArrayList<>();
		
		final FastaParser parser = new DefaultFastaParser();
		parser.addParseListener(new FastaParserAdapter() {
			private String name = null;
			private StringBuilder sequence = new StringBuilder();
			
			@Override
			public void sequenceNameRead(File file, String sequenceName) {
				this.name = sequenceName;
			}
			
			@Override
			public void sequenceFragmentRead(File file, String sequence) {
				this.sequence.append(sequence);
			}
			
			@Override
			public void sequenceEnd(File file) {
				try {
					final Path tmpFilePath = Files.createTempFile("bdbm", "fasta");
					
					final String content = name + '\n' + sequence;
					
					Files.write(tmpFilePath, content.getBytes());
					
					final File tmpFile = tmpFilePath.toFile();
					tmpFile.deleteOnExit();
					
					files.add(tmpFile);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		});
		
		parser.parse(fastaFile);
		
		if (files.size() == 1) {
			files.get(0).delete();
			return new File[] { fastaFile };
		} else {
			return files.toArray(new File[files.size()]);
		}
	}
	
	public static void fastaSequenceRenaming(
		FastaSequenceRenameMode mode,
		File fastaFile,
		PrintWriter writer,
		Map<ReformatFastaParameters, Object> additionalParameters
	) throws FastaParseException, IOException {
		fastaSequenceRenaming(mode, fastaFile, SequenceLengthConfiguration.buildNoChanges(), writer, additionalParameters);
	}
	
	public static void fastaSequenceRenaming(
		FastaSequenceRenameMode mode,
		File fastaFile,
		SequenceLengthConfiguration sequenceLengthConfiguration,
		PrintWriter writer,
		Map<ReformatFastaParameters, Object> renameParameters
	) throws FastaParseException, IOException {
		switch(mode) {
		case KNOWN_SEQUENCE_NAMES: {
			final int[] paramIndexes = extractRenameParameter(int[].class, ReformatFastaParameters.INDEXES, renameParameters);
			final String paramJoinerString = extractRenameParameter(String.class, ReformatFastaParameters.JOINER_STRING, renameParameters);
			final Boolean paramKeepDescription = extractRenameParameter(Boolean.class, ReformatFastaParameters.KEEP_DESCRIPTION, renameParameters);
			
			final ComposedSequenceRenameConfiguration renameConfiguration = new ComposedSequenceRenameConfiguration();
			renameConfiguration.setSelectedIndexes(paramIndexes);
			renameConfiguration.setJoinerString(paramJoinerString);
			renameConfiguration.setKeepDescription(paramKeepDescription);
			
			smartFastaSequenceRenaming(fastaFile, sequenceLengthConfiguration, renameConfiguration, writer);
			break;
		}
		case MULTIPART_NAME: {
			final int[] paramIndexes = extractRenameParameter(int[].class, ReformatFastaParameters.INDEXES, renameParameters);
			final String paramDelimiterString = extractRenameParameter(String.class, ReformatFastaParameters.DELIMITER_STRING, renameParameters);
			final String paramJoinerString = extractRenameParameter(String.class, ReformatFastaParameters.JOINER_STRING, renameParameters);
			final Boolean paramKeepDescription = extractRenameParameter(Boolean.class, ReformatFastaParameters.KEEP_DESCRIPTION, renameParameters);

			final ComposedSequenceRenameConfiguration renameConfiguration = new ComposedSequenceRenameConfiguration();
			renameConfiguration.setSelectedIndexes(paramIndexes);
			renameConfiguration.setDelimiterString(paramDelimiterString);
			renameConfiguration.setJoinerString(paramJoinerString);
			renameConfiguration.setKeepDescription(paramKeepDescription);
			
			genericFastaSequenceRenaming(fastaFile, sequenceLengthConfiguration, renameConfiguration, writer);
			break;
		}
		case PREFIX: {
			final String paramPrefix = extractRenameParameter(String.class, ReformatFastaParameters.PREFIX, renameParameters);
			final String paramJoinerString = extractRenameParameter(String.class, ReformatFastaParameters.JOINER_STRING, renameParameters);
			final Boolean paramAddIndex = extractRenameParameter(Boolean.class, ReformatFastaParameters.ADD_INDEX_WHEN_PREFIX, renameParameters);
			final Boolean paramKeepName = extractRenameParameter(Boolean.class, ReformatFastaParameters.KEEP_NAMES_WHEN_PREFIX, renameParameters);
			final Boolean paramKeepDescription = extractRenameParameter(Boolean.class, ReformatFastaParameters.KEEP_DESCRIPTION, renameParameters);
			
			final PrefixSequenceRenameConfiguration configuration = new PrefixSequenceRenameConfiguration();
			configuration.setPrefix(paramPrefix);
			configuration.setKeepNames(paramKeepName);
			configuration.setAddIndex(paramAddIndex);
			configuration.setJoinerString(paramJoinerString);
			configuration.setKeepDescription(paramKeepDescription);
			
			prefixFastaSequenceRenaming(fastaFile, sequenceLengthConfiguration, configuration, writer);
			break;
		}
		case NONE:
			changeSequenceLength(fastaFile, sequenceLengthConfiguration, writer);
			break;
		}
	}
	
	public static String prefixFastaSequenceNameRename(
		String sequenceName,
		int prefixCounter,
		PrefixSequenceRenameConfiguration renameConfiguration
	) {
		final PrefixSequenceNameSummarizer summarizer = new PrefixSequenceNameSummarizer(prefixCounter);
		
		return summarizer.summarize(sequenceName, renameConfiguration);
	}
	
	public static String prefixFastaSequenceNameRename(
		String sequenceName, PrefixSequenceRenameConfiguration renameConfiguration
	) {
		final PrefixSequenceNameSummarizer summarizer = new PrefixSequenceNameSummarizer();
		
		return summarizer.summarize(sequenceName, renameConfiguration);
	}
	
	@SuppressWarnings("unchecked")
	private static <T> T extractRenameParameter(
		Class<T> paramType,
		ReformatFastaParameters parameter,
		Map<ReformatFastaParameters, Object> renameParameters
	) {
		final Object paramValue = renameParameters.get(parameter);
		
		if (paramValue == null) {
			return null;
		} else {
			if (!paramType.isAssignableFrom(paramValue.getClass())) {
				throw new IllegalArgumentException(String.format("Invalid %s value", parameter));
			}
			
			return (T) paramValue;
		}
	}
	
	public static void prefixFastaSequenceRenaming(
		final File fastaFile,
		final PrefixSequenceRenameConfiguration renameConfiguration,
		final PrintWriter writer
	) throws FastaParseException, IOException {
		prefixFastaSequenceRenaming(fastaFile, SequenceLengthConfiguration.buildNoChanges(), renameConfiguration, writer);
	}
	
	public static void prefixFastaSequenceRenaming(
		final File fastaFile,
		final SequenceLengthConfiguration sequenceLengthConfiguration,
		final PrefixSequenceRenameConfiguration renameConfiguration,
		final PrintWriter writer
	) throws FastaParseException, IOException {
		if (!renameConfiguration.isValid()) {
			throw new IllegalArgumentException("You must select to add an index or to keep names");
		}
		
		final FastaParser parser = new DefaultFastaParser();
		
		parser.addParseListener(createFastaParser(sequenceLengthConfiguration, new SequenceRenamer() {
			private final PrefixSequenceNameSummarizer summarizer = new PrefixSequenceNameSummarizer();
			
			@Override
			public void sequenceNameRead(File file, String sequenceName) {
				writer.println(summarizer.summarize(sequenceName, renameConfiguration));
			}
			
			@Override
			public void sequenceFragment(File file, String sequenceFragment) {
				writer.println(sequenceFragment);
			}
		}));
		
		parser.parse(fastaFile);
	}
	
	public static String smartFastaSequenceNameRename(
		final String sequenceName,
		final ComposedSequenceRenameConfiguration renameConfiguration
	) {
		final StandardSequenceNameSummarizer summarizer = createStandardNameSummarizer(sequenceName, renameConfiguration);
		
		if (summarizer == null)
			throw new RuntimeException("Unrecognized sequence name: " + sequenceName);
		
		return summarizer.summarize(sequenceName, renameConfiguration);
	}
	
	public static void smartFastaSequenceRenaming(
		final File fastaFile, 
		final ComposedSequenceRenameConfiguration renameConfiguration,
		final PrintWriter writer
	) throws FastaParseException, IOException {
		smartFastaSequenceRenaming(fastaFile, SequenceLengthConfiguration.buildNoChanges(), renameConfiguration, writer);
	}
	
	public static void smartFastaSequenceRenaming(
		final File fastaFile,
		final SequenceLengthConfiguration sequenceLengthConfiguration,
		final ComposedSequenceRenameConfiguration renameConfiguration,
		final PrintWriter writer
	) throws FastaParseException, IOException {
		final FastaParser parser = new DefaultFastaParser();
		
		parser.addParseListener(createFastaParser(sequenceLengthConfiguration, new SequenceRenamer() {
			@Override
			public void sequenceNameRead(File file, String sequenceName) {
				writer.println(smartFastaSequenceNameRename(sequenceName, renameConfiguration));
			}
			
			@Override
			public void sequenceFragment(File file, String sequenceFragment) {
				writer.println(sequenceFragment);
			}
		}));
		
		parser.parse(fastaFile);
	}
	
	public static Map<String, StandardSequenceNameSummarizer> getFastaSequenceNameSummarizers(File fastaFile)
	throws FastaParseException, IOException {
		final Map<String, StandardSequenceNameSummarizer> summarizers = new HashMap<>();
		final FastaParser parser = new DefaultFastaParser();
		
		parser.addParseListener(new FastaParserAdapter() {
			@Override
			public void sequenceNameRead(File file, String sequenceName) throws FastaParseException {
				summarizers.put(sequenceName, createStandardNameSummarizer(sequenceName));
			}
		});
		
		parser.parse(fastaFile);
		
		return summarizers;
	}
	
	public static String getFirstSequence(File fastaFile)
	throws FastaParseException, IOException {
		class CustomFastaParserAdapter extends FastaParserAdapter {
			private String firstSequence;
			private FastaParseException stopException = new FastaParseException("Forced stop");
			
			@Override
			public void sequenceNameRead(File file, String sequenceName) throws FastaParseException {
				this.firstSequence = sequenceName;
				throw this.stopException;
			}
		};
		
		final CustomFastaParserAdapter listener = new CustomFastaParserAdapter();
		final FastaParser parser = new DefaultFastaParser();
		
		parser.addParseListener(listener);
		
		try {
			parser.parse(fastaFile);
		} catch (FastaParseException fpe) {
			if (!fpe.equals(listener.stopException))
				throw fpe; 
		}
		
		return listener.firstSequence;
	}
	
	public static StandardSequenceNameSummarizer getFastaSequenceNameSummarizerByFirstSequenceName(File fastaFile)
	throws FastaParseException, IOException {
		return createStandardNameSummarizer(getFirstSequence(fastaFile));
	}
	
	public static String genericFastaSequenceNameRename(
		String sequenceName,
		ComposedSequenceRenameConfiguration renameConfiguration
	) {
		return new GenericComposedSequenceNameSummarizer().summarize(sequenceName, renameConfiguration);
	}
	
	public static void genericFastaSequenceRenaming(
		final File fastaFile,
		final ComposedSequenceRenameConfiguration renameConfiguration,
		final PrintWriter writer
	) throws FastaParseException, IOException {
		genericFastaSequenceRenaming(fastaFile, SequenceLengthConfiguration.buildNoChanges(), renameConfiguration, writer);
	}
	
	public static void genericFastaSequenceRenaming(
		final File fastaFile,
		final SequenceLengthConfiguration sequenceLengthConfiguration,
		final ComposedSequenceRenameConfiguration renameConfiguration,
		final PrintWriter writer
	) throws FastaParseException, IOException {
		final FastaParser parser = new DefaultFastaParser();
		final GenericComposedSequenceNameSummarizer summarizer = createSplitterNameSummarizer();
		
		parser.addParseListener(createFastaParser(sequenceLengthConfiguration, new SequenceRenamer() {
			@Override
			public void sequenceNameRead(File file, String sequenceName) {
				writer.println(summarizer.summarize(sequenceName, renameConfiguration));
			}
			
			@Override
			public void sequenceFragment(File file, String sequenceFragment) {
				writer.println(sequenceFragment);
			}
		}));
		
		parser.parse(fastaFile);
	}
	
	@SuppressWarnings("unchecked")
	public static void mergeFastas(
		final File[] fastaFiles, final File outputFile
	) throws FastaParseException, IOException {
		final Map<String, List<int[]>[]> indexes = new LinkedHashMap<>();
		
		final RandomAccessFile[] rafs = new RandomAccessFile[fastaFiles.length];
		try {
			int i = 0;
			for (File fastaFile : fastaFiles) {
				final Map<String, List<int[]>> fastaIndexes = indexFastaFile(fastaFiles[i]);
				
				for (Map.Entry<String, List<int[]>> seqIndex : fastaIndexes.entrySet()) {
					if (!indexes.containsKey(seqIndex.getKey()))
						indexes.put(seqIndex.getKey(), (List<int[]>[]) new List[fastaFiles.length]);
					
					indexes.get(seqIndex.getKey())[i] = seqIndex.getValue();
				}
				
				rafs[i++] = new RandomAccessFile(fastaFile, "r");
			}
			
			try (PrintWriter pw = new PrintWriter(outputFile)) {
				for (Map.Entry<String, List<int[]>[]> entry : indexes.entrySet()) {
					pw.println(entry.getKey());
					
					final StringBuilder sb = new StringBuilder();
					for (int j = 0; j < rafs.length; j++) {
						final List<int[]> fastaIndexesList = entry.getValue()[j];
						
						for (int[] fastaIndexes : fastaIndexesList) {
							if (fastaIndexes[0] != fastaIndexes[1]) {
								final byte[] data = new byte[fastaIndexes[1] - fastaIndexes[0]];
								rafs[j].seek(fastaIndexes[0]);
								rafs[j].read(data);
								
								sb.append(new String(data));
							}
						}
					}
					
					pw.println(sb.toString().replaceAll("[\n\r]", ""));
				}
			}
		} finally {
			for (RandomAccessFile raf : rafs) {
				if (raf != null)
					try { raf.close(); }
					catch (IOException ioe) {}
			}
		}
	}
	
	public static Map<String, List<int[]>> indexFastaFile(File fasta)
	throws FastaParseException, IOException {
		final Map<String, List<int[]>> indexes = new LinkedHashMap<>();
		final int newLineSize = newLineSize(fasta);
		
		final FastaParser parser = new DefaultFastaParser();
		parser.addParseListener(new FastaParserAdapter() {
			private int index;
			private int[] currentIndexes;
			
			@Override
			public void parseStart(File file) throws FastaParseException {
				this.index = 0;
				this.currentIndexes = null;
			}
			
			@Override
			public void sequenceNameRead(File file, String sequenceName)
			throws FastaParseException {
				if (!indexes.containsKey(sequenceName))
					indexes.put(sequenceName, new LinkedList<int[]>());
				indexes.get(sequenceName).add(this.currentIndexes = new int[2]);
				
				this.index += sequenceName.getBytes().length;
				this.currentIndexes[0] = this.index + newLineSize;
			}
			
			@Override
			public void sequenceFragmentRead(File file, String sequence)
			throws FastaParseException {
				this.index += newLineSize + sequence.getBytes().length;
			}
			
			@Override
			public void emptyLine(File file) {
				this.index += newLineSize;
			}
			
			@Override
			public void sequenceEnd(File file) throws FastaParseException {
				this.currentIndexes[1] = this.index;
				
				this.index += newLineSize;
				this.currentIndexes = null;
			}
		});
		
		parser.parse(fasta);
		
		return indexes;
	}
	
	public static void changeSequenceLength(
		final File fasta,
		final SequenceLengthConfiguration sequenceLengthConfiguration,
		final PrintWriter pw
	) throws IOException, FastaParseException {
		final DefaultFastaParser parser = new DefaultFastaParser();
		parser.addParseListener(new SequenceResizingFastaParserListener(sequenceLengthConfiguration) {
			@Override
			public void sequenceNameRead(File file, String sequenceName) {
				pw.println(sequenceName);
			}
			
			@Override
			public void sequenceEnd(File file, String resizedSequence) {
				pw.println(resizedSequence);
			}
		});
		
		parser.parse(fasta);
	}
	
	public static String resizeSequence(
		final List<String> sequenceFragments, final SequenceLengthConfiguration sequenceLengthConfiguration
	) {
		if (sequenceLengthConfiguration.isNoChange()) {
			return String.join(System.getProperty("line.separator"), sequenceFragments);
		} else {
			final String sequenceNoLB = String.join("", sequenceFragments);
			
			if (sequenceLengthConfiguration.isRemoveLineBreaks()) {
				return sequenceNoLB;
			} else if (sequenceLengthConfiguration.isChangeFragmentLength()) {
				final StringBuilder sb = new StringBuilder();
				final String nl = System.getProperty("line.separator");
				
				final int fragmentLength = sequenceLengthConfiguration.getFragmentLength();
				for (int i = 0; i < sequenceNoLB.length(); i += fragmentLength) {
					final int endIndex = Math.min(sequenceNoLB.length(), i + fragmentLength);
					
					sb.append(sequenceNoLB.substring(i, endIndex)).append(nl);
				}
				
				// Last new line deletion
				sb.delete(sb.length() - nl.length(), sb.length());
				
				return sb.toString();
			} else {
				throw new IllegalStateException("Unexpected sqeuence length configuration");
			}
		}
	}
	
	private static int newLineSize(File file) throws IOException {
		int value;
		
		try (final Reader reader = new FileReader(file)) {
			while ((value = reader.read()) != -1) {
				final char chr = (char) value;
				
				if (chr == '\n') {
					return 1;
				} else if (chr == '\r') {
					value = reader.read();
					
					if (value != -1 && (char) value == '\n') {
						return 2;
					} else {
						return -1;
					}
				}
			}
		}
		
		return -1;
	}
	
	public static abstract class SequenceResizingFastaParserListener
	extends FastaParserAdapter {
		protected final SequenceLengthConfiguration sequenceLengthConfiguration;
		protected List<String> sequence;
		
		public SequenceResizingFastaParserListener(SequenceLengthConfiguration sequenceLengthConfiguration) {
			this.sequenceLengthConfiguration = sequenceLengthConfiguration;
		}
		
		@Override
		public void sequenceStart(File file) throws FastaParseException {
			this.sequence = new LinkedList<>();
		}
		
		@Override
		public void sequenceFragmentRead(File file, String fragment) {
			this.sequence.add(fragment);
		}
		
		@Override
		public void sequenceEnd(File file) {
			this.sequenceEnd(file, resizeSequence(sequence, this.sequenceLengthConfiguration));
			this.sequence = null;
		}
		
		public abstract void sequenceEnd(File file, String resizedSequence);
	}
	
	private static interface SequenceRenamer {
		public void sequenceNameRead(File file, String sequenceName);
		public void sequenceFragment(File file, String sequenceFragment);
	}
	
	private static FastaParserListener createFastaParser(
		final SequenceLengthConfiguration sequenceLengthConfiguration,
		final SequenceRenamer renamer
	) {
		if (sequenceLengthConfiguration.isNoChange()) {
			return new FastaParserAdapter() {
				@Override
				public void sequenceNameRead(File file, String sequenceName) {
					renamer.sequenceNameRead(file, sequenceName);
				}
				
				@Override
				public void sequenceFragmentRead(File file, String sequenceFragment) {
					renamer.sequenceFragment(file, sequenceFragment);
				}
			};
		} else {
			return new SequenceResizingFastaParserListener(sequenceLengthConfiguration) {
				@Override
				public void sequenceNameRead(File file, String sequenceName) {
					renamer.sequenceNameRead(file, sequenceName);
				}
				
				@Override
				public void sequenceEnd(File file, String resizedSequence) {
					renamer.sequenceFragment(file, resizedSequence);
				}
			};
		}
	}

	public static void mergeSequences(
		final File inputFasta,
		final PrintWriter writer,
		final String ... ignoreSequences
	) throws FastaParseException, IOException {
		final Map<String, List<int[]>> index = indexFastaFile(inputFasta);
		
		try (final RandomAccessFile inputFastaFile = new RandomAccessFile(inputFasta, "r")) {
			Arrays.sort(ignoreSequences);
			for (Map.Entry<String, List<int[]>> entry : index.entrySet()) {
				if (Arrays.binarySearch(ignoreSequences, entry.getKey().substring(1)) < 0) {
					writer.println(entry.getKey());
	
					for (int[] location : entry.getValue()) {
						final byte[] data = new byte[location[1] - location[0]];
						inputFastaFile.seek(location[0]);
						inputFastaFile.read(data);
						
						writer.append(new String(data));
					}
					
					writer.println();
				}
			}
		}
	}
	
	public static void mergeConsecutiveSequences(
		final File inputFasta,
		final PrintWriter writer,
		final String ... ignoreSequences
	) throws FastaParseException, IOException {
		Arrays.sort(ignoreSequences);
		
		final FastaParser parser = new DefaultFastaParser();
		parser.addParseListener(new FastaParserAdapter() {
			private boolean firstSequence;
			private String currentSequence = null;
			
			@Override
			public void parseStart(File file) throws FastaParseException {
				this.firstSequence = true;
			}
			
			@Override
			public void sequenceNameRead(File file, String sequenceName)
			throws FastaParseException {
				if (Arrays.binarySearch(ignoreSequences, sequenceName.substring(1)) >= 0) {
					this.currentSequence = null;
				} else if (this.currentSequence == null || !this.currentSequence.equals(sequenceName)) {
					this.currentSequence = sequenceName;
					
					if (this.firstSequence) {
						this.firstSequence = false;
					} else {
						writer.println();
					}
					
					writer.println(this.currentSequence);
				}
			}
			
			@Override
			public void sequenceFragmentRead(File file, String sequenceFragment)
			throws FastaParseException {
				if (this.currentSequence != null) {
					writer.print(sequenceFragment);
				}
			}
			
			@Override
			public void parseEnd(File file) throws FastaParseException {
				if (!this.firstSequence)
					writer.println();
			}
		});
		
		parser.parse(inputFasta);
	}

	public static void removeSequences(
		final File inputFasta,
		final PrintWriter writer,
		final String ... sequences
	) throws FastaParseException, IOException {
		Arrays.sort(sequences);
		
		final FastaParser parser = new DefaultFastaParser();
		parser.addParseListener(new FastaParserAdapter() {
			private boolean ignoreSequence;
			
			@Override
			public void sequenceNameRead(File file, String sequenceName)
			throws FastaParseException {
				if (Arrays.binarySearch(sequences, sequenceName.substring(1)) >= 0) {
					this.ignoreSequence = true;
				} else {
					this.ignoreSequence = false;
					writer.println(sequenceName);
				}
			}
			
			@Override
			public void sequenceFragmentRead(File file, String sequenceFragment)
			throws FastaParseException {
				if (!this.ignoreSequence) {
					writer.print(sequenceFragment);
				}
			}
			
			@Override
			public void sequenceEnd(File file) throws FastaParseException {
				if (!this.ignoreSequence)
					writer.println();
			}
		});
		
		parser.parse(inputFasta);
	}

	public static String extractFastaSequenceName(String line) {
		if (line.startsWith(">") && line.length() > 1) {
			if (line.contains(" ")) {
				return line.substring(1, line.indexOf(' '));
			} else {
				return line.substring(1);
			}
		} else {
			throw new IllegalArgumentException("Invalid sequence name: " + line);
		}
	}
}
