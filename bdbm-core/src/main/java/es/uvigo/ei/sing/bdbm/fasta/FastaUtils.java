/*
 * #%L
 * BDBM Core
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
package es.uvigo.ei.sing.bdbm.fasta;

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
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import es.uvigo.ei.sing.bdbm.fasta.naming.GenericNameSummarizer;
import es.uvigo.ei.sing.bdbm.fasta.naming.NameSummarizerFactory;
import es.uvigo.ei.sing.bdbm.fasta.naming.StandardNameSummarizer;

public class FastaUtils {
	public static enum RenameMode {
		NONE, SMART, GENERIC, PREFIX;
	}

	public static File[] splitFastaIntoFiles(File fastaFile) throws IOException, FastaParseException {
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
	
	public static void fastaSequenceRenaming(RenameMode mode, File fastaFile, PrintWriter writer, Map<ReformatFastaParameters, Object> additionalParameters)
	throws FastaParseException, IOException {
		fastaSequenceRenaming(mode, fastaFile, 0, writer, additionalParameters);
	}
	
	public static void fastaSequenceRenaming(
		RenameMode mode, File fastaFile, int fragmentLength, PrintWriter writer, Map<ReformatFastaParameters, Object> additionalParameters
	) throws FastaParseException, IOException {
		switch(mode) {
		case GENERIC: {
			final Object paramIndexes = additionalParameters.get(ReformatFastaParameters.INDEXES);
			if (!(paramIndexes instanceof int[])) {
				throw new IllegalArgumentException("Invalid indexes value");
			}
			final Object paramSeparator = additionalParameters.get(ReformatFastaParameters.SEPARATOR);
			if (!(paramSeparator instanceof String)) {
				throw new IllegalArgumentException("Invalid separator value");
			}
			
			genericFastaSequenceRenaming(fastaFile, (int[]) paramIndexes, (String) paramSeparator, fragmentLength, writer);
			break;
		}
		case PREFIX: {
			final Object paramPrefix = additionalParameters.get(ReformatFastaParameters.PREFIX);
			if (!(paramPrefix instanceof String)) {
				throw new IllegalArgumentException("Invalid prefix value");
			}
			final Object paramKeepName = additionalParameters.get(ReformatFastaParameters.KEEP_NAMES_WHEN_PREFIX);
			if (!(paramKeepName instanceof Boolean)) {
				throw new IllegalArgumentException("Invalid keep name value");
			}
			final Object paramAddIndex = additionalParameters.get(ReformatFastaParameters.ADD_INDEX_WHEN_PREFIX);
			if (!(paramAddIndex instanceof Boolean)) {
				throw new IllegalArgumentException("Invalid add index value");
			}
			final Object paramSeparator = additionalParameters.get(ReformatFastaParameters.SEPARATOR);
			if (!(paramPrefix instanceof String)) {
				throw new IllegalArgumentException("Invalid separator value");
			}
			
			prefixFastaSequenceRenaming(fastaFile, 
				(String) paramPrefix, (Boolean) paramKeepName, (Boolean) paramAddIndex, (String) paramSeparator, 
				fragmentLength, writer
			);
			break;
		}
		case SMART: {
			final Object paramSeparator = additionalParameters.get(ReformatFastaParameters.SEPARATOR);
			if (!(paramSeparator instanceof String)) {
				throw new IllegalArgumentException("Invalid separator value");
			}
			
			smartFastaSequenceRenaming(fastaFile, (String) paramSeparator, fragmentLength, writer);
			break;
		}
		case NONE:
			changeSequenceLength(fastaFile, fragmentLength, writer);
			break;
		}
	}
	
	public static void prefixFastaSequenceRenaming(File fastaFile, final String prefix, final boolean keepNames, final boolean addIndex, final String separator, final PrintWriter writer)
	throws FastaParseException, IOException {
		prefixFastaSequenceRenaming(fastaFile, prefix, keepNames, addIndex, separator, 0, writer);
	}
	
	public static void prefixFastaSequenceRenaming(File fastaFile, final String prefix, final boolean keepNames, final boolean addIndex, final String separator, final int fragmentLength, final PrintWriter writer)
	throws FastaParseException, IOException {
		if (prefix == null && !addIndex && !keepNames) {
			throw new IllegalArgumentException("At least prefix must be not null or addIndex true or keepNames true");
		}
		
		final FastaParser parser = new DefaultFastaParser();
		
		parser.addParseListener(createFastaParser(fragmentLength, new SequenceRenamer() {
			int prefixCounter = 1;
			
			@Override
			public void sequenceNameRead(File file, String sequenceName) {
				writer.print(">");
				
				if (prefix != null)
					writer.print(prefix);
				
				if (addIndex) {
					if (prefix != null) writer.print(separator);
					writer.print(prefixCounter++);
				}
				if (keepNames) {
					if (prefix != null || addIndex) writer.print(separator);
					writer.print(sequenceName.substring(1));
				}
				writer.println();
			}
			
			@Override
			public void sequenceFragment(File file, String sequenceFragment) {
				writer.println(sequenceFragment);
			}
		}));
		
		parser.parse(fastaFile);
	}
	
	public static void smartFastaSequenceRenaming(File fastaFile, final String separator, final PrintWriter writer)
	throws FastaParseException, IOException {
		smartFastaSequenceRenaming(fastaFile, separator, 0, writer);
	}
	
	public static void smartFastaSequenceRenaming(File fastaFile, final String separator, final int fragmentLength, final PrintWriter writer)
	throws FastaParseException, IOException {
		final FastaParser parser = new DefaultFastaParser();
		
		parser.addParseListener(createFastaParser(fragmentLength, new SequenceRenamer() {
			@Override
			public void sequenceNameRead(File file, String sequenceName) {
				final StandardNameSummarizer summarizer = NameSummarizerFactory.createStandardNameSummarizer(sequenceName);
				if (summarizer == null)
					throw new RuntimeException("Unrecognized sequence name: " + sequenceName);
				summarizer.setSeparator(separator);
				
				writer.println(summarizer.summarize(sequenceName));
			}
			
			@Override
			public void sequenceFragment(File file, String sequenceFragment) {
				writer.println(sequenceFragment);
			}
		}));
		
		parser.parse(fastaFile);
	}
	
	public static void genericFastaSequenceRenaming(File fastaFile, int[] indexes, String separator, final PrintWriter writer)
	throws FastaParseException, IOException {
		genericFastaSequenceRenaming(fastaFile, indexes, separator, 0, writer);
	}
	
	public static void genericFastaSequenceRenaming(File fastaFile, final int[] indexes, final String separator, final int fragmentLength, final PrintWriter writer)
	throws FastaParseException, IOException {
		final FastaParser parser = new DefaultFastaParser();
		final GenericNameSummarizer summarizer = NameSummarizerFactory.createGenericNameSummarizer(indexes);
		summarizer.setSeparator(separator);
		
		parser.addParseListener(createFastaParser(fragmentLength, new SequenceRenamer() {
			@Override
			public void sequenceNameRead(File file, String sequenceName) {
				writer.println(summarizer.summarize(sequenceName));
			}
			
			@Override
			public void sequenceFragment(File file, String sequenceFragment) {
				writer.println(sequenceFragment);
			}
		}));
		
		parser.parse(fastaFile);
	}
	
	@SuppressWarnings("unchecked")
	public static void mergeFastas(final File[] fastaFiles, File outputFile) throws FastaParseException, IOException {
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
	
	public static Map<String, List<int[]>> indexFastaFile(File fasta) throws FastaParseException, IOException {
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
			public void sequenceEnd(File file) throws FastaParseException {
				this.currentIndexes[1] = this.index;
				
				this.index += newLineSize;
				this.currentIndexes = null;
			}
		});
		
		parser.parse(fasta);
		
		return indexes;
	}
	
	public static void changeSequenceLength(File fasta, final int fragmentLength, final PrintWriter pw)
	throws IOException, FastaParseException {
		final DefaultFastaParser parser = new DefaultFastaParser();
		parser.addParseListener(new SequenceResizingFastaParserListener(fragmentLength) {
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
	
	public static String resizeSequence(String sequence, int fragmentLength) {
		if (fragmentLength < 0) {
			return sequence;
		} else {
			sequence = sequence.replaceAll("[\n\r]", "");
			
			if (fragmentLength == 0 || fragmentLength > sequence.length()) {
				return sequence;
			} else {
				final StringBuilder sb = new StringBuilder();
				final String nl = System.getProperty("line.separator");
				
				for (int i = 0; i < sequence.length(); i += fragmentLength) {
					final int endIndex = Math.min(sequence.length(), i + fragmentLength);
					
					sb.append(sequence.substring(i, endIndex)).append(nl);
				}
				
				// Last new line deletion
				sb.delete(sb.length() - nl.length(), sb.length());
				
				return sb.toString();
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
	
	public static abstract class SequenceResizingFastaParserListener extends FastaParserAdapter {
		protected final int fragmentLength;
		protected StringBuilder sequence;
		
		public SequenceResizingFastaParserListener(int fragmentLength) {
			this.fragmentLength = fragmentLength;
		}
		
		@Override
		public void sequenceStart(File file) throws FastaParseException {
			this.sequence = new StringBuilder();
		}
		
		@Override
		public void sequenceFragmentRead(File file, String fragment) {
			this.sequence.append(fragment);
		}
		
		@Override
		public void sequenceEnd(File file) {
			this.sequenceEnd(file, resizeSequence(sequence.toString(), this.fragmentLength));
			this.sequence = null;
		}
		
		public abstract void sequenceEnd(File file, String resizedSequence);
	}
	
	private static interface SequenceRenamer {
		public void sequenceNameRead(File file, String sequenceName);
		public void sequenceFragment(File file, String sequenceFragment);
	}
	
	private static FastaParserListener createFastaParser(final int fragmentLength, final SequenceRenamer renamer) {
		if (fragmentLength == 0) {
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
			return new SequenceResizingFastaParserListener(fragmentLength) {
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

	/**
	 * Merges the sequences with the same name in the input FASTA file.
	 * 
	 * @param inputFasta input FASTA file.
	 * @param writer output writer.
	 * @throws IOException 
	 * @throws FastaParseException 
	 */
	public static void mergeSequences(File inputFasta, PrintWriter writer, String ... ignoreSequences)
	throws FastaParseException, IOException {
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
	public static void mergeConsecutiveSequences(final File inputFasta, final PrintWriter writer, final String ... ignoreSequences)
	throws FastaParseException, IOException {
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

	public static void removeSequences(final File inputFasta, final PrintWriter writer, final String ... sequences)
	throws FastaParseException, IOException {
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
}
