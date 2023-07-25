package com.github.angelogalvao.artemis.mqtt.example;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Simple message consumer using Tahu library. 
 */
public final class TestTahu implements MqttCallbackExtended {

    // Configuration
	private String serverUrl = "tcp://localhost:61616";
	private String clientId = "mqtt";
	private String username = "admin";
	private String password = "admin";
	private MqttClient client;

    public static void main(String[] args) {
        TestTahu app = new TestTahu();
        app.run();
    }


    public void run() {
		try {

            System.out.println("Creating the connection object!");
			// Connect to the MQTT Server
			MqttConnectOptions options = new MqttConnectOptions();
			options.setAutomaticReconnect(true);
			options.setCleanSession(true);
			options.setConnectionTimeout(30);
			options.setKeepAliveInterval(30);
			options.setUserName(username);
			options.setPassword(password.toCharArray());
			client = new MqttClient(serverUrl, clientId);
			client.setTimeToWait(5000); // short timeout on failure to connect
			client.connect(options);
			client.setCallback(this);

			client.subscribe("TEST/TOPIC", 0);
            System.out.println("Subscribed to the topic! ");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    @Override
    public void connectionLost(Throwable cause) {
        System.out.println("The MQTT Connection was lost! - will auto-reconnect");
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        System.out.println("Message sent!");
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        System.out.println("message arrived!");
    }

    @Override
    public void connectComplete(boolean reconnect, String serverURI) {
        System.out.println("Connected to the broker!");
    }
}
