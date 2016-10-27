RSS-to-Kindle Transformer based on Camel
========================================
The application polls rss-feeds for subscriber, transforms them into ebook's mobi format using kindlegen tool
and send resulted mobi-files to email.

Prerequisites:
   1. MongoDB. It is supposed that all subscribers are stored in mongodb
   2. kindlegen. kindlegen has to be deployed independently.
   3. SMTP-server.



To build this project use

    mvn install

To run this project from within Maven use

    mvn exec:java

For more help see the Apache Camel documentation

    http://camel.apache.org/

