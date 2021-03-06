/*-
 * #%L
 * BDBM API
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

package es.uvigo.ei.sing.bdbm.environment.binaries;

import java.util.Map;

public interface BLASTBinaries extends Binaries {
	public final static String BLAST_PREFIX = "blast.";
	
	public final static String BASE_DIRECTORY_PROP = 
		BLAST_PREFIX + "binDir";
	
	public final static String MAKE_BLAST_DB_PROP = 
		BLAST_PREFIX + "makeblastdb";
	public final static String BLASTDB_ALIASTOOL_PROP = 
		BLAST_PREFIX + "blastdb_aliastool";
	public final static String BLASTDB_CMD_PROP = 
		BLAST_PREFIX + "blastdbcmd";
	public final static String BLAST_N_PROP = 
		BLAST_PREFIX + "blastn";
	public final static String BLAST_P_PROP = 
		BLAST_PREFIX + "blastp";
	public final static String T_BLAST_X_PROP = 
		BLAST_PREFIX + "tblastx";
	public final static String T_BLAST_N_PROP = 
		BLAST_PREFIX + "tblastn";

	public abstract String getMakeBlastDB();
	public abstract String getBlastDBAliasTool();
	public abstract String getBlastDBCmd();
	public abstract String getBlastN();
	public abstract String getBlastP();
	public abstract String getTBlastX();
	public abstract String getTBlastN();
	public abstract String getBlast(BLASTType blastType);
	public abstract Map<String, String> getConfigurationParameters();
}
