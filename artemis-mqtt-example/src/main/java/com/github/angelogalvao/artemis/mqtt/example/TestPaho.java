package com.github.angelogalvao.artemis.mqtt.example;

import java.util.Arrays;


import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class TestPaho {
    
    public static void main(String[] args) throws MqttException {
        
        MqttClient client = new MqttClient(
            "tcp://localhost:61616", // serverURI in format: "protocol://name:port"
            "mqtt", // ClientId
            new MemoryPersistence()); // Persistence

        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setUserName("admin");
        mqttConnectOptions.setPassword("admin".toCharArray());
        client.connect(mqttConnectOptions);

        client.setCallback(new MqttCallback() {
        
            @Override
            // Called when the client lost the connection to the broker
            public void connectionLost(Throwable cause) { 
                System.out.println("client lost connection " + cause);
            }
        
            @Override
            public void messageArrived(String topic, MqttMessage message) {
                System.out.println(topic + ": " + Arrays.toString(message.getPayload()));
            }
        
            @Override
            // Called when an outgoing publish is complete
            public void deliveryComplete(IMqttDeliveryToken token) { 
                System.out.println("delivery complete " + token);
            }
        });

        client.subscribe("TEST/TOPIC", 0); 

    }
}
