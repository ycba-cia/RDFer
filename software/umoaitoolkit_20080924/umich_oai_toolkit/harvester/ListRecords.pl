#!/l/local/bin/perl

# © 2007, The Regents of The University of Michigan, All Rights Reserved
#
# Permission is hereby granted, free of charge, to any person obtaining
# a copy of this software and associated documentation files (the
# "Software"), to deal in the Software without restriction, including
# without limitation the rights to use, copy, modify, merge, publish,
# distribute, sublicense, and/or sell copies of the Software, and to
# permit persons to whom the Software is furnished to do so, subject
# to the following conditions:
#
# The above copyright notice and this permission notice shall be
# included in all copies or substantial portions of the Software.

# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
# EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
# MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
# IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
# CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
# TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
# SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

END
{
    # Exit program
    exit;
}
# ----------------------------------------------------------------------
#               start of MAIN
# ----------------------------------------------------------------------


use strict;
use LWP::Simple;
use LWP::UserAgent;
use Getopt::Std;

my %opts;
getopts('i:r:s:f:t:l:ba', \%opts);

#Always assume metadata Prefix is oai_id
my $id                 = $opts{'i'};
my $repository         = $opts{'r'};
my $set                = $opts{'s'};
my $format             = $opts{'f'};
my $startTime          = $opts{'t'};
my $lastTotal          = $opts{'l'};
my $batch              = $opts{'b'};
my $tryagain           = $opts{'a'};

my $gIdentifier = $id;
my $gRecordsRejected = 0;
my $gRecordsReplaced = 0;
my $gOutputDir;
#Used to search for files in the file system.
my @gListOfFilesArray;
my @gListOfDirsArray;

##################################
#Global Parameters:
#################################
#LogDirectory, BackupDirectory, ScriptDirectory
#my $gLogDir = qq{/l1/bin/o/oaister/scripts/log};
#my $gScriptDir = qq{/l1/bin/o/oaister/scripts};
#my $gBackupDir = qq{/l1/prep/h/harvester_other/backup};
#my $gDataDir = qq{/l1/prep/h/harvester};
my $gLogDir = qq{log};
my $gScriptDir = qq{scripts};
my $gBackupDir = qq{data/backup};
my $gDataDir = qq{data};

&ProcessRequest ( $id, $repository, $set, $format, $startTime ) ;

exit;


# --------------------------------------------------
#      END    MAIN
# --------------------------------------------------

sub DoesDirExists
{
    my ( $dir ) = @_;

    open  (FH, "<$dir");
    my $thereIsADir = 0;
    if ( -d FH)
    {
	$thereIsADir = 1;
    }
    close(FH);

    return $thereIsADir;
}

sub UMHandler
{
    my ( $a ) = @_;

    return;
}


sub GetListRecordsResponse 
{
    my ( $id, $url ) = @_;

    if ( $id eq 'arXiv' )
    {
	sleep 120;
    }
    my $TryCount = 1;
    my $TryCount503 = 1;
    my $TryCount500 = 1;
    my $TryCountEmpty = 1;


    my $numRedirects = 0;
TRY_AGAIN:
    #Call to LWP to get response
    my $ua = LWP::UserAgent->new;
    $ua->timeout( 180 ); ## timeout for 180 seconds
    my $req = HTTP::Request->new( GET => $url );
    $req->content_type('application/x-www-form-urlencoded');
    #$req->header('Accept-Encoding' => '*');
    #Pass request to the user agent and get a response back
    my $res = $ua->request( $req );

    #my $error_code = $res->code;
    my $error_code = &GetErrorCode ( $res->content );
    if ($error_code==503)
      {
        my $sleep = $res->header('Retry-After');
        if (not defined($sleep)) 
        {
	  my $msg = qq{ERRO: $id with pid of $$ has received a 503 error without a delay time. Going to delay for 10 seconds and try again};
	  &ReportToActiveLog ( $msg );
	  &ReportToFinalLog ( $msg );
	  #Use a default retry time
	  $sleep = 10;

	  if ( $TryCount503 > 25 )
	    {
	      my $msg = qq{ERRO: $id with pid of $$ has terminated because it has delayed more than 25 times for error 503};
	      &ReportToActiveLog ( $msg );
	      &ReportToFinalLog ( $msg );
	      exit;
	    }
	  $TryCount503 = $TryCount503 + 1;
	}
        elsif (($sleep<0) or ($sleep>86400)) 
	{
	  #post error and exit
	  my $msg = qq{ERRO: $id with pid of $$ has ended because of a bad Retry-After time ($sleep)};
	  &ReportToActiveLog ( $msg );
	  &ReportToFinalLog ( $msg );
	  exit;
        } 
        sleep($sleep);

	goto TRY_AGAIN;
      } 
    elsif ($error_code==302) 
      {
         my $redirectURL=$res->header('Location');
         my ($redirectBaseURL)=$redirectURL=~/^([^\?]+)\?/;
         if (not defined($redirectURL)) 
         {
	   #print message that no redirect header given, and exit
	   #post error and exit
	   my $msg = qq{ERRO: $id with pid of $$ has ended because error code 302 was received with no redirect};
	   &ReportToActiveLog ( $msg );
	   &ReportToFinalLog ( $msg );
	   exit;
         } 
         elsif ($numRedirects<0) {
	   #print message that no redirect is not permitted
	   #post error and exit
	   my $msg = qq{ERRO: $id with pid of $$ has ended because no redirects are permitted};
	   &ReportToActiveLog ( $msg );
	   &ReportToFinalLog ( $msg );
	   exit;
         } elsif ((++$numRedirects)>5) {
	   #print message that exceeded max number of redirects
	   #post error and exit
	   my $msg = qq{ERRO: $id with pid of $$ has ended because it has exceeded the max number of redirects};
	   &ReportToActiveLog ( $msg );
	   &ReportToFinalLog ( $msg );
	   exit;
         } 
	 my $msg = qq{ERRO: $id with pid of $$ received a 302 error and is going to try again};
	 &ReportToActiveLog ( $msg );
	 &ReportToFinalLog ( $msg );
      
	 $url = $redirectURL;
	 goto TRY_AGAIN;

       }
    elsif (($error_code==500) || ($error_code==502))
      {
        my $sleep = $res->header('Retry-After');
	my $msg = qq{ERRO: $id with pid of $$ has received a 500 or 502 error. Going to delay for 10 seconds and try again};
	&ReportToActiveLog ( $msg );
	&ReportToFinalLog ( $msg );
	#Use a default retry time
	$sleep = 10;

	if ( $TryCount500 > 25 )
	  {
	    my $msg = qq{ERRO: $id with pid of $$ has terminated because it has delayed more than 25 times for error 500 or 502};
	    &ReportToActiveLog ( $msg );
	    &ReportToFinalLog ( $msg );
	    exit;
	  }
	$TryCount500 = $TryCount500 + 1;

        sleep($sleep);

	goto TRY_AGAIN;
      }
    #Check the outcome of the response
    elsif ($res->is_success)
    {
	#return the data
        if ( $res->content )
	  {
	    #&ReportToActiveLog ( 'start' );
	    #&ReportToActiveLog ( $res->content );
	    #&ReportToActiveLog ( $error_code );
	    #&ReportToActiveLog ( 'end' );

            ## make sure the content is not an error
            &ProcessPossibleError ( $id, $res->content, $url );

	    return $res->content;
	  }
	else
	  {
	    my $msg = qq{ERRO: $id with pid of $$ has empty content. Going to sleep for 10 secs and then trying again};
	    &ReportToActiveLog ( $msg );
	    &ReportToFinalLog ( $msg );
	    #Use a default retry time
	    sleep 10;

	    if ( $TryCountEmpty > 25 )
	      {
		my $msg = qq{ERRO: $id with pid of $$ has terminated because it has delayed more than 25 times with empty content};
		&ReportToActiveLog ( $msg );
		&ReportToFinalLog ( $msg );
		exit;
	      }
	    $TryCountEmpty = $TryCountEmpty + 1;

	    goto TRY_AGAIN;	    
	  }
    } 


    if ( $tryagain )
      {
	my $msg = qq{The following request ($url) has failed. This has been try number $TryCount };
	&ReportToActiveLog ( $msg );
	&ReportToFinalLog ( $msg );
	my $error = $res->message();
	my $msg = qq{ERRO: $id with pid of $$ has reported the following error:  $error};
	&ReportToActiveLog ( $msg );
	&ReportToFinalLog ( $msg );
	$TryCount = $TryCount + 1;
	if ( $TryCount > 10 )
	  {
	    my $msg = qq{ERRO: $id with pid of $$ has terminated. More than 10 tries have been done. };
	    &ReportToActiveLog ( $msg );
	    &ReportToFinalLog ( $msg );
	    exit;
	  }
	else
	  {
	    my $msg = qq{ERRO: $id trying again. };
	    &ReportToActiveLog ( $msg );
	    &ReportToFinalLog ( $msg );
	  }
	#Wait 1 minute, and then try again
	sleep 60;
	
	goto TRY_AGAIN;
	    
      }
    my $error = $res->message();
    my $msg = qq{ERRO: $id with pid of $$ has ended for the following error:  $error};
    &ReportToActiveLog ( $msg );
    &ReportToFinalLog ( $msg );
    print "$msg","\n";
    exit;
}

sub GetErrorCode 
{
  my ( $error_code ) = @_;

  if ( $error_code =~ m,.*?\<div class\=\"diags\"\>Error\: (.*?)\<\/div\>.*,s )
    {
      $error_code =~ s,.*?\<div class\=\"diags\"\>Error\: (.*?)\<\/div\>.*,$1,si;
      return $error_code;
    }


  return 200;
}

sub ConvertToValidDirFormat
{
    my ( $dir ) = @_;

    $dir =~ s,:,_,gs;
    $dir =~ s, ,_,gs;
    $dir =~ s,\",_,gs;
    $dir =~ s,\',_,gs;
    $dir =~ s,\`,_,gs;
    $dir =~ s,\*,_,gs;
    $dir =~ s,\;,_,gs;
    $dir =~ s,\#,_,gs;
    $dir =~ s,\?,_,gs;
    $dir =~ s,\!,_,gs;
    $dir =~ s,\&,_,gs;
    $dir =~ s,\<,_,gs;
    $dir =~ s,\>,_,gs;
    $dir =~ s,\$,_,gs;
    $dir =~ s,\[,_,gs;
    $dir =~ s,\],_,gs;
    $dir =~ s,\(,_,gs;
    $dir =~ s,\),_,gs;

    return $dir;
}

#The new NOH using LWP

sub ProcessRequest
{
    my ( $id, $repository, $set, $format, $startTime ) = @_;

    #Report the pid for this process to the active log
    my $msg = qq{MESG: PID for ListRecords for $id is $$.};
    if ( $set )
    {
	$msg = qq{MESG: PID for ListRecords for $id and set $set is $$.};
    }
    
    &ReportToActiveLog ( $msg );

    my $repositoryDir = $id;
    if ( $set )
    {
	#Make sure that the repositoryID dir exists in backup dir before you
	#try to move any set to the backup dir
	my $baseDir = qq{$gBackupDir/$repositoryDir};
	my $baseDirExists = &DoesDirExists ( $baseDir );
	
	if ( ! $baseDirExists )
	{
	 `mkdir $baseDir`;
	}
	$repositoryDir .= qq{/$set};
	$repositoryDir = &ConvertToValidDirFormat ( $repositoryDir );
    }

    my $targetDir = qq{$gDataDir/$repositoryDir};
    my $backupDir = qq{$gBackupDir/$repositoryDir};

    #Do some checking of dirs
    my $targetDirExists = &DoesDirExists ( $targetDir );
    my $backupDirExists = &DoesDirExists ( $backupDir );

    #This check is acutally done in UMHarvester, but have left it here in 
    #case we decided to move it to this module
    if ( $backupDirExists )
    {
	my $msg = qq{ERRO: Backup dir exist: $backupDir. Process terminating.};
	&ReportToActiveLog ( $msg );
	&ReportToFinalLog ( $msg );
	exit;
    }

    if ( $targetDirExists )
    {
	if ( $startTime )
	{
	    my $msg = qq{MESG: taring  $targetDir and moving it to backup.};
	    &ReportToActiveLog ( $msg );
	    #`cp -r $targetDir $backupDir`;
	    `/l/local/bin/mktgz -q $targetDir`;
	    `mv $targetDir.tgz $backupDir.tgz`;
	}
	else
	{
	    my $msg = qq{MESG: Moving $targetDir to backup.};
	    &ReportToActiveLog ( $msg );
	    `mv $targetDir $backupDir`;
	}

    }

    #Timeout is in units of seconds-- default is 3 minutes
    #Every parameter you pass to $harvester->ListRecords should exist
    my $msg = qq{MESG: First call to $repository.};
    &ReportToActiveLog ( $msg );

    my $url;
	
    if ( $set ) 
    {
		if ( $startTime )
		{
			$url = qq{$repository/OAIHandler?verb=ListRecords&metadataPrefix=$format&set=$set&from=$startTime};
		}
		else
		{
			$url = qq{$repository/OAIHandler?verb=ListRecords&metadataPrefix=$format&set=$set};
		}
    }
    else
    {
		if ( $startTime )
		{
			$url = qq{$repository/OAIHandler?verb=ListRecords&metadataPrefix=$format&from=$startTime};
		}
		else
		{
			$url = qq{$repository/OAIHandler?verb=ListRecords&metadataPrefix=$format};
		}
    }

	print $url;
    my $oaiResponse = &GetListRecordsResponse ( $id, $url );
    #&ProcessPossibleError ( $id, 'no_resumption_token', \$oaiResponse );

    my $count = 0;
    &ProcessOAIResponse ( \$oaiResponse, \$count, $repositoryDir, $startTime );

    while ( $oaiResponse =~ m,<resumptionToken.*?>(.*?)</resumptionToken>,is )
    {
	my $resumptionToken = $1;
	
	$resumptionToken =~ s,\&amp\;,&,gs;
	
	#Need to encode the resumptionToken for a few special characters
	#Section 3.1.1.3 of protocol
	#$resumptionToken =~ s,%,%25,g;
	&ReplaceFreePercent ( \$resumptionToken );

	$resumptionToken =~ s,/,%2F,g;
	$resumptionToken =~ s,\?,%3F,g;
	$resumptionToken =~ s,#,%23,g;
	$resumptionToken =~ s,=,%3D,g;
	$resumptionToken =~ s,&,%26,g;
	$resumptionToken =~ s,:,%3A,g;
	$resumptionToken =~ s,;,%3B,g;
	$resumptionToken =~ s, ,%20,g;
	$resumptionToken =~ s,\+,%2B,g;


	#break out if resumptionToken is empty
	last     if ( ! $resumptionToken );
    
	my $nextUrl = $url;
	$nextUrl =~ s,(http.*?:.*\?).*?(verb=[^\&\;]+).*,$1$2,;
	$nextUrl .= qq{\&resumptionToken=} . $resumptionToken;
	#if ( $id eq 'ajol' )
	#{
	#$msg = qq{RSTK: $id the resumption Token is $resumptionToken};
	#&ReportToActiveLog ( $msg ); 
	#}

	$oaiResponse = &GetListRecordsResponse ( $id, $nextUrl );	
	if ( ! $resumptionToken )
	  {
	    $resumptionToken = qq{empty_resumption_token};
	  }
        #&ProcessPossibleError ( $id, $resumptionToken, \$oaiResponse );

	&ProcessOAIResponse ( \$oaiResponse, \$count, $repositoryDir, $startTime )
    }

    my $recordsRequested = $count;
    $count = $count - $gRecordsRejected;
    my $msg;
    if ( $startTime )
    {
	$msg = qq{DONE: Incremental Harvest completed, $count records harvested out of $recordsRequested.};
    }
    else
    {
	$msg = qq{DONE: Harvest completed, $count records harvested out of $recordsRequested.};
    }

    &ReportToActiveLog ( $msg );

    #Harvesting has completed, so make an entry in repositoryId.log
    #This log is appended
    #Time Number_of_records_harvested
    my $msg;
    my $recordsRequested = $count + $gRecordsRejected;
    #If this is not an incremental harvest, lastTotal will be undef or treated like 0
    $lastTotal = $lastTotal + $count;
    if ( $startTime )
    {	
    	if ( $set )
	{	
	    $msg = qq{Harvest has completed for set $set in $format format.  Total Records harvested=$lastTotal ->};
	}	
	else	
	{	
	    $msg = qq{Harvest has completed in $format format.  Total Records harvested=$lastTotal ->};
	}	
	$msg = qq{Incremental $msg New Records=$count. Records replaced=$gRecordsReplaced.}
    }
    else
    {
	if ( $set )
	{
	    $msg = qq{Harvest has completed for set $set in $format format.  Total Records harvested=$lastTotal out of $recordsRequested.};
	}
	else
	{
	    $msg = qq{Harvest has completed in $format format.  Total Records harvested=$lastTotal out of $recordsRequested.};
	}	
    }

    &ReportToFinalLog ( $msg );

    return;

}

#This routine will determine if there is a percent that needs to be removed

sub ReplaceFreePercent
{
    my ( $resumptionTokenRef ) = @_;

    my $resumptionToken =  $$resumptionTokenRef;

    #Remove all occurrences of valid percents
    $resumptionToken =~ s,%2F,percent2F,g;
    $resumptionToken =~ s,%3F,percent3F,g;
    $resumptionToken =~ s,%23,percent23,g;
    $resumptionToken =~ s,%3D,percent3D,g;
    $resumptionToken =~ s,%26,percent26,g;
    $resumptionToken =~ s,%3A,percent3A,g;
    $resumptionToken =~ s,%3B,percent3B,g;
    $resumptionToken =~ s,%20,percent20,g;
    $resumptionToken =~ s,%2B,percent2B,g;
    $resumptionToken =~ s,%25,percent25,g;

    $resumptionToken =~ s,%2f,percent2F,g;
    $resumptionToken =~ s,%3f,percent3F,g;
    $resumptionToken =~ s,%3d,percent3D,g;
    $resumptionToken =~ s,%3a,percent3A,g;
    $resumptionToken =~ s,%3b,percent3B,g;
    $resumptionToken =~ s,%2b,percent2B,g;

    #Check if there is a percent around now
    if ( $resumptionToken =~ m,.*\%.*, )
    {
	$resumptionToken =~ s,\%,%25,g;
    }

    $resumptionToken =~ s,percent2F,%2F,g;
    $resumptionToken =~ s,percent3F,%3F,g;
    $resumptionToken =~ s,percent23,%23,g;
    $resumptionToken =~ s,percent3D,%3D,g;
    $resumptionToken =~ s,percent26,%26,g;
    $resumptionToken =~ s,percent3A,%3A,g;
    $resumptionToken =~ s,percent3B,%3B,g;
    $resumptionToken =~ s,percent20,%20,g;
    $resumptionToken =~ s,percent2B,%2B,g;
    $resumptionToken =~ s,percent25,%25,g;

    $$resumptionTokenRef = $resumptionToken;
}


sub ReportToFinalLog
{
    my ( $msg ) = @_;

    my $reporttime = scalar localtime(time());
    my $fileName = qq{$gLogDir/$gIdentifier.log};

    my $status = qq{$reporttime\t$gIdentifier\t$msg\n};

    #Now you want to append this to the file
    #If file does not exist, it will be created
    open ( OUTFILE, ">>$fileName" ) || die();
    print OUTFILE $status;
    close OUTFILE;


    #Make a report to the batch status log, if in batch mode
    if ( $batch )
    {
	&ReportToBatchLog ( $msg );
    }

    return;
}

sub ReportToActiveLog
{

    my ( $msg ) = @_;

    my $reporttime = scalar localtime(time());
    my $fileName = qq{$gLogDir/active.log};

    my $status = qq{$reporttime\t$gIdentifier\t$msg\n};
	#print $status;
		
    #Now you want to append this to the file
    open ( OUTFILE, ">>$fileName" ) || die();
    print OUTFILE $status;
    close OUTFILE;
 
    return;
}



sub ReportToBatchLog
{

    my ( $msg ) = @_;

    my $reporttime = scalar localtime(time());
    my $fileName = qq{$gLogDir/batch_status.log};

    my $status = qq{$reporttime\t\t$msg\n};

    #Now you want to append this to the file
    open ( OUTFILE, ">>$fileName" ) || die();
    print OUTFILE $status;
    close OUTFILE;

    return;

}


sub ReadFile
{
    my ( $fileName ) = @_;

    open FH, "<$fileName";
    my ($bytesRead, $buffer, $chunk);
    while ( $bytesRead = read(FH, $chunk, 1024) ) 
    {
        $buffer .= $chunk;
    }
    close FH;
    
    return $buffer;
}

sub CreateArrayOfRecords
{
    my ( $oaiRecordsRef ) = @_;

    $$oaiRecordsRef =~ s,.*?(<record(.*)</record>).*,$1,si;
    $$oaiRecordsRef =~ s,</record>,</record>placeforspliting,gsi;
    my @Records = split (/placeforspliting/,$$oaiRecordsRef);    
    
    return @Records;
}

sub GetResumptionToken
{
    my ( $oaiRecordsRef ) = @_;

    my $resumptionToken;
    if ( $$oaiRecordsRef =~ m,.*?<resumptionToken.*?>(.*)</resumptionToken>.*,si )
    {
	$$oaiRecordsRef =~ s,.*?<resumptionToken.*?>(.*)</resumptionToken>.*,$1,si;
	$resumptionToken = $$oaiRecordsRef;
    }
    
    return $resumptionToken;
}

sub SaveRecordInDir
{
    my ( $oaiRecordRef, $topLevelOutputDir, $countRef, $startTime ) = @_;

    my $record = $$oaiRecordRef;

    $record =~ s,< ,<,gs;;
    $record =~ s, >,>,gs;;
        
    #get identifier for record (to use as name of output file)
    my $identifier = $1    if ( $record =~ m,<header.*?>.*?<identifier>(.*?)</identifier>,s );
    if ( ! $identifier )
    {
	my $msg = qq{WARN: Record $$countRef has no identifier.};
	$gRecordsRejected = $gRecordsRejected + 1;
	&ReportToActiveLog ( $msg );
	next;
    }

    #put in newlines after close tags and empty elements
    $record =~ s,(/>),$1\n,gs;
    $record =~ s,(</.*?>),$1\n,gs;
    $record =~ s,(\cM|\cJ)+,\n,gs;

    my $fileName = $identifier;
    $fileName = &ConvertToValidDirFormat ( $fileName );
    $fileName =~ s,\/,_,gs;  #One additional.
    $fileName .= '.xml';

    my $fileToReplace;
    if ( $startTime )
    {
	my $dirName = $topLevelOutputDir;
	if ( $topLevelOutputDir =~ m,.*\/.*, )
	{
	    $dirName =~ s,.*\/(.*),$1,;
	}
	my $basedir = qq{$gDataDir/};
	$basedir .= $topLevelOutputDir;

	#This is the old way of doing it which was using up too much I/O
	#Find out if file exists and if it does, you'll want to replace 
	#it rather than add it to the repository
	#$fileToReplace = `find  $basedir -type f -name "$fileName"`;

        if ( ! @gListOfDirsArray )
        {
            #Get list of sub dirs
            my @shortDirs;
            if ( opendir( my $dir, $basedir ) )
            {
                @shortDirs = grep { ! /^\./ } readdir($dir); 
                close $dir; 
            }
            foreach ( @shortDirs ) { push @gListOfDirsArray, "$basedir/$_"; }
        }

        foreach my $dir ( @gListOfDirsArray )
        {
            my $fullName = "$dir/$fileName";
            if ( -f $fullName ) { $fileToReplace .= qq{$fullName\n}; }
        }

        my ( $NumOfFiles, @FilesToReplace ) = &GetFilesToReplace ( $fileToReplace );

	if ( $fileToReplace )
	{
	    #Since no new record is added, count is decremented
	    $$countRef = $$countRef - 1;

	    if ($NumOfFiles > 1)
	    {
		$fileToReplace =~ s,\n, and ,gs;
		$fileToReplace =~ s,(.*)and.*,$1,;
		my $msg = qq{DUPE: $NumOfFiles files to replace: $fileToReplace};
		&ReportToActiveLog ( $msg );
		&ReportToFinalLog ( $msg );
	    }
	    else
	    {
		#Report To active log that a record was replaced in the repository
		my $msg = qq{MESG: Record $fileToReplace is being replaced during this incremental harvest.};
		&ReportToActiveLog ( $msg );
	    }
	    $gRecordsReplaced = $gRecordsReplaced + 1;

	    foreach my $fileName (@FilesToReplace)
	    {
		&WriteFileOut ( $fileName, $record );
	    }
	}
    }
    
    #If you don't have a filename, then you better get one
    if ( ! $fileToReplace )
    {
	#If this is the first record of a group of 1000 records
	if ( ( $$countRef % 1000 ) == 1 )
	{
	    $gOutputDir = &MakeNewSubdir( $topLevelOutputDir, $countRef, $startTime );
	}
	$fileName = $gOutputDir . $fileName;
	&WriteFileOut ( $fileName, $record, $$countRef );
    }
    
}

sub WriteFileOut
{
    my ( $fileName, $record, $count ) = @_;


    my $justFileName = $fileName;
    my $justDirName = $fileName;
    $justDirName =~ s,(.*)/.*,$1,;
    $justFileName =~ s,.*/(.*),$1,;
    my $name_size = length $justFileName;
    #Unix only allows file names to be a max of 256 characters
    if ( $name_size <= 255 )
      {
        #Everything is fine in this case
      }
    elsif ( $count ) #only if count is passed in 
      {
         $fileName = qq{$justDirName/$set$count.xml};
         $fileName = &ConvertToValidDirFormat ( $fileName )
      }

    open ( OUTFILE, ">$fileName" ) || die("failed to open $fileName: $@ \n");
    print OUTFILE $record;
    close OUTFILE || die("failed to close $fileName: $@ \n");;
}

sub GetFilesToReplace
{
    my ( $FileList ) = @_;

    my @FilesToReplace = split (/\n/,$FileList);

    my $FileCount = scalar @FilesToReplace;
    
    return ( $FileCount, @FilesToReplace );

}

sub MakeNewSubdir
{
    my ( $topLevelOutputDir, $countRef, $startTime ) = @_;

    #Sometimes the topLevelOutputDir comes in with just the repositoryID, like epsilondiss
    #Other times it comes in with the repositoryID and set, like epsilondiss/0938409823
    #The dir name is based on the repositoryID for the first case, and the set name for the second
    #That is why the following check is done
    my $dirName = $topLevelOutputDir;
    if ( $topLevelOutputDir =~ m,.*\/.*, )
    {
	$dirName =~ s,.*\/(.*),$1,;
    }

    my $basedir = qq{$gDataDir/};
    my $chunk = $$countRef;
    
    #Repeat name of $gTopLevelOutputDir in subdirs 
    my $outputDir;
	$outputDir = $basedir . $topLevelOutputDir . '/';
	#print $outputDir,"\n";
	
#    if ( $startTime )
#    {
#		my $today = &GetTodaysDate ();
#		$outputDir = $basedir . $topLevelOutputDir . '/' . $dirName . $chunk . '-' . ( $chunk + 999 ) . '_' . $today;
#		#$outputDir = $basedir . $topLevelOutputDir . '/' . $dirName . $chunk . '-' . ( $chunk + 999 ) . '_' . $today . '/';
#    }
#    else
#    {
#		#$outputDir = $basedir . $topLevelOutputDir . '/' . $dirName . $chunk . '-' . ( $chunk + 999 ) . '/';
#		$outputDir = $basedir . $topLevelOutputDir . '/' . $dirName . $chunk . '-' . ( $chunk + 999 );
#	}


#    #my $MakeDirExecutable = '/bin/mkdir';
#    #my $MakeDirOutputLog  = '/tmp/harvest.log';

#	my $MakeDirExecutable = 'mkdir';
#    my $MakeDirOutputLog  = qq{$gLogDir/harvest.log};

	
#    #Create sub dir in which to deposit files
#    if (! -e $outputDir )
#    {
#		print "\n",$outputDir,"\n";
#        #my $mkdirCommand = qq{$MakeDirExecutable -m 2775 -p $outputDir};
#        my $mkdirCommand = qq{$MakeDirExecutable $outputDir};
#        `$mkdirCommand 2>> $MakeDirOutputLog`;
#    }
    return $outputDir;
}

sub GetTodaysDate 
{
    my $newtime = scalar localtime(time());
    my $year = substr($newtime, 20, 4);
    my %months = (
                  "Jan" => "01",
                  "Feb" => "02",
                  "Mar" => "03",
                  "Apr" => "04",
                  "May" => "05",
                  "Jun" => "06",
                  "Jul" => "07",
                  "Aug" => "08",
                  "Sep" => "09",
                  "Oct" => "10",
                  "Nov" => "11",
                  "Dec" => "12",
                 );
    my $month = $months{substr ($newtime,4, 3)};
    my $day = substr($newtime, 8, 2);
    $day =~ s, ,0,g;
    
    my $today = qq{$year-$month-$day};

    return $today;

}

sub ProcessPossibleError
{
    my ( $id, $htmlPage, $url ) = @_;

    if ( $htmlPage =~ m,<error code="(\w+)">,si )
    {
        my $error = $htmlPage;
        $error =~ s,.*?<error code="(\w+)">(.*?)</error>.*,$1 :: $2,si;

        my $msg = qq{ERROR: $id has reported the following error: $error for $url \nProcess has terminated.};
        &ReportToActiveLog ( $msg );
        &ReportToFinalLog ( $msg );
        print "$msg","\n";
        ## exit;
    }
}

sub OriginalProcessPossibleError
{
    my ( $id, $resumptionToken, $htmlPageRef ) = @_;

    if ( $$htmlPageRef =~ m,.*?<error code.*?>.*?</error>.*,si )
    {
      my $error = $$htmlPageRef;
      $error =~ s,.*?<error code.*?>(.*?)</error>.*,$1,si;

      my $msg = qq{ERRO: $id with resumption token $resumptionToken has reported the following xml error: $error.  Process has terminated.};
      &ReportToActiveLog ( $msg );
      &ReportToFinalLog ( $msg );
      print "$msg","\n";
      exit;

    }

    if ( $$htmlPageRef =~ m,.*<record.*?>.*?</record>.*,s )
    {
      #all is fine, we have data
    }
    else 
      #there are no records and records were expected
    {
      my $msg = qq{ERRO: $id with resumption token $resumptionToken has reported no records in its response.  Process has terminated.};
      &ReportToActiveLog ( $msg );
      &ReportToFinalLog ( $msg );
      print "$msg","\n";
      exit;      

    }

}


sub ProcessOAIResponse
{
    my ( $htmlPageRef, $countRef, $repositoryDir, $startTime ) = @_;

    while ( $$htmlPageRef =~ s,(<record.*?>.*?</record>),,s )
    {
        my $record = $1;
        $$countRef++;

        #Get identifier for record (to use as name of output file)
        my $identifier = $1    if ( $record =~ m,<header.*?>.*?<identifier.*?>(.*?)</identifier>,s );
        if ( ! $identifier )
        {
	    $gRecordsRejected++;
	    my $msg = qq{WARN: Record $$countRef has no identifier.};
	    &ReportToActiveLog ( $msg );
            next;
        }

        #Put in newlines after close tags and empty elements
        $record =~ s,(/>),$1\n,gs;
        $record =~ s,(</.*?>),$1\n,gs;
        $record =~ s,(\cM|\cJ)+,\n,gs;

	&SaveRecordInDir ( \$record, $repositoryDir, $countRef, $startTime );
    }

    my $msg = qq{MESG: $id still running, $$countRef records harvested so far.};
    &ReportToActiveLog ( $msg );
}

__END__;
