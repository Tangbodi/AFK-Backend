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
    public Queue MessageMentionQueue() {return new ActiveMQQueue("message-mention-redis");}
    @Bean
    public Queue UserRegistrationQueue() {return new ActiveMQQueue("user-registration-redis");}
    @Bean
    public Queue UpdateEmailQueue() {return new ActiveMQQueue("update-email-redis");}
    @Bean
    public Queue ForgotPasswordQueue() {return new ActiveMQQueue("forgot-password-redis");}

    @Bean
    public DefaultJmsListenerContainerFactory activeMQFactory(ConnectionFactory connectionFactory, DefaultJmsListenerContainerFactoryConfigurer configure) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        configure.configure(factory, connectionFactory);
        factory.setSessionTransacted(false);
        factory.setSessionAcknowledgeMode(3);
        return factory;
    }
}
