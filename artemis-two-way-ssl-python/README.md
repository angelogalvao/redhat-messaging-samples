# Two way TLS with AMQ Broker

# Configure the broker keystore

export PASSWORD=passwd

// Create the brokerr keystore and the certificate 
```sh
keytool -genkeypair -alias broker -keyalg RSA -keysize 2048 -storetype JKS -keystore keystore-server.jks -validity 3650 -ext SAN=dns:localhost,ip:127.0.0.1
keytool -export -alias broker -file server.crt -keystore keystore-server.jks
```
// Create the client certificate
```sh
openssl req -newkey rsa:2048 -new -nodes -x509 -days 3650 -keyout client-key.pem -out client-cert.pem
```
// Create the broker truststore from the client cert
```sh
openssl x509 -outform der -in client-cert.pem -out client-cert.der
keytool -import -alias python-client-cert -keystore truststore-server.jks -file client-cert.der
```
// Create the client CA (truststore)
```sh
openssl x509 -in server.crt -out ca.pem -outform PEM
```
## Configure a acceptor in the broker side.


Add the following acceptor in broker.xml. Don't forget to change the path to the correct path to keystore and truststore

```xml
<acceptor name="artemis-2-way-tls">tcp://0.0.0.0:61617?sslEnabled=true;keyStorePath=/path/to/keystore-server.jks;keyStorePassword=passwd;needClientAuth=true;trustStorePath=/path/to/truststore-server.jks;trustStorePassword=passwd</acceptor>
```

## Run the application

```sh
./artemis-example.py amqps://localhost:61617 testQueue "Test Message"
```