set CP=e:\applibs\paymentservices\lib\Signio.jar;E:\applibs\paymentservices\lib\clearcommerce.jar;E:\applibs\paymentservices\lib\crysec.jar;E:\apps\jdk\jre\lib\rt.jar;E:\development\snapfish\dev\compiled\classes;E:\development\snapfish\dev\javasrc;E:\development\snapfish\dev\javasrc\resources\com\snapfish\paymentservices;.


@REM Usage : java TestProcessor -mode [stress|point] -actionCode [A|S|D|C] -iter n [-cc ccNumber -exp 042003 -b billingZip]
@REM POINT MODE
e:\apps\jdk\bin\java -classpath %CP% com.snapfish.paymentservices.TestProcessor -mode POINT -iter 1 -actionCode a -cc 4242424242424242 -exp 072004 -b 94538


@REM STRESS MODE
@REM e:\apps\jdk\bin\java -classpath %CP% com.snapfish.paymentservices.TestProcessor -mode STRESS -iter 1 -actionCode r----------------------------
