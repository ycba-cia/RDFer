
This directory contains the various test cases used by the RepairTool (Doctor + Analysis) JUnit tests.

For each test, there is a 'report', 'reportoutput', 'model' and 'output' n3 file.

The purpose of these files are as follows -
	+ '...report' :::::::: The input Report to the repair
	+ '...reportoutput' :: The expected Report output after the analysis
	+ '...model' ::::::::: The input OntModel to the repair
	+ '...output' :::::::: The expected output from the doctor

Where '...' is replaced by the shortName of each {Doctor,Analysis} (see ../etc/eyeball2-config.n3)

