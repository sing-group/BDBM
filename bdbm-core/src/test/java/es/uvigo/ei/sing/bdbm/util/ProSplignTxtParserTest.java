/*-
 * #%L
 * BDBM Core
 * %%
 * Copyright (C) 2014 - 2017 Miguel Reboiro-Jato, Critina P. Vieira, Hugo
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
package es.uvigo.ei.sing.bdbm.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class ProSplignTxtParserTest {
	public static final Path TEST_FILE = new File(
		"src/test/resources/prosplign/pro.txt").toPath();

	private static final Map<String, String> QUERY_SEQUENCES_MAPPING = new HashMap<>();
	static {
		QUERY_SEQUENCES_MAPPING.put("NP_032143.1", "MAPPED_QUERY");
	}
	
	private static final Map<String, String> SUBJECT_SEQUENCES_MAPPING = new HashMap<>();
	static {
		SUBJECT_SEQUENCES_MAPPING.put("NT_010783.14", "MAPPED_SUBJECT");
	}
	
	private static final String FIRST_SEQUENCE = 
		"TCCCGGACGTCCCTGCTCCTGGCTTTTGCCCTGCTCTGCCTGCCCTGGCTTCAAGAGGCTGGTGCCGTCCAAACCGTTCCGTTATCCAGGCTTTTTGACCAC"
		+ "GCTATGCTCCAAGCCCATCGCGCGCACCAGCTGGCCATTGACACCTACCAGGAGTTTGAAGAAACCTATATCCCAAAGGACCAGAAGTATTCATTCCTGC"
		+ "ATGACTCCCAGACCTCCTTCTGCTTCTCAGACTCTATTCCGACACCCTCCAACATGGAGGAAACGCAACAGAAATCCAATCTAGAGCTGCTCCGCATCTC"
		+ "CCTGCTGCTCATCGAGTCGTGGCTGGAGCCCGTGCGGTTCCTCAGGAGTATGTTCGCCAACAACCTGGTGTATGACACCTCGGACAGCGATGACTATCAC"
		+ "CTCCTAAAGGACCTAGAGGAAGGCATCCAAACGCTGATGGGGAGGCTGGAAGACGGCAGCCGCCGGACTGGGCAGATCCTCAAGCAGACCTACAGCAAGT"
		+ "TTGACACAAACTCACACAACCATGACGCACTGCTCAAGAACTACGGGCTGCTCTACTGCTTCAGGAAGGACATGGACAAGGTCGAGACATTCCTGCGCAT"
		+ "GGTGCAGTGCCGCTCTGTAGAGGGTAGCTGT";
	
	private static final String FIRST_RESULT_SEQUENCE = 
		"TGTGGACAGCTCACCTAGCGGCAATGGCTGCAGGTAAGCGCCCCTAAAATCCCTTTGGGCACAACGTGTCCTGAGGGGAGAGGCAGCGCCCTGTAGATGG"
		+ "GACGGGGGCACTAACCCTCAGGTTTGGGGCTTATGAATGTGAGTATCGCCATCTAAGGCCAGATATTTGGCCAATCTCTGAATGTTCCTGGTCTCTGGAG"
		+ "GGATGGAGAGAGAGAAAAAAACAAACAGCTCCTGGAGCAGGGAGAGCGCTGGCCTCTTCCTCTCCGGCTCCCTCCATTGCCCTCCGGTTTCTCCCCAGGC"
		+ "TCCCGGACGTCCCTGCTCCTGGCTTTTGCCCTGCTCTGCCTGCCCTGGCTTCAAGAGGCTGGTGCCGTCCAAACCGTTCCGTTATCCAGGCTTTTTGACC"
		+ "ACGCTATGCTCCAAGCCCATCGCGCGCACCAGCTGGCCATTGACACCTACCAGGAGTTTGTAAGTTCTTGGGGAATGGGTGCGGGTCAGGGGTGGCAAGA"
		+ "AGGGGTGACTTTCCCCCACTGGGGAAGTAATGGGAGGAGACTAAGGAGCTCAGGGTTGTTTTCTGAAGCGAAAATGCAGGCAGATGAGCATAGGCTGAGC"
		+ "CAGGTTCCCAGAAAAGCAACAATGGGAGCTGGTCTCCAGCATAGAAACCAGCAGTCCTTCTTGGTGGGGGGTCCTTCTCCTAGGAAGAAACCTATATCCC"
		+ "AAAGGACCAGAAGTATTCATTCCTGCATGACTCCCAGACCTCCTTCTGCTTCTCAGACTCTATTCCGACACCCTCCAACATGGAGGAAACGCAACAGAAA"
		+ "TCCGTGAGTGGATGCCGTCTCCCCTAGGCGGGGATGGGGGAGACCTGTGGTCAGGGCTCCCGGGCAGCACAGCCACTGCCGGTCCTTCCCCTGCAGAATC"
		+ "TAGAGCTGCTCCGCATCTCCCTGCTGCTCATCGAGTCGTGGCTGGAGCCCGTGCGGTTCCTCAGGAGTATGTTCGCCAACAACCTGGTGTATGACACCTC"
		+ "GGACAGCGATGACTATCACCTCCTAAAGGACCTAGAGGAAGGCATCCAAACGCTGATGGGGGTGAGGGTGGCGCCAGGGGTCGCCAATCCTGGAACCCCA"
		+ "CTGGCTTAGAGGGCTGGGGGAGAGAAACACTGCTGCCCTCTTTGTAGCAGTCAGGCGCTGACCCAAGAGAACTCACCTTATTCTTCATTTCGCCTGGTGA"
		+ "ATCCTCCAGGCCCTTCTCTACACCCTGAAGGGGAGGGAGGAAAATGGATGAATGAGAGAGGGAGGGAACAGTGCCCAAGCGCTTGGCCTCTCCTTCTCTT"
		+ "CCTTCACTTTGCAGAGGCTGGAAGACGGCAGCCGCCGGACTGGGCAGATCCTCAAGCAGACCTACAGCAAGTTTGACACAAACTCACACAACCATGACGC"
		+ "ACTGCTCAAGAACTACGGGCTGCTCTACTGCTTCAGGAAGGACATGGACAAGGTCGAGACATTCCTGCGCATGGTGCAGTGCCGCTCTGTAGAGGGT"
		+ "AGCTGTGGCTTCTAGGTGCCCGCGTGGCATCCTGTGACCGACCCCTCCCCAGTGCCTCTCCTGGCCCTGGAAGGTGCCACTCCAGTGCCCATCAGCCTTG"
		+ "TCCTAATAAAATTAAGTTGTATCATTTCATCTGACTAGGTGTCATTCTATAATATTATGGGGTGGAAGGTGGTGGTATGGAGCAAGGGGTAGGTGGAAAG"
		+ "AAGACCTGGAGGGCCTTCAAGGTCTATTGGGAACTAGGCCGCTGAAATATAAGAGGCTTGGCTGTTCCTGGGCCAGAAAAAGGCTGACACATCACCGCTA";
	
	@Test
	public void testProSplignTxtParser() throws IOException {
		ProSplignTxtParser parser = new ProSplignTxtParser();
		parser.parse(TEST_FILE, QUERY_SEQUENCES_MAPPING,
			SUBJECT_SEQUENCES_MAPPING);
		List<String> sequences = parser.getSequences();
		List<String> fullSequences = parser.getFullSequences();

		Assert.assertEquals(14, sequences.size());
		Assert.assertTrue(sequences.get(1).startsWith(FIRST_SEQUENCE));
		Assert.assertEquals(FIRST_SEQUENCE, sequences.get(1));

		Assert.assertEquals(10, fullSequences.size());
		Assert.assertEquals(FIRST_RESULT_SEQUENCE, fullSequences.get(1));
	}
}