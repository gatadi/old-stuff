# This script runs a clean rebuild, and reports on
# its success or failure.
#
#

use File::Copy cp;
use File::Spec;
use File::Path;
use Win32::Service;
use Net::SMTP;
use Getopt::Std;
use URI::Telnet ();
use Net::FTP;
use buildutils;
use Snapshot;


getopts("ab:cdef:g:hijl:m:no:p:qrs:t:u:w:xz:");

$opt_q = '-q' if $opt_q eq 1;
$opt_q = '-f' if $opt_q eq 2;
$restart = "-s" unless $opt_r;

$usgm = << "EOMSG";

Usage:  $0 options

options:

    -b level
          >3 - clobber, updatedb, build, install
          3  updatedb, build, install
          2  build, install
                1  install
    -n          do not install
    -h      print this message and exit
    -i      interactive, no log files
    -f host   install from host, default moscow.
    -t host     install to hosts (default all hosts in the spec)
                separate list with commas, if -host, do not install it
    -s spec     spec file to use
    -m address  mail status to, default dev\@snapfish.com
    -e          ignore install errors
    -l label    label to build at, must NOT include \@,
                if LATEST, then use head revision
                if not defined, build at head revision, if successfull,
                increment the existing label and apply to the sources
    -u user     user name for telnet (get from env if not defined)
    -p passwd   password for telnet
    -q          force lazy check (1) or force FORCE (2)
    -o component  only install component (if -component, do not install it)
    -r          DO NOT restart component on target computer
    -z tag      tag for dbusers
    -c          do not lock perforce
    -j          DO deploy jms server, default - NO
    -g host     gateway for ssh (default nice)
    -a          do log file processing (b,l are ignored)
    -d          Use bash shell (defaults to csh)
    -w branch   Specify the Branch. Dev is default.
    -x          Don't use xcopy on local deploy

EOMSG
#print "opt_h is ".$opt_h." \n";
#print "opt_b is ".$opt_b." \n";
#print "opt_s is ".$opt_s." \n";
#print "opt_a is ".$opt_a." \n";
print "\n";  ## Prints a blank line after the command prompt.  Better readability.

usage() if ($opt_h or !$opt_b or !$opt_s or !$opt_l) and !$opt_a;

####################################
## The following variables are defined in the spec files,
## if they are defined at all.
##   %context
##   $public_install
##   $ssh
####################################


if (defined $opt_l and $opt_l ne 'LATEST')
{
    $label = '@' . $opt_l;
}
### CC add release branch option.
print "opt_w is ".$opt_w."\n";
if ( !defined $opt_w) {
    $branch = "DEV";
}
else {
    $branch = $opt_w;
}

my $noxcopy = "";
$noxcopy = "-g" if $opt_x;

#$mhost = $opt_f || `hosname`;
$mhost = $opt_f || "192.168.168.247"; # default to moscow
chomp $mhost;
$me = $ENV{USERNAME};

$maillist = $opt_m || 'dev@snapfish.com';

%dbusers = (
       'default' => ['sfodev01', 'sfqa', 'sfqa'],
       'staging' => ['sfostg01', 'web01', 'web01'],
       );

$tag = $opt_z || 'default';
mail2medie("Invalid db tag") unless exists $dbusers{$tag};
($db, $dbuser, $dbpass) = @{$dbusers{$tag}};

$spec = $opt_s;

if (!$opt_n and !$opt_a and $spec eq 'production.pl')
{

   print <<MSG;

*!*!*!*!*!*!*!*!WARNING*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!
*!*!*!*!*!*!*!*!WARNING*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!
*!*!*!*!*!*!*!*!WARNING*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!

    You are about to deploy on the production machines

*!*!*!*!*!*!*!*!WARNING*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!
*!*!*!*!*!*!*!*!WARNING*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!

MSG

   print "Please step back, think about it and confirm:(y/n)[n]";

   $ans = <>;
   chomp $ans;

   $ans = lc $ans;

   if ($ans ne 'y')
   {
      print "\nDeployment Terminated\n";
      exit;
   }
   print "\n";
}


if ($opt_t)
{
   for $h (split(',', $opt_t))
   {
      if ($h =~ s/^-//)
      {
         $nodephosts{$h} = 1;
      }
      else
      {
         $dephosts{$h} = 1;
      }
   }

   print "\n";
   print "Only host(s) ${\( join (qq(,), keys %dephosts) )} will be deployed\n" if %dephosts;
   print "host(s) ${\( join (qq(,), keys %nodephosts) )} will NOT be deployed\n" if %nodephosts;
}

if ($opt_o and !$opt_a)
{
   for $h (split(',', $opt_o))
   {
      if ($h =~ s/^-//)
      {
         $nodepcomp{$h} = 1;
      }
      else
      {
         $depcomp{$h} = 1;
      }
   }

   print "\n";
   print "Only components(s) ${\( join (qq(,), keys %depcomp) )} will be deployed\n" if %depcomp;
   print "components(s) ${\( join (qq(,), keys %nodepcomp) )} will NOT be deployed\n" if %nodepcomp;
}

$comperrorre = '^Error: (.+)\(\d+\)';
$smakeerrorre = 'SMAKE (fatal)* error:';

$user = $opt_u || $ENV{'USERNAME'};

require "$user.pwd" if -f "$user.pwd";

if ($opt_p)
{
   $defpasswd = $opt_p;
}

$this = File::Spec->canonpath($0);
#print "canonpath is : ".$this."\n";  ### CCDEBUG


($v, $dir, $n) = File::Spec->splitpath($this);
$dir =~ s/\\$//;
(undef, $dir, $n) = File::Spec->splitpath($dir);
$dir =~ s/\\$//;
(undef, $dir, $n) = File::Spec->splitpath($dir);
$dir =~ s/\\$//;
(undef, $root, $n) = File::Spec->splitpath($dir);
$root =~ s/\\$//;

$root = File::Spec->catpath($v, $root);
$root =~ s/\\$//;
## Add ability work from different Branches. . . CCTASK.
## This is failing. Not sure why accessing this path from client.
##$sdir = "$root/snapfish//install/spec";
##$sdir = "$root/snapfish/REL1.1/install/spec";
$sdir = "E:\\development\\Snapfish\\".$branch."\\install\\spec";

$tspec = "$spec.tmp";
$root = "E:\\development";


($chk_status, $chk_msg) = check_dirs("//e/development/Snapfish/$branch/install");

if ($chk_status)
{
   print "\nThe following errors were discovered when comparing rsync directories in modules.pl and rsyncd.conf.in:\n\n$chk_msg\n";

   if($chk_status >0)
   {

      if($chk_status%2 == 1)
      {
         print "A directory is rsynced in modules.pl that is not listed in rsycnd.conf.in.\n";
         print "This will cause an error if this module is installed.\n";
         print "Do you still wish to continue??[y/n](n) ";

         $ans = <>;
         chomp $ans;

         if ($ans ne 'y')
         {
            print "Deployment Terminated\n";
            exit;
         }
      }

      if(int($chk_status/2) == 1)
      {
         print "\nA directory in rsyncd.conf.in is not used in modules.pl.\nThis directory will not be installed through modules.pl.\n\n";
         print "Do you still wish to continue?[y/n](n) ";

         $ans = <>;
         chomp $ans;

         if ($ans ne 'y')
         {
            print "Deployment Terminated\n";
            exit;
         }
         print "\n";
      }
   }
   else
   {
      print "The directories in modules.pl and rsyncd.conf.in could not be checked, because there was an error with one of the files:\n$chk_msg";
      print "Do you still wish to continue??[y/n](n) ";

      $ans = <>;
      chomp $ans;

      if ($ans ne 'y')
      {
         print "Deployment Terminated\n";
         exit;
      }
      print "\n";
   }
}

#print "Using spec $spec\n";
#print "root is ".$root."\n";

# Pick up our spec file, and start deploying on the available machines.
evalFile("$sdir\\$spec");

if ($public_install)   # Only check for open files if this is a public install.
{
   $files_out = `p4 opened`;
   die "Error checking Perforce for checked out files." if $?;
   if ($files_out)
   {
      print "\nThe following files are checked out on this client:\n$files_out\nDo you wish to continue?(y/n)[n]";

      $ans = <>;
      chomp $ans;

      $ans = lc $ans;

      if ($ans ne 'y')
      {
         print "\nDeployment Terminated\n";
         exit;
      }
      print "\n";
   }
}

if ($ssh)
{
   $gateway = $opt_g || 'nice';
   $gpwd = defined $defpasswd ? $defpasswd : $hostaccess{$gateway};

   if (!$gpwd)
   {
      "No login password available for ssh gateway host $host\n";
      exit;
   }
}

####################################
## Clobber
####################################
unless ($opt_b < 4 or opt_a)
{

   print "Clobbering\n";

   # Step 0. Check out a fresh copy of sources.
   ## This looks dangerous.
   rmtree("$root");

   my $ret = system("p4 sync -f //depot...$label >> c:\\temp\\p4.log 2>&1");
   mailLogAndDie($maillist, "Build checkout failed", "c:\\temp\\p4.log") unless (ret == 0);
   ## Now Flush the buffer.
   $| = 1;
   # Step 1. Clean up the application and library directory.

   rmtree("E:/applibs");
   rmtree("E:/apps/snapserver");

   #bootstrap myself prior to the build.
   rmtree("c:\\temp\\boot.log");

   $ret = system("perl $root\\Snapfish\\".$branch."\\install\\bootstrap.pl rsync://$mhost buildbox.pl builder c:\\temp > c:\\temp\\boot.log 2>&1");
   mailLogAndDie($maillist, "Bootstrap build failed", "c:\\temp\\boot.log")unless ($ret == 0);

}


####################################
## Update Database
####################################
unless ($opt_b < 3 or opt_a)
{

   rmtree("c:\\temp\\make.log");

   print "Updating DB\n";

   # First, update the database schema
   $ret = system("smake USER=$dbuser PASSWD=$dbpass DB=$db ROOT=$root /f $root\\snapfish\\".$branch."\\build\\Root.mk updatedb > c:\\temp\\makedb.log 2>&1");
   mailLogAndDie($maillist, "Db Schema update Failed", "c:\\temp\\makedb.log") unless ($ret == 0);
}


####################################
## Build
####################################
unless ($opt_b < 2 or opt_a)
{

   # Lock the thing
   ## Don't need this for now. CCDEBUG
   #lock_perforce() unless $opt_c;

   #Run the build
   rmtree("c:\\temp\\make.log");

   #$ret = system
   #    ("smake /f $root\\snapfish\\".$branch."\\build\\Root.mk  > c:\\temp\\make.log 2>&1");
   #mailLogAndDie($maillist, "Make clean failed", "c:\\temp\\make.log") unless ($ret == 0);

   $bld_status = 'Building';
   ### MARK 1 CCDEBUG
   print "Rebuilding ... \n";


   my $ret = system("p4 sync //depot...$label> c:\\temp\\p4.log 2>&1");
   mailLogAndDie($maillist, "Build checkout failed", "c:\\temp\\p4.log") unless ($ret == 0);

   # Then, do a full build
   #$ret = system("smake /f $root\\snapfish\\".$branch."\\build\\Root.mk ROOT=$root > c:\\temp\\make.log 2>&1");
   #mailLogAndDie($maillist, "Build failed", "c:\\temp\\make.log") unless ($ret == 0);

   $MAX_BUILD_ATTEMPTS=10;
   $buildAttempts=0;
   ## Workaround fact that our code must be compiled multiple times. ##CC MARK A
   while ($buildAttempts <= $MAX_BUILD_ATTEMPTS)
   {
      print "Attempting to build. . . Attempt: ".$buildAttempts."\n";
      my $ret = system("smake /f $root\\snapfish\\".$branch."\\build\\Root.mk ROOT=$root > c:\\temp\\make.log 2>&1");
      if ( $ret == 0 )
      {
         print "smake passed \n";
         #update_label();
         last;
      }
      else
      {
         mailLogAndDie($maillist, "Build failed", "c:\\temp\\make.log") unless ($buildAttempts < $MAX_BUILD_ATTEMPTS);
         ## Now Flush the buffer.
         $| = 1;
      }
      $buildAttempts++;
   }
}



if (-f $saved_prot)
{
    system("p4 protect -i < $saved_prot") if -f $saved_prot;
    unlink $saved_prot;
}



####################################
## Install
####################################
unless ($opt_n)  ## also, unless opt_b < 1
{

   while (($mod, $hlist) = each %context)
   {
      if ($opt_o and ((defined %depcomp and !exists $depcomp{$mod}) or (defined %nodepcomp and exists $nodepcomp{$mod})))
      {
         print "skipping component $mod\n";
         $depstat .= "skipping component $mod\n";
      }
      elsif ($mod =~ /_jms_server$/ and ! $opt_j and !$opt_a)
      {
         print "Skipping $mod by default !!!\n";
         $depstat .= "Skipping $mod by default !!!\n";
         $nodepcomp{$mod} = 1;
      }
      elsif (ref($hlist) eq "ARRAY")
      {            # This is list of servers to install on
         foreach $host (@$hlist)
         {
            $host =~ s/:.*$//;   # Chop possible weblogic stuff

            if ($host eq 'undefined')
            {
               print "host for $mod undefined, skipping\n";
               $depstat .= "host for $name undefined, skipping\n";
            }
            elsif ($opt_t and ((defined %dephosts and !exists $dephosts{$host}) or (defined %nodephosts and exists $nodephosts{$host})))
            {
               print "skipping host $host\n";
               $depstat .= "skipping host $host\n";
            }
            else
            {
               $hostlist{$host} .= "$mod,";
               print "Did not skip $mod\n";
            }
         }
      }
      #print "Bailing out of skip while loop\n";
   }
   print "\n";

   my $compopt = "";
   if (%nodepcomp or %depcomp)
   {

      while (($c) = each %depcomp)
      {
         if (exists $nodepcomp{$c})
         {
            print "$c is in both yes and no lists\n";
            exit 1;
         }
      }

      while (($c) = each %nodepcomp)
      {
         if (exists $depcomp{$c})
         {
            print "$c is in both yes and no lists\n";
            exit 1;
         }
      }

      $compopt .= "-c ";
      $compopt .= join(',', keys %depcomp) if %depcomp;
      $compopt .= "," if (%depcomp and %nodepcomp);
      $compopt .= "-".join(',-', keys %nodepcomp) if %nodepcomp;
   }

   $bld_status = 'Installing';

   print "\nInstalling\n\n" unless $opt_a;
   print "Processing log files\n" if $opt_a;

   @hosts = keys %hostlist;
   @hosts = sort jmsfirst @hosts;

   $nhosts = scalar keys %hostlist;
   $ihost = 0;

   $logproc = '-l' if $opt_a;

   $statstr = 'status';
   $statstr = '?' if $opt_d == 1;

   ## Use fully qualified pathname for hostname command or it blows up.
   $homedir = '$HOME';
   #$instdir = '$HOME/tmp/install/`/usr/bin/hostname`';
   # HOST will be substituted with the hostname, like in the perl command
   $instdir = '$HOME/tmp/install/HOST';


   # This is the old bootstrap command. -CC
   # "perl $instdir/bootstrap.pl -b $branch $restart $logproc $opt_q rsync://$mhost $spec HOST $instdir ;echo STATUS=\$$statstr");
   #        "sleep WAITSEC;rsync --recursive rsync://$mhost/$branch.install $instdir;echo STATUS=\$$statstr",
   @commands = (
           "test -d $instdir || mkdir -p $instdir ;echo STATUS=\$$statstr",
           "sleep WAITSEC;rsync --recursive rsync://$mhost/$branch.install $instdir;echo STATUS=\$$statstr",
           "cd $instdir;echo STATUS=\$$statstr",
           "perl bootstrap.pl -b $branch $compopt $restart $noxcopy $logproc $opt_q -a $label rsync://$mhost $spec HOST $instdir ;echo STATUS=\$$statstr");

   $tmout = 1000;
   $sec = 1;
   $mult = 3;
   $cnt = 1;
   $delay = 1;

   $err = $werr = 0;

   foreach $host (@hosts)
   {
      $hostlist{$host} =~ s/,$//;
      print "--- Connecting to install $hostlist{$host} on $host\n";

      $passwd = defined $defpasswd ? $defpasswd : $hostaccess{$host};

      $logf = "c:/temp/install_$host.out";
      #print "writing to log file $logf \n";

      $thandles{$host} = new Net::Telnet(Timeout=>10);
      $thandles{$host}->errmode('return');

      $thandles{$host}->input_log($logf);

      if ($ssh)
      {
         $thandles{$host}->open($gateway);
         if (!$thandles{$host}->login('dev', 'f0rgetful'))
         {
            my ($msg) = $thandles{$host}->errmsg();
            print "$msg\n";
            delete $thandles{$host};
            $depstat .= "$msg\ngateway $gateway login for host $host failed\n";
            $failed++;
         }
         else
         {

            $thandles{$host}->print("ssh $host");
            if ($thandles{$host}->waitfor('/continue connecting \(yes\/no\)/i'))
            {
               ## If there is no entry in the public key file, generate it, then check the password
               $thandles{$host}->print('yes');
            }
            if (!$thandles{$host}->waitfor('/password[: ]*$/i'))
            {
               $werr = 1;
            }
            else
            {
               $thandles{$host}->print($passwd);
               if (!(($pre, $mat) = $thandles{$host}->waitfor('/(password[: ]*$)|([\$%#>] *$)/')))
               {
                  $werr = 1;
               }
               elsif ($mat =~ /password:\s+$/)
               {
                  $msg = "ssh login to host $host failed\n";
                  $err = 1;
               }
            }
         }
      }
      else
      {
         $thandles{$host}->open($host);
         if (! $thandles{$host}->login($user, $passwd))
         {
            $msg .= "login to host $host failed\n";
            $err = 1;
         }
      }


      if ($err or $werr)
      {
         if ($werr)
         {
            $msg = $thandles{$host}->errmsg();
            $msg .= " on host $host\n";
         }
         print "Error on host $host: $msg\n";
         delete $thandles{$host};
         $depstat .= $msg;
         $failed++;
         $werr = $err = 0;
      }
      else
      {
         @{$command{$host}} = @commands;

         foreach $c (@{$command{$host}})
         {
            if ($c =~ s/WAITSEC/$delay/)
            {
               $delay = $sec + $mult*($cnt++);
            }

            $c =~ s/HOST/$host/g;
         }

         $thandles{$host}->timeout(1);
         $tcount{$host} = $tmout;
         $label{$host} = 'sendcommand';
         $comind{$host} = 0;
      }
   }

   $regex1 = '/(Continue\? \(y)|(STATUS=\d+)/';
   $regex2 = '/[\$%#>] *$/';

   print "\nStarting Deployment on connected hosts\n\n";

   $error = 0;

   while (keys %thandles)
   {
      foreach $host (sort jmsfirst keys %thandles)
      {
         $com = $command{$host}[$comind{$host}];
         if ($label{$host} eq "sendcommand")
         {
            print "Sending $com to $host\n" if $opt_i;
            $thandles{$host}->print($com);
            $label{$host} = 'readcommandresponse';
         }
         elsif ($label{$host} eq "wait4prompt")
         {
            print "Waiting for $regex2 on $host ...\n" if $opt_i;
            ($pre1, $mat) = $thandles{$host}->waitfor($regex2);

            if ($mat)
            {
               # Success
               $tcount{$host} = $tmout;
               $label{$host} = 'sendcommand';
               delete $realcom{$host};

               if (++$comind{$host} > $#commands)
               {
                  $ihost++;
                  print "Deployment on $host SUCCEEDED [ $ihost of $nhosts]\n";
                  $thandles{$host}->close();
                  delete $thandles{$host};
                  $depstat .= "Deployment on host $host SUCCESSFULL\n";
                  ++$numinstall;
               }
            }
            else
            {
               $error = 1;
            }
         }
         elsif ($label{$host} eq "readcommandresponse")
         {
            print "Waiting for $regex1 on $host ...\n" if $opt_i;
            ($pre, $mat) = $thandles{$host}->waitfor($regex1);
            if ($mat)
            {
               if ($mat =~ /STATUS=(\d+)/)
               {
                  if ($1 eq 0)
                  {
                     $label{$host} = 'wait4prompt'; # Wait for prompt
                  }
                  else
                  {
                     $thandles{$host}->close();
                     delete $thandles{$host};
                     $depstat .= "Deployment on host $host Failed\n";
                     $failed++;
                     $ihost++;
                     print "Deployment on $host FAILED [ $ihost of $nhosts]\n";
                  }
               }
               elsif ($mat eq 'Continue\? \(y')
               {
                  $thandles{$host}->print('n');
               }
               else
               {
                  $thandles{$host}->close();
                  delete $thandles{$host};
                  $depstat .= "Unknown match $mat for $host\nDeployment on host $host Failed\n";
                  $failed++;
                  $ihost++;
                  print "Deployment on $host FAILED [ $ihost of $nhosts]\n";
               }

               next;
            }
            else
            {
               $error = 1;
            }
         }
         elsif ($label{$host} eq "error")
         {
            $error = 1;
         }

         if ($error)
         {
            my ($msg) = $thandles{$host}->errmsg();

            if ($msg eq 'pattern match timed-out')
            {
               if (! --$tcount{$host})
               {
                  $thandles{$host}->close();
                  delete $thandles{$host};
                  $depstat .= "Deployment on host $host timed out\n";
                  $ihost++;
                  print "Deployment on $host FAILED [ $ihost of $nhosts]\n";
               }
            }
            else
            {
               $thandles{$host}->close();
               delete $thandles{$host};
               $depstat .= "Unknown error $msg on $host\nDeployment on host $host Failed\n";
               $failed++;
               $ihost++;
               print "Deployment on $host FAILED [ $ihost of $nhosts]\n";
            }
            $error = 0;
         }
      }
   }

   unless ($opt_l or !$numinstall or $failed or $opt_b == 1 or $opt_a)
   {
      # Update the whole depot
      system("p4 labelsync -l $lab") && mail2medie("p4 labelsync -l $lab: $!\n");

      # Lock the label
      open(INPIPE, "p4 label -o $lab|") || mail2medie("p4 label -o $lab|: $!\n");
      open(OUTPIPE, "|p4 label -i") || mail2medie("|p4 label -i: $!\n");

      while(<INPIPE>)
      {
         s/Options:   unlocked/Options:   locked/;
         print OUTPIPE;
      }

      close(INPIPE);
      close(OUTPIPE);

      my $date = localtime;

      my $msg = <<EOF;
The checked in version of Snapfish on $date was
successfully built from scratch.

The label $lab has been assigned to the build

EOF

      mailMessage($maillist, "Build Successfull", $msg) unless $opt_i;

   }

}  ## End of install

print "\n";
create_snapshot("$sdir\\$spec", $user, $passwd) if $public_install;


if ($depstat)
{
   my $msg = <<EOF;

   The build $lab has been deployed according to the spec file $spec
   with the following results:

$depstat

EOF

   mailMessage($maillist, "Build Deploy Results", $msg) unless $opt_i;
   print $msg if $opt_i;
}
else
{
   print "Nothing deployed\n";
}

exit ;




sub mail2medie
{
   my ($msg) = @_;

   mailMessage('yuri@snapfish.com', "Fatal build failure", $msg);
}

sub usage
{
   print "Usage: $0 options\n";

   print $usgm;

   exit;
}

# Evaluate the contents of a file in the current context,
# and leave behind all the subroutines, etc defined.
# The other thing we do, is strip out any ^M characters
# in the input. This keeps everyone happy.
sub evalFile
{
   my ($p) = @_;

   open(P, "<$p") || die "Could not read $p: $!\n";

   my ($s) = "";
   while (<P>)
   {
      s/\r//g;
      $s .= $_;
   }
   close(P);
   my($ret) = eval $s;

   die "$@\n$p stopped" if $@;
   return $ret;
}

sub mailMessageAndDie
{
   my($to, $title, $msg) = @_;

   if ($opt_i)
   {
      print"$msg\n";
   }
   else
   {
      mailMessage($to, $title, $msg);
   }

   die "$title\nStopped";
}

sub mailMessage
{
   my($to, $title, $msg) = @_;

   my $smtp = Net::SMTP->new('mail.webnexus.com');
   $smtp->mail($me);
#    $smtp->mail('BPFH');
   $smtp->to($to);
   $smtp->data();
   $smtp->datasend("To: \"Development Team\"\n");
   $smtp->datasend("Subject: $title\n\n");

   $smtp->datasend($msg);

   $smtp->dataend();
   $smtp->quit();
}

sub notify
{
   my ($str) = @_;

   print $str if $opt_i;
   $smtp->datasend($str) unless $opt_i;
}

sub mailLogAndDie
{
   my ($to, $title, $logfile) = @_;

   if (-f $saved_prot)
   {
      system("p4 protect -i < $saved_prot") if -f $saved_prot;
      unlink $saved_prot;
   }

   if ($opt_i && $logfile && -f $logfile)
   {
      open (F, $logfile);

      while (<F>)
      {
         print;
      }
      close F;
      goto decode;
   }

   $smtp = Net::SMTP->new('mail.webnexus.com');
   $smtp->mail($me);
   $smtp->to($to);
   $smtp->data();
   $smtp->datasend("To: \"dev\@snapfish.com\"\n");
#    $smtp->datasend("To: \"yuri\@snapfish.com\"\n");
   $smtp->datasend("Subject: $title\n\n");

   $smtp->datasend("***********Java Files failed to compile************");

   decode:
      goto donedecode unless $bld_status = 'Building' or ! $logfile or ! -f $logfile;

      if (open(LOG, "<$logfile"))
      {
         while (<LOG>)
         {
            if (/$comperrorre/)
            {
               $file = $1;

               if (! -f $file)
               {
                  notify("Cannot interpret error in $_\n");
               }
               else
               {
                  push (@cmperrors, $file);
               }

               next;
            }
            elsif (/$smakeerrorre/)
            {
               push (@makeerrors, $_);
               next;
            }
         }
      }
      else
      {
         notify("Could not open log file $logfile: $!\n");
         goto done;
      }

      close(LOG);

      notify("The following files failed to compile:\n\r") unless $#cmperrors == -1;

      foreach $file (@cmperrors)
      {

         $l = (split("\n", `p4  filelog $file`))[1];

         $l =~ / by (\S+) /;
         $who = (split('@', $1))[0];

         notify("$file by $who");
      }

      notify("The following make errors have occured:\n@makeerrors") unless $#makeerrors == -1;

      notify("Could not interpret compilation errors\n\r") if $#makeerrors == -1 and $#cmperrors == -1;

   donedecode:

      goto done if $opt_i;

      if (open(LOG, "<$logfile")) {
         my $i = 0;
         LOOP:
         while (<LOG>)
         {
            last LOOP unless ($i < 150);
            $smtp->datasend($_);
            $i ++;
        }
        if ($i == 150)
        {
           $smtp->datasend("***********Truncated log file************");
           close(LOG);
        }
     }
     else
     {
        $smtp->datasend("Could not open log file $logfile: $!\n");
     }

   done:

      goto end if $opt_i;

      $smtp->dataend();
      $smtp->quit();

   end:
      die "$title\nstopped\n" unless $opt_e;

      $failed = 1;
}

sub telnet
{
   my ($t, $com, $log) = @_;
   my ($fail, $line, $l);

   if (! $opt_i)
   {
      open (LOG, ">$log") || die "open >$log: $! ";
      my ($tmp) = select(LOG);$|=1;select($tmp);
   }

   $com .= ";echo STATUS=\$$statstr";
   $t->print($com);

   while ($line = $t->get())
   {
      if ($line =~ /STATUS=[1-9]/)
      {
         $fail = 1 ;
      }
      $l = $line;
      $l =~ s/$prompt//g;
      $l =~ s/;echo STATUS=.*//;
      $l =~ s/STATUS=.*//;
      print LOG $l unless $opt_i;
      print $l if $opt_i;
      last if $line =~ /STATUS=\d+/;
      if ($line =~ /Continue\? \(y/)
      {
         $t->print('n');
      }
   }

   close(LOG) unless $opt_i;
   $fail;
}

sub lock_perforce
{
   $saved_prot = "$ENV{'TEMP'}/p4tmp.$$";
   system("p4 protect -o > $saved_prot") && die "p4 protect failed\n";

   system("p4 protect -i < p4.protect") && die "p4 protect failed\n";
}

sub update_label
{
# Gather all labels
   open(PIPE, "p4 labels |") || die "open PIPE to p4: $! ";

   while (<PIPE>)
   {
      next unless /^Label REV/i;
      my ($l, $r) = (split('\s+', $_))[1];

      ($r = $l) =~ s/REV//i;

      $lhash{$r} = "";
   }

   close(PIPE);

# Increment the build label
   $labfile = "$root/snapfish/".$branch."/build/rev/label.txt";
   open(FILE, $labfile) || die "open $labfile: $! ";

   $lab = (<FILE>)[0];

   close(FILE);

   $lab = (split('\s', $lab))[0];

   ($pref, $v1, $v2) = ($lab =~ /([^\d]+)(\d+).(\d+)/);

# Generate new label name
   while (1)
   {
      $v2++;

      last unless exists $lhash{"$v1.$v2"};
      print "Skipping existing label REV$v1.$v2\n";
   }

   $lab = "$pref" . "$v1.$v2";

# Create new label
   system("p4 label -o $lab | p4 label -i") &&
   mail2medie("p4 label -o $lab | p4 label -i$!\n");

   ($m, $h, $d, $mo, $y) = (localtime())[1,2,3,4,5];
   $mo++;
   $y += 1900;

   $m = "0$m" if $m < 10;
   $h = "0$h" if $h < 10;
   $d = "0$d" if $d < 10;
   $mo = "0$mo" if $mo < 10;

   $labs = "$lab\t# Created by $ENV{USERNAME} on $ENV{COMPUTERNAME} at $h:$m $mo/$d/$y";

   $chnum = create_changlist($chnum, "Temporary for label update");

   system("p4 edit -c $chnum $labfile") &&
   die "p4 edit -c $chnum $labfile ";

   open(FILE, ">$labfile") || die "open >$labfile: $! -";
   print FILE "$labs";

   close(FILE);

   system("p4 submit -c $chnum");
   system("p4 change -d $chnum");
}

sub jmsfirst
{

   if ($hostlist{$a} =~ /york_jms_server/)
   {
      -1;
   }
   else
   {
      1;
   }
}

sub get_host_errors
{
   my ($host) = @_;

   my $password = defined $defpasswd ? $defpasswd : $hostaccess{$host};

   $ftp_handle = Net::FTP->new($host);
   return "Can't open FTP session to get host logfile from $host\n" unless $ftp_handle;
   return "Can't login to host to get host logfile from $host\n" unless $ftp_handle->login("$user", "$password");
   if($user eq "dev")
   {
      $ftp_handle->cwd('/opt/usr/apps');
   }
   else
   {
      $ftp_handle->cwd("/opt/usr/apps/$user");
   }
   return "Unable to get host logfile from host $host\n" unless
      $ftp_handle->get('startup.log' ,"c:\\temp\\deploy\\$host.log");
   $ftp_handle->quit;

   return "Unable to open host logfile from $host\n" unless open(LOGFILE, "c:\\temp\\deploy\\$host.log");

   $check_line = <LOGFILE>;
   chomp $check_line;
   if ($check_line = "0")
   {
      return;
   }
   else
   {
      while (<LOGFILE>)
      {
         $host_errors .= $_;
      }
      return $host_errors;
   }
}
