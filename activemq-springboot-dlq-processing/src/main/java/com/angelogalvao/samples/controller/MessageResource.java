package com.angelogalvao.samples.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessageResource {
	
	@Autowired
    private JmsTemplate jmsTemplate;

    @Value("${test.destination.producer}")
    private String destination;
	
	@RequestMapping("/runDLQTest")
	public String runDLQTest() {
		
		String message = "This is a test message: ActiveMQ + Spring Boot";
		
		jmsTemplate.setPubSubDomain(true);
		jmsTemplate.convertAndSend(destination, message);
		
	
		return "Test finished!";
	}

}
