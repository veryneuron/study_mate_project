from AWSIoTPythonSDK.MQTTLib import AWSIoTMQTTClient
import drivers
import threading
import time
import serial
import json

#lcd
display = drivers.Lcd()
studying_time = 0
temp = 0
hour = 0
min = 0
sec = 0

#mqtt
global status_focused
status_focused = False

#json
global userId
global temperatureSetting
global humiditySetting
global raspberrypiAddress
userId = ""
temperatureSetting = ""
humiditySetting = ""
raspberrypiAddress = ""

#arduino
global status_start
global temp
global humi
status_start = False
temp = 0
humi = 0

UPDATE_TIME = 60

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
    global userId
    global temperatureSetting
    global humiditySetting
    global raspberrypiAddress

    dict = json_data.loads(json_data)

    userId = dict['userId']
    temperatureSetting = dict['temperatureSetting']
    humiditySetting = dict['humiditySetting']
    raspberrypiAddress = dict['raspberrypiAddress']

def set_setting_info():
    global userId
    global temperatureSetting
    global humiditySetting
    global raspberrypiAddress

    json_obj = {
        'userId' : "" + userId,
        'temperatureSetting' : "" + temperatureSetting,
        'humiditySetting' : "" + humiditySetting,
        'raspberrypiAddress' : "" + raspberrypiAddress
    }

    json_string = json.dumps(json_obj)

    return json_string


def pub_message(client, topic, msg):
    client.publish(topic, msg)

def sub_message(client):
    rc = 0
    while rc == 0:
        rc = client.loop()

#receive json object from mqtt server
def on_message_server(client, userdata, message):
    recv_data = str(message.payload.decode("utf-8"))
    get_setting_info(recv_data)

def on_message_pi(client, userdata, message):
    global status_focused

    recv_data = str(message.payload.decode("utf-8"))
    if recv_data == "unfocused":
        status_focused = False
    else:
        status_focused = True


# arduino serial setting
port = '/dev/ttyACM0'
brate = 9600

ser = serial.Serial(port, baudrate=brate, timeout=None)

#arduino serial communication
serial_thread = threading.Thread(target=get_serial_line, args=(ser,))
serial_thread.start()

#수정
myMQTTClient = AWSIoTMQTTClient("piID")
myMQTTClient.configureEndpoint("a8qp9iz9gi35h-ats.iot.us-east-1.amazonaws.com", 8883)
myMQTTClient.configureCredentials("/Users/lsy/Documents/untitled folder/certs/RootCA.pem",
                                  "/Users/lsy/Documents/untitled folder/certs/private.pem.key",
                                  "/Users/lsy/Documents/untitled folder/certs/certificate.pem.crt")
myMQTTClient.configureOfflinePublishQueueing(-1)
myMQTTClient.configureDrainingFrequency(2)
myMQTTClient.configureConnectDisconnectTimeout(10)
myMQTTClient.configureMQTTOperationTimeout(5)
print("initializing IoT Core Topic...")

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

#raspberrypi mqtt communication
#status_focused thread
#json com thread
#thread start



while True:

    # 아두이노 신호 받기
    get_serial_line(ser)

    # focused
    if status_focused == True:
        temp = studying_time
        hour = temp / 3600
        temp = temp % 3600
        min = temp / 60
        temp = temp % 60
        sec = temp

        display.lcd_display_string("time", 1)
        display.lcd_display_string(str(hour) + " : " + str(min) + " : " + str(sec), 2)
        studying_time += 1
    # unfocused
    else:
        print("alarm")
        # set alarm

    time.sleep(1)
