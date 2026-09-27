package zw.ac.uz.dpdms.gateway;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ForwardAuthFilter {
    @Bean
    GlobalFilter forwardAuthorization() {
        return (exchange, chain) -> {
            String auth = exchange.getRequest().getHeaders().getFirst("Authorization");
            if (auth != null) {
                exchange.getRequest().mutate().headers(h -> h.set("Authorization", auth)).build();
            }
            return chain.filter(exchange);
        };
    }
}