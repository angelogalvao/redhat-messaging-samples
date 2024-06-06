#!/usr/bin/python3

import sys

from proton.handlers import MessagingHandler
from proton.reactor import Container, ReceiverOption
from proton import SSLDomain, symbol, Terminus

class ReceiveHandler(MessagingHandler):
    def __init__(self, conn_url, address, subscription, desired):
        super(ReceiveHandler, self).__init__()

        self.conn_url = conn_url
        self.address = address
        self.subscription = subscription
        self.desired = desired
        self.received = 0

    def on_start(self, event):

        self.client_domain = SSLDomain(SSLDomain.MODE_CLIENT)

        self.client_domain.set_credentials("certificates/client-cert.pem", "certificates/client-key.pem", "")
        self.client_domain.set_trusted_ca_db("certificates/ca.pem")
        self.client_domain.set_peer_authentication(SSLDomain.ANONYMOUS_PEER)

        conn = event.container.connect(self.conn_url, reconnect=False, ssl_domain=self.client_domain)

        # To connect with a user and password:
        # conn = event.container.connect(self.conn_url, user="<user>", password="<password>")

        event.container.create_receiver(conn, self.address, name=self.subscription, options=SubscriptionOptions())


    def on_link_opened(self, event):
        print("RECEIVE: Created receiver for source address '{0}'".format(self.address))

    def on_message(self, event):
        message = event.message

        print("RECEIVE: Received message '{0}'".format(message.body))

        self.received += 1

        if self.received == self.desired:
            event.receiver.close()
            event.connection.close()

class SubscriptionOptions(ReceiverOption):
    def apply(self, receiver):
        receiver.source.capabilities.put_object(symbol("shared"))

def main():
    try:
        conn_url, address, subscription = sys.argv[1:4]
    except ValueError:
        sys.exit("Usage: qpid-receiver-subscription.py <connection-url> <address>  <subscription> [<message-count>]")

    try:
        desired = int(sys.argv[4])
    except (IndexError, ValueError):
        desired = 0

    handler = ReceiveHandler(conn_url, address, subscription, desired)
    container = Container(handler)
    container.run()

if __name__ == "__main__":
    try:
        main()
    except KeyboardInterrupt:
        pass