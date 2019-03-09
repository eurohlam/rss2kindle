RSS-to-Kindle Transformer based on Camel
========================================

## Overview
The application polls rss-feeds for subscriber, transforms them into ebook's mobi format using kindlegen tool
and send resulted mobi-files by email.


## Prerequisites:
1. MongoDB. It is supposed that all subscribers are stored in mongodb
2. Tomcat. It is supposed that REST API and Web UI are deployed in Tomcat container or any other java container  
3. kindlegen. kindlegen is command-line tool for transformation of documents from xml-format to mobi-format
kindlegen is developed and supported by amazon.  kindlegen has to be deployed independently. To download kindlegen go to https://www.amazon.com/gp/feature.html?docId=1000765211
4. SMTP-server. Any SMTP-server that allows to send messages from external applications
5. Java 1.8


## How to configure
The main configuration file is rss-2-kindle-camel/src/main/resources/camel-context.properties
The follwoing properties has to be configured according to certain environment:
* mongodb.host - in case of docker it should be mongo
* kindlegen.path - absolute path to kindlegen.sh 
* smtp.uri - SMTP server URI
* storage.path.root - parent folder for saving rss and mobi files 


## How to build
To build this project use

    mvn clean install -Dmaven.skip.test.exec


## How to deploy
Docker is used to deploy and run necessary environment. 
Docker configuration was tested on versions:
* Docker 1.12.6
* Docker-compose 1.8.1

There are three containers that have to be run: 
* MongoDB 3.2.10. Dockerfile is located into docker-dir/mongo. https://hub.docker.com/_/mongo/ 
* Tomcat 9.0. Dockerfile is located into docker-dir/tomcat. https://hub.docker.com/_/tomcat/
* MailHog 1.0. Dockerfile is located into docker-dir/mailhog. https://hub.docker.com/r/mailhog/mailhog/

You can run whole environment using one maven command:

    mvn clean install docker-compose:up
    

You also can build and deploy step by step:     
    
* build the project:
        
        mvn clean install
        
* make sure that war files have been copied to docker folder:
        
        docker-dir/tomcat/resources/rss2kindle.war
        docker-dir/tomcat/resources/r2kweb.war
        
* go to docker directory
        
        cd docker-dir
        
* run docker:
 
        docker-compose build
        docker-compose up 

    
## How to run

* Web UI should be available by URL:

        http://localhost:8080/r2kweb
        
* Tomcat admin console should be available by URL:
        
        http://localhost:8080
        
* MailHog Web UI should be available by URL:
        
        http://localhost:8025


* Alternative way to get URL is by container IP:

   Get CONTAINER_ID of tomcat
 
        docker ps  

   Get IP Address of tomcat

        docker inspect --format='{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' $CONTAINER_ID

   Web UI will be available as

        http://ip_address:8080/r2kweb


* MailHog SMTP port is 1025
 
* MongoDB port is 27017 


* To run this project from within Maven in command-line mode use

        mvn exec:java

It will run only rss-2-kindle-camel application that does not have user interface,
but can be invoked via camel configuration


### Additional
For more help see the Apache Camel documentation

    http://camel.apache.org/

### Structure of document in mongodb

    { 
    "_id" : "8a60e197-3ad8-4581-83e5-09c17bd3ee96" , 
    "username" : "test" , 
    "password" : "test" ,
    "email" : "test@test.com",
    "dateCreated" : "2017-11-13" ,
    "status" : "active" ,
    "subscribers" : 
    [ 
        { 
        "email" : "test@gmail.com" , 
        "name" : "test" , 
        "rsslist" : 
        [ 
            { "rss" : "test.org/feed" , 
            "status" : "active"}
        ] , 
        "settings" : { 
            "starttime" : "2017-11-13" , 
            "timeout" : "24"
            } , 
        "status" : "active"
        }
    ] , 
    "roles" : [ "ROLE_USER"]
    }
