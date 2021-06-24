from flask import Flask
from kafka import KafkaConsumer
from kafka import KafkaProducer

from . import nlp_lib as nl

import threading

BOOTSTRAP_SERVERS = ['localhost:9092']
producer = KafkaProducer(bootstrap_servers=BOOTSTRAP_SERVERS)
MAGIC_SPLITTER = "<==>"


def _get_args(data):
    args_text = str(data.value.decode('utf8'))
    args = args_text.split(MAGIC_SPLITTER)
    return args


def listen_kafka(topic, callback, delay):
    def poll():
        consumer = KafkaConsumer(topic, bootstrap_servers=BOOTSTRAP_SERVERS)
        consumer.poll(timeout_ms=delay)
        for message in consumer:
            callback(message)

    threading.Thread(target=poll).start()


def dispatch(data):
    service_name, graph_id, *args = _get_args(data)
    service_map = {
        "deleteNode": nl.Hints.label_rm,
        "deleteEdge": nl.Hints.rel_rm,
        "modifyNode": nl.Hints.label_mod,
        "modifyEdge": nl.Hints.rel_mod,
        "addNode": nl.Hints.label_new,
        "nre": nl.Hints.nre,
    }
    handle = service_map[service_name]
    result = handle(nl.hint_map.get(graph_id), *args)
    if result:
        message = f'{result}'.encode('utf-8')
        producer.send('py2java', message)


def create_app() -> Flask:
    listen_kafka('java2py', dispatch, 1000)
    app = Flask(__name__)
    return app
