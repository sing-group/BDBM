# ![Logo](bdbm-logo.png) BLAST DataBase Manager Project [![license](https://img.shields.io/badge/LICENSE-GPLv3-blue.svg)](https://github.com/sing-group/BDBM/) [![release](https://img.shields.io/github/release/sing-group/BDBM.svg)](http://www.sing-group.org/BDBM/download.html)
> *BLAST DataBase Manager* provides a graphical user interface to create high quality sequence datasets using the third-party tools included in it.

## Motivation

High quality sequence datasets are needed to perform inferences on the evolution of species, genes and gene families, or to get evidence for adaptive amino acid evolution, among others. Nevertheless, very often, sequence data is spread over several databases, many useful genomes and transcriptomes are non-annotated, the available annotation is not for the desired CDS isoform, and/or the gene annotation is unlikely to be true given the annotation based on real data from a closely related species. These issues can be addressed using the available software applications, but there is no easy to use single piece of software that allows performing all these tasks within the same graphical interface, such as the one here presented, named BDBM (*BLAST DataBase Manager*).

BDBM is a software application implemented in Java that acts as a front-end for several tools commonly used in phylogenetics (i.e. [EMBOSS](http://emboss.sourceforge.net/), [bedtools](http://bedtools.readthedocs.org/), and NCBI's [BLAST](http://blast.ncbi.nlm.nih.gov/), [Splign](http://www.ncbi.nlm.nih.gov/sutils/splign/splign.cgi), [Compart](http://www.ncbi.nlm.nih.gov/IEB/ToolBox/CPP_DOC/doxyhtml/dir_cdca7f19e05338435a42c4b6982717a2.html), [ProSplign](https://www.ncbi.nlm.nih.gov/sutils/static/prosplign/prosplign.html) and [ProCompart](https://www.ncbi.nlm.nih.gov/sutils/static/prosplign/prosplign.html)) providing a GUI that makes them much easier to use. In addition, BDBM manages a repository, where all the input and output files are stored.

More info can be found at the [BDBM Home Page](https://www.sing-group.org/BDBM), including:

* [Manual](https://www.sing-group.org/BDBM/manual.html): a complete manual that describes the BDBM operations.
* [Use Cases](https://www.sing-group.org/BDBM/usecases.html): a section that presents different guided examples.
* [Downloads](https://www.sing-group.org/BDBM/download.html): downloads and installation instructions.

<p align="center">
  <img height="560" src="bdbm-gui/src/main/resources/help/HTML/Images/ViewSearchFiles.png">
</p>

## Modules
This project is comprised of the following modules:

* BDBM-API: Contains the main interfaces of the BDBM project.
* BDBM-Core: Contains the default implementation of the BDBM API.
* BDBM-CLI: Contains a command-line interface client for BDBM.
* BDBM-GUI: Contains a graphical user interface client for BDBM.

## Team

This project is an idea and is developed by:

* Jorge Vieira [Molecular Evolution Group](http://evolution.ibmc.up.pt)
* Miguel Reboiro-Jato [SING Group](https://www.sing-group.org/)
* Cristina P. Vieira [Molecular Evolution Group](http://evolution.ibmc.up.pt)
* Florentino Fdez-Riverola [SING Group](https://www.sing-group.org/)
* Hugo López-Fernández [SING Group](https://www.sing-group.org/)
* Noé Vázquez-Fdez [SING Group](https://www.sing-group.org/)
