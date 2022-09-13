from AWSIoTPythonSDK.MQTTLib import AWSIoTMQTTClient
import drivers
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
global serial_status
global myMQTTClient

status_start = False
is_received = False
serial_status = False
temper = 0
humi = 0


# lcd
display = drivers.Lcd()
studying_time = 0
temp = 0
hour = 0
mins = 0
sec = 0


def callback(self, params, packet):
    global raspberrypiAddress
    global userId
    global startTimestamp
    global endTimestamp
    global myMQTTClient
    global status_start

    topic = str(packet.topic)
    payload = str(packet.payload.decode('UTF-8'))

    if topic == "setting":

        dict = json.loads(packet.payload)

        tempAddress = dict['rasberrypiAddress']
        print(dict)

        if tempAddress == raspberrypiAddress:
            userId = dict['userId']

    elif topic == "status/focused":
        if status_start == True:
            # focus -> unfocus
            if payload == 'False':

                endTimestamp = datetime.datetime.now().replace(microsecond=0).isoformat()

                json_obj = {
                    'userId': userId,
                    'startTimestamp': None,
                    'endTimestamp': endTimestamp
                }

                json_string = json.dumps(json_obj)

                myMQTTClient.publish("study_record", json_string, 0)

                serial_status = False


            elif payload == 'True':
                print("packet true")
                startTimestamp = datetime.datetime.now().replace(microsecond=0).isoformat()

                json_obj = {
                    'userId': userId,
                    'startTimestamp': startTimestamp,
                    'endTimestamp': None
                }

                json_string = json.dumps(json_obj)

                myMQTTClient.publish("study_record", json_string, 0)

                serial_status = False

                
def send_time_stamp(status_start, client):

    if status_start == False:

        endTimestamp = datetime.datetime.now().isoformat()

        json_obj = {
            'userId': userId,
            'startTimestamp': None,
            'endTimestamp': endTimestamp
        }

        json_string = json.dumps(json_obj)

        client.publish("study_time", json_string, 0)

    if status_start == True:

        startTimestamp = datetime.datetime.now().isoformat()

        json_obj = {
            'userId': userId,
            'startTimestamp': startTimestamp,
            'endTimestamp': None
        }

        json_string = json.dumps(json_obj)

        client.publish("study_time", json_string, 0)



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

            #시작x
            if line == '0':
                status_start = False
                send_time_stamp(status_start, myMQTTClient)

            #시작
            elif line == '1':
                status_start = True
                send_time_stamp(status_start, myMQTTClient)

            # temp
            else:
                if line[0:4] == 'temp':
                    data = line[4:9]
                    #print("temp: " + data)
                    temper = float(data)

                elif line[0:4] == 'humi':
                    data = line[4:9]
                    #print("humi: " + data)
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
                'temperature': temperatureSetting,
                'humidity': humiditySetting,
                'timestamp' : datetime.datetime.now().replace(microsecond=0).isoformat(),
                'raspberrypiAddress': raspberrypiAddress
            }

            json_string = json.dumps(json_obj)

            client.publish("measure_data", json_string, 1)

            serial_status = False



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


# settingDTO Thread (raspi <-> server)
settingDTO_thread = threading.Thread(target=set_setting_info, args=(myMQTTClient,))
settingDTO_thread.start()
    

while True:
  
    # focused
    if status_start == True:
        temp = studying_time
        hour = temp // 3600
        temp = temp % 3600
        mins = temp // 60
        temp = temp % 60
        sec = temp
        
        display.lcd_display_string("time", 1)
        display.lcd_display_string(str(hour) + " : " + str(mins) + " : " + str(sec), 2)
        studying_time += 1   

    time.sleep(1)
