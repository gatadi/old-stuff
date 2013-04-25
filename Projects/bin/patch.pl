use Net::FTP;
use Net::Telnet;
use Getopt::Long;

my ($file_with_path);
my ($file)  ;
my ($to_dir) ;
my $clean=0 ; #value 1 removes deployed file


my $telnet ;
my $ftp ;
my $hosts ;  #comma separated host names


sub usage {
    die "Usage: perl patch.pl -f filename -p filenamewithpath -d /opt/usr/apps -h fell,again\nStopped";
}

#perl e:\workarea\bin\patch.pl -f review_order_single.jsp -p e:\development\Snapfish\Dev\public_html\default\jsp\store\review_order_single.jsp -d /opt/usr/apps/snapcat/webapps/ROOT/default/jsp/store/ -h fell

#perl e:\workarea\bin\patch.pl -f dogbowl-review.jpg -p e:\development\Snapfish\Dev\public_html\default\images\store\dogbowl-review.jpg -d /opt/usr/apps/scw/htdocs/default/images/store -h fell


GetOptions("f|spurcefile=s" => \$file,
           "p|sourcepath=s" => \$file_with_path,
           "d|destpath=s" => \$to_dir,
           "h|hosts=s" => \$hosts,) ||
    usage();

if( !$file || !$file_with_path ||  !$to_dir || !$hosts ){
    usage();
}


my ($ftp_dir) = "/export/home/dev/vijay/deploy" ;
#my ($ftp_dir) = "/tmp" ;
my ($SCP_COMMAND) = "scp -v \"dev\@nice:$ftp_dir/$file\" $to_dir" ;
my ($backup_file) = $file.".bak" ;
my ($telnethost) = "localhost" ;


main() ;

sub main
{
    print "\n\nFile : ".$file ;
    print "\nFile with Path : ".$file_with_path ;
    print "\nDestination Dir : ".$to_dir ;
    print "\nHost : ".$hosts ;
    print "\n\n";

    patch_a_file();

    #unpatch_a_file();

    #delete_a_file();


}   #end of main()





sub patch_a_file()
{

    ftp_a_file();        #ftp file to nice box
    do_telnet_login();   #open telnet connection to nice
    do_patch($hosts) ;   #path file onto host
    do_telnet_logout() ; #close telnet connection

}   # end of main()


sub unpatch_a_file
{
    do_telnet_login();   #open telnet connection to nice
    do_unpatch($hosts) ;   #path file onto host
    do_telnet_logout() ; #close telnet connection

}   #unpatch_a_file ()


sub delete_a_file()  #removes the deployed and backup file
{
    do_telnet_login();   #open telnet connection to nice
    do_delete_deployed_files($hosts); #delete deployed files
    do_telnet_logout() ; #close telnet connection

}   #endof delete_a_file()



sub ftp_a_file
{
    do_ftp_login();
    do_chdir($ftp_dir) ;
    do_put($file_with_path);
    do_ls() ;
    do_ftp_logout() ;

}   #end of ftp_a_file()









sub do_patch
{
    my ($args) = @_ ;
    my ($host) = $args ;

    do_ssh_login($host) ;
    do_scp();
    do_ssh_logout();

}   #end of do_patch()


sub do_unpatch
{
    my ($args) = @_ ;
    my ($host) = $args ;

    do_ssh_login($host) ;

    print "\n\nMove backup file to original..." ;
    $telnet->cmd("mv \"$backup_file\" \"$file\"");
    #$telnet->cmd("chmod -w $file"); print "DONE\n" ;
    do_println($telnet->cmd("ls -l"));

    do_ssh_logout();

}   #end do_unpatch()



sub do_ftp_login
{
    print "\nOpening FTP Connection..." ;

    $ftp = Net::FTP->new($telnethost, Timeout => 15, Debug => 0) ;
    $login_status = $ftp->login("dev", "dlyl00n");

    if( $login_status ){
        print "CONNECTED";
    }else{
        print "FAILED" ;
    }
    print "\n" ;

}   #end of do_ftp_login

sub do_ftp_logout
{
    print "\n\nClosing FTP Connection..." ;
    $ftp->close() ;
    print "CLOSED\n\n" ;
}   #end of do_ftp_logout()

sub do_get
{
    my ($file) = @_ ;

    print "\ndo_get(\"$file\")" ;
    $ftp->get($file) or die "", $ftp->message ;

}   #end of do_get


sub do_put
{
    my ($file) = @_ ;

    print "\ndo_put($file)" ;
    $ftp->put($file) or die "", $ftp->message ;

}   #end of do_put


sub do_chdir
{
    my ($dir) = @_ ;
    print "\ndo_chdir($dir)" ;

    $ftp->cwd($dir) or die "", $ftp->message ;

}   #end of do_chdir()


sub do_ls
{
    $pwd = $ftp->pwd() ;
    print "\ndo_ls($pwd)" ;

    @ls = $ftp->ls() or die "", $ftp->message;
    foreach my $type (@ls){
        print "\n$type" ;
    }

}   #end of do_ls()


sub do_mkdir
{
    my ($dir) = @_ ;
    print "\ndo_mkdir($dir)" ;

    $ftp->mkdir($dir) or die "", $ftp->message ;
}   #end of do_mkdir()








#************************************************************************







sub do_telnet_login
{
    print "\nOpening TELNET Connection ..." ;
    $telnet = Net::Telnet->new(Timeout => 20);
    $telnet->errmode('return');
    $telnet->open("localhost");
    $telnet->login("dev", "dlyl00n");
    $telnet->print("cd $ftp_dir"); print $telnet->getlines(All=>"");
    print "CONNECTED" ;

}   #end of do_telnet_login()


sub do_ssh_login
{
    my ($args) = @_ ;
    my ($host) = $args ;
    my ($SSH_STRING) = "ssh -l dev $host" ;

    print "\n\nSSH to $host" ;

    $telnet->print($SSH_STRING);

    if (!$telnet->waitfor('/password[: ]*$/i'))
    {
        die "\n\ncould not connect to $host";
    }
    else
    {
        $telnet->print('dlyl00n'); $telnet->waitfor(Timeout => 5);
    }

    $telnet->print("hostname"); print $telnet->getlines(All=>"");
    $telnet->print("pwd"); print $telnet->getlines(All=>"");
    $telnet->print("cd $to_dir"); print $telnet->getlines(All=>"");
    $telnet->print("hostname"); print $telnet->getlines(All=>"") ;
    $telnet->print("pwd"); print $telnet->getlines(All=>"") ;

}   #end do_ssh_login()


sub do_scp
{
    print "\n\nAdding write permissions...." ;
    $telnet->cmd("chmod +w $file");   print "DONE" ;
    do_println($telnet->cmd("ls -l $file"));

    print "\n\nCreating backup file..." ;
    $telnet->cmd("cp $file $backup_file"); print "DONE\n" ;

    print "\n\nCopying file securely(scp)..." ; print flush;
    print "\n$SCP_COMMAND\n\n" ;
    do_println($telnet->print($SCP_COMMAND));
    if (!$telnet->waitfor('/password[: ]*$/i'))
    {
        die "\ncould not connect to nice";
    }
    else
    {
        $telnet->print('dlyl00n'); $telnet->waitfor(Timeout => 5);
    }
    print "DONE" ;

    print "\n\nRemoving write permissions...." ;
    $telnet->cmd("chmod -w $file");   print "DONE" ;
    do_println($telnet->cmd("ls -l $file"));
    do_println($telnet->cmd("ls -l $file.bak"));

}   #end of do_scp()


sub do_delete_deployed_files
{
    my ($args) = @_ ;
    my ($host) = $args ;

    do_ssh_login($host) ;

    print "\n\nRemoving deployed file:$file ...." ;
    $telnet->cmd("chmod +w $file");   print "DONE" ;
    do_println($telnet->cmd("rm $file"));

    print "\n\nRemoving backup file:$backup_file ...." ;
    $telnet->cmd("chmod +w $backup_file");   print "DONE" ;
    do_println($telnet->cmd("rm $backup_file"));

    do_println($telnet->cmd("ls -l"));

    do_ssh_logout();

}   #do_delete_deployed_files()


sub do_ssh_logout
{
    #closiong SSH
    $telnet->cmd("exit");
}   #end of do_ssh_logout()


sub do_telnet_logout
{
    $telnet->close() ;
}   #end of do_telnet_logout()


sub do_println
{
    print "\n";
    my $line = "" ;
    foreach my $type (@_){
        print $line ;
        $line = $type ;
    }
}

