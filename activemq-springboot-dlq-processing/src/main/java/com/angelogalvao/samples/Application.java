package com.angelogalvao.samples;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;

import javax.jms.ConnectionFactory;

/**
 * It starts the Spring Application
 *
 * @author <a href="mailto:angelogalvao@gmail.com">Ângelo Galvão</a>
 */
@SpringBootApplication
@EnableJms
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /*
     * Spring Boot auto-configuration in place.
     */
    @Bean
    public JmsListenerContainerFactory<?> createMyBrokerConnectionFactory(ConnectionFactory connectionFactory,
                                                                            DefaultJmsListenerContainerFactoryConfigurer configurer) {


        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();

        configurer.configure(factory, connectionFactory);
        return factory;
    }


}
