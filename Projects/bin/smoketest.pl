# This script runs a clean rebuild, runs a set of unit tests
# and reports its success or failure.
#

use Net::SMTP;
use File::Path;
use Getopt::Long;

my($branch) = "DEV" ;

GetOptions("b|branch=s" => \$branch) ;






sub mailMessageAndDie
{
   my($to, $title, $msg) = @_;

   mailMessage($to, $title, $msg);
   die "$title\nStopped";
}

sub mailMessage
{
   my($to, $title, $msg) = @_;

   my $smtp = Net::SMTP->new('localhost');
   $smtp->mail("vijay\@snapfish.com");
   my (@rcpt) = split(' ', $to);
   foreach my $r (@rcpt) {
       $smtp->to($r);
   }
   $smtp->data();
   $smtp->datasend("From: vijay\@snapfish.com\n");
   $smtp->datasend("To: \"Valuelabs Snafpish Development Team\"\n");
   $smtp->datasend("Subject: $title\n\n");

   $smtp->datasend($msg);

   $smtp->dataend();
   $smtp->quit();
}

sub mailLogAndDie
{
   my ($to, $title, $logfile) = @_;

   $smtp = Net::SMTP->new('localhost');
   $smtp->mail("vijay\@snapfish.com");
   my (@rcpt) = split(' ', $to);
   foreach my $r (@rcpt) {
       $smtp->to($r);
   }
   $smtp->data();
   $smtp->datasend("From: vijay\@snapfish.com\n");
   $smtp->datasend("To: \"Valuelabs Snapfish Development Team\"\n");
   $smtp->datasend("Subject: $title\n\n");

   #$smtp->datasend("***********Log file************\n");

   if (open(LOG, "<$logfile")) {
       my $i = 0;
       while (<LOG>) {
           #if($i > 250)
           $smtp->datasend($_);
           $i ++;
       }
       #if ($i == 250) {
           #$smtp->datasend("***********Truncated log file************\n");
       #}
       close(LOG);
   }
   else {
       $smtp->datasend("Could not open log file $logfile: $!\n");
   }
   $smtp->dataend();
   $smtp->quit();
}


$maillist = 'vijay@snapfish.com';


runSmokeTest();



sub runSmokeTest
{
    print "Runnign Smoketest Against $branch\n" ;
    sync();
    updateDB();
    buildSource();
    deploy() ;

    exit 0;
}



# ************************************************************
# Step 1. Sync source(Check out a fresh copy of sources.).
# ************************************************************


$| = 1;

rmtree("e:\\workarea\\bin\\smoketest\\*.log");


sub sync
{
   my $logfile = "e:\\workarea\\bin\\smoketest\\p4-$branch.log" ;
   rmtree($logfile);

   print "Syncing source...\n";
   open(P4LOG, ">>$logfile") ||  die "Cannot open $logfile $!\n";
   print P4LOG "Syncing source...\n";
   close(P4LOG);
   my $ret = system("p4 sync //depot/Snapfish/$branch/... >> $logfile 2>&1");

   if( ret == 0 )
   {
       print "Sync source ...Done\n" ;
       open(P4LOG, ">>$logfile") ||  die "Cannot open $logfile $!\n";
       print P4LOG "Sync source ...Done\n" ;
       close(P4LOG);

   }else
   {
       print "Sync source ...Failed\n" ;
       open(P4LOG, ">>$logfile") ||  die "Cannot open $logfile $!\n";
       print P4LOG "Sync source ...Failed\n" ;
       close(P4LOG);
   }

   mailLogAndDie($maillist, "SMOKETEST:$branch p4 sync //depot/Snapfish/$branch/...", $logfile);

}  # endof sync()



# ************************************************************
# Step 2. Rebuild the database.
# ************************************************************


sub updateDB
{
    my $logfile = "e:\\workarea\\bin\\smoketest\\updatedb-$branch.log" ;

    rmtree($logfile);

    rmtree("e:\\workarea\\bin\\smoketest\\uspszip.log");
    print "\n\n\nUpdate DB againts $branch...\n";

    $ret = system("e: & cd \\development\\snapfish\\$branch\\build & ant updatedb >> $logfile 2>&1");

    mailLogAndDie($maillist, "SMOKETEST:$branch updatedb", $logfile) ;

    open(DB_PROPERTIES, "<e:\\development\\snapfish\\$branch\\build\\db.properties")
        ||  die "Cannot open e:\\development\\snapfish\\$branch\\build\\db.properties $!\n";
    while(<DB_PROPERTIES>)
    {
        @props = split('=', $_) ;
        if( $props[0] eq "db.host" ){
            $DB = $props[1] ;
            $DB =~ s/^\s+//s;   # trim annoying leading whitespace
            $DB =~ s/\s+$//s;   # trim annoying trailing whitespace

        }
        if( $props[0] eq "db.sid" ){
            $SID = $props[1] ;
            $SID =~ s/^\s+//s;   # trim annoying leading whitespace
            $SID =~ s/\s+$//s;   # trim annoying trailing whitespace
        }
        if( $props[0] eq "db.user" ){
            $USER = $props[1] ;
            $USER =~ s/^\s+//s;   # trim annoying leading whitespace
            $USER =~ s/\s+$//s;   # trim annoying trailing whitespace
        }
    }
    close(DB_PROPERTIES);

    #print "Creating USPS State Zip Codes...$USER $SID\n";
    #$ret = system("cd \\workarea\\snapfish\\sql\\uspszipcodes & create_usps_city_state_zip.bat $USER $SID >> e:\\workarea\\bin\\smoketest\\uspszip.log 2>&1");
    #mailLogAndDie($maillist, "STATUS: Create USPS State Zip Codes", "e:\\workarea\\bin\\smoketest\\uspszip.log")

}  #end of updateDB()





# ************************************************************
# Step 3. Do a clean build of all sources
# ************************************************************

sub buildSource
{
    my $logfile = "e:\\workarea\\bin\\smoketest\\make-$branch.log" ;
    rmtree($logfile);

    print "\n\n\nBuild source against $branch...\n";

    $ret = system("e: & cd \\development\\snapfish\\$branch\\build & ant >> $logfile");
    if ( $ret == 0 )
    {
        print "Build source ...Done\n" ;
        mailLogAndDie($maillist, "SMOKETEST:$branch Build Status", $logfile) ;
    }
    else
    {
        print "Build source ...Failed\n" ;
        #print P4LOG "Build source ...Failed\n" ;
        mailLogAndDie($maillist, "SMOKETEST:$branch Build Status", $logfile) ;
    }

    print "\n\nreturn state = $ret\n" ;
    if ($ret != 0){
       exit 0 ;
    }
}   #end of buildSource()





# ************************************************************
# Step 4. Do a  deploy
# ************************************************************

sub deploy
{
    my $logfile = "e:\\workarea\\bin\\smoketest\\deploy-$branch.log" ;
    rmtree($logfile);


    print "\n\n\nDeploying...\n";

    $ret = system("e: & cd \\workarea\\bin & deploy -b $branch >> $logfile");
    if( $ret == 0 )
    {
        print "Deploying... Done\n" ;
        mailLogAndDie($maillist, "SMOKETEST:$branch Deploy Status", $logfile) ;
    }
    else
    {
        print "Deploying... Failed\n" ;
        mailLogAndDie($maillist, "SMOKETEST:$branch Deploy Status", $logfile) ;
    }
}   #end of deploy()







