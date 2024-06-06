#!/usr/bin/python3
# Commands to make this work with Thoth (in the same directory as this file):
#
#   thams advise
#   pipenv install
#   FLASK_APP=hello.py pipenv run flask run
#
"""An example flask server for Thoth."""

from flask import Flask
app = Flask(__name__)


@app.route('/')
def hello_world():
    """Just explain the application."""
    return 'This application does nothing. Log into the pod and try the Python scripts by yourself.'

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=8080, debug=False)