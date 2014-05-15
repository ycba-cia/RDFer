@echo off
setlocal

:: RS
set source=C:\Users\acdr4\Desktop\RDFer_v3\objects_sample.txt

set xmlPath=C:\Users\acdr4\Desktop\RDFer_v3\harvest\data\images

set outputPath=C:\Users\acdr4\Desktop\RDFer_v3\data\researchspace\objects2images
set config=C:\Users\acdr4\Desktop\RDFer_v3\config\objects2images.xsl

set box="C:\Users\acdr4\Desktop\RDFer_v3\My Box Files\ResearchSpace\data\"

:: software 
set curl=C:\Program Files\cURL\curl.exe
set saxon=C:\Users\acdr4\Desktop\RDFer_v3\software\saxon9he.jar
set any23jar=C:\Users\acdr4\Desktop\RDFer_v3\software\any23-0.6.1\any23-core\target\any23-core-0.6.1-jar-with-dependencies.jar
set rdf2rdf=C:\Users\acdr4\Desktop\RDFer_v3\software\rdf2rdf\rdf2rdf-1.0.1-2.3.1.jar
set zip7=C:\Program Files\7-Zip\7z.exe

:: URIs
set prefix=http://collection.britishart.yale.edu
set endpoint=http://localhost/openrdf-sesame/repositories/ycba

FOR /f %%a in (%source%) do (	
	echo Processing: %%a
	java -jar "%saxon%" -xsl:"%config%" -s:"%xmlPath%\%%a.xml" -o:"%outputPath%\rdf\%%a.rdf"
	java -jar %rdf2rdf% %outputPath%\rdf\%%a.rdf %outputPath%\ttl\%%a.ttl 
)

::	java -cp "%any23jar%" -Xmx256M org.deri.any23.cli.Rover "%outputPath%\rdf\%%a.rdf" > "%outputPath%\ttl\%%a.ttl"	

:: --- !!! --- remove RDF folder and all files --- !!! --- 
rmdir /S /Q %outputPath%\rdf

:: 7zip all files 
call "%zip7%" a %outputPath%.zip %outputPath%

::	move %outputPath%\%%a.ttl %box%ycba2cds >nul 2>&1

:: get all files from web service
::call "%curl%" http://deliver.odai.yale.edu/info/repository/YCBA/object/%%a/type/2?output=xml -o %xmlPath%\%%a.xml
:: >nul 2>&1

::copy /Y %xmlPath%\%%a.xml %box%cds_xml\ >nul 2>&1
::java -cp "C:\Users\acdr4\Desktop\RDFer_v3\tools\any23-0.6.1\any23-core\target\any23-core-0.6.1-jar-with-dependencies.jar" -Xmx256M org.deri.any23.cli.Rover "%outputPath%\%%a.rdf"
:: move %outputPath%\%%a.ttl %box%cds_ttl >nul 2>&1

	::echo ^<id^>%%a^</id^> >> %xmlPath%\%%a.xml 

	:: commented out these lines as they are redundant if we run cds_daniel.bat first
	::call "%curl%" http://deliver.odai.yale.edu/info/repository/YCBA/object/%%a/type/2?output=xml -o %xmlPath%\%%a.xml
	::call fart.exe %xmlPath%\%%a.xml "<contentSet>" "<contentSet id=\"%%a\">"
	::copy /Y %xmlPath%\%%a.xml %box%cds_xml\ >nul 2>&1
	


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

