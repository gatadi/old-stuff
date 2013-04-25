use Cwd ;
use POSIX ;

$file = (shift) ;
$branch = (shift) ;
$branch = "Dev" ;

$file =~ s/C:\\development\\Snapfish\\$branch\\public_html\\//g ;
$file =~ s/\\/\//g ;

print "to\n//C/apps/$branch/snapcat/webapps/ROOT/" ;
Cwd::chdir("C:\\development\\snapfish\\$branch\\public_html");
system("rsync -vR $file //c/apps/$branch/snapcat/webapps/ROOT");



