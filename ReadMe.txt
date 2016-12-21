RSS-to-Kindle Transformer based on Camel
========================================
The application polls rss-feeds for list of subscribers, transforms them into ebook's mobi format using kindlegen tool
and send resulted mobi-files to subscribers by email.

Prerequisites:
   1. MongoDB. It is supposed that all subscribers are stored in mongodb
   2. Kindlegen. kindlegen is command-line tool that has to be deployed independently. To download kindlegen go to https://www.amazon.com/gp/feature.html?docId=1000765211
   3. SMTP-server. Any SMTP-server that allows to send messages from external applications





To build this project use

    mvn install

To run this project from within Maven use

    mvn exec:java

For more help see the Apache Camel documentation

    http://camel.apache.org/

