# Resiliency patterns demo using resilience4j

This demo shows how to use the fault tolerance library [Resilience4j](https://github.com/resilience4j/resilience4j) in a Spring Boot 2 application.

See [User Guide](https://resilience4j.readme.io/docs/) for more details.

The [resilientClient]() shows how to use the Resilience4j provided Annotations.

## Getting Started

Just run the both spring boot apps in your IDE.  
Service is running on http://localhost:8080.
Client is running on http://localhost:8085.

## Monitoring with Prometheus and Grafana (OPTIONAL)

### Requirements
[Docker](https://docs.docker.com/install/) and [Docker Compose](https://docs.docker.com/compose/install/) installed.
 
### Step 1
Use docker-compose to start Grafana and Prometheus servers.  
- In the root folder  
```sh  
docker-compose -f docker-compose.yml up   
```  
Make sure 9090 port is exposed for prometheus and 3000 for grafana.  
and docker/prometheus.yml file has -  
targets: ['host.docker.internal:8085']  
Note: above target is only applicable for Docker for mac set up to make sure prometheus is able to scrape resilientClient application actuator endpoint by accessing host.  

Alternate:  
```sh  
docker-compose -f docker-compose-desktop.yml up  
```  
This will run proetheus, grafana and resilientClient on a dedicated network "net", thus urls would not be accessible via host.  
You can still ssh into docker container and curl relevant endpoints.  

### Step 2
Check the Prometheus server.
- Open http://localhost:9090
- Access status -> Targets, both endpoints must be "UP"

### Step 3
Configure the Grafana.
- Open http://localhost:3000
- **Configure integration with Prometheus**
    - Access configuration
    - Add data source
    - Select Prometheus
    - Use url "http://localhost:9090" and access with value "Browser"
- **Configure dashboard**
    - Access "home"
    - Import dashboard
    - Upload dashboard.json from /docker
    
### Step 3
Use postman or any suitable rest client to access below endpoints:  
http://localhost:8085/ - Retuns a success message  
http://localhost:8085/slow - Returns a delayed success message provided resilience4j.timelimiter.configs.default.timeoutDuration is less than sleep interval in unreliableService controller, else times out and recorded as an exception by resilience4j.  
http://localhost:8085/error - Returns an exception and recorded as a failure by resilience4j, retried by @Retry proxy with exponential back off till it opens the circuit.  
http://localhost:8085/errorwithfallback - Returns an exception and recorded as a failure by resilience4j but application gracefully handles an exception and returns fallback response even if circuit is open.  
http://localhost:8085/businesserror - Returns an exception but not recorded as a failure as it is ignored by resilience4j based on conf provided in application.yml resilience4j.retry.configs.default.ignoreExceptions: - com.example.BusinessException  

