import { mqtt } from 'aws-iot-device-sdk-v2';
import { TextDecoder } from 'util';

const decoder = new TextDecoder('utf-8');

export function middleMqtt(connection: mqtt.MqttClientConnection) {
  (async () => {
    await connection.subscribe(
      '#',
      mqtt.QoS.AtMostOnce,
      (
        topic: string,
        payload: ArrayBuffer,
        dup: boolean,
        qos: mqtt.QoS,
        retain: boolean
      ) => {
        const json = decoder.decode(payload);
        console.log(
          `Publish received. topic:"${topic}" dup:${dup} qos:${qos} retain:${retain}`
        );
        console.log(json);
      }
    );
  })();
}
