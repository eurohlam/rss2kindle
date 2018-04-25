FROM tomcat:9.0.7-jre8
MAINTAINER eurohlam
LABEL vendor="eurohlam" version="2.0" project="rss2kindle"

COPY resources/tomcat-users.xml /usr/local/tomcat/conf/tomcat-users.xml
COPY resources/manager/META-INF/context.xml /usr/local/tomcat/webapps/manager/META-INF/context.xml
COPY resources/host-manager/META-INF/context.xml /usr/local/tomcat/webapps/host-manager/META-INF/context.xml

COPY resources/rss2kindle.war /usr/local/tomcat/webapps/rss2kindle.war
COPY resources/r2kweb.war /usr/local/tomcat/webapps/r2kweb.war
#VOLUME ["/usr/local/tomcat/webapps", "/usr/local/tomcat/conf", "/usr/local/tomcat/logs"]

EXPOSE 8080
