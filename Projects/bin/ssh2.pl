use Net::FTP;
use Net::Telnet;
use Getopt::Long;

sub do_ssh_login
{
    my ($args) = @_ ;
    my ($host) = "64.147.177.7" ;
    my ($SSH_STRING) = "ssh2 -i c:/gatadi/Snapfish/gatadi-ssh.pub $host" ;

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

