package com.orvix.user.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/v1/user")
public class Diagnostics {
    @Value("${POD_NAME:unknown}")
    private String podName;
    @Value("${POD_NAMESPACE:unknown}")
    private String podNamespace;
    @Value("${POD_IPAddr:unknown}")
    private String podIpAddress;

    private final DiscoveryClient discoveryClient;

    public Diagnostics(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }
    @GetMapping("/diagnostics/test")
    public Mono<String> getTestMessage() {
        List<ServiceInstance> serviceInstances = discoveryClient.getInstances("user-service");

        StringBuilder response = new StringBuilder();
        AtomicLong count = new AtomicLong(0L);

        serviceInstances.forEach(serviceInstance -> {
            response.append("\n")
                    .append(count.incrementAndGet()).append(":\n")
                    .append("serviceId = ").append(serviceInstance.getUri())
                    .append("host = ").append(serviceInstance.getHost())
                    .append("port = ").append(serviceInstance.getPort())
                    .append("\n");
        });

        response.append("pod name = ").append(podName).append("\n")
                .append("pod namespace = ").append(podNamespace).append("\n")
                .append("pod ip address = ").append(podIpAddress).append("\n");

        return Mono.just(response.toString());
    }
}
