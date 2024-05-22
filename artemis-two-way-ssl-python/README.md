# Two way TLS with AMQ Broker

# Configure the broker keystore

export PASSWORD=passwd

// Create the brokerr keystore and the certificate 
keytool -genkeypair -alias broker -keyalg RSA -keysize 2048 -storetype JKS -keystore keystore-server.jks -validity 3650 -ext SAN=dns:localhost,ip:127.0.0.1
keytool -export -alias broker -file server.crt -keystore keystore-server.jks

// Create the client certificate
openssl req -newkey rsa:2048 -new -nodes -x509 -days 3650 -keyout client-key.pem -out client-cert.pem

// Create the broker truststore from the client cert
openssl x509 -outform der -in client-cert.pem -out client-cert.der
keytool -import -alias python-client-cert -keystore truststore-server.jks -file client-cert.der

// Create the client CA (truststore)
openssl x509 -in server.crt -out ca.pem -outform PEM


## 

keytool -genkeypair -alias broker -keyalg RSA -keysize 2048 -storetype JKS -keystore keystore-server.jks -validity 3650 -ext SAN=dns:localhost,ip:127.0.0.1
keytool -genkeypair -alias client -keyalg RSA -keysize 2048 -storetype JKS -keystore client.jks -validity 3650 -ext SAN=dns:localhost,ip:127.0.0.1

keytool -export -alias broker -file server.crt -keystore server.jks
keytool -export -alias client -file client.crt -keystore client.jks

keytool -import -alias server -file server.crt -keystore client.jks
keytool -import -alias client -file client.crt -keystore server.jks

## Convert the keystore to pem

keytool -importkeystore -srckeystore client.jks -destkeystore client.p12 -srcstoretype jks -deststoretype pkcs12
openssl pkcs12 -in client.p12 -out client.pem

keytool -importkeystore -srckeystore server.jks -destkeystore server.p12 -srcstoretype jks -deststoretype pkcs12
openssl pkcs12 -in server.p12 -out server.pem

## Create the cert/key for the Phyton applciation

openssl req -newkey rsa:2048 -new -nodes -x509 -days 3650 -keyout client-key.pem -out client-cert.pem
openssl x509 -outform der -in client-cert.pem -out client-cert.der
keytool -import -alias your-alias -keystore cacerts -file client-cert.der


