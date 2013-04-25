@cd e:\development\snapfish\dev\build
rem perl e:\development\snapfish\Dev\build\deploy.pl --branch DEV --label CURRENT --spec devspec.pl --user dev --from 192.168.168.174 --component snapservers,nes_servers  --to showme,frankfurt --norestart "-b" SET BRANCH=%4%

perl nightly.pl -b 1 -f 10.250.100.137 -i -t bolinuxdb1 -s valuelabsdev.pl -m vijay@snapfish.com -l REL6.4.001 -u oracle -p oracle -o snapcat -q -r -c -w DEV


rem perl bootstrap.pl -b Dev snapcat -q rsync://10.250.100.137 valuelabsdev.pl HOST /opt/usr/apps

rem rsync -vR default/jsp/ad/ad.jsp loclhost::jsp --port=1873



