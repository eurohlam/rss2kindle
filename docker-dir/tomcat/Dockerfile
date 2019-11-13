FROM tomcat:9.0.27-jdk8-openjdk
LABEL vendor="eurohlam" version="3.1" project="rss2kindle"

RUN mkdir /usr/local/tomcat/restserver /usr/local/tomcat/webserver
RUN mkdir /usr/local/tomcat/conf/Catalina /usr/local/tomcat/conf/Catalina/webserver

COPY resources/bin/setenv.sh /usr/local/tomcat/bin/setenv.sh
COPY resources/conf/tomcat-users.xml /usr/local/tomcat/conf/tomcat-users.xml
COPY resources/conf/server.xml /usr/local/tomcat/conf/server.xml
COPY resources/conf/Catalina/webserver/manager.xml /usr/local/tomcat/conf/Catalina/webserver/manager.xml
COPY resources/conf/tomcat-ssl-keystore.jks /usr/local/tomcat/conf/tomcat-ssl-keystore.jks
COPY resources/conf/r2kweb-truststore.jks /usr/local/tomcat/conf/r2kweb-truststore.jks
COPY resources/manager/META-INF/context.xml /usr/local/tomcat/webapps/manager/META-INF/context.xml
COPY resources/host-manager/META-INF/context.xml /usr/local/tomcat/webapps/host-manager/META-INF/context.xml

COPY resources/rss2kindle##*.war /usr/local/tomcat/restserver/
COPY resources/r2kweb##*.war /usr/local/tomcat/webserver/

COPY resources/kindle /usr/local/kindle

EXPOSE 8080
EXPOSE 8443
