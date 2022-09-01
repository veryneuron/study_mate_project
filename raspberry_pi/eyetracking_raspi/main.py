from AWSIoTPythonSDK.MQTTLib import AWSIoTMQTTClient

"""
import drivers
"""
import netifaces as ni
import threading
import time
import serial
import json
import socket
import datetime

# mqtt
global status_focused
status_focused = False

# json
global userId
global temperatureSetting
global humiditySetting
global raspberrypiAddress
global startTimestamp
global endTimestamp

startTimestamp = None
endTimestamp = None

userId = None
temperatureSetting = None
humiditySetting = None

raspberrypiAddress = ni.ifaddresses('wlan0')[ni.AF_INET][0]['addr']

# arduino
global status_start
global temper
global humi
global is_received
status_start = False
temper = 0
humi = 0
is_received = False

global serial_status
serial_status = False

# lcd
"""
display = drivers.Lcd()
"""
studying_time = 0
temp = 0
hour = 0
min = 0
sec = 0

UPDATE_TIME = 60


def callback(self, params, packet):
    global raspberrypiAddress
    global userId
    global startTimestamp
    global endTimestamp

    topic = str(packet.topic)

    if topic == "setting":

        dict = json.loads(packet.payload)

        tempAddress = dict['rasberrypiAddress']
        print(dict)

        if tempAddress == raspberrypiAddress:
            userId = dict['userId']

    elif topic == "status/focused":

        # focus -> unfocus
        if str(packet.payload) == False:

            endTimestamp = datetime.datime.now().replace(microsecond=0).isoformat()

            json_obj = {
                'userId': userId,
                'startTimestamp': None,
                'endTimestamp': endTimestamp
            }

            json_string = json.dumps(json_obj)

            client.publish("study_record", json_string, 1)

            serial_status = False


        elif str(packet.payload) == True:

            startTimestamp = datetime.datime.now().replace(microsecond=0).isoformat()

            json_obj = {
                'userId': userId,
                'startTimestamp': startTimestamp,
                'endTimestamp': None
            }

            json_string = json.dumps(json_obj)

            client.publish("study_record", json_string, 1)

            serial_status = False


def get_serial_line(ser, client):
    global status_start
    global temper
    global humi
    global serial_status
    global is_received

    while True:
        if ser.in_waiting != 0:
            content = ser.readline()
            try:
                line = content[:-1].decode()
            except:
                print('error')
                pass

            print("line : " + str(line))
            # print("is_received :" + str(is_received))
            # unfocused
            if line == '1':
                status_start = False

                if is_received == True:
                    is_received = False

                    print("is_received set false")

                    endTimestamp = datetime.datime.now().replace(microsecond=0).isoformat()

                    json_obj = {
                        'userId': userId,
                        'startTimestamp': None,
                        'endTimestamp': endTimestamp
                    }

                    json_string = json.dumps(json_obj)

                    client.publish("study_time", json_string, 1)


            # focused
            elif line == '0':
                status_start = True

                if is_received == False:
                    is_received = True

                    startTimestamp = datetime.datetime.now().replace(microsecond=0).isoformat()

                    json_obj = {
                        'userId': userId,
                        'startTimestamp': startTimestamp,
                        'endTimestamp': None
                    }

                    json_string = json.dumps(json_obj)

                    client.publish("study_time", json_string, 1)


            # temp
            else:
                if line[0:4] == 'temp':
                    data = line[4:9]
                    print("temp: " + data)
                    temper = float(data)

                elif line[0:4] == 'humi':
                    data = line[4:9]
                    print("humi: " + data)
                    humi = float(data)
                    serial_status = True


def get_setting_info(json_data):
    global userId
    global temperatureSetting
    global humiditySetting
    global raspberrypiAddress

    dict = json_data.loads(json_data)

    userId = dict['userId']
    raspberrypiAddress = dict['rasberrypiAddress']


def set_setting_info(client):
    global userId
    global temperatureSetting
    global humiditySetting
    global raspberrypiAddress
    global temper
    global humi
    global serial_status

    while True:

        if serial_status == True:
            temperatureSetting = str(temper)
            humiditySetting = str(humi)

            json_obj = {
                'userId': userId,
                'temperatureSetting': temperatureSetting,
                'humiditySetting': humiditySetting,
                'rasberrypiAddress': raspberrypiAddress
            }

            json_string = json.dumps(json_obj)

            client.publish("measure_data", json_string, 1)

            serial_status = False


# 수정
myMQTTClient = AWSIoTMQTTClient("pi")
myMQTTClient.configureEndpoint("a27cn38pezif4g-ats.iot.ap-northeast-1.amazonaws.com", 8883)
myMQTTClient.configureCredentials("/home/pi/aws_certificate/RootCA.cer",
                                  "/home/pi/aws_certificate/private.pem.key",
                                  "/home/pi/aws_certificate/certificate.pem.crt")
myMQTTClient.configureOfflinePublishQueueing(-1)
myMQTTClient.configureDrainingFrequency(2)
myMQTTClient.configureConnectDisconnectTimeout(10)
myMQTTClient.configureMQTTOperationTimeout(5)
print("initializing IoT Core Topic...")
myMQTTClient.connect()

myMQTTClient.subscribe("#", 1, callback)

# arduino serial setting
port = '/dev/ttyACM0'
brate = 9600

ser = serial.Serial(port, baudrate=brate, timeout=None)

# arduino serial communication
serial_thread = threading.Thread(target=get_serial_line, args=(ser, myMQTTClient,))
serial_thread.start()

"""
##mqtt연결
mqttc = mqtt.Client("laptop")
mqttc.connect("172.20.10.9", 1883)
##mqtt 상태 초기화
mqttc.subscribe("#", 1, )

#client1 <- RasberrySettingDTO
client1 = mqtt.Client("server")
client1.connect("127.0.0.1")
client1.subscribe("setting")
client1.on_message = on_message_server

t = threading.Thread(target=sub_message, args=(client1,))
t.start()

#client2 <- RaspberrypiServer
client2 = mqtt.Client("pi")
client2.connect("")
client2.subscribe("status")
client2.on_message = on_message_pi

t2 = threading.Thread(target=sub_message, args=(client2,))
t2.start()
"""

# raspberrypi mqtt communication
# status_focused thread
# json com thread
# thread start


settingDTO_thread = threading.Thread(target=set_setting_info, args=(myMQTTClient,))
settingDTO_thread.start()

while True:

    # focused
    if status_focused == True:
        temp = studying_time
        hour = temp / 3600
        temp = temp % 3600
        min = temp / 60
        temp = temp % 60
        sec = temp
        """
        display.lcd_display_string("time", 1)
        display.lcd_display_string(str(hour) + " : " + str(min) + " : " + str(sec), 2)
        studying_time += 1
        """
    # unfocused
    # else:
    # print("main_thread")
    # set alarm

    time.sleep(1)
