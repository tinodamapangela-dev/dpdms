package zw.ac.uz.dpdms.alert.messaging;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    public static final String EXCHANGE = "dpdms.incidents";
    public static final String QUEUE = "incident.approved.queue";
    public static final String ROUTING_KEY = "incident.approved";

    @Bean TopicExchange exchange() { return new TopicExchange(EXCHANGE, true, false); }
    @Bean Queue queue() { return QueueBuilder.durable(QUEUE).build(); }
    @Bean Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
    }
    @Bean Jackson2JsonMessageConverter jackson() { return new Jackson2JsonMessageConverter(); }
}