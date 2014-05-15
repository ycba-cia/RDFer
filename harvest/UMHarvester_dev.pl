#!/usr/bin/perl

# � 2004-2005, The Regents of The University of Michigan, All Rights Reserved
# Version 2.1
# 2.0 included incremental harvesting
# 2.1 includes automated batch harvesting and retry-after feature
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

BEGIN
{
    require "strict.pm";
    strict::import();
}

END
{
    # Exit program
    exit;
}
# ----------------------------------------------------------------------
#               start of MAIN
# ----------------------------------------------------------------------

use DBI;
use File::Path;
use Getopt::Std;
use LWP::UserAgent;
use Date::Calc qw(:all);
use XML::LibXML;

my %opts;
getopts('dnbav:i:s:f:whz', \%opts);

#Always assume metadata Prefix is oai_id
my $help            = $opts{'h'};
my $debug           = $opts{'d'};
my $wait            = $opts{'w'};
my $verb            = lc( $opts{'v'} );
my $id              = $opts{'i'};
my $set             = $opts{'s'};
my $allSets         = $opts{'z'};
my $format          = $opts{'f'};
my $incremental     = $opts{'n'};
my $batch           = $opts{'b'};  #Indicates a batch request so don't prompt
my $tryagain        = $opts{'a'};  #Indicates whether to try again, if request fails

my $usage = "Usage: UMHarvester -v OaiVerb -i repository_id\n" .
            "\t[-f format (optional)] \n" .
            "\t[-n incremental harvest (optional)] \n" .
            "\t[-s setSpec] \n" .
            "\t[-z (all sets in the repository)] \n" .
            "\t[-a retry-after (optional)] \n" .
            "\t[-w (don't run ListRecords.pl in background]\n" .
            "\t[-h (help, get this message)]\n";

if ( ! $verb || ! $id || $help ) { die $usage; }


##################################
#Global Parameters:
#################################
#LogDirectory, BackupDirectory, ScriptDirectory

#my $gLogDir = qq{/l1/bin/o/oaister/scripts/log};
#my $gScriptDir = qq{/l1/bin/o/oaister/scripts};
#my $gBackupDir = qq{/l1/prep/h/harvester_other/backup};

my $gLogDir = qq{log};
my $gScriptDir = qq{scripts};
my $gBackupDir = qq{backup};
my $repositoryConfigFile = "RepositoryConfig_dev.cfg";
my $repositorySetsFile   = "RepositorySets.cfg";

my %repoSets     = &GetRepositorySets ( $id );
my %repositories = &GetRepositories ( $id );

my @KeyList = keys (%repositories);
#Only check backupDir for ListRecords.pl request
if ( ( $verb eq 'ListRecords.pl' ) || ( $verb eq 'lr' ) )
{
    &CreateActiveLog; #Create an active log
    foreach my $id ( @KeyList ) { &CheckBackupDirs ( $id, $set ); }
}

#Process request
foreach $id ( @KeyList ) 
{
    if ( $set ne "" || $allSets ) { &ProcessRequest ( $verb, $id, $set, $format ); }
    elsif ( $repoSets{$id} && ($verb eq "lr") )
    {
        foreach $set ( @{$repoSets{$id}} ) 
        {
            &ProcessRequest ( $verb, $id, $set, $format ) ;
        }
    } 
    else { &ProcessRequest ( $verb, $id, $set, $format ); }
}

exit;


# --------------------------------------------------
#      END    MAIN
# --------------------------------------------------
sub GetRepositories 
{
    my ( $id ) = @_;
    my @ids = split (/,/,$id);

    my $parser = new XML::LibXML;
    my (%repositories, $source);
    eval { $source = $parser->parse_file( $repositoryConfigFile ); };
    die "failed to load $repositoryConfigFile: $@ \n" if $@;

    foreach my $repositoryNode ( $source->findnodes( "/RepositoryConfig/repository" ) )
    {
        my $repoID = $repositoryNode->findvalue( '@id' );
		
		# grep does not work in windows, we have to replace this or install grep...
        #if ( grep /^$repoID$/, @ids )
        #{
            my $baseUrl = $repositoryNode->findvalue( "baseUrl" );
            $repositories{$repoID} = $baseUrl;
        #}
		#print $baseUrl,"\n";
    }

    return %repositories;
}

sub GetRepositorySets
{
    ## %hash = id1 => [set1, set2], id2 => [set1, set2]...
    my ( $id ) = @_;
    my @ids = split (/,/,$id);

    my $parser = new XML::LibXML;
    my (%repositorySets, $source);
    if ( ! -e $repositorySetsFile ) { return %repositorySets; }

    eval { $source = $parser->parse_file( $repositorySetsFile ); };
    die "failed to load $repositorySetsFile: $@ \n" if $@;

    foreach my $repositoryNode ( $source->findnodes( "/RepositorySets/repository" ) )
    {
        my $repoID = $repositoryNode->findvalue( '@id' );

        if ( grep /^$repoID$/, @ids )
        {
            foreach ( $repositoryNode->findnodes( "set" ) )
            {
                push @{$repositorySets{$repoID}}, $_->findvalue( '@id' );
            }
        }
    }

    return %repositorySets;
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

sub CheckBackupDirs
{
    my ( $id, $set ) = @_;

    my $repositoryDir = $id;
    
    if ( $set )
    {
	$repositoryDir .= qq{/$set};
	$repositoryDir = &ConvertToValidDirFormat ( $repositoryDir );
    }

    my $backupDir = qq{$gBackupDir/$repositoryDir};
	#my $backupDir = qq{$gBackupDir\\$repositoryDir};

	
    #Do some checking of dirs
    my $backupDirExists = &DoesDirExists ( $backupDir );

    if ( $backupDirExists )
    {
	my $msg = qq{ERRO: Backup dir exist: $backupDir. Process terminating.};
	print $msg,"\n";
	&ReportToActiveLog ( $msg );
	&ReportToBatchLog  ( $msg );
	exit;
    }

    return;
}

sub ProcessRequest
{
    my ( $verb, $id, $set, $format ) = @_;

    my $repository = $repositories{$id};
    if ( ( $verb eq 'ListRecords.pl' ) || ( $verb eq 'lr' ) )
    {
        &ListRecords ( $verb, $id, $repository, $set, $format )
    }
    elsif ( ( $verb eq 'listsets' ) || ( $verb eq 'ls' ) )
    {
        &ListSets ( $verb, $repository, $set )
    }
    elsif ( ( $verb eq 'listmetadataformats' ) || ( $verb eq 'lf' ) )
    {
        &ListMetadataFormats ( $verb, $repository, $set )
    }
    elsif ( ( $verb eq 'identify' ) || ( $verb eq 'id' ) )
    {
        &Identify ( $verb, $repository, $set )
    }

    return;
}

#Output to screen only
sub Identify
{
    my ( $verb, $repository, $set ) = @_;

    my $url = qq{$repository?verb=Identify};

    my $response = &GetOAIResponse ( $url );

    my $repositoryName    = $response;
    if ( $repositoryName =~ m,<repositoryName>.*?</repositoryName>, )
    {
	$repositoryName =~ s,.*?<repositoryName>(.*?)</repositoryName>.*,$1,s;
    }
    else 
    {
	$repositoryName = '';
    }

    my $protocolVersion   = $response;
    if ( $protocolVersion =~ m,<protocolVersion>.*?</protocolVersion>, )
    {
	$protocolVersion =~ s,.*?<protocolVersion>(.*?)</protocolVersion>.*,$1,s;
    }
    else 
    {
	$protocolVersion = '';
    }

    my $earliestDatestamp = $response;
    if ( $earliestDatestamp =~ m,<earliestDatestamp>.*?</earliestDatestamp>, )
    {
	$earliestDatestamp =~ s,.*?<earliestDatestamp>(.*?)</earliestDatestamp>.*,$1,s;
    }
    else 
    {
	$earliestDatestamp = '';
    }

    my $adminEmail        = $response;
    if ( $adminEmail =~ m,<adminEmail>.*?</adminEmail>, )
    {
	$adminEmail =~ s,.*?<adminEmail>(.*?)</adminEmail>.*,$1,s;
    }
    else 
    {
	$adminEmail = '';
    }

    my $granularity    = $response;
    if ( $granularity =~ m,<granularity>YYYY-MM-DDThh:mm:ssZ</granularity>, ) 
    {
	$granularity =~ s,.*?<granularity>(.*?)</granularity>.*,$1,s;
    }
    else 
    {
	$granularity = 'YYYY-MM-DD';
    }

    print "repository name: ",$repositoryName,"\n";
    print "protocol version: ",$protocolVersion,"\n";
    print "earliest date stamp: ",$earliestDatestamp,"\n";
    print "granularity: ",$granularity,"\n";
    print "admin email(s): ", $adminEmail, "\n\n";

    return;
}

#Don't bother with the set implementation of this one
#Output to screen only
sub ListMetadataFormats
{
    my ( $verb, $repository, $set ) = @_;

    my $url = qq{$repository?verb=ListMetadataFormats};
    my $response = &GetOAIResponse ( $url );

    my @Formats;
    while ( $response =~ s,(<metadataFormat>.*?</metadataFormat>),,s )
    {
        my $format = $1;
	$format =~ s,.*?<metadataPrefix>(.*?)</metadataPrefix>.*,$1,s;
	push ( @Formats, $format );
    }


    print "archive supports metadata prefixes: ",
	join( ',', @Formats ),"\n";

    print "\n";

    return;
}

#Output to screen only
sub ListSets
{
    my ( $verb, $repository, $set ) = @_;

    my $url = qq{$repository?verb=ListSets};
    my $response = &GetOAIResponse ( $url );

    my %Sets;
    while ( $response =~ s,(<set>.*?</set>),,s )
    {
        my $setSpec = $1;
	my $setName = $setSpec;
	if ( $setSpec =~ m,<setSpec>.*?</setSpec>, )
	{
	    $setSpec =~ s,.*?<setSpec>(.*?)</setSpec>.*,$1,s;
	}
	else 
	{
	    $setSpec = '';
	}

	if ( $setName =~ m,<setName>.*?</setName>, )
	{
	    $setName =~ s,.*?<setName>(.*?)</setName>.*,$1,s;
	}
	else 
	{
	    $setName = '';
	}

	$Sets{$setSpec} = $setName;
    }

    my @KeyList = keys (%Sets);
    foreach my $spec ( sort @KeyList ) { 
	my $name = $Sets{$spec};
	print "$spec ==> $name\n";
    }
    print "\n";

    return;
}

sub query
{
    my ( $query ) = @_;

    print "$query ";

    my $ans = <STDIN>;

    chomp $ans;
    if( ! $ans )
    {  print "Aborting ...\n"; exit; }
    chomp $ans;

    $ans =~ s,^\s*(.*?)\s*$,$1,;

    return $ans;
}

sub ListRecords
{
    my ( $verb, $id, $repository, $set, $format ) = @_;

    #If no format specified, use oai_dc as default.
    if ( ! $format )
    {
	$format = 'oai_dc';
    }

    my ( $startTime, $totalRec );
    $totalRec = 0;
    #If incremental harvest
    if ( $incremental )
    {
	#format is set to long when the format is YYYY-MM-DDThh:mm:ssZ
	#format is set to short when the format is YYYY-MM-DD
	my $format = &DetermineTimeFormat ( $id );
	
	#Compute StartTime based on the format
	( $startTime, $totalRec ) = &GetStartTimeAndTotal ( $id, $set, $format );

	#Make sure user wants to go on
	my $msg = qq{Incremental harvest requested for $id $set starting $startTime and last total = $totalRec.};
	print STDOUT qq{$msg\n};
	if ( ! $batch )
	{
	    my $ans = query( "Make this request (y/n)? " );
	    if ( $ans ne 'y' )
	    {  return;  } 
	}
	$msg = qq{Starting Incremental harvest.};
	print STDOUT qq{$msg\n};

	my $msg = qq{Incremental harvest requested for $id $set starting $startTime.};
	&ReportToActiveLog ( $msg );

    }

    #For now assume don't bother with format and set
    #Call a separate perl script that will handle just ListRecords.pl
    #This way simultaneous harvesting can take place
    my $cmd;
    if ( $incremental )
    {
	#If batch, pass the batch request on to ListRecords.pl and don't put it in the background
        #For batch we do one request at a time so that we can coordinate the reading of the batch_status.log file
	if ( $batch )
	{
	    if ( $set )
	    {
		$cmd = qq{ListRecords.pl -i $id -r $repository -s '$set' -f $format -t $startTime -l $totalRec -b};
	    }
	    else
	    {
		$cmd = qq{ListRecords.pl -i $id -r $repository -f $format -t $startTime -l $totalRec -b};
	    }
	}
	else
	{
	    if ( $set )
	    {
		$cmd = qq{ListRecords.pl -i $id -r $repository -s '$set' -f $format -t $startTime -l $totalRec};
		if ( ! $wait ) { $cmd .= "&"; }
	    }
	    else
	    {
		$cmd = qq{ListRecords.pl -i $id -r $repository -f $format -t $startTime -l $totalRec};
		if ( ! $wait ) { $cmd .= "&"; }
	    }
	}
    }
    else
    {
	if ( $set )
	{
	    $cmd = qq{ListRecords.pl -i $id -r $repository -s '$set' -f $format};
	    if ( ! $wait ) { $cmd .= "&"; }
	}
	else
	{
	    $cmd = qq{ListRecords.pl -i $id -r $repository -f $format};
	    if ( ! $wait ) { $cmd .= "&"; }
	}
    }

    #Add the tryagin flag if requested
    #Not such a neat way of doing it, but don't want to rewrite the code above
    if ( $tryagain )
    {
	$cmd =~ s,\-r,-a -r,;
    }

    if ( $debug ) { print "DEBUG: $cmd \n"; }
    else          { system ($cmd); }

    return;
}

sub DetermineTimeFormat
{
    my ( $id ) = @_;
    
    #Using the Identify verb, determine what format the repository supports
    my %repositories = &GetRepositories ( $id );

    my $repository = $repositories{$id};

    my $url = qq{$repository?verb=Identify};

    my $response = &GetOAIResponse ( $url );
    
    my $granularity    = $response;
    if ( $granularity =~ m,<granularity>YYYY-MM-DDThh:mm:ssZ</granularity>, ) 
    {
	$granularity =~ s,.*?<granularity>(.*?)</granularity>.*,$1,s;
    }
    else 
    {
	$granularity = 'YYYY-MM-DD';
    }

    if ( $granularity eq 'YYYY-MM-DD' )
    {
	return 'short';
    }
    else
    {
	return 'long';
    }

}


sub GetStartTimeAndTotal
{
    my ( $id, $set, $format ) = @_;

    #If a harvest from scratch is being requested, then just get last time
    #If set harvest get the last time for that set

    my $file =  qq{$gLogDir/$id.log};
    my $logFile = &ReadFile ( $file );

    my ( $startTime, $year, $month, $day, $hour, $minute, $second );
    if ( $set )
    {
	$startTime = $logFile;
	$startTime =~ s,(.*)Harvest has completed for set $set .*,$1,gs;
	$startTime =~ s,(.*)\cI$id.*,$1,gs;
	$startTime =~ s,.*\cJ(.*),$1,gs;
	$startTime =~ s,  , 0,gs;
    }
    else 
        #Just get the last time in file
    {
	$startTime = $logFile;
	$startTime =~ s,(.*)Harvest has completed.*,$1,gs;
	$startTime =~ s,(.*)\cI$id.*,$1,gs;
	$startTime =~ s,.*\cJ(.*),$1,gs;
	$startTime =~ s,  , 0,gs;
    }
    
    $month = $startTime;
    $month =~ s,.*? (.*?) .*,$1,;
    $month = &GetNumericMonthValue ( $month );
    $day   = $startTime;
    $day =~ s,.*? .*? (.*?) .*,$1,;
    $year  = $startTime;
    $year =~ s,.* (.*),$1,;

    $hour  = $startTime;
    $hour =~ s,(.*?):.*,$1,;
    $hour =~ s,.* (.*),$1,;
    $minute  = $startTime;
    $minute =~ s,.*?:(.*):.*,$1,;
    $second  = $startTime;
    $second =~ s,.*?:.*:(.*) .*,$1,;

    if ( $format eq 'long' )
    {
	( $year, $month, $day, $hour, $minute, $second ) =
	    Add_Delta_DHMS($year,$month,$day, $hour,$minute,$second,
			   0,0,0,1);
	$month  = &PadValue ( $month );
	$day    = &PadValue ( $day );
	$hour   = &PadValue ( $hour );
	$minute = &PadValue ( $minute );
	$second = &PadValue ( $second );

	#YYYY-MM-DDThh:mm:ssZ
	$startTime = qq{$year-$month-$day\T$hour:$minute:$second\Z};
    }
    else 
        #must be short format.
    {
	($year,$month,$day) =
	    Add_Delta_Days($year,$month,$day,
                     1);

	$month  = &PadValue ( $month );
	$day    = &PadValue ( $day );
	#YYYY-MM-DD
	$startTime = qq{$year-$month-$day};
    }

    #Find out the total records harvested last time
    my $TotalRecHarvested = &GetTotalRecords ( $logFile, $set );

    return ( $startTime, $TotalRecHarvested );

}


sub GetTotalRecords
{
    my ( $logFile, $set ) = @_;
    
    my $total;
    if ( $set )
    {
	$total = $logFile;
	$total =~ s,.*Harvest has completed for set $set .*?Total Records harvested=(.*?) .*,$1,gs;
    }
    else 
        #Just get the last time in file
    {
	$total = $logFile;
	$total =~ s,.*Harvest has completed.*?Total Records harvested=(.*?) .*,$1,gs;
    }
    
    return $total;
	 
}


sub PadValue
{
    my ( $value ) = @_;
    
    if ( $value < 10 )
    {
	$value = qq{0$value};
    }
    
    return $value;
	 
}

sub GetNumericMonthValue 
{
    my ( $month ) = @_;

    if ( $month eq 'Jan' )
    {
	return '01';
    }
    elsif ( $month eq 'Feb' )
    {
	return '02';
    }
    elsif ( $month eq 'Mar' )
    {
	return '03';
    }
    elsif ( $month eq 'Apr' )
    {
	return '04';
    }
    elsif ( $month eq 'May' )
    {
	return '05';
    }
    elsif ( $month eq 'Jun' )
    {
	return '06';
    }
    elsif ( $month eq 'Jul' )
    {
	return '07';
    }
    elsif ( $month eq 'Aug' )
    {
	return '08';
    }
    elsif ( $month eq 'Sep' )
    {
	return '09';
    }
    elsif ( $month eq 'Oct' )
    {
	return '10';
    }
    elsif ( $month eq 'Nov' )
    {
	return '11';
    }
    elsif ( $month eq 'Dec' )
    {
	return '12';
    }

}

sub ReadFile
{
    my ( $filename ) = @_;

    open FH, "<$filename";
    my ($bytesRead, $buffer, $chunk);
    while ( $bytesRead = read(FH, $chunk, 1024) )
    {
        $buffer .= $chunk;
    }
    close FH;

    return $buffer;

}

sub CreateActiveLog
{
    my $reporttime = scalar localtime(time());
    my $status = qq{$reporttime\t\tActive log file created\n};
    my $fileName = qq{$gLogDir/active.log};

    #Now you want to create the file
    open ( OUTFILE, ">$fileName" ) || die( "$fileName $@ \n");
    print OUTFILE $status;
    close OUTFILE;

}

sub ReportToActiveLog
{

    my ( $msg ) = @_;

    my $reporttime = scalar localtime(time());
    my $fileName = qq{$gLogDir/active.log};

    my $status = qq{$reporttime\t\t$msg\n};

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

sub GetOAIResponse 
{
    my ( $url ) = @_;

    #Call to LWP to get response
    my $ua = LWP::UserAgent->new;
    $ua->timeout( 180 ); ## timeout for 180 seconds
    my $req = HTTP::Request->new( GET => $url );
    #Pass request to the user agent and get a response back
    my $res = $ua->request( $req );
    #Check the outcome of the response
    if ($res->is_success)
    {
	#return the data
	my $response = $res->content;
	$response =~ s,< ,<,gs;
	$response =~ s, >,>,gs;
        return $response;
    } 
    else
    {
	my $error = $res->message();
 	my $msg = qq{ERRO: request $url has ended for the following error:  $error};
 	print "$msg","\n";
 	exit;
    }

}

__END__;
