package zw.ac.uz.dpdms.fire.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    public static final String EXCHANGE = "dpdms.incidents";

    @Bean TopicExchange incidentsExchange() { return new TopicExchange(EXCHANGE, true, false); }
    @Bean Jackson2JsonMessageConverter jackson() { return new Jackson2JsonMessageConverter(); }
}