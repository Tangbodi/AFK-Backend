package com.example.demo.Configuration;

import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;

import javax.jms.ConnectionFactory;
import javax.jms.Queue;

@Configuration
public class ActiveMQConfig {

    @Bean
    public Queue LikeSaveQueue() {
        return new ActiveMQQueue("like-save-redis");
    }

    @Bean
    public Queue CommentCountQueue() {
        return new ActiveMQQueue("comment-count-redis");
    }
    @Bean
    public Queue ReplyCountQueue() {
        return new ActiveMQQueue("reply-count-redis");
    }
    @Bean
    public DefaultJmsListenerContainerFactory activeMQFactory(ConnectionFactory connectionFactory, DefaultJmsListenerContainerFactoryConfigurer configure) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        configure.configure(factory, connectionFactory);
        factory.setSessionTransacted(false);
        factory.setSessionAcknowledgeMode(3);
        return factory;
    }
}
