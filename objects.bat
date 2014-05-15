@echo off
setlocal

:: remember start time 
set t0=%time: =0%

:: RS
set source=C:\Users\acdr4\Desktop\RDFer_v3\objects_sample43.txt

set lido=C:\Users\acdr4\Desktop\RDFer_v3\harvest\data\lido\
set xmlPath=C:\Users\acdr4\Desktop\RDFer_v3\harvest\data\lidors

set outputPath=C:\Users\acdr4\Desktop\RDFer_v3\data\researchspace\34 object samples\objects
set config=C:\Users\acdr4\Desktop\RDFer_v3\config\objects.xsl

set box=C:\Users\acdr4\Desktop\RDFer_v3\My Box Files\ResearchSpace\data\YCBA Art Collection\34 object samples\objects
set dropbox=C:\Users\acdr4\Desktop\RDFer_v3\Dropbox\ResearchSpace\data\YCBA Art Collection\34 object samples\objects

:: software 
set curl=C:\Program Files\cURL\curl.exe
set saxon=C:\Users\acdr4\Desktop\RDFer_v3\software\saxon9he.jar
set any23jar=C:\Users\acdr4\Desktop\RDFer_v3\software\any23-0.6.1\any23-core\target\any23-core-0.6.1-jar-with-dependencies.jar
set rdf2rdf=C:\Users\acdr4\Desktop\RDFer_v3\software\rdf2rdf\rdf2rdf-1.0.1-2.3.1.jar
set zip7=C:\Program Files\7-Zip\7z.exe

::ECHO copy select files from %source% for processing and to BOX
::FOR /f %%a in (%source%) do (
::	copy /Y %lido%oai_tms.ycba.yale.edu_%%a.xml %xmlPath% >nul 2>&1
::	copy /Y %lido%oai_tms.ycba.yale.edu_%%a.xml %box%lido\ >nul 2>&1
::)

FOR /f %%a in (%source%) do (
	echo Processing: %%a
	copy /Y %lido%oai_tms.ycba.yale.edu_%%a.xml %xmlPath%  >nul 2>&1
	java -jar "%saxon%" -xsl:"%config%" -s:"%xmlPath%\oai_tms.ycba.yale.edu_%%a.xml" -o:"%outputPath%\rdf\%%a.rdf"
	java -jar %rdf2rdf% "%outputPath%\rdf\%%a.rdf" "%outputPath%\ttl\%%a.ttl"
)

:: --- !!! --- remove RDF folder and all files --- !!! --- 
rmdir /S /Q "%outputPath%\rdf"

:: 7zip all files 
::call "%zip7%" a "%outputPath%.zip" "%outputPath%"

::copy zip file to box and dropbox
copy /Y "%outputPath%.zip" "%box%.zip"
copy /Y "%outputPath%.zip" "%dropbox%.zip"

:: java -jar %rdf2rdf% %outputPath%\rdf\%%a.rdf %outputPath%\ttl\%%a.ttl 
::	java -cp "%any23jar%" -Xmx256M org.deri.any23.cli.Rover "%outputPath%\rdf\%%a.rdf" > "%outputPath%\ttl\%%a.ttl"

::java -jar %rdf2rdf% %outputPath%\rdf\oai_tms.ycba.yale.edu_%%a.rdf .ttl 
	
::move %outputPath%\oai_tms.ycba.yale.edu_%%a.ttl %box%ttl >nul 2>&1

:: process with RDFer
::RDFer.exe /c "%configPath%" /i "%xmlPath%" /o "%outputPath%"

:: convert RDF to TTL and move TTL to yale.box.com
::FOR /f %%a in (%source%) do (
::	java -jar C:\Users\acdr4\Desktop\RDFer_v3\tools\rdf2rdf\rdf2rdf-1.0.1-2.3.1.jar %outputPath%\oai_tms.ycba.yale.edu_%%a.rdf .ttl 
::	move %outputPath%\oai_tms.ycba.yale.edu_%%a.ttl %box%ttl >nul 2>&1
::)

::ECHO copy RDF files to BOX
::copy /Y %outputPath% %box%rdf >nul 2>&1

::ECHO update the ycba repository
::set prefix=http://collection.britishart.yale.edu
::set endpoint=http://localhost/openrdf-sesame/repositories/ycba
::set curl=C:\Program Files\cURL\curl.exe

:: get all .rdf files and paths from outputPath folder
::for /r %outputPath% %%X in (*.rdf) do (
::	:: YCBA file names have id in them, parse it out: oai_tms.ycba.yale.edu_757.rdf
::	for /f "tokens=7 delims=._ " %%b in ("%%X") do ( 
::   
::	call "%curl%" -X POST %endpoint%/rdf-graphs/service?graph=%prefix%/id/object/%%b/graph -H "Content-Type:application/rdf+xml" -T %%X
::	)
::)



ECHO Done...





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
