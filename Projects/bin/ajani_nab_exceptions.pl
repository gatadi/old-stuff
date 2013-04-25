#!/usr/bin/env perl

my $CUTOFF = 10;
my $IGNORE_HEAD = 1;
my $GET_LOGGING_CLASS = 1;

my %traces;
my %traces_head;

my $last_of_last;
my $last;

my $state = 'INITIAL';

my $cnt = 0;

while (<>)
{
  my $cur = $_;
  if (/^\tat /) {
     if ($state eq 'INPROGRESS') {
        if ($CUTOFF > $cnt) {
          $buf .= $cur;
          $cnt++;
        }
     }
     else {
        $state = 'INPROGRESS';
        $buf = "";
        if ($GET_LOGGING_CLASS == 1) {
           my @line = split /\s+/, $last_of_last;
           my $class = $line[4];
           $buf .= "LOGGING CLASS = $class\n";
        }
        if ($IGNORE_HEAD == 1) {
           $buf .= $cur;
        }
        else {
           $buf .= $last;
           $buf .= $cur;
        }
        $cnt = 1;
     }
  }
  else {
     if ($state eq 'INPROGRESS') {
        $traces{$buf} ++;
        $state = 'INITIAL';
        $cnt = 0;
     }
  }

  $last_of_last = $last;
  $last = $cur;
}

foreach my $x (sort { $traces{$a} <=> $traces{$b} } keys (%traces)) {
  print "COUNT: ".$traces{$x}."\n";
  print $x;
}
