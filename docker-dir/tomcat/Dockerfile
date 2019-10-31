FROM tomcat:9.0.7-jre8
LABEL vendor="eurohlam" version="2.0" project="rss2kindle"

COPY resources/bin/setenv.sh /usr/local/tomcat/bin/setenv.sh
COPY resources/conf/tomcat-users.xml /usr/local/tomcat/conf/tomcat-users.xml
COPY resources/conf/server.xml /usr/local/tomcat/conf/server.xml
COPY resources/conf/tomcat-ssl-keystore.jks /usr/local/tomcat/conf/tomcat-ssl-keystore.jks
COPY resources/manager/META-INF/context.xml /usr/local/tomcat/webapps/manager/META-INF/context.xml
COPY resources/host-manager/META-INF/context.xml /usr/local/tomcat/webapps/host-manager/META-INF/context.xml

COPY resources/rss2kindle##*.war /usr/local/tomcat/webapps/
COPY resources/r2kweb##*.war /usr/local/tomcat/webapps/

COPY resources/kindle /usr/local/kindle

EXPOSE 8080
EXPOSE 8443
