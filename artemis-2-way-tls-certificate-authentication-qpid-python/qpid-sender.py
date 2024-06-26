#!/usr/bin/python3

import sys

from proton import Message, SSLDomain
from proton.handlers import MessagingHandler
from proton.reactor import Container

class SendHandler(MessagingHandler):
    def __init__(self, conn_url, address, message_body):
        super(SendHandler, self).__init__()

        self.conn_url = conn_url
        self.address = address

        try:
            self.message_body = unicode(message_body)
        except NameError:
            self.message_body = message_body

    def on_start(self, event):

        self.client_domain = SSLDomain(SSLDomain.MODE_CLIENT)

        self.client_domain.set_credentials("certificates/client-cert.pem", "certificates/client-key.pem", "")
        self.client_domain.set_trusted_ca_db("certificates/ca.pem")
        self.client_domain.set_peer_authentication(SSLDomain.ANONYMOUS_PEER)

        # To connect with a user and password:
        conn = event.container.connect(self.conn_url, reconnect=False, ssl_domain=self.client_domain)
        event.container.create_sender(conn, self.address)

    def on_link_opened(self, event):
        print("SEND: Opened sender for target address '{0}'".format(event.sender.target.address))

    def on_sendable(self, event):
        message = Message(self.message_body)
        event.sender.send(message)

        print("SEND: Sent message '{0}'".format(message.body))

        event.sender.close()
        event.connection.close()

def main():
    try:
        conn_url, address, message_body = sys.argv[1:4]
    except ValueError:
        sys.exit("Usage: qpid-sender.py <connection-url> <address> <message-body>")

    handler = SendHandler(conn_url, address, message_body)
    container = Container(handler)
    container.run()

if __name__ == "__main__":
    try:
        main()
    except KeyboardInterrupt:
        pass