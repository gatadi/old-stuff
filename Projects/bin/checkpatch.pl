print "Checking Patch details...\n";
$x = system("p4 changes -s submitted //depot/Patch/ProductionPatch/SNF0019141/...");

print $x ;