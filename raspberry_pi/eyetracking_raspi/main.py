import paho.mqtt.client as mqtt
import drivers
import threading
import time
import serial
import json

display = drivers.Lcd()
studying_time = 0

global status_start
global temp
global humi
status_start = False
temp = 0
humi = 0

userId = ""
temperatureSetting = ""
humiditySetting = ""
raspberrypiAddress = ""


UPDATE_TIME = 60

# arduino serial setting
port = '/dev/ttyACM0'
brate = 9600

ser = serial.Serial(port, baudrate=brate, timeout=None)

# ser.write(cmd.encode())

def get_serial_line(ser):
    global status_start
    global temp
    global humi

    while True:
        if ser.in_waiting != 0:
            content = ser.readline()
            # receive string from arduino
            line = content[:-2].decode()

            # unfocused
            if line == '0':
                status_start = False

            # focused
            elif line == '1':
                status_start = True

            # temp
            elif line == '2':
                while ser.int_waiting == 0:
                    pass
                temp = int(line)

            # humi
            elif line == '3':
                while ser.int_waiting == 0:
                    pass
                humi = int(line)

def get_setting_info(json_data):
    dict = json_data.loads(json_data)


"""
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
"""

serial_thread = threading.Thread(target=get_serial_line, args=(ser,))
serial_thread.start()

while True:
    # print("Main Thread")
    # pub_message(client1, "status/unfocus", "hi")


    # 아두이노 신호 받기
    get_serial_line(ser)

    
    # mqtt로 temp, humi 신호 전송 (json)
    mqttname.publish('')

    # mqtt로 신호 전송받기
    # focused
    if status_focused == True:
        display.lcd_display_string("time", 1)
        display.lcd_display_string(str(studying_time), 2)
        studying_time += 1
    # unfocused
    else:
        print("alarm")
        # set alarm

    time.sleep(1)
