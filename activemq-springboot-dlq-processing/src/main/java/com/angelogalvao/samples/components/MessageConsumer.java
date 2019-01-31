package com.angelogalvao.samples.components;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class MessageConsumer {

	@JmsListener(destination="${test.destination}" )
	public void receiveMessage(String message) {
		
		System.out.println("Just recevied the message : " + message);
		
		System.out.println("I'll ignore it just to sent it to DLQ");
		
		throw new RuntimeException("Go to DLQ");
	}
}
