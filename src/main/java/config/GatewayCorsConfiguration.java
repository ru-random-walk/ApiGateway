package config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsUtils;
import org.springframework.web.server.WebFilter;
import reactor.core.publisher.Mono;

import java.util.Set;

@Configuration
public class GatewayCorsConfiguration extends CorsConfiguration {

    private static final Set<String> allowedOrigins = Set.of(
            "http://localhost:5500",
            "http://127.0.0.1:5500"
    );

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public WebFilter corsFilter() {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            if (CorsUtils.isCorsRequest(request)) {
                ServerHttpResponse response = exchange.getResponse();
                String origin = request.getHeaders().getOrigin();
                if (allowedOrigins.contains(origin)) {
                    response.getHeaders().add("Access-Control-Allow-Origin", origin);
                    response.getHeaders().add("Vary", "Origin");
                }
                //response.getHeaders().add("Access-Control-Allow-Origin", "*");
                response.getHeaders().add("Access-Control-Allow-Methods", "GET, PUT, POST, DELETE, OPTIONS");
                response.getHeaders().add("Access-Control-Max-Age", "3600");
                response.getHeaders().add("Access-Control-Allow-Credentials", "true");
                response.getHeaders().add("Access-Control-Allow-Headers", "*");
                if (request.getMethod() == HttpMethod.OPTIONS) {
                    response.setStatusCode(HttpStatus.OK);
                    return Mono.empty();
                }
            }
            return chain.filter(exchange);
        };
    }
}
