# Two way TLS with AMQ Broker

## Configure the broker keystore

```sh
export PASSWORD=passwd
```

- Create the brokerr keystore and the certificate

```sh
keytool -genkeypair -alias broker -keyalg RSA -keysize 2048 -storetype JKS -keystore keystore-server.jks -validity 3650 -ext SAN=dns:localhost,ip:127.0.0.1
keytool -export -alias broker -file server.crt -keystore keystore-server.jks
```

- Create the client certificate

```sh
openssl req -newkey rsa:2048 -new -nodes -x509 -days 3650 -keyout client-key.pem -out client-cert.pem
```

- Create the broker truststore from the client cert

```sh
openssl x509 -outform der -in client-cert.pem -out client-cert.der
keytool -import -alias python-client-cert -keystore truststore-server.jks -file client-cert.der
```

- Create the client CA (truststore)

```sh
openssl x509 -in server.crt -out ca.pem -outform PEM
```

## Configure a acceptor in the broker side.

- Add the following acceptor in broker.xml. Don't forget to change the path to the correct path to keystore and truststore

```xml
<acceptor name="artemis-2-way-tls">tcp://0.0.0.0:61617?sslEnabled=true;keyStorePath=/path/to/keystore-server.jks;keyStorePassword=passwd;needClientAuth=true;trustStorePath=/path/to/truststore-server.jks;trustStorePassword=passwd</acceptor>
```

## Configure the queue

- Add the folowing queues in broker.xml

```xml
    <address name="test">
        <anycast>
            <queue name="test" />
        </anycast>
    </address>
    <address name="TEST-M">
        <multicast>
            <queue name="TEST-A" />
        </multicast>
    </address>
```

## Configure the Certificate based authentication/authorization

- Add the user in artemis-users-properties file (example):

```
python-app=EMAILADDRESS=asouza@redhat.com, CN=Python Application, OU=CEE, O=Red Hat Inc., L=Raleigh, ST=NC, C=US
```

- Add the role to that user in artemis-roles-properties file:

```
test = python-app
```

- Configure new TextFileCertificateLoginModule in login.config file:

```
    org.apache.activemq.artemis.spi.core.security.jaas.TextFileCertificateLoginModule sufficient
       debug=true
       org.apache.activemq.jaas.textfiledn.user="artemis-users.properties"
       org.apache.activemq.jaas.textfiledn.role="artemis-roles.properties";
```

- Also set the PropertiesLoginModule to sufficient
- Set the new security settings in the broker.xml file:

```xml
<security-setting match="test">
    <permission type="createNonDurableQueue" roles="test"/>
    <permission type="deleteNonDurableQueue" roles="test"/>
    <permission type="createAddress" roles="test"/>
    <permission type="deleteAddress" roles="test"/>
    <permission type="consume" roles="test"/>
    <permission type="send"    roles="test"/>
    <permission type="browse"  roles="test"/>
    <permission type="manage"  roles="test"/>
</security-setting>
<security-setting match="TEST-M.#">
    <permission type="createNonDurableQueue" roles="test"/>
    <permission type="deleteNonDurableQueue" roles="test"/>
    <permission type="createAddress" roles="test"/>
    <permission type="deleteAddress" roles="test"/>
    <permission type="consume" roles="test"/>
    <permission type="send"    roles="test"/>
    <permission type="browse"  roles="test"/>
    <permission type="manage"  roles="test"/>
</security-setting>
```

## Run the application on Linux

- Install QPID Proton module:

```sh
 sudo yum install python3-qpid-proton python-qpid-proton-docs
```

- Execute the application where the configuration is on the code:

```sh
./qpid-sender.py amqps://localhost:61617 testQueue "Test Message"
```

- Execute the application where the configuration is on the file:

```sh
./qpid-sender-config-file.py testQueue "Test Message"
```

## Run the application on MacOS

- Install the virtual env to install QPID Proton module:

```sh
python3 -m venv .venv
source .venv/bin/activate
python3 -m pip install python-qpid-proton
python3 -m pip install flask
```

- Run the application

```sh
python3 qpid-sender.py amqps://localhost:61617 testQueue "Test Message"
```

## Run the application on Openshift

- Create the Openshift Project

```sh
oc new-project python-artemis-client-project 
```

- Deplot the app:

```sh
oc new-app --context-dir='artemis-2-way-tls-certificate-authentication-qpid-python'
```

*That deploy just enable a simple web server that do nothing! This is just an example in how to use s2i on a Python project*

- Login to the pod and call the application:

```sh
oc rsh <pod>

python3 qpid-sender.py amqps://IPorService:61617 testQueue "Test Message"
```