import paho.mqtt.client as mqtt
import threading
import time


def pub_message(client, topic, msg):
    client.publish(topic, msg)


def sub_message(client):
    rc = 0
    while rc == 0:
        rc = client.loop()


def on_message(client, userdata, message):
    print("message received", str(message.payload.decode("utf-8")))


client1 = mqtt.Client("pi")
client1.connect("127.0.0.1")
client1.subscribe("status/unfocused")
client1.on_message = on_message

t = threading.Thread(target=sub_message, args=(client1,))
t.start()

while True:
    print("Main Thread")
    pub_message(client1, "status/unfocus", "hi")
    time.sleep(1)
