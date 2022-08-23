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
      '#',
      mqtt.QoS.AtMostOnce,
      (topic: string, payload: ArrayBuffer) => {
        if (topic === 'study_time' || topic === 'study_record') {
          console.log(`${topic} received ${decoder.decode(payload)}`);
          const type = topic === 'study_time' ? 'StudyTime' : 'StudyRecord';
          try {
            const message = JSON.parse(decoder.decode(payload)) as mqttData;
            userMap.forEach((value, key) => {
              if (value === message.userId) {
                key.send(JSON.stringify(new connData(message.userId, type)));
              }
            });
          } catch (err) {
            console.log(err);
          }
        }
      }
    );
    console.log('Subscribed to AWS IoT');
  })();
}
