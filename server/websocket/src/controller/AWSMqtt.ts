import { mqtt } from 'aws-iot-device-sdk-v2';
import { TextDecoder } from 'util';
import { WebSocket } from 'ws';
import mqttData from './mqttData';
import connData from './connData';

const decoder = new TextDecoder('utf-8');

export function middleMqtt(
  connection: mqtt.MqttClientConnection,
  userMap: Map<WebSocket, string>
) {
  (async () => {
    await connection.subscribe(
      'study_time',
      mqtt.QoS.AtMostOnce,
      (topic: string, payload: ArrayBuffer) => {
        console.log(`study_time received ${payload.toString()}`);
        try {
          const message = JSON.parse(decoder.decode(payload)) as mqttData;
          userMap.forEach((value, key) => {
            if (value === message.userId) {
              key.send(new connData(message.userId, 'StudyTime'));
            }
          });
        } catch (err) {
          console.log(err);
        }
      }
    );
    console.log('Subscribed to study_time');
    await connection.subscribe(
      'study_record',
      mqtt.QoS.AtMostOnce,
      (topic: string, payload: ArrayBuffer) => {
        console.log(`study_record received ${payload.toString()}`);
        try {
          const message = JSON.parse(decoder.decode(payload)) as mqttData;
          userMap.forEach((value, key) => {
            if (value === message.userId) {
              key.send(new connData(message.userId, 'StudyRecord'));
            }
          });
        } catch (err) {
          console.log(err);
        }
      }
    );
    console.log('Subscribed to study_record');
  })();
}
