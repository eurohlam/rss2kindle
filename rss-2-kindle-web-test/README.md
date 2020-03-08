Module for automated testing of Web UI
======================================

This is a module for automated testing of service functionality via web interface. 
It is an autonomous module that has no dependencies from other `rss-2-kindle` modules. 
The module uses `Page Objects` pattern as the main design pattern.  
On technical level it is based on `Selenide`, `Allure` and `JUnit5` frameworks.   

## How to run automated tests

By default, all tests for `rss-2-kindle-web-test` are disabled. 
Before running tests we need to bring up environment for testing. The easiest way is to run the project via docker.
* go to docker directory
        
        cd docker-dir
        
* run docker:
 
        docker-compose build
        docker-compose up   
                
After that we can run automated tests
* go to testing module directory
        
        cd rss-2-kindle-web-test
        
* run maven for docker-env profile

        mvn clean install -P docker-env
                        
By default tests run in headless mode. If you want to see the browser window then use enable-head profile

    mvn test -P docker-env -P enable-head
                             
There might be a problem that selenium browser driver is not able to resolve URLs like `https://web.localhost`.
If so, you just need to add following mappings into `hosts` file (for linux systems `/etc/hosts`)
   
    127.0.0.1   web.localhost
    127.0.0.1   api.localhost
    127.0.0.1   smtp.localhost
                                 
    
 ## How to run test report
 
 As soon as tests completed we can generate `Allure` test report. 
 * go to testing module directory
 
        cd rss-2-kindle-web-test
 
 * run allure via maven 
 
        mvn allure:serve
    
    