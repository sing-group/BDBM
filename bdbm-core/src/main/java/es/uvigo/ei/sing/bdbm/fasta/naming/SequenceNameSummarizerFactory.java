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

package es.uvigo.ei.sing.bdbm.fasta.naming;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import es.uvigo.ei.sing.bdbm.fasta.naming.configuration.ComposedSequenceRenameConfiguration;
import es.uvigo.ei.sing.bdbm.fasta.naming.standard.StandardSequenceNameSummarizer;

public class SequenceNameSummarizerFactory {
	private final static ServiceLoader<StandardSequenceNameSummarizer> SL_SUMMARIZERS =
		ServiceLoader.load(StandardSequenceNameSummarizer.class);

	/*
	 * NCBI - http://www.ncbi.nlm.nih.gov/toolkit/doc/book/ch_demo/?rendertype=table&id=ch_demo.T5
	 * local                        lcl|integer                                 lcl|123
	 *                              lcl|string                                  lcl|hmm271	 
	 * GenInfo backbone seqid       bbs|integer                                 bbs|123
	 * GenInfo backbone moltype     bbm|integer                                 bbm|123
	 * GenInfo import ID            gim|integer                                 gim|123
	 * GenBank                      gb|accession|locus                          gb|M73307|AGMA13GT
	 * EMBL                         emb|accession|locus                         emb|CAM43271.1|
	 * PIR                          pir|accession|name                          pir||G36364
	 * SWISS-PROT                   sp|accession|name                           sp|P01013|OVAX_CHICK
	 * patent                       pat|country|patent|sequence                 pat|US|RE33188|1
	 * pre-grant patent             pgp|country|application-number|seq-number   pgp|EP|0238993|7
	 * RefSeq 2                     ref|accession|name                          ref|NM_010450.1|
	 * general database reference   gnl|database|integer                        gnl|taxon|9606
	 *                              gnl|database|string                         gnl|PID|e1632 
	 * GenInfo integrated database  gi|integer                                  gi|21434723
	 * DDBJ                         dbj|accession|locus                         dbj|BAC85684.1|
	 * PRF                          prf|accession|name                          prf||0806162C
	 * PDB                          pdb|entry|chain                             pdb|1I4L|D
	 * third-party GenBank          tpg|accession|name                          tpg|BK003456|
	 * third-party EMBL             tpe|accession|name                          tpe|BN000123|
	 * third-party DDBJ             tpd|accession|name                          tpd|FAA00017|
	 * TrEMBL                       tr|accession|name                           tr|Q90RT2|Q90RT2_9HIV1
	 * genome pipeline 3            gpp|accession|name                          gpp|GPC_123456789|
	 * named annotation track 3     nat|accession|name                          nat|AT_123456789.1|
	 * 
	 * Wikipedia (ES|EN) - NOT IMPLEMENTED
	 * GenBank                           gi|gi-number|gb|accession|locus
	 * EMBL Data Library                 gi|gi-number|emb|accession|locus
	 * DDBJ, DNA Database of Japan       gi|gi-number|dbj|accession|locus
	 * 
	 * NBRF PIR                          pir||entry
	 * Protein Research Foundation       prf||name
	 * Brookhaven Protein Data Bank (2)  entry:chain|PDBID|CHAIN|SEQUENCE
	 * 
	 * Patents                           pat|country|number
	 */
	public static StandardSequenceNameSummarizer createStandardNameSummarizer(String name) {
		for (StandardSequenceNameSummarizer summarizer : SL_SUMMARIZERS) {
			if (summarizer.recognizes(name)) 
				return summarizer;
		}
		
		return null;
	}
	
	public static StandardSequenceNameSummarizer createStandardNameSummarizer(String name, ComposedSequenceRenameConfiguration configuration) {
		for (StandardSequenceNameSummarizer summarizer : SL_SUMMARIZERS) {
			if (summarizer.recognizes(name, configuration)) 
				return summarizer;
		}
		
		return null;
	}
	
	public static Map<String, String> getStandardSummarizerInfo() {
		final Map<String, String> summarizers = new HashMap<>();

		for (StandardSequenceNameSummarizer summarizer : SL_SUMMARIZERS) {
			summarizers.put(summarizer.getPrefix(), summarizer.getDescription());
		}
		
		return summarizers;
	}
	
	public static GenericComposedSequenceNameSummarizer createSplitterNameSummarizer() {
		return new GenericComposedSequenceNameSummarizer();
	}
	
	public static PrefixSequenceNameSummarizer createPrefixSequenceNameSummarizer() {
		return new PrefixSequenceNameSummarizer();
	}
	
	public static PrefixSequenceNameSummarizer createPrefixSequenceNameSummarizer(int initialCounterValue) {
		return new PrefixSequenceNameSummarizer(initialCounterValue);
	}
}
