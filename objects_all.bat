@ECHO off
:: example command to excecute to get data into triple store
:: Add context file
:: curl -X PUT http://collections.britishart.yale.edu/openrdf-sesame/repositories/ycba/rdf-graphs/service?graph=http://collections.britishart.yale.edu/it/object/499 -H "Content-Type:application/rdf+xml" -T @C:\rdf_data_path\oai_tms.ycba.yale.edu_499.rdf
:: Remove context
:: curl -X POST http://collections.britishart.yale.edu/openrdf-sesame/repositories/ycba/rdf-graphs/service?graph=http://collections.britishart.yale.edu/it/object/499

:: remember start time 
set t0=%time: =0%
:: define URIs
set prefix=http://collection.britishart.yale.edu
::set endpoint=http://localhost/openrdf-sesame/repositories/fts_test5
set endpoint=http://collection.britishart.yale.edu/openrdf-sesame/repositories/ycba

:: where the rdf files are located
set source=C:\Users\acdr4\Desktop\RDFer_v3\RdfOutput

:: where ontology files are located
set ontologies=C:\Users\acdr4\Desktop\RDFer_v3\RdfOutput\Ontologies

:: where RelBuilder rdfs are located
set relbuilder=C:\Users\acdr4\Desktop\RDFer_v3\RdfOutput\relbuilder\rdf\

:: OAI Harvester - harvests latest xml files
chdir /D harvest
:: call harvest_dev.bat
UMHarvester_dev.pl -v lr -i ycba -f lido
chdir /D .. 
::ECHO Done harvesting objects...

:: ImageHarvest - harvest images
java -jar ImageHarvest\ImageHarvest.jar -s harvest\data\lido -o harvest\data\images
::echo Done harvesting images...

:: convert xml files to rdf
java -jar RDFConverter\RDFConverter.jar -c RDFConverter\config\objects.properties
echo Done converting object xml files to rdf...

:: convert image xml files to rdf
java -jar RDFConverter\RDFConverter.jar -c RDFConverter\config\images.properties
echo Done converting image xml files to rdf...

:: connect objects to images
java -jar RDFConverter\RDFConverter.jar -c RDFConverter\config\objects2images.properties
echo Done connecting objects to images...

::start uploading rdf files to repository

::upload objects2images rdfs
for /f "delims=" %%X in ('dir /a-d /b /s "%source%\objects2images\rdf\"') do (
	for /f "tokens=10 delims=\." %%a in ("%%X") do (
		echo %%a
		call curl -X PUT %endpoint%/rdf-graphs/service?graph=%prefix%/id/object/%%a/graph -H "Content-Type:application/rdf+xml" -d "@%%X"
	)
)
echo Done uploading objects2images rdfs...

::upload images rdfs
for /f "delims=" %%X in ('dir /a-d /b /s "%source%\images\rdf"') do (
	for /f "tokens=10 delims=\." %%a in ("%%X") do (
		echo %%a
		call curl -X PUT %endpoint%/rdf-graphs/service?graph=%prefix%/id/object/%%a/graph -H "Content-Type:application/rdf+xml" -d "@%%X"
	)
)
echo Done uploading images rdfs...

::upload objects rdfs
for /f "delims=" %%X in ('dir /a-d /b /s "%source%\objects\rdf"') do (
	for /f "tokens=10 delims=\." %%a in ("%%X") do (
		echo %%a
		call curl -X PUT %endpoint%/rdf-graphs/service?graph=%prefix%/id/object/%%a/graph -H "Content-Type:application/rdf+xml" -d "@%%X"
	)
)
echo Done uploading objects rdfs...

::finally upload relbuilder rdfs
for /f "delims=" %%X in ('dir /a-d /b /s "%relbuilder%"') do (
	for /f "tokens=9 delims=\." %%a in ("%%X") do (
		echo %%a
		call curl -X PUT %endpoint%/rdf-graphs/service?graph=%prefix%/id/object/%%a/graph -H "Content-Type:application/rdf+xml" -d "@%%X"
	)
)
echo DONE...

:: Capture the end time before doing anything else
set t=%time: =0%

:: make t0 into a scaler in 100ths of a second, being careful not 
:: to let SET/A misinterpret 08 and 09 as octal
set /a h=1%t0:~0,2%-100
set /a m=1%t0:~3,2%-100
set /a s=1%t0:~6,2%-100
set /a c=1%t0:~9,2%-100
set /a starttime = %h% * 360000 + %m% * 6000 + 100 * %s% + %c%

:: make t into a scaler in 100ths of a second
set /a h=1%t:~0,2%-100
set /a m=1%t:~3,2%-100
set /a s=1%t:~6,2%-100
set /a c=1%t:~9,2%-100
set /a endtime = %h% * 360000 + %m% * 6000 + 100 * %s% + %c%

:: runtime in 100ths is now just end - start
set /a runtime = %endtime% - %starttime%
set runtime = %s%.%c%

echo Started at: %t0%
echo Ended at: set %time%
echo Ran for: %runtime%0 ms

if %runtime% LSS 0 set /a runtime=%runtime%+24*60*60
set /a runtime=%runtime%/100
set /a h=(%runtime%/3600)
set /a m=(%runtime%/60)-60*%h%
set /a s=%runtime%-60*(%runtime%/60)

echo hrs=%h% min=%m% sec=%s%



