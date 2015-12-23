Blast DataBase Manager Project
==============================
High quality sequence datasets are needed to perform inferences on the evolution of species, genes and gene families, or to get evidence for adaptive amino acid evolution, among others. Nevertheless, very often, sequence data is spread over several databases, many useful genomes and transcriptomes are non-annotated, the available annotation is not for the desired CDS isoform, and/or the gene annotation is unlikely to be true given the annotation based on real data from a closely related species. These issues can be addressed using the available software applications, but there is no easy to use single piece of software that allows performing all these tasks within the same graphical interface, such as the one here presented, named BDBM (Blast DataBase Manager).

BDBM is a software application implemented in Java that acts as a front-end for several tools commonly used in phylogenetics (i.e. [EMBOSS](http://emboss.sourceforge.net/), [bedtools](http://bedtools.readthedocs.org/), and NCBI's [BLAST](http://blast.ncbi.nlm.nih.gov/), [Splign](http://www.ncbi.nlm.nih.gov/sutils/splign/splign.cgi) and [Compart](http://www.ncbi.nlm.nih.gov/IEB/ToolBox/CPP_DOC/doxyhtml/dir_cdca7f19e05338435a42c4b6982717a2.html), providing a GUI that makes them much easier to use. In addition, BDBM manages a repository, where all the input and output files are stored.

More info at the [BDBM Home Page](http://sing.ei.uvigo.es/BDBM).

Modules
-------
This project is comprised of the following modules:
* BDBM-API: Contains the main interfaces of the BDBM project.
* BDBM-Core: Contains the default implementation of the BDBM API.
* BDBM-CLI: Contains a command-line interface client for BDBM.
* BDBM-GUI: Contains a graphical user interface client for BDBM.

Team
----
This project is an idea and is developed by:
* Jorge Vieira [Molecular Evolution Group](http://evolution.ibmc.up.pt)
* Miguel Reboiro-Jato [SING Group](http://sing.ei.uvigo.es)
* Cristina P. Vieira [Molecular Evolution Group](http://evolution.ibmc.up.pt)
* Florentino Fdez-Riverola [SING Group](http://sing.ei.uvigo.es)
* Hugo López-Fdez [SING Group](http://sing.ei.uvigo.es)
* Noé Vázquez-Fdez [SING Group](http://sing.ei.uvigo.es)
