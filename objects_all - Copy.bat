@ECHO off
:: example command to excecute to get data into triple store
:: Add context file
:: curl -X PUT http://collections.britishart.yale.edu/openrdf-sesame/repositories/ycba/rdf-graphs/service?graph=http://collections.britishart.yale.edu/it/object/499 -H "Content-Type:application/rdf+xml" -T @C:\rdf_data_path\oai_tms.ycba.yale.edu_499.rdf
:: Remove context
:: curl -X POST http://collections.britishart.yale.edu/openrdf-sesame/repositories/ycba/rdf-graphs/service?graph=http://collections.britishart.yale.edu/it/object/499

:: define URIs
set prefix=http://collection.britishart.yale.edu
::set endpoint=http://localhost/openrdf-sesame/repositories/fts_test5
set endpoint=http://collection.britishart.yale.edu/openrdf-sesame/repositories/ycba


::PUT 405 Method Not Allowed when using prefix
::set endpoint=%prefix%/openrdf-sesame/repositories/owlim

:: define folders
set curl=C:\Program Files\cURL\curl.exe
set source=C:\Users\acdr4\Desktop\RDFer_v3\RdfOutput

:: OAI Harvester - harvests latest xml files
harvest_dev.bat
:: UMHarvester_dev.pl -v lr -i ycba -f lido
ECHO Done harvesting objects...

:: ImageHarvest - harvest images
java -jar ImageHarvest\ImageHarvest.jar -s harvest\data\lido -o harvest\data\images
echo Done harvesting images...

:: convert xml files to rdf
java -jar RDFConverter\RDFConverter.jar -c RDFConverter\config\objects.properties
echo Done converting object xml files to rdf...

:: convert image xml files to rdf
java -jar RDFConverter\RDFConverter.jar -c RDFConverter\config\images.properties
echo Done converting image xml files to rdf...

:: connect objects to images
java -jar RDFConverter\RDFConverter.jar -c RDFConverter\config\objects2images.properties
echo Done connecting objects to images...

::clear repository
set clear=DELETE { GRAPH ?g{ ?s ?p ?o }. ?s ?p ?o.} WHERE { GRAPH ?g{ ?s ?p ?o }}
echo clearing repository
call "%curl%" -g -X POST -H "Content-Type: application/x-www-form-urlencoded" -d "update=%clear%" "%endpoint%/statements"
echo done clearing. starting to upload

::start uploading rdf files to repository
for /f "delims=" %%X in ('dir /a-d /b /s "%source%\objects2images\rdf"') do (
	for /f "tokens=14 delims=\." %%a in ("%%X") do (
		echo %%a
		call "%curl%" -X POST %endpoint%/rdf-graphs/service?graph=%prefix%/id/object/%%a/graph -H "Content-Type:application/rdf+xml" -T "%%X"
	)
)
echo Done uploading objects2images rdfs...

for /f "delims=" %%X in ('dir /a-d /b /s "%source%\images\rdf"') do (
	for /f "tokens=14 delims=\." %%a in ("%%X") do (
		echo %%a
		call "%curl%" -X POST %endpoint%/rdf-graphs/service?graph=%prefix%/id/object/%%a/graph -H "Content-Type:application/rdf+xml" -T "%%X"
	)
)
echo Done uploading images rdfs...

for /f "delims=" %%X in ('dir /a-d /b /s "%source%\objects\rdf"') do (
	for /f "tokens=14 delims=\." %%a in ("%%X") do (
		echo %%a
		call "%curl%" -X POST %endpoint%/rdf-graphs/service?graph=%prefix%/id/object/%%a/graph -H "Content-Type:application/rdf+xml" -T "%%X"
	)
)
echo Done uploading objects rdfs...

echo done...


