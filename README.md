RSS-to-Kindle Transformer based on Camel
========================================

## Overview
The application polls rss-feeds for subscriber, transforms them into ebook's mobi format using kindlegen tool
and send resulted mobi-files to email.


## Prerequisites:
1. MongoDB. It is supposed that all subscribers are stored in mongodb
2. kindlegen. kindlegen is command-line tool for transformation of documents from xml-format to mobi-format.
kindlegen is developed and supported by amazon.  kindlegen has to be deployed independently. To download kindlegen go to https://www.amazon.com/gp/feature.html?docId=1000765211
3. SMTP-server. Any SMTP-server that allows to send messages from external applications


## How to build
To build this project use

    mvn clean install -Dmaven.test.skip=true


## How to configure
The main configuration file is rss-2-kindle-camel/src/main/resources/camel-context.properties
The follwoing properties has to be configured according to certain environment:
* mongodb.host - in case of docker it should be mongo
* kindlegen.path - absolute path to kindlegen.sh 
* smtp.uri - SMTP server URI
* storage.path.root - parent folder for saving rss and mobi files 


## How to deploy
Docker is used to deploy and run necessary environment. 
Docker configuration was tested on versions:
* Docker 1.12.6
* Docker-compose 1.8.1

There are to containers that have to be run: 
* MongoDB 3.2.10: Dockerfile is located into docker-dir/mongo 
* Tomcat 9.0. Dockerfile is located into docker-dir/tomcat

To run whole environment use

    docker-compose up 

    
## How to run
* To run this project from within Maven in command-line mode use

        mvn exec:java


* To run this project from within Tomcat

        http://localhost:8080/rss2kindle/main.html


### Additional
For more help see the Apache Camel documentation

    http://camel.apache.org/

### Structure of document in mongodb

    { 
    "_id" : "8a60e197-3ad8-4581-83e5-09c17bd3ee96" , 
    "username" : "test" , 
    "password" : "test" , 
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
