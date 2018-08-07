# Artemis Broker Plugin Example

This example logs the message flowing IN and OUT the broker.

Use it as a reference for your own implementation.

## How to deploy it

1. Copy the compiled jar into `${artemis_home}/lib` directory;
2. Add the Broker Plugin configuration into `${artemis_instance_home}/etc/broker.xml`
```
<broker-plugins>
   <broker-plugin class-name="com.angelogalvao.samples.artemis.MyBrokerPlugin"/>
</broker-plugins>
```
3. Start to send and to consume messages of the broker;
4. Look the logs;