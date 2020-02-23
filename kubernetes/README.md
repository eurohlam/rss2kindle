Kubernetes configs to deploy rss-2-kindle application
====================================================

## Prerequisites

Docker images has to be built in local docker repository 
* build application artifacts
     
        mvn clean install
     
* navigate to `docker-dir` folder and run
    
        docker-compose build
            
* check docker repository using
            
        docker images
            
* docker repository has to contain the following images
    * rss2kindle/mongo:3.3
    * rss2kindle/mailhog:3.3
    * rss2kindle/api:3.3
    * rss2kindle/web:3.3

## To run deployment

Navigate into `kubernetes` folder and run 

    kubectl apply -f mailhog-deployment.yaml
    kubectl apply -f mailhog-service.yaml
    kubectl apply -f mongo-deployment.yaml
    kubectl apply -f mongo-service.yaml
    kubectl apply -f rss2kindle-api-deployment.yaml
    kubectl apply -f rss2kindle-api-service.yaml
    kubectl apply -f rss2kindle-web-deployment.yaml
    kubectl apply -f rss2kindle-web-service.yaml
    kubectl apply -f traefik-controller.yaml
    kubectl apply -f traefik-deployment.yaml
    kubectl apply -f traefik-ingressroute.yaml
    
Check pods and services
    
    kubectl get pods -o wide
    kubectl get services -o wide
    
## How to run

Web UI should be available by URL:

    https://web.localhost
        
Tomcat admin console for Web UI should be available by URL:
        
    https://web.localhost/manager
        
REST API should be available by URL:
       
    https://api.localhost

Tomcat admin console for REST API should be available by URL:
        
    https://api.localhost/manager
        
MailHog Web UI should be available by URL:
        
    http://smtp.localhost
    
