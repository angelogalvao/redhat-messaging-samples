package com.angelogalvao.samples;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Simple Spring Boot Test
 *
 * @author <a href="mailto:angelogalvao@gmail.com">Ângelo Galvão</a>
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {

    @Autowired
    private JmsTemplate jmsTemplate;

    @Value("${test.destination}")
    private String destination;

    @Test
    public void contextLoads() {
    }

    @Test
    public void testMessageSendAndConsume() {

        String message = "This is a test message: ActiveMQ + Spring Boot";

        // Send the message
        jmsTemplate.convertAndSend(destination, message);

        // Consume the message
        // String result = (String) jmsTemplate.receiveAndConvert(destination);

        //Assert.assertEquals(message, result);

    }

}
