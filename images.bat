@echo off
setlocal

:: RS
set source=C:\Users\acdr4\Desktop\RDFer_v3\objects_sampleAll.txt

set xmlPath=C:\Users\acdr4\Desktop\RDFer_v3\harvest\data\images

set outputPath=C:\Users\acdr4\Desktop\RDFer_v3\data\researchspace\images
set config=C:\Users\acdr4\Desktop\RDFer_v3\config\images.xsl

set box="C:\Users\acdr4\Desktop\RDFer_v3\My Box Files\ResearchSpace\data\"

:: software 
set curl=C:\Program Files\cURL\curl.exe
set saxon=C:\Users\acdr4\Desktop\RDFer_v3\software\saxon9he.jar
set any23jar=C:\Users\acdr4\Desktop\RDFer_v3\software\any23-0.6.1\any23-core\target\any23-core-0.6.1-jar-with-dependencies.jar
set rdf2rdf=C:\Users\acdr4\Desktop\RDFer_v3\software\rdf2rdf\rdf2rdf-1.0.1-2.3.1.jar
set zip7=C:\Program Files\7-Zip\7z.exe
set fart==C:\Users\acdr4\Desktop\RDFer_v3\software\fart.exe

:: URIs
set prefix=http://collection.britishart.yale.edu
set endpoint=http://localhost/openrdf-sesame/repositories/ycba

:: get all image xml from Content Delivery Service (CDS) web service API
FOR /f %%a in (%source%) do (	
	call "%curl%" http://deliver.odai.yale.edu/info/repository/YCBA/object/%%a/type/2?output=xml -o %xmlPath%\%%a.xml
	call "%fart%" %xmlPath%\%%a.xml "<contentSet>" "<contentSet id=\"%%a\">"
)

echo done...

::move %outputPath%\ttl %box%images >nul 2>&1

:: ---------------------------------- other things -------------------------

:: get all files from web service
::call "%curl%" http://deliver.odai.yale.edu/info/repository/YCBA/object/%%a/type/2?output=xml -o %xmlPath%\%%a.xml
:: >nul 2>&1

::copy /Y %xmlPath%\%%a.xml %box%cds_xml\ >nul 2>&1
::java -cp "C:\Users\acdr4\Desktop\RDFer_v3\tools\any23-0.6.1\any23-core\target\any23-core-0.6.1-jar-with-dependencies.jar" -Xmx256M org.deri.any23.cli.Rover "%outputPath%\%%a.rdf"
:: move %outputPath%\%%a.ttl %box%cds_ttl >nul 2>&1

	::echo ^<id^>%%a^</id^> >> %xmlPath%\%%a.xml 


::copy /Y %outputPath% %box%cds_rdf >nul 2>&1







:: process with RDFer
::RDFer.exe /c "%configPath%" /i "%xmlPath%" /o "%outputPath%"



::FOR /f %%a in (%source%) do (
::	call "%curl%" -X POST %endpoint%/rdf-graphs/service?graph=%prefix%/id/object/%%a/graph -H "Content-Type:application/rdf+xml" -T %%X	
::	java -jar C:\Users\acdr4\Desktop\RDFer_v3\tools\rdf2rdf\rdf2rdf-1.0.1-2.3.1.jar %outputPath%\%%a.rdf .ttl >nul 2>&1
::	move %outputPath%\%%a.ttl %box%cds_ttl >nul 2>&1
::)

::ECHO copy RDF files to BOX
::copy /Y %outputPath% %box%cds_rdf >nul 2>&1

