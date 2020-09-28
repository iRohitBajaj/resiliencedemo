package com.example;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

@RestController
public class ResilientClientController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResilientClientController.class);
    private final RestTemplate restTemplate;
    private final String serviceUri;
    private final String BACKEND = "unreliableservice";
    private final TimeLimiter timeLimiter;

    public ResilientClientController(RestTemplate restTemplate, TimeLimiterRegistry timeLimiterRegistry, @Value("${unreliableservice.uri}") String serviceUri) {
        this.restTemplate = restTemplate;
        this.timeLimiter = timeLimiterRegistry.timeLimiter(BACKEND);
        this.serviceUri = serviceUri;
    }

    @Bulkhead(name = BACKEND)
    @GetMapping("/")
    public String success() {
        return restTemplate.getForObject(serviceUri, String.class);
    }

    @CircuitBreaker(name = BACKEND)
    @Retry(name = BACKEND)
    @GetMapping("/error")
    public String failure() {
        return restTemplate.getForObject(serviceUri+"error", String.class);
    }

    @CircuitBreaker(name = BACKEND, fallbackMethod = "fallback")
    @Retry(name = BACKEND)
    @GetMapping("/errorwithfallback")
    public String failureWithFallback() {
        return restTemplate.getForObject(serviceUri+"error", String.class);
    }

    @CircuitBreaker(name = BACKEND, fallbackMethod = "fallback")
    @GetMapping("/slow")
    public String slowTimeout() throws Exception {
        return timeLimiter.executeFutureSupplier(
                () -> CompletableFuture.supplyAsync(() -> restTemplate.getForObject(serviceUri+"slow", String.class)));
    }

    @CircuitBreaker(name = BACKEND, fallbackMethod = "fallback")
    @GetMapping("/businesserror")
    public String businessFailure() {
        throw new BusinessException("Out of business");
    }

    private String fallback(Exception ex) {
        return "Recovered: " + ex.toString();
    }

}
