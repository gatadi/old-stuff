DEVROOT=\development\snapfish\Dev
JDKROOT=$(JAVA_HOME)


BUILDROOT=$(DEVROOT)\compiled
#APPROOT=/apps/$(BRANCH)
#SNAPCAT=$(APPROOT)/snapcat/webapps/ROOT/WEB-INF/classes
APPROOT=/workarea/javaprojects/classes
SNAPCAT=/workarea/javaprojects/classes
INTAPPCAT=$(APPROOT)/intappcat/webapps/ROOT/WEB-INF/classes
IMAGERENDERER=$(APPROOT)/imagerenderer/webapps/ROOT/WEB-INF/classes
TASKSERVER=$(APPROOT)/taskserver/webapps/ROOT/WEB-INF/classes
WORKAREA=/workarea/JavaProjects/classes

JSPROOT=$(DEVROOT)\public_html\default\jsp

TOMCAT_JARS=$(APPROOT)/snapcat/common/lib/activation.jar;$(APPROOT)/snapcat/common/lib/ant-launcher.jar;$(APPROOT)/snapcat/common/lib/ant.jar;$(APPROOT)/snapcat/common/lib/commons-collections-3.1.jar;$(APPROOT)/snapcat/common/lib/commons-collections.jar;$(APPROOT)/snapcat/common/lib/commons-dbcp-1.2.1.jar;$(APPROOT)/snapcat/common/lib/commons-dbcp.jar;$(APPROOT)/snapcat/common/lib/commons-el.jar;$(APPROOT)/snapcat/common/lib/commons-logging-api.jar;$(APPROOT)/snapcat/common/lib/commons-pool-1.2.jar;$(APPROOT)/snapcat/common/lib/commons-pool.jar;$(APPROOT)/snapcat/common/lib/jasper-compiler.jar;$(APPROOT)/snapcat/common/lib/jasper-runtime.jar;$(APPROOT)/snapcat/common/lib/jdbc2_0-stdext.jar;$(APPROOT)/snapcat/common/lib/jndi.jar;$(APPROOT)/snapcat/common/lib/jsp-api.jar;$(APPROOT)/snapcat/common/lib/jta.jar;$(APPROOT)/snapcat/common/lib/mail.jar;$(APPROOT)/snapcat/common/lib/naming-common.jar;$(APPROOT)/snapcat/common/lib/naming-factory.jar;$(APPROOT)/snapcat/common/lib/naming-java.jar;$(APPROOT)/snapcat/common/lib/naming-resources.jar;$(APPROOT)/snapcat/common/lib/servlet-api.jar;$(APPROOT)/snapcat/common/lib/servlet.jar;$(APPROOT)/snapcat/webapps/ROOT/WEB-INF/lib/catalina-ant.jar;$(APPROOT)/snapcat/webapps/ROOT/WEB-INF/lib/catalina-cluster.jar;$(APPROOT)/snapcat/webapps/ROOT/WEB-INF/lib/catalina-i18n-es.jar;$(APPROOT)/snapcat/webapps/ROOT/WEB-INF/lib/catalina-i18n-fr.jar;$(APPROOT)/snapcat/webapps/ROOT/WEB-INF/lib/catalina-i18n-ja.jar;$(APPROOT)/snapcat/webapps/ROOT/WEB-INF/lib/catalina-optional.jar;$(APPROOT)/snapcat/webapps/ROOT/WEB-INF/lib/catalina.jar;$(APPROOT)/snapcat/webapps/ROOT/WEB-INF/lib/commons-beanutils.jar;$(APPROOT)/snapcat/webapps/ROOT/WEB-INF/lib/commons-digester.jar;$(APPROOT)/snapcat/webapps/ROOT/WEB-INF/lib/commons-fileupload-1.0.jar;$(APPROOT)/snapcat/webapps/ROOT/WEB-INF/lib/commons-logging.jar;$(APPROOT)/snapcat/webapps/ROOT/WEB-INF/lib/commons-modeler.jar;$(APPROOT)/snapcat/webapps/ROOT/WEB-INF/lib/jaas.jar;$(APPROOT)/snapcat/webapps/ROOT/WEB-INF/lib/jakarta-regexp-1.2.jar;$(APPROOT)/snapcat/webapps/ROOT/WEB-INF/lib/jakarta-regexp-1.3.jar;$(APPROOT)/snapcat/webapps/ROOT/WEB-INF/lib/jkconfig.jar;$(APPROOT)/snapcat/webapps/ROOT/WEB-INF/lib/jkshm.jar;$(APPROOT)/snapcat/webapps/ROOT/WEB-INF/lib/mx4j-jmx.jar;$(APPROOT)/snapcat/webapps/ROOT/WEB-INF/lib/mx4j.license;$(APPROOT)/snapcat/webapps/ROOT/WEB-INF/lib/servlets-cgi.renametojar;$(APPROOT)/snapcat/webapps/ROOT/WEB-INF/lib/servlets-common.jar;$(APPROOT)/snapcat/webapps/ROOT/WEB-INF/lib/servlets-default.jar;$(APPROOT)/snapcat/webapps/ROOT/WEB-INF/lib/servlets-invoker.jar;$(APPROOT)/snapcat/webapps/ROOT/WEB-INF/lib/servlets-manager.jar;$(APPROOT)/snapcat/webapps/ROOT/WEB-INF/lib/servlets-ssi.renametojar;$(APPROOT)/snapcat/webapps/ROOT/WEB-INF/lib/servlets-webdav.jar;$(APPROOT)/snapcat/webapps/ROOT/WEB-INF/lib/tomcat-coyote.jar;$(APPROOT)/snapcat/webapps/ROOT/WEB-INF/lib/tomcat-http11.jar;$(APPROOT)/snapcat/webapps/ROOT/WEB-INF/lib/tomcat-jk.jar;$(APPROOT)/snapcat/webapps/ROOT/WEB-INF/lib/tomcat-jk2.jar;$(APPROOT)/snapcat/webapps/ROOT/WEB-INF/lib/tomcat-jni.jar;$(APPROOT)/snapcat/webapps/ROOT/WEB-INF/lib/tomcat-util.jar;$(APPROOT)/snapcat/webapps/ROOT/WEB-INF/lib/tomcat-warp.jar;$(APPROOT)/snapcat/webapps/ROOT/WEB-INF/lib/activation.jar;$(APPROOT)/snapcat/webapps/ROOT/WEB-INF/lib/apiproxy.jar;$(APPROOT)/snapcat/webapps/ROOT/WEB-INF/lib/ATT-API-1-2-sp0r1-MERCHANT-SDK-lib.jar;$(APPROOT)/snapcat/webapps/ROOT/WEB-INF/lib/castor-0.9.3.19-xml.jar;$(APPROOT)/snapcat/webapps/ROOT/WEB-INF/lib/castor.properties;$(APPROOT)/snapcat/webapps/ROOT/WEB-INF/lib/classes12.jar;$(APPROOT)/snapcat/webapps/ROOT/WEB-INF/lib/classes12.zip;$(APPROOT)/snapcat/webapps/ROOT/WEB-INF/lib/clearcommerce.jar;$(APPROOT)/snapcat/webapps/ROOT/WEB-INF/lib/CORE-COMMON-1-10-sp0r6-lib.jar;$(APPROOT)/snapcat/webapps/ROOT/WEB-INF/lib/crysec.jar;$(APPROOT)/snapcat/webapps/ROOT/WEB-INF/lib/fmprtl.zip;$(APPROOT)/snapcat/webapps/ROOT/WEB-INF/lib/HTTPClient.jar;$(APPROOT)/snapcat/webapps/ROOT/WEB-INF/lib/HTTPClient.zip;$(APPROOT)/snapcat/webapps/ROOT/WEB-INF/lib/j2ee.jar;$(APPROOT)/snapcat/webapps/ROOT/WEB-INF/lib/jce-jdk13-115.jar;$(APPROOT)/snapcat/webapps/ROOT/WEB-INF/lib/jcert.jar;$(APPROOT)/snapcat/webapps/ROOT/WEB-INF/lib/jms.jar;$(APPROOT)/snapcat/webapps/ROOT/WEB-INF/lib/jmxremote.jar;$(APPROOT)/snapcat/webapps/ROOT/WEB-INF/lib/jmxremote_optional.jar;$(APPROOT)/snapcat/webapps/ROOT/WEB-INF/lib/jmxri.jar;$(APPROOT)/snapcat/webapps/ROOT/WEB-INF/lib/jmxtools.jar;$(APPROOT)/snapcat/webapps/ROOT/WEB-INF/lib/jndi.jar;$(APPROOT)/snapcat/webapps/ROOT/WEB-INF/lib/jnet.jar;$(APPROOT)/snapcat/webapps/ROOT/WEB-INF/lib/jsse.jar;$(APPROOT)/snapcat/webapps/ROOT/WEB-INF/lib/log4j-1.2.8.jar;$(APPROOT)/snapcat/webapps/ROOT/WEB-INF/lib/log4j.jar;$(APPROOT)/snapcat/webapps/ROOT/WEB-INF/lib/mail.jar;$(APPROOT)/snapcat/webapps/ROOT/WEB-INF/lib/naming.jar;$(APPROOT)/snapcat/webapps/ROOT/WEB-INF/lib/nls_charset12.jar;$(APPROOT)/snapcat/webapps/ROOT/WEB-INF/lib/nls_charset12.zip;$(APPROOT)/snapcat/webapps/ROOT/WEB-INF/lib/pop3.jar;$(APPROOT)/snapcat/webapps/ROOT/WEB-INF/lib/README;$(APPROOT)/snapcat/webapps/ROOT/WEB-INF/lib/rmissl.jar;$(APPROOT)/snapcat/webapps/ROOT/WEB-INF/lib/Signio.jar;$(APPROOT)/snapcat/webapps/ROOT/WEB-INF/lib/smack.jar;$(APPROOT)/snapcat/webapps/ROOT/WEB-INF/lib/Tools.jar;$(APPROOT)/snapcat/webapps/ROOT/WEB-INF/lib/TOPLink.jar;$(APPROOT)/snapcat/webapps/ROOT/WEB-INF/lib/TOPLinkX.jar;$(APPROOT)/snapcat/webapps/ROOT/WEB-INF/lib/tradedoubler_md5.jar;$(APPROOT)/snapcat/webapps/ROOT/WEB-INF/lib/xalan.jar;$(APPROOT)/snapcat/webapps/ROOT/WEB-INF/lib/xerces.jar;$(APPROOT)/snapcat/webapps/ROOT/WEB-INF/lib/xercesImpl.jar;$(APPROOT)/snapcat/webapps/ROOT/WEB-INF/lib/xml-apis.jar;$(APPROOT)/snapcat/webapps/ROOT/WEB-INF/lib/xml.jar;$(APPROOT)/snapcat/webapps/ROOT/WEB-INF/lib/commons-httpclient.jar;$(APPROOT)/snapcat/webapps/ROOT/WEB-INF/lib/axis-1.1.jar
BASE_CLASSPATH=$(APPROOT)/snapcat/webapps/ROOT/WEB-INF/classes;$(JDKROOT)/jre/lib/rt.jar;$(TOMCAT_JARS);$(APPROOT)/snapcat/webapps/ROOT/WEB-INF/lib/mobile-wsdl-client-1.0.jar
CLASSPATH=$(BASE_CLASSPATH);$(DEVROOT)/compiled/classes;$(DEVROOT)/javasrc/resources;

#########################################
#   VSlick Command line Configurations

#   execute:   cd \workarea\bin & pmake -f Make.mk execute FILE_JAVA=%f FILE=%n BRANCH=Dev
#   compile:   cd \workarea\bin & pmake -f Make.mk compile FILE_JAVA=%f BRANCH=Dev
#   rebuild:   cd \workarea\bin & pmake -f Make.mk deployjsp FILE_JAVA=%f BRANCH=dev


all: donothing

compile:
    @echo compile: compiling $(FILE_JAVA) ...
    @echo copying compiled file to $(SNAPCAT)  ...
    @set CLASSPATH=$(CLASSPATH);$(WORKAREA);;
    @$(JDKROOT)\bin\javac -d $(SNAPCAT) -g $(FILE_JAVA)

compile_imagerenderer:
    @echo compile_imagerenderer:compiling $(FILE_JAVA) .
    @echo copying compiled file to $(IMAGERENDERER)  .....
    @set CLASSPATH=$(CLASSPATH);$(WORKAREA);$(IMAGERENDERER);
    @$(JDKROOT)\bin\javac -d $(IMAGERENDERER) -g $(FILE_JAVA)

compile_test:
    @echo compile_test:compiling $(FILE_JAVA)  ...
    @set CLASSPATH=$(WORKAREA);$(CLASSPATH);
    @$(JDKROOT)\bin\javac -deprecation -d $(WORKAREA) -g $(FILE_JAVA)

execute: compile_test
    @echo execute:executing $(FILE) ...
    @set CLASSPATH=$(WORKAREA);$(CLASSPATH);
    @$(JDKROOT)\bin\java -mx512m -ms12m $(FILE)

compileJsp:
    @set CLASSPATH=$(CLASSPATH);$(BUILDROOT)\classes
    @echo Testing JSPs in $(DEVROOT)\$(JSPDIR)
    @$(JDKROOT)\bin\java weblogic.jspc -d $(BUILDROOT)\jspclasses -nowrite -compiler $(JDKROOT)\bin\javac.exe -docroot $(JSPROOT) $(DEVROOT)\$(JSPDIR)\*.jsp

deployjsp:
    @echo deploying $(FILE_JAVA) ...
    @perl e:\workarea\bin\deployjsp.pl $(FILE_JAVA) $(BRANCH)

updatedb:
    @echo updating db GATADI64
    @cd \development\Snapfish\Dev\build && pmake USER=GATADI64_WEB01 DBHOST=bolinuxdb1 SID=SFODEV02 -f Root.mk updatedb


donothing:
    @echo no command is passed


