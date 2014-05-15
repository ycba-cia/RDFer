@ECHO off
:: example command to excecute to get data into triple store
:: Add context file
:: curl -X PUT http://collections.britishart.yale.edu/openrdf-sesame/repositories/ycba/rdf-graphs/service?graph=http://collections.britishart.yale.edu/it/object/499 -H "Content-Type:application/rdf+xml" -T @C:\rdf_data_path\oai_tms.ycba.yale.edu_499.rdf
:: Remove context
:: curl -X POST http://collections.britishart.yale.edu/openrdf-sesame/repositories/ycba/rdf-graphs/service?graph=http://collections.britishart.yale.edu/it/object/499

:: define URIs
set prefix=http://collection.britishart.yale.edu
::set endpoint=http://localhost/openrdf-sesame/repositories/fts_test5
set endpoint=http://localhost/openrdf-sesame/repositories/ycba_dev


::PUT 405 Method Not Allowed when using prefix
::set endpoint=%prefix%/openrdf-sesame/repositories/owlim

:: define folders
set curl=C:\Program Files\cURL\curl.exe
set source=C:\Users\acdr4\Desktop\RDFer_v3\yale\YCBA\RDFData\YCBA Art Collection\All Objects\objects\ttl

for /f "delims=" %%X in ('dir /a-d /b /s "%source%"') do (
	for /f "tokens=14 delims=\." %%a in ("%%X") do (
		echo %%a
		call "%curl%" -X POST %endpoint%/rdf-graphs/service?graph=%prefix%/id/object/%%a/graph -H "Content-Type:application/x-turtle" -T "%%X"
	)
)

echo done...


