#!/usr/bin/python3

import sys

from proton import Message
from proton.handlers import MessagingHandler
from proton.reactor import Container

class SendHandler(MessagingHandler):
    def __init__(self, conn_url, address, message_body):
        print("Init 0.")
        super(SendHandler, self).__init__()

        print("Init.")

        self.conn_url = conn_url
        self.address = address

        try:
            self.message_body = unicode(message_body)
        except NameError:
            self.message_body = message_body

    def on_start(self, event):

        print(" on_start.")

        # To connect with a user and password:
        conn = event.container.connect(self.conn_url, user="admin", password="admin", sasl_enabled=False)
        print("create_sender")
        event.container.create_sender(conn, self.address)
        print("create_sender finish")

    def on_link_opened(self, event):
        print("SEND: Opened sender for target address '{0}'".format
              (event.sender.target.address))

    def on_sendable(self, event):
        print("on_sendable")
        message = Message(self.message_body)
        event.sender.send(message)

        print("SEND: Sent message '{0}'".format(message.body))

        event.sender.close()
        event.connection.close()

def main():
    try:
        conn_url, address, message_body = sys.argv[1:4]
    except ValueError:
        sys.exit("Usage: send.py <connection-url> <address> <message-body>")

    print(sys.argv)
    handler = SendHandler(conn_url, address, message_body)
    container = Container(handler)
    container.run()

if __name__ == "__main__":
    try:
        main()
    except KeyboardInterrupt:
        pass